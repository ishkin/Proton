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
package com.ibm.hrl.proton.metadata;

import java.io.Serializable;
import java.util.Collection;

import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.metadata.event.IEventType;

public class EventProcessingNetwork implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected Collection<IEventProcessingAgent> epaTypes;
	protected Collection<IEventType> eventTypes;
	protected Collection<IContextType> contexts;
	
	public Collection<IEventProcessingAgent> getEpaTypes() {
		return epaTypes;
	}
	public void setEpaTypes(Collection<IEventProcessingAgent> epaTypes) {
		this.epaTypes = epaTypes;
	}
	public Collection<IEventType> getEventTypes() {
		return eventTypes;
	}
	public void setEventTypes(Collection<IEventType> eventTypes) {
		this.eventTypes = eventTypes;
	}
	public Collection<IContextType> getContexts() {
		return contexts;
	}
	public void setContexts(Collection<IContextType> contexts) {
		this.contexts = contexts;
	}
	
	
}
