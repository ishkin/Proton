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
package com.ibm.hrl.proton.server.adapter.connectors;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Logger;

import com.ibm.hrl.proton.adapters.connector.IOutputConnector;
import com.ibm.hrl.proton.adapters.connector.ServerConnectionException;

public class ServerOutputConnector implements IOutputConnector {

	public final static Logger logger= Logger.getLogger(ServerOutputConnector.class.getName());
	
	
	private int connectionPort;
	private Socket socket;
	protected ObjectInputStream ois;
	
	
	public ServerOutputConnector(int connectionPort) {
		super();
		this.connectionPort = connectionPort;
	}
	
	@Override
	public void establishServerConnection() throws ServerConnectionException {
		try {
			socket = new Socket("localhost",connectionPort);  			 
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			throw new ServerConnectionException("Error connecting output adapter to Proton server, reason:" +e.getMessage());
		} 

	}

	@Override
	public void disconnectServer() throws ServerConnectionException {
		try 
		{			
				ois.close();
				socket.close();
						
		} catch (Exception e) {
			throw new ServerConnectionException("Error disconnecting adapter to Proton server, reason:" +e.getMessage());
		}finally
		{
			if (ois != null)
			{
				try{
					ois.close();
				}catch(Exception e)
				{
					//nothing to do here
				}
			}
			
			if (socket != null)
			{
				try{
					socket.close();
				}catch(Exception e)
				{
					//nothing to do here
				}
			}
		}

	}

	@Override
	public Serializable receiveObject() throws ServerConnectionException {
		try {			
				if (!socket.isClosed())
					return (Serializable)ois.readObject();
				return null;			
			 //TODO: what happens if no object - waits or returns?
		} catch (IOException e) {
			logger.info("No more reading from Proton server, reason:" +e.getMessage());
			disconnectServer();
			return null;
		} catch (ClassNotFoundException e) {
			logger.severe("Error in adapter while writing to Proton server, reason:" +e.getMessage());
			return null;
		}      
	}

}
