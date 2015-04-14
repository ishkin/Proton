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
package com.ibm.hrl.proton.metadata.context;

import java.util.ArrayList;
import java.util.Date;

import com.ibm.hrl.proton.metadata.context.enums.ContextInitiatorPolicyEnum;
import com.ibm.hrl.proton.metadata.event.EventType;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;

public class ContextAbsoluteTimeInitiator extends ContextInitiator {

	private static final long serialVersionUID = 1L;
	final static String eventName = "AbsoluteTimeInitiator"; 
	
	protected Date initiationTime;

	protected boolean isRepeating;
	protected long repeatingIntervalMiliseconds;
	protected IEventType event;	

	public ContextAbsoluteTimeInitiator() {
		super();
	}

	public ContextAbsoluteTimeInitiator(Date initiationTime, boolean isRepeating,
			long repeatingInterval, ContextInitiatorPolicyEnum initiatorPolicy) {
		
		super();
		this.initiationTime = initiationTime;
		this.repeatingIntervalMiliseconds = repeatingInterval;
		this.isRepeating = isRepeating;
		this.initiatorPolicy = initiatorPolicy;
		
		event = new EventType(eventName,new ArrayList<TypeAttribute>());
	}
	
	@Override
	public IEventType getInitiatorType() {
		return event;
	}
	
	public Date getInitiationTime() {
		return initiationTime;		
	}
		
	public long getRepeatingInterval() {
		return repeatingIntervalMiliseconds;
	}
	
	public boolean isRepeating() {
		return isRepeating;
	}

}
