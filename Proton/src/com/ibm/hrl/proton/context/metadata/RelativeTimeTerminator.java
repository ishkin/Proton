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

import com.ibm.hrl.proton.context.management.ContextNotification;
import com.ibm.hrl.proton.context.management.ContextTerminationNotification;
import com.ibm.hrl.proton.metadata.context.ContextRelativeTimeTerminator;
import com.ibm.hrl.proton.metadata.context.enums.ContextTerminationTypeEnum;
import com.ibm.hrl.proton.metadata.context.enums.ContextTerminatorPolicyEnum;

/**
 * Represents relative time terminator instance.
 * timeTerminatorType as reference to metadata and notification as notification that
 * triggered this terminator generation.
 * <code>RelativeTimeTerminator</code>.
 * 
 */
public class RelativeTimeTerminator implements ITemporalContextBound {

	protected ContextRelativeTimeTerminator timeTerminatorType;
	protected ContextTerminationNotification notification;

	public RelativeTimeTerminator(ContextRelativeTimeTerminator terminator,
			ContextTerminationNotification notification) {
		timeTerminatorType = terminator;
		this.notification = notification; 
	}

	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.context.metadata.ITemporalContextBound#getId()
	 */
	@Override
	public UUID getId() {
		return UUID.fromString(notification.getContextBoundId());
	}
	
	public ContextTerminationTypeEnum getTerminationType() {
		return timeTerminatorType.getTerminationType(); 
	}	

	public ContextTerminatorPolicyEnum getTerminationPolicy() {
		return timeTerminatorType.getTerminationPolicy();
	}

	public ContextNotification getNotification() {
		return notification;
	}
}
