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

import com.ibm.hrl.proton.adapters.files.FileInputAdapter;
import com.ibm.hrl.proton.adapters.files.FileTimedInputAdapter;
import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.adapters.interfaces.IAdapter;
import com.ibm.hrl.proton.adapters.interfaces.IInputAdapter;
import com.ibm.hrl.proton.adapters.rest.client.RESTInputAdapter;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.inout.ConsumerProducerMetadata;
import com.ibm.hrl.proton.metadata.inout.ProducerMetadata;
import com.ibm.hrl.proton.metadata.inout.ProducerMetadata.ProducerType;
import com.ibm.hrl.proton.runtime.metadata.IMetadataFacade;
import com.ibm.hrl.proton.server.adapter.connectors.ServerInputConnector;
import com.ibm.hrl.proton.server.adapter.eventHandlers.RequestHandler;
import com.ibm.hrl.proton.server.executorServices.ExecutorUtils;
import com.ibm.hrl.proton.utilities.facadesManager.IFacadesManager;

public class InputServer extends AbstractServer {



	private static final Logger logger = Logger.getLogger(InputServer.class.getName());

	
	public InputServer(int port, int backlog,IFacadesManager facadesManager,IMetadataFacade metadataFacade,EepFacade eep) {
		super(port, backlog,facadesManager,metadataFacade,eep);			
	}

	@Override
	public List<IAdapter> initializeAdapters() throws AdapterException {	
		List<IAdapter> adapters = new LinkedList<IAdapter>();
		Collection<ProducerMetadata> producers = ConsumerProducerMetadata.getInstance().getProducers();
    	for (ProducerMetadata producerMetadata : producers) {
			//get the producer name, type and properties
    		ProducerType producerType = producerMetadata.getProducerType();
    		
    		IInputAdapter inputAdapter = null;
    		switch (producerType) {
			case FILE:
				//get the input file properties
				//TODO - the parsing of the properties should be done by the specific adapter implementation in static method, return the appropriate configuration object				
				inputAdapter = new FileInputAdapter(producerMetadata, new ServerInputConnector(this.port),metadataFacade.getEventMetadataFacade(),eep);
				break;
			case TIMED:
				inputAdapter = new FileTimedInputAdapter(producerMetadata, new ServerInputConnector(this.port),metadataFacade.getEventMetadataFacade(),eep);
			case DB:
				break;
			case JMS:				
				break;
			case REST:
				inputAdapter = new RESTInputAdapter(producerMetadata, new ServerInputConnector(this.port),metadataFacade.getEventMetadataFacade(),eep);
				break;
			case CUSTOM:
				//fetch the class name and load the implementation class
			default:
				break;
			}
    		
    		
    		adapters.add(inputAdapter);
    	}
    	
    	return adapters;
	}
	
	public void run()
    {
        // Start the server
    	logger.info("Proton server Started, listening on port: " + this.port);
       
       
        while( running )
        {
            try
            {
                // Accept the next connection
                Socket s = serverSocket.accept();
                
                // Log some debugging information
                InetAddress addr = s.getInetAddress();
                logger.fine( "Received a new connection from (" + addr.getHostAddress() + "): " + addr.getHostName());

                // Add the socket to the new RequestQueue
                ExecutorUtils.execute(new RequestHandler(s,facadesManager));
                
               
                
            }
            catch( SocketException se )
            {
                // We are closing the ServerSocket in order to shutdown the server, so if
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
       logger.info( "Proton server has been shut down..." );
    }

}
