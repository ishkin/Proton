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
import com.ibm.hrl.proton.metadata.context.ContextEventInitiator;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

/**
 * Represents event initiator instance.
 * eventInitiatorType as reference to metadata, segment is this event segmentation value with
 * regards to a context it initializes, and event is an instance.
 * <code>EventInitiator</code>.
 * 
 */
public class EventInitiator extends TemporalContextInitiator {

	protected ContextEventInitiator eventInitiatorType;
	protected SegmentationValue segment;
	protected IEventInstance event;

	
	public EventInitiator(ContextEventInitiator eventInitiatorType,
			IEventInstance event, SegmentationValue segment) {
		super();
		this.eventInitiatorType = eventInitiatorType;
		this.segment = segment;
		this.event = event;
	}

	@Override
	public UUID getId() {
		return event.getEventId();
	}
	
	public IEventType getEventType() {
		return event.getEventType();
	}
	
	//@Override
	public SegmentationValue getSegmentationValue() {
		return segment;
	}
	
	public IEventInstance getEventInstance() {
		return event;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || this.getClass() != this.getClass()) return false;
		
		EventInitiator otherInitiator = (EventInitiator)other;
		if (getId() != otherInitiator.getId()) {
			return false;
		}
		
		return true;
	}	

}
