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
 * Used to maintain context related info, carried context notification, generated by timer.
 * We keep context and agent name, notification type (either initiator or terminator) and the id
 * of initiator/terminator type this notification refers to.
 * <code>AdditionalInformation</code>.
 * 
 */
public class AdditionalInformation {

	public enum NotificationTypeEnum {
		INITIATOR,
		TERMINATOR
	}
	
	protected UUID contextBoundId;
	protected NotificationTypeEnum notificationType;
	protected String contextName;
	protected String agentName;

	
	public AdditionalInformation(String contextName, String agentName, UUID contextBoundId,
			NotificationTypeEnum notificationType) {
		this.contextBoundId = contextBoundId;
		this.notificationType = notificationType;
		this.contextName = contextName;
		this.agentName = agentName;		 
	}
	
	public UUID getContextBoundId() {
		return contextBoundId;
	}

	public NotificationTypeEnum getNotificationType() {
		return notificationType;
	}
	
	public String getAgentName(){
		return agentName;
	}

}
