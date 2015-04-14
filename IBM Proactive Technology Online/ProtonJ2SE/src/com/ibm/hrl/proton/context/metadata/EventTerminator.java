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
package com.ibm.hrl.proton.context.metadata;

import java.util.UUID;

import com.ibm.hrl.proton.context.management.SegmentationValue;
import com.ibm.hrl.proton.metadata.context.ContextEventTerminator;
import com.ibm.hrl.proton.metadata.context.enums.ContextTerminationTypeEnum;
import com.ibm.hrl.proton.metadata.context.enums.ContextTerminatorPolicyEnum;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

/**
 * Represents event terminator instance.
 * eventTerminatorType as reference to metadata, segment is this event segmentation value with
 * regards to a context it terminates, and event is an instance.
 * <code>EventTerminator</code>.
 * 
 */
public class EventTerminator implements ITemporalContextBound {
	
	protected ContextEventTerminator eventTerminatorType;
	protected SegmentationValue segment;
	protected IEventInstance event;

	
	public EventTerminator(ContextEventTerminator eventTerminatorType,
			IEventInstance event, SegmentationValue segment) {
		this.eventTerminatorType = eventTerminatorType;
		this.segment = segment;
		this.event = event;
	}
	
	public ContextTerminationTypeEnum getTerminationType() {
		return eventTerminatorType.getTerminationType(); 
	}
	
	public ContextTerminatorPolicyEnum getTerminationPolicy() {
		return eventTerminatorType.getTerminationPolicy();
	}
	
	public IEventInstance getEventInstance() {
		return event;
	}

	public IEventType getEventType() {
		return event.getEventType();
	}

	public SegmentationValue getSegmentationValue() {
		return segment;
	}

	@Override
	public UUID getId() {
		return event.getEventId();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || this.getClass() != this.getClass()) return false;
		
		EventTerminator otherTerminator = (EventTerminator)other;
		if (event.getEventId() != otherTerminator.getEventInstance().getEventId()) {
			return false;
		}
		
		// events id is equal - it should be the same initiator
		// make sure they have the same segmentation (assertion check)
		assert (segment.equals(otherTerminator.getSegmentationValue()));		

		return true;
	}	

}
