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

import java.util.UUID;

/**
 * Represents internal partition (refers to local segmentation) inside a temporal partition.
 * The partition is "virtual" - it only maitains its id, not participating event list.
 * <code>InternalSegmentationPartition</code>.
 * 
 */
public class InternalSegmentationPartition implements ISegmentationPartition {
	
	protected UUID id;
	
	// reference to metadata context definition
	protected SegmentationValue segmentationValue;	
	//protected Collection<IEventInstance> events;

	public InternalSegmentationPartition(SegmentationValue segmentationValue) {
		this.segmentationValue = segmentationValue;
		//events = new HashSet<IEventInstance>();
		id = UUID.randomUUID();
	}
	
	public UUID getPartitionId() {
		return id;
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
