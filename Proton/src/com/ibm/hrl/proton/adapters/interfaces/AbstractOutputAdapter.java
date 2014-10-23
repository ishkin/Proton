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
package com.ibm.hrl.proton.adapters.interfaces;

import java.util.logging.Logger;

import com.ibm.hrl.proton.adapters.configuration.IOutputAdapterConfiguration;
import com.ibm.hrl.proton.adapters.connector.IOutputConnector;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.inout.ConsumerMetadata;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

public abstract class AbstractOutputAdapter extends BaseOutputAdapter {

	public static final Logger logger = Logger.getLogger(AbstractOutputAdapter.class.getName());
	
			

	public AbstractOutputAdapter(ConsumerMetadata consumerMetadata,IOutputConnector serverConnector) throws AdapterException {
		super(consumerMetadata,serverConnector);

	}

	public abstract IOutputAdapterConfiguration createConfiguration(ConsumerMetadata consumerMetadata);

	
	

	/**
	 * Check if the instance should be filtered out
	 * @param eventInstance
	 * @return
	 */
	public boolean sendInstance(IDataObject dataInstance)
	{
		boolean result = false; //initially do not filter out
		IExpression condition = null;

		//it is an event , check in events list
		if (eventsFilter == null) return result;
		IEventInstance eventInstance = (IEventInstance)dataInstance;
		IEventType eventType = eventInstance.getEventType();
		if (eventsFilter.containsKey(eventType))
		{				
			condition = eventsFilter.get(eventType);				 
		}else
		{
			return result;
		}

		
		
		if (condition == null) return true;
		result = (Boolean) condition.evaluate(dataInstance);
		
		return result;

	}

	


	
	


	
	
}
