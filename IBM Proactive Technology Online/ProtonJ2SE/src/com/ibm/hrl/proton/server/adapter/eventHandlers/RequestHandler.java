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
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Logger;

import com.ibm.hrl.proton.router.IEventRouter;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.utilities.facadesManager.IFacadesManager;


public class RequestHandler implements Runnable {

	public static final Logger logger = Logger.getLogger(RequestHandler.class.getName());
	private IFacadesManager facadesManager;


	Socket socket;
	
	public RequestHandler(Socket socket,IFacadesManager facadesManager2)
	{
		this.socket = socket;
		this.facadesManager = facadesManager2;
	}

	@Override
	public void run() {
		ObjectInputStream in = null;
		IEventInstance eventInstance = null;
		IEventRouter eventRouter = facadesManager.getEventRouter();
		//read the information from the socket , translate to 
		//event instance form and submit to router
		try
		{
			in = new ObjectInputStream((socket.getInputStream()));

			//read as long as there is content in the stream
			while ((eventInstance = (IEventInstance)in.readObject())!= null)
			{
				eventRouter.routeTimedObject(eventInstance);
			}
		}
		catch (IOException e)
		{
			logger.info("run: could not process input request on the Proton server, the input stream is down due to:"+e.getMessage());
			//cannot read input anyhow, orderly shutdown the stream
			//close the stream and exit
			try {
				if (in != null) in.close();
				this.socket.close();				
			} catch (IOException e1) {
				//nothing to do here
			}

		}catch (Exception e)
		//application error - either consumer definitions are wrong, 
		//something wrong with internal routing etc
		//report and continue
		{
			logger.info("run: could not process input on Proton server, reason: "+e.getMessage());	
		}finally{
			try 
			{					
				if (in != null) in.close();
				this.socket.close();				
			} catch (IOException e1) {
				//nothing to do here
			}
		}

		//we are finished with reading the data, close the stream
		try 
		{
			if (in != null) in.close();
			this.socket.close();
		} catch (IOException e1) {
			//nothing to do here
		}
	}

}
