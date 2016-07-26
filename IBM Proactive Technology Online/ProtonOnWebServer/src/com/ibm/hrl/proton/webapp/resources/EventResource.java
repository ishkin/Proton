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
package com.ibm.hrl.proton.webapp.resources;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.router.IEventRouter;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.webapp.WebFacadesManager;
import com.ibm.hrl.proton.webapp.WebMetadataFacade;
import com.ibm.hrl.proton.webapp.exceptions.ResponseException;

@Resource
@Path("/events")
public class EventResource {


	
	private static final Logger logger = Logger.getLogger(EventResource.class.getName());
	private static final IEventRouter eventRouter = WebFacadesManager.getInstance().getEventRouter();
	
	@POST
	@Consumes("application/json, application/xml, text/plain")
	@Produces("application/json, application/xml, text/plain")
	public Response submitNewEvent(EventInstance event) {
		
		logger.info("starting submitNewEvent");		
		//createAndSendEvents(eventString);
		       		       	
		try {
			eventRouter.routeTimedObject(event);
		}
		catch (Exception e) {
		    String msg = "Could not send event, reason: " + e + ", message: " + e.getMessage();
		    logger.severe(msg);
		    
		    throw new ResponseException(msg);
		    
		}
		logger.info("events sent to proton runtime...");		
		return Response.ok().build();
	}
	
	private void createAndSendEvents(String flag) {
		IEventType eventType= WebMetadataFacade.getInstance().getEventMetadataFacade().getEventType("StockBuy");		
		Map<String,Object> attrValues = new HashMap<String,Object>();
		
       	attrValues.put("id","111");
		attrValues.put("amount","100");
		attrValues.put("price","5000");
		
       	IEventInstance event = new EventInstance(eventType,attrValues);
       	event.setDetectionTime(System.currentTimeMillis());
       	
		IEventRouter eventRouter = WebFacadesManager.getInstance().getEventRouter();

		try {
			eventRouter.routeTimedObject(event);
		}
		catch (Exception e) {
		    String msg = "Could not send event, reason: " + e + ", message: " + e.getMessage();
		    logger.severe(msg);
		    
		    throw new ResponseException(msg);
		}
		
		//-------------------------------------------------------------------------------------
		
		eventType= WebMetadataFacade.getInstance().getEventMetadataFacade().getEventType("StockSell");		
		attrValues.clear();
		
       	attrValues.put("id","111");
		attrValues.put("amount","100");
		attrValues.put("price","6000");
		
       	event = new EventInstance(eventType,attrValues);
       	event.setDetectionTime(System.currentTimeMillis());
		      	
		try {
			eventRouter.routeTimedObject(event);
		}
		catch (Exception e) {
		    String msg = "Could not send event, reason: " + e + ", message: " + e.getMessage();
		    logger.severe(msg);

		    throw new ResponseException(msg);			
		}
	}
	
}
