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

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.ibm.hrl.proton.adapters.configuration.IInputAdapterConfiguration;
import com.ibm.hrl.proton.adapters.connector.IInputConnector;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.inout.ProducerMetadata;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

public abstract class AbstractInputTimedAdapter implements IInputAdapter {

	public static final Logger logger = Logger.getLogger(AbstractInputAdapter.class.getName());
	

	protected boolean running = true;
	private String producerName;	
	protected IInputAdapterConfiguration configuration;	
	protected IInputConnector serverConnector;
	protected Map<IEventType,IExpression> eventsFilter = null;
	private long prevTimestamp;	
	protected EventMetadataFacade eventMetadata;
	
	public AbstractInputTimedAdapter(ProducerMetadata producerMetadata,IInputConnector serverConnector,EventMetadataFacade eventMetadata) throws AdapterException {		
		this.eventMetadata = eventMetadata;
		this.configuration = createConfiguration(producerMetadata);		
		this.serverConnector = serverConnector;
		eventsFilter = producerMetadata.getEventsFilter();
		producerName = producerMetadata.getProducerName();
		
	}
	
		
	public abstract IInputAdapterConfiguration createConfiguration(ProducerMetadata producerMetadata);
	
	public IInputAdapterConfiguration getConfiguration() {
		return configuration;
	}
	
	/**
	 * Check if the instance should be filtered out
	 * @param eventInstance
	 * @return
	 */
	public boolean filterInstance(IEventInstance eventInstance)
	{
		boolean result = false; //initially do not filter out
		if (eventsFilter == null) return result;
		
		//check if the event should be filtered out, and if it has condition defined
		IEventType eventType = eventInstance.getEventType();
		if (eventsFilter.containsKey(eventType))
		{
			IExpression condition = eventsFilter.get(eventType);
			if (condition == null) return true;  //if the event type is mentioned in filter out list and has no condition - filter out the instance
			
			//evaluate the expression, only if it is true the event should be filtered out
			result = (Boolean)condition.evaluate(eventInstance);			
									
		}
		
		return result;
	}
	
	protected void initialize() throws AdapterException
	{
		//connect to the server		
		try {
			serverConnector.establishServerConnection();
		} catch (Exception e) {
			throw new AdapterException();
		} 
	}

	
	public void shutdown() throws AdapterException {
		running = false;
		// disconnect from the server 
		try {
			serverConnector.disconnectServer();
		} catch (Exception e) {
			throw new AdapterException();
		}

	}

	public void processInput() throws AdapterException {
		try {
				IEventInstance eventInstance = null;
				while ((eventInstance = readData()) != null)
				{					
					if (!filterInstance(eventInstance))
					{
						long timestamp = eventInstance.getOccurenceTime();
						long delayMillis = prevTimestamp > 0 ? timestamp - prevTimestamp : 0;
						if (delayMillis >= 0) {
							prevTimestamp = timestamp;
						} else {
							delayMillis = 0;
						}
						Thread.sleep(delayMillis);	
						serverConnector.sendObject(eventInstance);
					}					
				}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new AdapterException(e.getMessage());
		}
	}
	
	@Override
	public void run() {
		//while true - perform the processing of input
				while (running)
				{
					try {						
						processInput();
											
					} catch (AdapterException e) {
						// TODO Auto-generated catch block
						if (running)
						logger.severe("Could not read from input, reason:"+e.getMessage());
					} 
				}

	}

	@Override
	public String toString() {
		return "Input adapter for producer: "+ producerName;
	}
	
	@Override
	public List<IEventInstance> readBatchedData() throws AdapterException {
		throw new AdapterException("Illegal operation for this type of adapter");
	}

	

}
