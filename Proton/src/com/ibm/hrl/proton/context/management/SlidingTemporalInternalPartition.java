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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import com.ibm.hrl.proton.context.metadata.ComposedSegmentation;
import com.ibm.hrl.proton.metadata.context.SegmentationContextType;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.utilities.containers.Pair;

/**
 * Sliding temporal internal partition - a single (out of many) temporal window, with internal
 * local segmentation partitions; it maintains a map with SegmentationValue and InternalSegmentationPartition
 * as a value; this class is analogous to the TemporalPartition for regular temporal window. 
 * <code>SlidingTemporalInternalPartition</code>.
 * 
 */

public class SlidingTemporalInternalPartition {
	
	protected UUID id;	
	protected Map<SegmentationValue,InternalSegmentationPartition> internalPartitions;
	protected Date initiationTime;
	protected SegmentationValue globalSegmentation;

	public SlidingTemporalInternalPartition(SegmentationValue globalSegmentation) {
		
		internalPartitions = new HashMap<SegmentationValue,InternalSegmentationPartition>();
		id = UUID.randomUUID();
		this.initiationTime = Calendar.getInstance().getTime();
		this.globalSegmentation = globalSegmentation;
	}
	
	public Map<SegmentationValue,InternalSegmentationPartition> getInternalPartitions() {		
		return internalPartitions;
	}

	public Collection<Pair<String,Map<String,Object>>> terminate() {
		
		// terminate all internal partitions
		Map<String,Object> globalSegmentValue = new HashMap<String,Object>();
		Map<UUID,String> globalValues = globalSegmentation.getValues();
		ComposedSegmentation globalCS = globalSegmentation.getType();
		Collection<SegmentationContextType> globalSegments = globalCS.getSegments(); 
		for (SegmentationContextType segment: globalSegments) {
			String name = segment.getName();
			globalSegmentValue.put(name,globalValues.get(segment.getId()));
		}
		
		Collection<Pair<String,Map<String,Object>>> terminatedPartitions = new HashSet<Pair<String,Map<String,Object>>>();
		if (!internalPartitions.isEmpty()) {
			for (SegmentationValue key: internalPartitions.keySet()) {
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
			//TODO: inna - global context
			// in case we have a terminator potentially closing temporal window,
			// but no internal partitions, we return a simulated internal partition needed
			// for agent of type "absence" (since it is the only agent that its evaluation
			// should be triggered even if no events arrived; for all other (deferred)
			// agents this work around should not matter
			Map<String,Object> segmentValue = new HashMap<String,Object>();			
			segmentValue.put(MetadataParser.TIME_WINDOW, Calendar.getInstance().getTime().getTime()-initiationTime.getTime());
			segmentValue.putAll(globalSegmentValue);
			terminatedPartitions.add(new Pair<String,Map<String,Object>>(new String("simulated partition"),segmentValue));						
		}
		return terminatedPartitions;		
	}
	
	public UUID getId() {
		return id;
	}

	public Collection<Pair<String,Map<String,Object>>> findInternalPartition(SegmentationValue eSegmentation) {		
				
		// terminate all internal partitions
		Map<String,Object> globalSegmentValue = new HashMap<String,Object>();
		Map<UUID,String> globalValues = globalSegmentation.getValues();
		ComposedSegmentation globalCS = globalSegmentation.getType();
		Collection<SegmentationContextType> globalSegments = globalCS.getSegments(); 
		for (SegmentationContextType segment: globalSegments) {
			String name = segment.getName();
			globalSegmentValue.put(name,globalValues.get(segment.getId()));
		}
								
		Collection<Pair<String,Map<String,Object>>> participating = new HashSet<Pair<String,Map<String,Object>>>();		
		for (SegmentationValue segmentation: internalPartitions.keySet()) {
			if (segmentation.compliesWith(eSegmentation)) {
				InternalSegmentationPartition existingPartition = internalPartitions.get(segmentation);				
				String currentPartition = existingPartition.getPartitionId().toString();
				
				Map<String,Object> segmentValue = new HashMap<String,Object>();
				Map<UUID,String> values = internalPartitions.get(segmentation).getSegmentationValue().getValues();
				ComposedSegmentation cs = internalPartitions.get(segmentation).getSegmentationValue().getType();
				Collection<SegmentationContextType> segments = cs.getSegments(); // add getSegments() to ComposedSegmentation
				for (SegmentationContextType segment: segments) {
					String name = segment.getName();
					segmentValue.put(name,values.get(segment.getId()));
				}
				segmentValue.put(MetadataParser.TIME_WINDOW, Calendar.getInstance().getTime().getTime()-initiationTime.getTime());
				segmentValue.putAll(globalSegmentValue);
				
				participating.add(new Pair<String,Map<String,Object>>(currentPartition,segmentValue));
			}
		}

		return participating;
	}

	public Pair<String,Map<String,Object>> addInternalPartition(SegmentationValue eSegmentation) {
		// terminate all internal partitions
		Map<String,Object> globalSegmentValue = new HashMap<String,Object>();
		Map<UUID,String> globalValues = globalSegmentation.getValues();
		ComposedSegmentation globalCS = globalSegmentation.getType();
		Collection<SegmentationContextType> globalSegments = globalCS.getSegments(); 
		for (SegmentationContextType segment: globalSegments) {
			String name = segment.getName();
			globalSegmentValue.put(name,globalValues.get(segment.getId()));
		}
				
		InternalSegmentationPartition newInternalPartition =
			new InternalSegmentationPartition(eSegmentation);		
		internalPartitions.put(eSegmentation,newInternalPartition);
		
		Map<String,Object> segmentValue = new HashMap<String,Object>();
		Map<UUID,String> values = internalPartitions.get(eSegmentation).getSegmentationValue().getValues();
		ComposedSegmentation cs = internalPartitions.get(eSegmentation).getSegmentationValue().getType();
		Collection<SegmentationContextType> segments = cs.getSegments(); // add getSegments() to ComposedSegmentation
		for (SegmentationContextType segment: segments) {
			String name = segment.getName();
			segmentValue.put(name,values.get(segment.getId()));
		}
		segmentValue.put(MetadataParser.TIME_WINDOW, Calendar.getInstance().getTime().getTime()-initiationTime.getTime());
		segmentValue.putAll(globalSegmentValue);
		
		return new Pair<String,Map<String,Object>>(newInternalPartition.getPartitionId().toString(),segmentValue);
	}
	
}
