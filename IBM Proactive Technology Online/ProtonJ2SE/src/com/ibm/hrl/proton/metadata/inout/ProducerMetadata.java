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
package com.ibm.hrl.proton.metadata.inout;

import java.io.Serializable;
import java.util.Map;

import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

public class ProducerMetadata implements Serializable{
	public enum ProducerType {FILE,TIMED,DB,JMS,REST,CUSTOM};
	
	private ProducerType producerType;
	private String producerName;	
	private Map<String,Object> producerProperties;
	

	private Map<IEventType,IExpression> eventsFilter;

	
	public ProducerMetadata(String producerName,ProducerType producerType, 
			Map<String, Object> producerProperties,Map<IEventType,IExpression> events) {
		super();
		this.producerType = producerType;
		this.producerName = producerName;		
		this.producerProperties = producerProperties;
		this.eventsFilter = events;
	}
	
	
	
	public Map<String, Object> getProducerProperties() {
		return producerProperties;
	}

	public ProducerType getProducerType() {
		return producerType;
	}

	public String getProducerName() {
		return producerName;
	}

	public Object getProducerProperty(String propertyName) {
		return producerProperties.get(propertyName);
	}
	
	public Map<IEventType, IExpression> getEventsFilter() {
		return eventsFilter;
	}

}
