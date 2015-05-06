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

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

import com.ibm.hrl.proton.adapters.configuration.IOutputAdapterConfiguration;
import com.ibm.hrl.proton.adapters.connector.IOutputConnector;
import com.ibm.hrl.proton.adapters.connector.ServerConnectionException;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.inout.ConsumerMetadata;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

public abstract class BaseOutputAdapter implements IOutputAdapter {
	public static final Logger logger = Logger.getLogger(BaseOutputAdapter.class.getName());

	
	private static final long RESOURCE_SENDING_DELAY  = 1000;
	
	private boolean running = true;
	private String consumerName;	
	protected IOutputAdapterConfiguration configuration;	
	protected IOutputConnector serverConnector;
	protected Map<IEventType,IExpression> eventsFilter = null;	
	protected EventMetadataFacade eventMetadata;
	
	protected BaseOutputAdapter(ConsumerMetadata consumerMetadata,IOutputConnector serverConnector,EventMetadataFacade eventMetadata) throws AdapterException {		
		this.eventMetadata = eventMetadata;
		this.configuration = createConfiguration(consumerMetadata);		
		this.serverConnector = serverConnector;
		eventsFilter = consumerMetadata.getConsumerEvents();		
		consumerName = consumerMetadata.getConsumerName();
	}
	
	public abstract IOutputAdapterConfiguration createConfiguration(ConsumerMetadata consumerMetadata);
	public abstract boolean sendInstance(IDataObject dataInstance);
	
	protected void initialize() throws AdapterException {
		//connect to the server		
		try {
			serverConnector.establishServerConnection();
		} catch (Exception e) {
			throw new AdapterException();
		} 

	}
	
	protected void shutdown() throws AdapterException {
		running = false;
		// disconnect from the server 
		try {
			serverConnector.disconnectServer();
		} catch (Exception e) {
			throw new AdapterException();
		}

	}
	
	@Override
	public void run() {
		//while true - perform the processing of output
		while (running)
		{
			try {
				Thread.sleep(RESOURCE_SENDING_DELAY);
				processAndOutput();
			} catch (AdapterException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}

	}
	
	public void processAndOutput() throws AdapterException {

		try 
		{
			Serializable dataObject;
			//blocking operation - waits until there is something in the stream
			//returns null only after the socket is closed
			while ((dataObject = serverConnector.receiveObject()) != null)
			{				

				IDataObject instance = (IDataObject)dataObject;
				boolean sendInstance = sendInstance(instance); //can be both event or action
				
				if (sendInstance)
				{
					writeObject(instance);
					
				}

			}

		} catch (ServerConnectionException e) {
			logger.info("processAndOutput: no more reading from Proton server, the output server is down");			
		} finally
		{
			shutdown();
		}

	}

	@Override
	public String toString() {
		return "Output adapter for consumer: "+consumerName;
	}
}
