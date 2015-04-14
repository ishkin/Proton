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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;

import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.adapters.interfaces.IAdapter;
import com.ibm.hrl.proton.server.workManager.WorkManagerFacade;
import com.ibm.hrl.proton.utilities.asynchronousWork.IWorkManager;

public abstract class AbstractServer extends Thread
{
	
	
	private static final Logger logger = Logger.getLogger(AbstractServer.class.getName());
	private List<IAdapter> adapters;
	protected ServerSocket serverSocket;
	protected boolean running = true;
	protected int port;
	private int backlog;
	private IWorkManager workManagerFacade;
	
	protected AbstractServer(int port, int backlog)
	{
		this.port = port;
		this.backlog = backlog;
		workManagerFacade = WorkManagerFacade.getInstance();
	}
	
	public void startServer() throws ProtonServerException
    {
        try
        {
            // Create server socket
            ServerSocketFactory ssf = ServerSocketFactory.getDefault();
            serverSocket = ssf.createServerSocket(port, backlog );

            // Start the thread
            this.start();
            this.adapters = initializeAdapters();
            //start the adapters
            startAdapters();
           
        }
        catch( Exception e )
        {
           throw new ProtonServerException(e.getMessage());
        }
    }	

	public abstract List<IAdapter> initializeAdapters() throws AdapterException;
	
	private void startAdapters() throws AdapterException{
    	//iterate over all the adapters ,initialize and then start them
		for (IAdapter adapter : this.adapters) 
		{
			try 
			{			
				adapter.initializeAdapter();
				Runnable workItem = workManagerFacade.createWork(adapter);
				workManagerFacade.runWork(workItem);
				//ExecutorUtils.execute(adapter);    		
			}catch (Exception e) 
			{
				logger.warning("initializeAdapters: failed to initialize adapter "+adapter+", reason: "+e.getMessage());
				continue;
			}
		}
    	
    }
	
	 public void stopServer() throws ProtonServerException
	    {
	        
	            running = false;
	          //shutdown adapters
	            try{
	            	if (this.adapters != null)
	            	{
	            		for (IAdapter adapter : this.adapters) 
		            	{
		            		try{
		            			adapter.shutdownAdapter();
		            		}catch(AdapterException e)
		    		        {
		    	    			logger.warning("stopServer: could not shutdown adapter, reason: "+e.getMessage());
		    		        	continue;		        
		    		        }
		    		}	
	            }	            	            
	        }finally
	        {
	        	try {
	        		if (this.serverSocket != null)
	        			this.serverSocket.close();
				} catch (IOException e) {
					logger.severe("stopServer: could not shutdown socket server, reason: "+e.getMessage());
				}
	        }	
	     }
	    
}
