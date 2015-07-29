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
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import com.ibm.hrl.proton.adapters.connector.IInputConnector;
import com.ibm.hrl.proton.adapters.connector.ServerConnectionException;


public class ServerInputConnector implements IInputConnector {
	public static final Logger logger = Logger.getLogger(ServerInputConnector.class.getName()); 

	
	
	private int connectionPort;
	private Socket socket;
	protected ObjectOutputStream oos;
	
	public ServerInputConnector(int connectionPort) {
		super();
		this.connectionPort = connectionPort;
	}

	
	@Override
	public void establishServerConnection() throws ServerConnectionException {
		try {
			socket = new Socket("localhost",connectionPort);  			 
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			throw new ServerConnectionException("Error connecting adapter to Proton server, reason:" +e.getMessage());
		} 

	}

	@Override
	public void disconnectServer() throws ServerConnectionException {
		try {
			oos.close();
			socket.close();
		} catch (Exception e) {
			throw new ServerConnectionException("Error disconnecting adapter from Proton server, reason:" +e.getMessage());
		}finally{
			if (oos != null){
				try{
					oos.close();
				}catch(Exception e){
					//nothing to do here
				}
			}
			
			if (socket != null){
				try{
					socket.close();
				}catch(Exception e){
					//nothing to do here
				}
			}
		}

	}

	@Override
	public void sendObject(Object serializable) throws ServerConnectionException {
		try {
			oos.writeObject(serializable);
		} catch (IOException e) {
			logger.info("Error in adapter while writing to Proton server, reason:" +e.getMessage());
			disconnectServer();
		} 
		
	}

}
