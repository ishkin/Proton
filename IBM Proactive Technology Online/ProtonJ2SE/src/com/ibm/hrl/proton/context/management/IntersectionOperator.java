/*******************************************************************************
 * Copyright 2014 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ibm.hrl.proton.context.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.ibm.hrl.proton.context.exceptions.ContextServiceException;
import com.ibm.hrl.proton.context.facade.ContextServiceFacade;
import com.ibm.hrl.proton.context.metadata.EventTerminator;
import com.ibm.hrl.proton.context.metadata.ITemporalContextBound;
import com.ibm.hrl.proton.context.metadata.RelativeTimeTerminator;
import com.ibm.hrl.proton.context.metadata.TemporalContextInitiator;
import com.ibm.hrl.proton.metadata.context.CompositeContextType;
import com.ibm.hrl.proton.metadata.context.SlidingTimeWindow;
import com.ibm.hrl.proton.metadata.context.enums.ContextTerminationTypeEnum;
import com.ibm.hrl.proton.metadata.context.enums.ContextTerminatorPolicyEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.containers.Pair;

/**
 * Represents and maintains the intersection logic of multiple temporal contexts.
 * On initiation: all initiators required (at least one for each context member), on termination:
 * first terminator that arrives terminates the entire partition(s).
 * Termination is done for all temporal partitions comprising at least one initiator that is subject
 * for removal, then the relevant initiator(s) are removed.
 * <code>IntersectionOperator</code>.
 * 
 */
public class IntersectionOperator implements ITemporalWindowsOperator {

	//private static IntersectionOperator instance = null;
	
	protected CompositeContextInstance context;
	// auxiliary data members for internal functions
	private Collection<String>				newPartitions;
	private ITemporalContextBound			initiators[];
	private ActiveTemporalContextSegment	segments[];
	public static final Logger logger = Logger.getLogger(IntersectionOperator.class.getName());

	private int finestSegmentation;
	private ContextServiceFacade facade;

	public IntersectionOperator(CompositeContextInstance context,ContextServiceFacade facade) {
		segments = new ActiveTemporalContextSegment[context.activeContextWindows.size()];
		initiators = new TemporalContextInitiator[context.activeContextWindows.size()];
		newPartitions = new HashSet<String>();		
		this.context = context;
		this.facade = facade;
		
		// all temporal dimensions (members) comply to a single composed segmentation
		// some of them participate only partially (all initiators however need to participate
		// identically); here we look up the temporal member that participates in the biggest
		// number of segments, checking compliance we compare to the finest context 
		finestSegmentation = context.getTemporalContextWithFinestSegment();
	}
		
	@Override
	public TemporalWindowsOperatorEnum  getOperatorType() {
		return TemporalWindowsOperatorEnum.INTERSECTION;
	}
	
	@Override
	/**
     * If we have at least one active segment for each temporal context member,
     * we attempt to create new partitions (at this point we definitely added new initiator)
     * @return 	Collection<String> - initiated temporal partitions
     */  
	public Collection<String> initiate() throws ContextServiceException {
		// check if we have at least one initiator for each temporal context
		boolean allInitiatorsExist = true;
		for (TemporalContextWindow contextInstance: context.activeContextWindows) {
			if (!contextInstance.hasActiveSegments()) {
				allInitiatorsExist = false;
				break;
			}
		}		
		if (!allInitiatorsExist) return new HashSet<String>();
		
		// at this point we know that the new event was added as at least one initiator
		// and there is at least one initiator for each temporal context type (intersection)
		// recursively go over all initiators and open new temporal partitions if required
		
		return createNewPartitions();

	}
	
	private Collection<String> createNewPartitions() throws ContextServiceException {
		
		newPartitions.clear();

		// list of active context instances and inside each one a list of active segments
		// for each combination of active segments - if all can do together according to segment,
		// and such a temporal partition does not exist yet - create a new partition
		
		int startLevel = 0;
		int totalLevels = context.activeContextWindows.size();		
		//segments = new ActiveTemporalContextSegment[totalLevels];		
		traverseSegments(startLevel,totalLevels);
		
		return newPartitions;		
	}
	
	private void traverseSegments(int level, int depth) throws ContextServiceException {

		// if we reached the full depth of the loop
		if (level == depth) {
			boolean hasNewlyAddedInitiator = false;
			// check if at least one segment has a "newly added" initiator, otherwise skip
			for (ActiveTemporalContextSegment segment: segments) {
				if (segment.hasNewlyAddedInitiator()) {
					hasNewlyAddedInitiator = true;
				}
			}
			if (hasNewlyAddedInitiator) {
				checkSegmentsCompliance();
			}
			return;
		}
		
		ActiveTemporalContextSegment[] currentSegmentsList =
			context.activeContextWindows.get(level).getActiveSegmentsValues();
		
		for (ActiveTemporalContextSegment current: currentSegmentsList) {
			segments[level] = current;
			traverseSegments(level+1,depth);
		}		
	}

	/**
     * For a given list of segments, check if they comply with each other -
     * all are a subset of the finest segment and all comply in terms of values
     * @return 	boolean - segments compliance
     */  
	private boolean checkSegmentsCompliance() throws ContextServiceException {

		// at this point we have a combination of active segments (one per temporal context)
		// at the segments array; we should create a new temporal partition if (1) all segment
		// values are compatible and (2) such a partition does not exist yet
		
		ActiveTemporalContextSegment finest = segments[finestSegmentation];				
		for (int i=0; i<segments.length; i++) {
			ActiveTemporalContextSegment current = segments[i];
			if (!current.complyWith(finest)) {
				return false; 
			}			
		}
		
		// at this point we know that our combination is a good fit in terms of segment
		// check if there exists an open temporal partition with combination of this initiators
		// each ActiveTemporalContextSegment may have several initiators (according to policy)
		// another recursion is required to test all possible combinations of initiators
		
		int startLevel = 0;
		int totalLevels = segments.length;
		traverseInitiators(startLevel,totalLevels);
						
		return true;
		
	}
	
	/**
     * For "add" initiation policy we may have multiple initiators in a single segment,
     * we traverse all initiators recursively attempting to find a combination comprising
     * the newly added initiator - that combination creates a new temporal partition
     * @param 	level - current recursion level
     * @param 	depth - total number of levels (segments number)
     */  	
	private void traverseInitiators(int level, int depth) throws ContextServiceException {
		// if we reached the full depth of the loop
		if (level == depth) {
			boolean newlyAddedInitiator = false;
			// check if at least one initiator in the list is "newly added", otherwise skip
			for (ITemporalContextBound initiator: initiators) {
				if (((TemporalContextInitiator)initiator).isNewlyAdded()) {
					newlyAddedInitiator = true;
				}
			}
			if (newlyAddedInitiator) {
				checkAndCreateNewPartitions();
			}
			return;
		}
		
		ArrayList<ITemporalContextBound> currentInitiatorslist = segments[level].getInitiators();
		
		for (ITemporalContextBound current: currentInitiatorslist) {
			initiators[level] = current;
			traverseInitiators(level+1,depth);
		}		
	}

	private void checkAndCreateNewPartitions() throws ContextServiceException {

		// at this point we have array of initiators that can initiate partition

		// create temporary partition and compare to existing partitions		
		// TODO consider maintaining temporal partitions as a map with key (init. comb.)
		// to make a quick lookup here and not traverse all...
		ArrayList<ITemporalContextBound> potentialInitiators =  new ArrayList<ITemporalContextBound>();
		
		for (int i=0; i< initiators.length; i++) {
			potentialInitiators.add(initiators[i]);
		}
		
		// create regular or sliding temporal partition
		TemporalPartition temporary = null;
		SlidingTimeWindow internalContext = isSlidingWindowType();
		if (internalContext != null)
		{	
			String compositeContextName = null;
			if (context.contextType instanceof CompositeContextType)
			{
				
				CompositeContextType compositeContext = (CompositeContextType)context.contextType;
				compositeContextName = compositeContext.getName();
				logger.fine("IntersectionOperator:checkAndCreateNewPartitions: creating a new partition of the composite context"+compositeContextName  );
			}
			temporary = new SlidingTemporalPartition(potentialInitiators,internalContext.getDuration(),
					internalContext.getSlidingPeriod(),internalContext.getName(),compositeContextName,segments[finestSegmentation].getSegmentationValue(),context.getAgentName(),facade);
		}
		/*else if (context.contextType instanceof TemporalContextType) {
			temporary = new TemporalPartition(potentialInitiators);	

		} else {
			throw (new ContextServiceException("Unknown context type",null));
		}*/
		else
		{
			temporary = new TemporalPartition(potentialInitiators,segments[finestSegmentation].getSegmentationValue());	
		}
		 		
		// TODO this check might be redundant, we only get here with new initiator
		boolean foundPartition = false;
		for (TemporalPartition current: context.activePartitions) {
			// check if this partition already exists
			if (current.equals(temporary)) {
				foundPartition = true;
			}
		}

		if (!foundPartition) {			
			// mark the newly added initiator as "old" (not newly added)
			for (ITemporalContextBound initiator: initiators) {
				TemporalContextInitiator tInitiator = (TemporalContextInitiator)initiator; 
				if (tInitiator.isNewlyAdded()) {
					Collection<TemporalPartition> partitions = new HashSet<TemporalPartition>();
					partitions.add(temporary);
					// insert new entry for this initiator into the initiator-partitions map
					context.initiatorPartitionsMap.put(tInitiator,partitions);
					(tInitiator).notNewlyAdded();					
				}
				else { // not newly added initiator
					// add the new partition to existing initiator in the initiator-partitions map 
					context.initiatorPartitionsMap.get(tInitiator).add(temporary);
				}				
			}
			// create a new active temporal partition with given initiators
			newPartitions.add(temporary.getPartitionUUID().toString());
			context.activePartitions.add(temporary);
			
		}
	}	
	
	private SlidingTimeWindow isSlidingWindowType() {
		if (context.contextType instanceof SlidingTimeWindow) return (SlidingTimeWindow)context.contextType;
		if (context.contextType instanceof CompositeContextType)
		{
			CompositeContextType compositeContext = (CompositeContextType)context.contextType;
			;
			for (Iterator iterator = compositeContext.getMemberContexts().iterator(); iterator.hasNext();) {
				IContextType memberContextType = (IContextType) iterator.next();
				if (memberContextType instanceof SlidingTimeWindow) return (SlidingTimeWindow)memberContextType;
			}
		}
		return null;
	}

	@Override
	/**
     * Terminator was added to at least one segment, at this point we terminate all
     * relevant temporal partitions (with internal partitions inside), and return a collection
     * of terminated partitions ids.
     * TODO seems that we can go directly to terminateSegments without terminator,
     * ITemporalContextBound should support getting termination policy
     * @return 	Collection<String> - terminated internal partitions
     */  
	public Collection<Pair<String,Map<String,Object>>> terminate(ITimedObject object) throws ContextServiceException {
		
		// terminator was added to at least one segment
		// we need to traverse all active segments and terminate partitions
		// based on terminators policy (first/last/each); temporal partition is identified
		// by segment value and combination of initiators
		
		Collection<Pair<String,Map<String,Object>>> terminatedPartitions = new HashSet<Pair<String,Map<String,Object>>>();
		
		for (TemporalContextWindow window: context.activeContextWindows) {
			terminatedPartitions.addAll(terminateSegments(window));

			// go over all active segments and remove empty (no initiators)
			Set<Entry<SegmentationValue, ActiveTemporalContextSegment>> entrySet =
				window.getActiveSegments().entrySet();
			
			for (Iterator<Map.Entry<SegmentationValue,ActiveTemporalContextSegment>> iterator =
				entrySet.iterator(); iterator.hasNext();) {
				Map.Entry<SegmentationValue,ActiveTemporalContextSegment> entry =
					(Map.Entry<SegmentationValue,ActiveTemporalContextSegment>)iterator.next();
				ActiveTemporalContextSegment current = entry.getValue();
				//SegmentationValue segment = entry.getKey();					
				if (current.getInitiators().isEmpty()) {
					iterator.remove();
				}
			}
		}
		return terminatedPartitions;
	}

	private Collection<Pair<String,Map<String,Object>>> terminateSegments(
			TemporalContextWindow window) throws ContextServiceException {
		
		Collection<Pair<String,Map<String,Object>>> terminatedPartitions = new HashSet<Pair<String,Map<String,Object>>>();		
		for (ActiveTemporalContextSegment segment: window.getActiveSegmentsValues()) {
			// if terminators list is not empty - it is our terminator
			if (!segment.getTerminators().isEmpty()) {
				// handle terminator according to termination policy
				// relevant initiator is removed regardless of temporal partitions terminated
				ITemporalContextBound term = segment.getTerminators().get(0);

				ContextTerminationTypeEnum terminationType;
				ContextTerminatorPolicyEnum terminationPolicy;
				if (term instanceof EventTerminator) {
					terminationPolicy = ((EventTerminator)term).getTerminationPolicy();
					terminationType = ((EventTerminator)term).getTerminationType();
				}
				else { // instanceof RelativeTimeTerminator
					terminationPolicy = ((RelativeTimeTerminator)term).getTerminationPolicy();
					terminationType = ((RelativeTimeTerminator)term).getTerminationType();
				}
				
				// comment regarding termination type: it is only relevant if agent associated
				// with this context is in "deferred" mode; the implementation below assumes that
				// termination type is "terminate" only in case we care about partitions terminated,
				// i.e., we have deferred agent with terminator with "terminate" policy
				// in any other case we assume "discard" value...
				
				switch (terminationPolicy) {
					case FIRST: {
						ITemporalContextBound initiator = segment.getInitiators().get(0);
						Collection<Pair<String,Map<String,Object>>> terminated = terminateTemporalPartitions(initiator);
						if (terminationType != ContextTerminationTypeEnum.DISCARD) {
							terminatedPartitions.addAll(terminated);
						}
						context.initiatorPartitionsMap.remove(initiator);
						segment.getInitiators().remove(0);
						segment.removeTerminator();
						break;
					}
					case LAST: {
						ITemporalContextBound initiator = segment.getInitiators().get(
								segment.getInitiators().size()-1);
						Collection<Pair<String,Map<String,Object>>> terminated = terminateTemporalPartitions(initiator);
						if (terminationType != ContextTerminationTypeEnum.DISCARD) {
							terminatedPartitions.addAll(terminated);
						}						
						context.initiatorPartitionsMap.remove(initiator);
						segment.getInitiators().remove(segment.getInitiators().size()-1);
						segment.removeTerminator();
						break;
					}
					case EACH: {
						for (ITemporalContextBound initiator: segment.getInitiators()) {
							Collection<Pair<String,Map<String,Object>>> terminated = terminateTemporalPartitions(initiator);
							if (terminationType != ContextTerminationTypeEnum.DISCARD) {
								terminatedPartitions.addAll(terminated);
							}
							context.initiatorPartitionsMap.remove(initiator);
						}
						segment.getInitiators().clear();
						segment.removeTerminator();
						break;
					}
					default: {
						throw new ContextServiceException("Unknown terminator policy");
					}
				}
			}
		}
		return terminatedPartitions;
	}

	private Collection<Pair<String,Map<String,Object>>> terminateTemporalPartitions(ITemporalContextBound initiator) {
		
		Collection<Pair<String,Map<String,Object>>> terminatedPartitions = new HashSet<Pair<String,Map<String,Object>>>();
		// terminate temporal partitions comprising given initiator
		for (Iterator<TemporalPartition> iterator =
			context.activePartitions.iterator(); iterator.hasNext();) {
			TemporalPartition partition = (TemporalPartition)iterator.next();
			if (partition.getInitiators().contains(initiator)) {
				// terminate internal partitions
				terminatedPartitions.addAll(partition.terminate());
				/*if (partition.getInternalPartitionsValues().length > 0) {
					for (InternalSegmentationPartition internal: partition.getInternalPartitionsValues()) {
						terminatedPartitions.add(internal.getPartitionId().toString());
					}
				}
				else { // no internal partitions
					// in case we have a terminator potentially closing temporal window,
					// but no internal partitions, we return a simulated internal partition needed
					// for agent of type "absence" (since it is the only agent that its evaluation
					// should be triggered even if no events arrived; for all other (deferred)
					// agents this work around should not matter
					terminatedPartitions.add(new String("simulated partition"));
				}*/				
				iterator.remove();
			}									
		}
		
		// find all temporal partitions to remove and remove them
		// since we maintain a map with temporal partitions per initiator, we should only pull
		// all relevant partitions for a given initiator; each temporal partition should also be
		// removed from other initiators entries!
		/*Collection<TemporalPartition> partitions = context.initiatorPartitionsMap.get(initiator);
		for (TemporalPartition partition: partitions) {
			for (InternalSegmentationPartition internal: partition.getInternalPartitionsValues()) {
				terminatedPartitions.add(internal.getPartitionId().toString());
			}
			context.activePartitions.remove(partition);
		}*/
				
		return terminatedPartitions;		
	}
}
