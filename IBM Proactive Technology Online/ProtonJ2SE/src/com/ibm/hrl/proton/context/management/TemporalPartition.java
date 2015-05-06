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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.hrl.proton.context.metadata.ComposedSegmentation;
import com.ibm.hrl.proton.context.metadata.EventInitiator;
import com.ibm.hrl.proton.context.metadata.ITemporalContextBound;
import com.ibm.hrl.proton.metadata.context.SegmentationContextType;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.utilities.containers.Pair;

/**
 * Represents a single temporal partition, which is a result of temporal members intersection.
 * TemporalPartition is represented by its (global) segmentation value and a list of initiators that
 * (partially) comply with this value. Also, a temporal partition consist of a collection of
 * internal partitions (local segmentation) - leaf partitions, maintained in a map. 
 * <code>TemporalPartition</code>. 
 * 
 */
public class TemporalPartition implements ISegmentationPartition {

	protected UUID id;
	
	protected Map<SegmentationValue,InternalSegmentationPartition> internalPartitions;
	protected ArrayList<ITemporalContextBound> initiators;
	// the most detailed segmentation value of this partition
	protected SegmentationValue segmentationValue;
	protected Date initiationTime;
	//create a SegmentationValue data member holding global context value
	// initialize it with parameter that will be passed to this ctor 
	protected SegmentationValue globalSegmentation;
	
	public Date getInitiationTime() {
		return initiationTime;
	}
	
	private static Logger logger = LoggerFactory.getLogger(TemporalPartition.class);

	public TemporalPartition(ArrayList<ITemporalContextBound> initiators,SegmentationValue globalSegmentatition) {
		this.initiators = initiators;
		internalPartitions = new HashMap<SegmentationValue,InternalSegmentationPartition>();
		this.initiationTime = Calendar.getInstance().getTime();
		this.globalSegmentation = globalSegmentatition;
		
		// we need the segmentation value, it should be evaluated with
		// the most detailed initiator segmentation value (all the rest are its subsets)
		
		segmentationValue = new SegmentationValue();
		
		int maxSegmentsNumber = 0;
		for (ITemporalContextBound initiator: initiators) {
			if (!(initiator instanceof EventInitiator)) {
				break;
			}
			EventInitiator eventInitiator = (EventInitiator)initiator;
			if (eventInitiator.getSegmentationValue().getValues().size() > maxSegmentsNumber) {
					maxSegmentsNumber = eventInitiator.getSegmentationValue().getValues().size();
					segmentationValue = eventInitiator.getSegmentationValue();
				}
		}	
		
		id = UUID.randomUUID();
	}
	
	@Override
	public boolean equals(Object other) {		
		if (this == other) return true;
		if (other == null || this.getClass() != other.getClass()) return false;
		
		TemporalPartition otherPartition = (TemporalPartition)other;
		ArrayList<ITemporalContextBound> otherInitiators  = otherPartition.getInitiators();
		
		for (int i=0; i<initiators.size(); i++) {
			if (!initiators.get(i).getId().equals(otherInitiators.get(i).getId())) {
				return false;
			}
		}
		
		return true;		
	}

	public Map<SegmentationValue,InternalSegmentationPartition> getInternalPartitions() {		
		return internalPartitions;
	}
	
	public SegmentationValue getContextSegmentationValue() {
		return segmentationValue; 	
	}
	
	public ArrayList<ITemporalContextBound> getInitiators() {
		return initiators;
	}
	
	public UUID getPartitionUUID() {
		return id;
	}

	public Collection<Pair<String,Map<String,Object>>> findInternalPartition(IEventInstance event,
			ComposedSegmentation localSegmentation) {
		// at this point we know that event falls into this temporal partition
		// now we lookup the internal partition this event falls into, and if it does not exist -
		// we create a new one; this methods returns a collection with single partition id
		// in order to be compatible with SlidingTemporalPartition.findInternalPartition 
			
		//boolean foundPartition = false;			
		logger.debug("findInternalPartition: finding internal partitions for event" + event);
		
		Map<String,Object> globalSegmentValue = new HashMap<String,Object>();
		Map<UUID,String> globalValues = globalSegmentation.getValues();
		ComposedSegmentation globalCS = globalSegmentation.getType();
		logger.debug("findInternalPartition: globalValues"+globalValues+", global CS: "+globalCS);
		if (globalCS != null)
		{
			Collection<SegmentationContextType> globalSegments = globalCS.getSegments(); 
			for (SegmentationContextType segment: globalSegments) {
				String name = segment.getName();
				globalSegmentValue.put(name,globalValues.get(segment.getId()));
			}
		}
		
		
		Collection<Pair<String,Map<String,Object>>> participating = new HashSet<Pair<String,Map<String,Object>>>();
		SegmentationValue eSegmentation = localSegmentation.getSegmentationValue(event);
		logger.debug("findInternalPartition: eSegmentation"+eSegmentation);
		// go over all internal partitions and find one with relevant value
		for (SegmentationValue segmentation: internalPartitions.keySet()) {
			logger.debug("findInternalPartition: iterating over internal partitions" +segmentation);
			if (segmentation.compliesWith(eSegmentation)) {
				logger.debug("findInternalPartition: segmetnation" + segmentation+" complies with eSegmentation"+eSegmentation);
				InternalSegmentationPartition existingPartition = internalPartitions.get(segmentation);				
				logger.debug("findInternalPartition: existing partition"+existingPartition);
				String currentPartition = existingPartition.getPartitionId().toString();
				
				Map<String,Object> segmentValue = new HashMap<String,Object>();
				Map<UUID,String> values = internalPartitions.get(segmentation).getSegmentationValue().getValues();
				ComposedSegmentation cs = internalPartitions.get(segmentation).getSegmentationValue().getType();
				logger.debug("findInternalPartition: internal partition segementation values"+values);
				logger.debug("findInternalPartition: internal partition composed segmentation"+cs);
				Collection<SegmentationContextType> segments = cs.getSegments(); 
				logger.debug("findInternalPartition: internal partition segments"+segments);
				for (SegmentationContextType segment: segments) {
					logger.debug("findInternalPartition: iterating over segments"+segment);
					String name = segment.getName();
					segmentValue.put(name,values.get(segment.getId()));
				}
				segmentValue.put(MetadataParser.TIME_WINDOW, Calendar.getInstance().getTime().getTime()-initiationTime.getTime());
				segmentValue.putAll(globalSegmentValue);
				
				participating.add(new Pair<String,Map<String,Object>>(currentPartition,segmentValue));
				
				logger.debug("findInternalPartition: returning participating partitions"+participating);
				return participating;
			}
		}
		
		// at this point we know that there is no internal partition with event's segment
		// we need to create a new one and return its id
		InternalSegmentationPartition newInternalPartition =
			new InternalSegmentationPartition(eSegmentation);	
		internalPartitions.put(eSegmentation,newInternalPartition);
		
		Map<String,Object> segmentValue = new HashMap<String,Object>();
		Map<UUID,String> values = internalPartitions.get(eSegmentation).getSegmentationValue().getValues();
		ComposedSegmentation cs = internalPartitions.get(eSegmentation).getSegmentationValue().getType();
		Collection<SegmentationContextType> segments = cs.getSegments(); 
		for (SegmentationContextType segment: segments) {
			String name = segment.getName();
			segmentValue.put(name,values.get(segment.getId()));
		}
		segmentValue.put(MetadataParser.TIME_WINDOW, Calendar.getInstance().getTime().getTime()-initiationTime.getTime());
		segmentValue.putAll(globalSegmentValue);
		
		participating.add(new Pair<String,Map<String,Object>>(newInternalPartition.getPartitionId().toString(),segmentValue));		
		return participating;
	}
	
	public Collection<Pair<String,Map<String,Object>>> terminate() {
		
		// (new) ella - another var for global context should be returned (in addition to collection)
				// all internal partitions have the same global context, so it is one for all
				// Map<String,String> - global segmentation value, generated like this:
		Map<String,Object> globalSegmentValue = new HashMap<String,Object>();
		Map<UUID,String> globalValues = globalSegmentation.getValues();
		ComposedSegmentation globalCS = globalSegmentation.getType();
		if (globalCS != null)
		{
		
			Collection<SegmentationContextType> globalSegments = globalCS.getSegments(); 
			for (SegmentationContextType segment: globalSegments) {
				String name = segment.getName();
				globalSegmentValue.put(name,globalValues.get(segment.getId()));
			}
		}
		
			
		// terminate all internal partitions
		Collection<Pair<String,Map<String,Object>>> terminatedPartitions = new HashSet<Pair<String,Map<String,Object>>>();
		if (!internalPartitions.isEmpty()) {
			for (SegmentationValue key: internalPartitions.keySet()) {				
				//TODO: inna - add global context partitions to the map
				Map<String,Object> segmentValue = new HashMap<String,Object>();
				Map<UUID,String> values = internalPartitions.get(key).getSegmentationValue().getValues();
				ComposedSegmentation cs = internalPartitions.get(key).getSegmentationValue().getType();
				Collection<SegmentationContextType> segments = cs.getSegments(); // add getSegments() to ComposedSegmentation
				for (SegmentationContextType segment: segments) {
					String name = segment.getName();
					segmentValue.put(name,values.get(segment.getId()));
				}
				segmentValue.put(MetadataParser.TIME_WINDOW, Calendar.getInstance().getTime().getTime()-initiationTime.getTime());
				segmentValue.putAll(globalSegmentValue);
				terminatedPartitions.add(new Pair<String,Map<String,Object>>(internalPartitions.get(key).getPartitionId().toString(),segmentValue));
			}
		}
		else { // no internal partitions
			// in case we have a terminator potentially closing temporal window,
			// but no internal partitions, we return a simulated internal partition needed
			// for agent of type "absence" (since it is the only agent that its evaluation
			// should be triggered even if no events arrived; for all other (deferred)
			// agents this work around should not matter
			//TODO - inna - here need to add only global partitions to the map
			Map<String,Object> segmentValue = new HashMap<String,Object>();			
			segmentValue.put(MetadataParser.TIME_WINDOW, Calendar.getInstance().getTime().getTime()-initiationTime.getTime());
			segmentValue.putAll(globalSegmentValue);
			terminatedPartitions.add(new Pair<String,Map<String,Object>>(new String("simulated partition"),segmentValue));
		}
		return terminatedPartitions;
	}

	public InternalSegmentationPartition[] getInternalPartitionsValues() {
		return (internalPartitions.values().toArray(new InternalSegmentationPartition[0]));
		
	}

	@Override
	public SegmentationValue getSegmentationValue() {
		return segmentationValue;
	}

	@Override
	public void setSegmentationValue(SegmentationValue segmentationValue) {
		this.segmentationValue = segmentationValue;
	}
	
}
