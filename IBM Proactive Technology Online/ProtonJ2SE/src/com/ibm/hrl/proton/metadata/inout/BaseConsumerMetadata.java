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

import java.util.HashMap;
import java.util.Map;

import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

public abstract class BaseConsumerMetadata {
	public enum ConsumerType {FILE,DB,JMS,REST, CUSTOM};
	
	private String consumerName;
	private ConsumerType consumerType;
	private Map<String,Object> consumerProperties;
	private Map<IEventType,IExpression> consumerEvents;
	
	
	public BaseConsumerMetadata(String consumerName, ConsumerType consumerType, Map<String,Object> consumerProperties,Map<IEventType,IExpression> consumerEvents)
	{
		this.consumerName = consumerName;
		this.consumerType = consumerType;
		this.consumerProperties = consumerProperties;
		this.consumerEvents = consumerEvents;		
		
	}
		

	public Map<String, Object> getConsumerProperties() {
		return consumerProperties;
	}
	
	public String getConsumerName() {
		return consumerName;
	}

	public ConsumerType getConsumerType() {
		return consumerType;
	}

	public Object getConsumerProperty(String propertyName) {
		return consumerProperties.get(propertyName);
	}

	public Map<IEventType,IExpression> getConsumerEvents() {
		if (consumerEvents != null) return consumerEvents;
		return new HashMap<IEventType,IExpression>();
	}
	
	
}
