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
package com.ibm.hrl.proton.server.adapter;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.ibm.hrl.proton.adapters.email.GmailOutputAdapter;
import com.ibm.hrl.proton.adapters.files.FileOutputAdapter;
import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.adapters.interfaces.IAdapter;
import com.ibm.hrl.proton.adapters.interfaces.IOutputAdapter;
import com.ibm.hrl.proton.adapters.rest.client.RESTOutputAdapter;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.inout.BaseConsumerMetadata.ConsumerType;
import com.ibm.hrl.proton.metadata.inout.ConsumerMetadata;
import com.ibm.hrl.proton.router.DataSenderException;
import com.ibm.hrl.proton.runtime.metadata.IMetadataFacade;
import com.ibm.hrl.proton.server.adapter.connectors.ServerOutputConnector;
import com.ibm.hrl.proton.server.adapter.eventHandlers.DataSender;
import com.ibm.hrl.proton.server.adapter.eventHandlers.StandaloneDataSender;
import com.ibm.hrl.proton.server.executorServices.ExecutorUtils;
import com.ibm.hrl.proton.utilities.facadesManager.IFacadesManager;

public class OutputServer extends AbstractServer
{

	
	private static final Logger logger = Logger.getLogger(OutputServer.class.getName());	  
	
    
    public OutputServer(int port, int backlog,IFacadesManager facadesManager,IMetadataFacade metadataFacade,EepFacade eep) throws ProtonServerException
    {
    	super(port, backlog,facadesManager,metadataFacade,eep);    	
    	
    }
    
    @Override
    public void run() {
    	 // Start the server
    	logger.info("Proton output server started, listening on output port: " + this.port);
       
       
        while( running )
        {
            try
            {
                // Accept the next connection from consumer adapter
                Socket s = serverSocket.accept();

                // Log some debugging information
                InetAddress addr = s.getInetAddress();
                logger.fine( "Received a new connection from (" + addr.getHostAddress() + "): " + addr.getHostName());

                // Add the socket to the new RequestQueue
                DataSender eventSender = new DataSender(s);
                ((StandaloneDataSender)facadesManager.getDataSender()).addDataSender(eventSender);
                ExecutorUtils.execute(eventSender);
             
            }
            catch( SocketException se )
            {
                // If we are closing the ServerSocket in order to shutdown the server, so if
                // we are not currently running then ignore the exception.
                if( this.running )
                {
                    se.printStackTrace();                    
                }
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }
       logger.info( "Proton server has been shut down ..." );
    }

	@Override
	public List<IAdapter> initializeAdapters() throws AdapterException {
		List<IAdapter> adapters = new LinkedList<IAdapter>();
		Collection<ConsumerMetadata> consumers = metadataFacade.getConsumerProducerMetadata().getConsumers();
    	for (ConsumerMetadata consumerMetadata : consumers) {
			//get the producer name, type and properties
    		ConsumerType consumerType = consumerMetadata.getConsumerType();
    		
    		IOutputAdapter outputAdapter = null;
    		switch (consumerType) {
			case FILE:
			case TIMED:
				//for consumers, file and timed adapters are the same
				//get the input file properties
				//TODO - the parsing of the properties should be done by the specific adapter implementation in static method, return the appropriate configuration object				
				outputAdapter = new FileOutputAdapter(consumerMetadata, new ServerOutputConnector(this.port),metadataFacade.getEventMetadataFacade(),eep);
				break;
			case MAIL:
				outputAdapter = new GmailOutputAdapter(consumerMetadata, new ServerOutputConnector(this.port), metadataFacade.getEventMetadataFacade(),eep);
				break;
			case DB:
				break;
			
			case REST:
				outputAdapter = new RESTOutputAdapter(consumerMetadata, new ServerOutputConnector(this.port),metadataFacade.getEventMetadataFacade(),eep);
				break;
			case CUSTOM:
				//fetch the class name and load the implementation class
			default:
				break;
			}
    		
    		
    		adapters.add(outputAdapter);
    	}
    	
    	return adapters;
	}
	
	
	public void stopServer() throws ProtonServerException
	{
		super.stopServer();
		try {
			facadesManager.getDataSender().shutdownDataSender();
		} catch (DataSenderException e) {
			logger.severe("Error shutting down data senders, reason: "+e.getMessage());
			throw new ProtonServerException("Error shutting down data senders, reason: "+e.getMessage());
		}
	}
}
