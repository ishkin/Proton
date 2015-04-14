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

import com.ibm.hrl.proton.context.management.ContextInitiationNotification;
import com.ibm.hrl.proton.context.management.ContextNotification;
import com.ibm.hrl.proton.metadata.context.ContextAbsoluteTimeInitiator;

/**
 * Represents absolute time initiator instance.
 * timeInitiatorType as reference to metadata and notification as notification that
 * triggered this initiator generation.
 * <code>AbsoluteTimeInitiator</code>.
 * 
 */
public class AbsoluteTimeInitiator extends TemporalContextInitiator {

	protected ContextAbsoluteTimeInitiator timeInitiatorType;
	protected ContextInitiationNotification notification;

	public AbsoluteTimeInitiator(ContextAbsoluteTimeInitiator initiator,
			ContextInitiationNotification notification) {
		super();
		timeInitiatorType = initiator;
		this.notification = notification; 
	}
	
	@Override
	public UUID getId() {
		return UUID.fromString(notification.getContextBoundId());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || this.getClass() != this.getClass()) return false;
		
		AbsoluteTimeInitiator otherInitiator = (AbsoluteTimeInitiator)other;
		if (getId().equals(otherInitiator.getId())) {
			return true;
		}
		return false;
	}
	
	public ContextNotification getNotification() {
		return notification;
	}

}
