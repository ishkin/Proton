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

import com.ibm.hrl.proton.metadata.context.enums.ContextTerminationTypeEnum;
import com.ibm.hrl.proton.metadata.context.enums.ContextTerminatorPolicyEnum;
import com.ibm.hrl.proton.metadata.event.EventType;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;

public class ContextRelativeTimeTerminator extends ContextTerminator {

	final static String eventName = "RelativeTimeTerminator";

	protected long relativeMiliSeconds;	
	protected IEventType event;
	
	public ContextRelativeTimeTerminator(long relativeMiliSeconds,
			ContextTerminatorPolicyEnum terminatorPolicy,
			ContextTerminationTypeEnum terminationType) {

		super(terminatorPolicy,terminationType);
		this.relativeMiliSeconds = relativeMiliSeconds;
		event = new EventType(eventName,new ArrayList<TypeAttribute>());
	}
	 @ Override
	public IEventType getTerminatorType() {
		return event;
	}
	
	 
	public long getRelativeTerminationTime() {
		return relativeMiliSeconds;
	}
}
