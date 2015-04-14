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
package com.ibm.hrl.proton.server.adapter.eventHandlers;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.router.DataSenderException;
import com.ibm.hrl.proton.router.IDataSender;


public class DataSender implements Runnable, IDataSender{


	
	private Socket socket;
	private BlockingQueue<IDataObject> queue;
	private ObjectOutputStream out ;
	
	
	public DataSender(Socket socket) throws DataSenderException
	{
		this.socket = socket;
		this.queue = new LinkedBlockingQueue<IDataObject>(); 
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			throw new DataSenderException("Could not create handler for consumer thread, reason: "+e.getMessage());
		}
	}
	
	/*
	 * check if there is anything on the queue, if so send it over the socket to the consumer(non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */		
	@Override
	public void run() {
		try {
						
			while (true)
			{			
				IDataObject instance = queue.take();				
				out.writeObject(instance); //since it is a blocking queue the method will block until there is an item in the queue				
			}
		}catch (Exception e)
		{
			
			//cannot write output anyhow, orderly shutdown the stream
			//close the stream and exit
			try {
				
				if (out != null) out.close();
				this.queue.clear();
				this.socket.close();
				
			} catch (IOException e1) {
				//nothing to do here
			}
		}finally
		{
			try
			{
				
				if (out != null) out.close();
				this.queue.clear();
				this.socket.close();
				
			}
				catch(Exception e){
				
			}
		}
		
	}

	
	/**
	 * Called by event router, adds the event which should be delivered to the event sender queue
	 */
	@Override
	public void sendDataToConsumer(IDataObject instance)
			throws DataSenderException {
				
		queue.offer(instance);
		
	}

	@Override
	public void shutdownDataSender() throws DataSenderException {
		try {
			if (out != null)
			{
				out.close();
				socket.close();
			}
		} catch (IOException e) {
			throw new DataSenderException("Could not shutdown event sender, reason: "+ e.getMessage());
		}finally
		{
			try{
				if (out != null)
				{
					out.close();
					socket.close();
				}
			}catch(Exception e){};
			
		}
		
	}

	
}
