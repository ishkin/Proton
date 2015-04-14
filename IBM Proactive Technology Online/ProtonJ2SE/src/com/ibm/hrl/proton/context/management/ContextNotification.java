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

import com.ibm.hrl.proton.runtime.context.notifications.IContextNotification;

/**
 * <code>ContextNotification</code>.
 * 
 */
public class ContextNotification implements IContextNotification {
	
	protected String contextName;
	protected long occurrenceTime;
	protected long detectionTime;
	protected String agentName;
	
	protected String contextBoundId;
	
	public ContextNotification(String name, long occurrence, long detection, String boundId,String agentName) {
		contextName = 		name;
		occurrenceTime =	occurrence;
		detectionTime = 	detection;		
		contextBoundId = 	boundId;
		this.agentName 	 =      agentName;
	}
	
		
	@Override
	public String getContextName() {
		return contextName; 	
	}

	@Override
	public String getContextBoundId() {
		return contextBoundId; 	
	}	
	
	@Override
	public long getDetectionTime() {
		return detectionTime;
	}

	@Override
	public long getOccurenceTime() {
		return occurrenceTime;
	}

	@Override
	public String getAgentName() {
		return agentName;
	}
	
	

}
