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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.hrl.proton.context.exceptions.ContextServiceException;
import com.ibm.hrl.proton.context.facade.ContextServiceFacade;
import com.ibm.hrl.proton.context.management.AdditionalInformation.NotificationTypeEnum;
import com.ibm.hrl.proton.context.metadata.AbsoluteTimeInitiator;
import com.ibm.hrl.proton.context.metadata.ComposedSegmentation;
import com.ibm.hrl.proton.context.metadata.EventInitiator;
import com.ibm.hrl.proton.context.metadata.EventTerminator;
import com.ibm.hrl.proton.context.metadata.RelativeTimeTerminator;
import com.ibm.hrl.proton.context.metadata.TemporalContextInitiator;
import com.ibm.hrl.proton.metadata.context.CompositeContextType;
import com.ibm.hrl.proton.metadata.context.ContextAbsoluteTimeInitiator;
import com.ibm.hrl.proton.metadata.context.ContextAbsoluteTimeTerminator;
import com.ibm.hrl.proton.metadata.context.ContextEventInitiator;
import com.ibm.hrl.proton.metadata.context.ContextEventTerminator;
import com.ibm.hrl.proton.metadata.context.ContextInitiator;
import com.ibm.hrl.proton.metadata.context.ContextRelativeTimeTerminator;
import com.ibm.hrl.proton.metadata.context.ContextTerminator;
import com.ibm.hrl.proton.metadata.context.SegmentationContextType;
import com.ibm.hrl.proton.metadata.context.TemporalContextType;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.runtime.context.notifications.IContextNotification;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.containers.Pair;
import com.ibm.hrl.proton.utilities.timerService.ITimerListener;

/**
 * Represents a composite context and agent associated with this context.
 * Each CompositeContextInstance is a listener to its own timer notifications. 
 * 
 * Assumptions: (1) all contexts are represented as composite context, if we have a single
 * temporal context it is represented by composite context with a single level
 * (2) we support different segments for different dimensions, however, they should be a proper
 * subset one of the other, e.g., I1 with V1 and V2 and I2 with V1...
 * (3) in a single temporal context we can not have repeating event type for initiator/terminator
 * (4) composite context instance is context type + local segmentation
 * (5) temporal context type - if at startup and never ending, should have empty
 * initiators and terminators list
 * <code>CompositeContextInstance</code>.
 * 
 */
public class CompositeContextInstance implements ITimerListener {
	
	protected UUID id;
	private static final long serialVersionUID = 1L;

	// reference to metadata context definition
	// if all contexts are composite can be CompositeContextType
	protected IContextType contextType;
	// we maintain composite context type per agent type due to the internal (local)
	// segmentation, which eventually is translated to leaf context partition
	protected IEventProcessingAgent agentType;

	// auxiliary map enabling easy look up of temporal partitions initiated by initiator 
	protected Map<TemporalContextInitiator,Collection<TemporalPartition>> initiatorPartitionsMap;
	
	// segmentation defined by agent, at epa level (can have multiple segments)
	protected ComposedSegmentation localSegmentation;
	// segmentation defined by context, refers to initiators (can have multiple segments)
	protected ComposedSegmentation globalSegmentation;	
	// each temporal member context is represented by TemporalContextWindow
	protected List<TemporalContextWindow> activeContextWindows;	
	// active partitions in this context, each partition consists of a list of initiators
	// and (not necessarily) unique segmentation value
	protected List<TemporalPartition> activePartitions;
	
	private ContextServiceFacade facade;
	
	 private static Logger logger = LoggerFactory.getLogger(CompositeContextInstance.class);
	
	public CompositeContextInstance(IContextType contextType, IEventProcessingAgent agentType,ContextServiceFacade facade) {
			
		this.agentType = agentType;
		this.facade = facade;
		this.contextType = contextType;
		this.id = UUID.randomUUID();
		activePartitions = new ArrayList<TemporalPartition>();
		globalSegmentation = new ComposedSegmentation();
		
		activeContextWindows = new ArrayList<TemporalContextWindow>();		
		// context type can be composite or single (temporal or segmentation) context		
		// for composite context - go over all its members
		if (contextType instanceof CompositeContextType) {
			List<IContextType> members = ((CompositeContextType)contextType).getMemberContexts();
			for (IContextType context: members) {
				if (context instanceof TemporalContextType) {
					activeContextWindows.add(new TemporalContextWindow(
							(TemporalContextType)context,facade));
				}
				else { // context instanceof SegmentationContextType
					globalSegmentation.add((SegmentationContextType)context);
				}
			}
		}
		// for single context - create a composite one
		if (!(contextType instanceof CompositeContextType)) { // temporal or segmentation
			if (contextType instanceof TemporalContextType) {
				activeContextWindows.add(new TemporalContextWindow(
						(TemporalContextType)contextType,facade));
			}
			else { // context instanceof SegmentationContextType
				globalSegmentation.add((SegmentationContextType)contextType);
			}			
		}			
		
		initiatorPartitionsMap = new HashMap<TemporalContextInitiator,Collection<TemporalPartition>>();
		localSegmentation = new ComposedSegmentation(agentType.getLocalSegmentation());
	}	
	
	public String getContextName() {
		return contextType.getName();
	}
	
	public String getAgentName() {
		return agentType.getName();
	}
	
	public boolean hasSystemStartupInitiator() {		
		if (contextType instanceof CompositeContextType) {
			for (IContextType context: ((CompositeContextType)contextType).getMemberContexts()) {
				if (context instanceof TemporalContextType) {
					if (((TemporalContextType)context).startsAtSystemStartup()) {
						return true;
	 				}
				}
			}					
		}
		else if (contextType instanceof TemporalContextType) {
			if (((TemporalContextType)contextType).startsAtSystemStartup()) {
				return true;
			}
		}		
		return false;
	}

	public boolean hasAbsoluteTimeInitiator() {
		if (contextType instanceof CompositeContextType) {
			for (IContextType context: ((CompositeContextType)contextType).getMemberContexts()) {
				if (context instanceof TemporalContextType) {
					if (((TemporalContextType)context).hasAbsoluteTimeInititators()) {
						return true;
					}
				}
			}
		}
		else if (contextType instanceof TemporalContextType) {
				if (((TemporalContextType)contextType).hasAbsoluteTimeInititators()) {
					return true;
				}
		}
		return false;
	}

	public Collection<ContextAbsoluteTimeInitiator> getAbsoluteTimeInitiators() {

		Collection<ContextAbsoluteTimeInitiator> initiators =
			new HashSet<ContextAbsoluteTimeInitiator>();

		if (contextType instanceof CompositeContextType) {
			for (IContextType context: ((CompositeContextType)contextType).getMemberContexts()) {
				if (context instanceof TemporalContextType) {
					if (((TemporalContextType)context).hasAbsoluteTimeInititators()) {
						initiators.addAll(((TemporalContextType)context).
								getAbsoluteTimeInitiators());
					}
				}
			}
		}
		else if (contextType instanceof TemporalContextType) {
				if (((TemporalContextType)contextType).hasAbsoluteTimeInititators()) {
					initiators.addAll(((TemporalContextType)contextType).
							getAbsoluteTimeInitiators());
				}
		}
		return initiators;
	}
	
	public int getTemporalContextWithFinestSegment() {
				
		if (contextType instanceof CompositeContextType) {
			return ((CompositeContextType)contextType).getTemporalContextWithFinestSegment();
		}
		else if (contextType instanceof TemporalContextType) {
			return 0; // index of the only temporal context
		}
		
		// it is a single segmentation context
		return -1;
	}
	
	
	/**
     * Context initiator can be either event instance or notification.
     * Event instance can potentially initiate several temporal context members,
     * while notification refers to "at startup" or absolute time initiator
	 * @param 	object (EventInstance or ContextInitiationNotification)
     * @throws 	ContextServiceException
     * @return 	Collection<String> - temporal partitions this object initiated
     */  
	public Collection<String> processContextInititiator(ITimedObject object) 
		throws ContextServiceException {
				
		// for each context type (member in ArrayList)
		//	if this event potentially initiates this context type
		//		check if there exists open ActiveTemporalContextSegment for this event
		//		if does not exist - 
		//			open a new ActiveTemporalContextSegment for its segment
		//		else (ActiveTemporalContextSegment exists)
		//			add/ignore this initiator according to initiation policy
		//		end if
		//	end if (potentially initiates)
		// end for (context type)
		//
		// if the new instance was added at least once (change)
		//	if there is at least one initiator for each temporal context type (intersection)
		//		for each temporal context type
		//			concatenate all ActiveTemporalContextSegments into a single list
		//		end for
		//		recursively go over all initiators and open new TemporalPartition(s)
		//		according to segmentation match of all initiators
		//	and if (at least one initiator)
		// end if (new instance)
		boolean globalEventAdded = false;
		boolean eventAdded = false;
		for (TemporalContextWindow window: activeContextWindows) {
			// check if the new event instance initiates this context type
			// TODO make optimization to work with right list according to timedObject:
			// ContextEventInitiator or ContextAbsoluteTimeInitiator, we can pull out the relevant
			// initiator without explicitly traversing the entire list...
			List<ContextInitiator> initiators = window.getContextType().getInitiators();
			for (ContextInitiator initiator: initiators) {
				eventAdded = false;
				assert (initiator instanceof ContextEventInitiator ||
					initiator instanceof ContextAbsoluteTimeInitiator);
				
				// initiation object can be either event initiator or absolute time initiator  
				if (initiator instanceof ContextEventInitiator &&
						object instanceof EventInstance) {
					
					IEventInstance event = (EventInstance)object;					
					if (initiator.getInitiatorType().equals(event.getEventType()) &&
							window.satisfiesInitiatorPredicate(event)) {						
						SegmentationValue segmentValue = globalSegmentation.getSegmentationValue(event);
						//ComposedSegmentation segmentationType = window.getSegmentationContextType();
						//SegmentationValue segmentValue = segmentationType.getSegmentValue(event);
						EventInitiator initiatorInstance = new EventInitiator(
								(ContextEventInitiator)initiator,event,segmentValue);

						eventAdded = window.handleInitiatorInstance(initiatorInstance,
								event,segmentValue);								
					}
				}
				else if (initiator instanceof ContextAbsoluteTimeInitiator &&
						object instanceof ContextNotification) { 
					// initiator instanceof ContextAbsoluteTimeInitiator					
					ContextInitiationNotification notification = (ContextInitiationNotification)object;
					if (initiator.getId().toString().equals(notification.getContextBoundId())) {
						SegmentationValue segmentValue = new SegmentationValue();
						AbsoluteTimeInitiator initiatorInstance = new AbsoluteTimeInitiator(
								(ContextAbsoluteTimeInitiator)initiator,notification);
												
						eventAdded = window.handleInitiatorInstance(initiatorInstance,
								notification,segmentValue);							
					}					
				}
				if (eventAdded) {
					window.handleAbsoluteAndRelaiveTerminators(this,agentType);
					globalEventAdded = eventAdded;
				}
			}
			// handle special case where context starts at system startup
			if (object instanceof ContextInitiationNotification &&
					window.getContextType().startsAtStartup()) {
				ContextInitiationNotification notification = (ContextInitiationNotification)object;
				SegmentationValue segmentValue = new SegmentationValue();
				AbsoluteTimeInitiator initiatorInstance = new AbsoluteTimeInitiator(
						new ContextAbsoluteTimeInitiator(),notification);
										
				globalEventAdded = window.handleInitiatorInstance(initiatorInstance,notification,segmentValue);
				// the startup initiator was definitely added as initiator
				window.handleAbsoluteAndRelaiveTerminators(this,agentType);				
			}	
		}
			
		// event instance was added as an initiator for each context instance
		// according to initiation policy; if it was added at least once (new segment,
		// add to existing segment) - there was a "change" and we are going to try and open
		// new active temporal partitions
		
		if (!globalEventAdded) return new HashSet<String>();

		// event was added at least once
		IntersectionOperator intersection = new IntersectionOperator(this,facade);
		return intersection.initiate();		
	}	
	
	/**
     * Context participant is necessarily an event instance.
     * We should find (1) temporal partition(s) this event falls into, according to its
     * compliance with context global key and (2) for each such a partition, find internal partition
     * this event falls into, according to its local key; if an internal partition does not exist,
     * we create a new one. Contexts without local key maintain a single internal partition
     * with empty ("null") segmentation value.
	 * @param 	event
     * @return 	Collection<String> - internal partitions this event falls into
     */  
	public Collection<Pair<String,Map<String,Object>>> processContextParticipant(IEventInstance event) throws ContextServiceException {
		
		// if all participants must agree on internal segmentation -
		// check if there are ActiveContextSegment(s) this event falls into
		// (if event has attributes in context segmentation - we decide according to them)

		// for each ActiveContextSegment 
		// 	calculate the internal segmentation value of newly arrived event
		// 	check if there is existing partition for this segment
		// 	if there is
		//		add the new instance to existing partition
		// 	else (no partition for this segment)
		//		create new partition and add event to it
		// 	end if
		// end for
		
		Collection<Pair<String,Map<String,Object>>> internalPartitions = new HashSet<Pair<String,Map<String,Object>>>();
		logger.debug("processContextParticipant: processing context participant: "+event);
		// check what temporal partitions this instance falls into
		for (TemporalPartition partition: activePartitions) {
			logger.debug("processContextParticipant: iterating over active partitions" +partition);
			SegmentationValue pSegmentation = partition.getContextSegmentationValue();
			SegmentationValue eSegmentation = new SegmentationValue(pSegmentation.getType());
			logger.debug("processContextParticipant: pSegmentation"+pSegmentation+", eSegmentation: "+eSegmentation);
			
			if (!pSegmentation.isEmpty()) {
				eSegmentation = pSegmentation.getType().getSegmentationValue(event);
				logger.debug("processContextParticipant: pSegmentation not empty, assigning value to eSegmentation"+eSegmentation);
				
			}
			
			// check if event falls into this temporal partition
			if (pSegmentation.compliesWith(eSegmentation)) {
				logger.debug("processContextParticipant: pSegmentation complies with eSegmentation, adding internal partition:"+partition);
				// we don't add an event to internal partition, just return partition id
				// partition.findInternalPartition has different implementation in TemporalPartition
				// and in SlidingTemporalPartition, since in the latter there is additional level
				// of hierarchy (SlidingTemporalInternalPartition) 
				try{
					logger.debug("processContextParticipant: before finding internal partition, event:"+event);
					logger.debug("processContextParticipant: before finding internal partition,localSegmentation:"+localSegmentation);
					internalPartitions.addAll(partition.findInternalPartition(event, localSegmentation));
				}catch(Exception e)
				{
					logger.error("processContextParticipant: finding internal partitions failed "+ e.getMessage());
					throw new ContextServiceException(e.getMessage());
				}
				logger.debug("processContextParticipant: finished adding internal partitions to the list,returning: "+internalPartitions);
			}			
		}
		
		return internalPartitions;
	}
	
	/**
     * Context terminator can be either EventInstance or ContextTerminationNotification.
     * For context terminator we are looking for temporal context members this terminator can
     * potentially terminate (event instance can terminate multiple members, while notification
     * can only terminate a single member due to its unique id (terminator type id).
     * Terminator (possibly partially) participates in a context global segment - we traverse all
     * segments and assign the terminator to segments it complies with. After that, we perform
     * another iteration to terminate all relevant temporal partitions.
     * 
     * Comment: in case of temporal members intersection (currently the only case), we could
     * terminate relevant temporal partitions already during the first pass,however, the two phases
     * are implemented for the sake of generality (e.g., for "union" operator). 
	 * @param 	object (EventInstance or ContextTerminationNotification)
     * @throws 	ContextServiceException
     * @return 	Collection<String> - temporal partitions this object terminated
     */  
	public Collection<Pair<String,Map<String,Object>>> processContextTerminator(ITimedObject object)
		throws ContextServiceException {		
		
		// context terminator can be either real or simulated (timer) event
		// terminator event can be a "wildcard" event - can terminate several context partitions
		
		// terminator can terminate several windows, since enough to terminate
		// at least one window, we will evaluate terminator segmentation value with the smallest
		// segment it potentially terminates (W1 with V1 and V2, W2 with V1 - take W2)
				
		// for each active context window
		//	if the new event potentially terminates this context type
		//		calculate its (possibly partial) segmentation value for this context type
		//		for each active segment in this context type
		//			if event's segment complies with this active segment
		//				add this event to active segment's terminators - 
		//				(it surely terminates at least one initiator there (first/last/each))
		//				according to terminating policy -
		//				find all temporal partitions that this event terminates
		//				(all partitions that an initiator participates in...)
		//				for each temporal partition
		//					terminate the partition
		//				end for (partition)
		//				remove initiator from initiators list
		//				after handling initiators, remove terminator from the list
		//				if initiators list is empty
		//					remove this active segment
		//				end if (empty)
		//			end if (complies)
		//		end for (active segment)
		//	end if (terminates)
		// end for
		
		// sliding window termination is a special case
		if (object instanceof SlidingWindowTerminationNotification) {
			return handleSlidingWindowTermination(object);
		}		
		
		boolean eventAdded = false;
		for (TemporalContextWindow window: activeContextWindows) {
			List<ContextTerminator> terminators = window.getContextType().getTerminators();
			for (ContextTerminator terminator: terminators) {				

				assert (terminator instanceof ContextEventTerminator ||
						terminator instanceof ContextRelativeTimeTerminator ||
						terminator instanceof ContextAbsoluteTimeTerminator);
				
				// termination object can be either event terminator or relative time terminator  
				if (terminator instanceof ContextEventTerminator &&
						object instanceof EventInstance) {				
				
					IEventInstance event = (EventInstance)object;					
					// event potentially terminates this context window
					if (terminator.getTerminatorType().equals(event.getEventType()) &&
							window.satisfiesTerminatorPredicate(event)) {
						//ComposedSegmentation segmentationType = window.getSegmentationContextType();
						SegmentationValue segmentValue = globalSegmentation.getSegmentationValue(event);
						
						EventTerminator terminatorInstance = new EventTerminator(
								(ContextEventTerminator)terminator,event,segmentValue);
											
						eventAdded = window.handleTerminatorInstance(terminatorInstance,
								event,segmentValue);					
					}
				}
				else if (object instanceof ContextNotification) { 

					// terminator is ContextAbsoluteTimeInitiator or ContextRelativeTimeInitiator 					
					ContextTerminationNotification notification = (ContextTerminationNotification)object;
					if (terminator.getId().toString().equals(notification.getContextBoundId())) {
						SegmentationValue segmentValue = new SegmentationValue();
						RelativeTimeTerminator terminatorInstance = new RelativeTimeTerminator(
								(ContextRelativeTimeTerminator)terminator,notification);
												
						eventAdded = window.handleTerminatorInstance(terminatorInstance,
								notification,segmentValue);	
					}					
				}
				
			}
		}
		
		if (!eventAdded) return new HashSet<Pair<String,Map<String,Object>>>();
		
		IntersectionOperator intersection = new IntersectionOperator(this,facade);
		return intersection.terminate(object);				
	}		

	private Collection<Pair<String,Map<String,Object>>> handleSlidingWindowTermination(ITimedObject object) {

		// another termination type is notification that terminates sliding window partition
		// in this case we need to find right SlidingTemporalPartition and SlidingTemporalInternalPartition
		// and invoke SlidingTemporalInternalPartition.terminate()
		
		UUID activePartition = ((SlidingWindowTerminationNotification)object).getPartitionId();
		UUID internalPartition = ((SlidingWindowTerminationNotification)object).getInternalPartitionId();
		
		// find relevant sliding temporal partition and terminate its relevant internal partition
		SlidingTemporalPartition sliding = (SlidingTemporalPartition)getActivePartition(activePartition);
		return sliding.terminate(internalPartition);
	}

	private TemporalPartition getActivePartition(UUID activePartition) {
		for (TemporalPartition partition: activePartitions) {
			if (partition.getPartitionUUID().toString().equals(activePartition.toString()))
				return partition;
		}
		return null;
	}

	@Override
	public String getListenerId() {
		return id.toString();
	}

	/**
	 * @Override
     * This function is automatically invoked upon a timer expiration.
     * Additional information is extracted and relevant notification is created (either
     * ContextInitiationNotification or ContextTerminationNotification); finally the notification
     * is routed to epa manager via the event handler.
	 * @param 	object (AdditionalInformation)
     * @return 	Object
     */  
	public Object onTimer(Object info) throws Exception {
		String contextBoundId = ((AdditionalInformation)info).getContextBoundId().toString();
		NotificationTypeEnum notificationType = ((AdditionalInformation)info).getNotificationType();

		System.out.println("on timer - composite context instance, with " +
				notificationType + " at " + System.currentTimeMillis()+"for "+contextBoundId +" and agent "+agentType.getName());
		
		IContextNotification notification;
		if (notificationType == NotificationTypeEnum.INITIATOR) {
		notification = new ContextInitiationNotification(contextType.getName(),
				System.currentTimeMillis(),System.currentTimeMillis(),
				contextBoundId,agentType.getName());
		}
		else { // notificationType == NotificationTypeEnum.terminator
			notification = new ContextTerminationNotification(contextType.getName(),
					System.currentTimeMillis(),System.currentTimeMillis(),
					contextBoundId,agentType.getName());			
		}
	
		facade.getEventHandler(
				).routeContextNotification(notification);
		
		return null;
	}
		
}
