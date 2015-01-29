/*******************************************************************************
 * Copyright 2015 IBM
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
package com.ibm.hrl.proton.routing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import backtype.storm.tuple.Tuple;

import com.ibm.hrl.proton.agentQueues.queuesManagement.AgentQueuesManager;
import com.ibm.hrl.proton.context.facade.ContextServiceFacade;
import com.ibm.hrl.proton.epaManager.EPAManagerFacade;
import com.ibm.hrl.proton.eventHandler.EventHandler;
import com.ibm.hrl.proton.eventHandler.IEventHandler;
import com.ibm.hrl.proton.expression.facade.EEPException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.metadata.parser.ParsingException;
import com.ibm.hrl.proton.metadata.parser.ProtonParseException;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.RoutingMetadataFacade;
import com.ibm.hrl.proton.server.timerService.TimerServiceFacade;
import com.ibm.hrl.proton.server.workManager.WorkManagerFacade;
import com.ibm.hrl.proton.utilities.asynchronousWork.IWorkManager;

public class MetadataFacade {
	

	private static MetadataFacade instance = null;
	public static final String ATTRIBUTES_FIELD = "attributes";
	public static final String AGENT_NAME_FIELD = "agentName";
	public static final String CONTEXT_NAME_FIELD = "contextName";
	public static final String CONTEXT_PARTITION_FIELD = "contextPartition";	
	public static final String CONTEXT_SEGMENTATION_VALUES = "segmentationValues";	
	
	public static final String EVENT_STREAM = "events";
	public static final String CONSUMER_EVENTS_STREAM = "consumerEvents";
	public static final String TERMINATOR_EVENT_NAME = "contextTerminatorEventStream";
	
	private static final Logger logger = Logger.getLogger(MetadataFacade.class.getName());


		

	public MetadataFacade(String propertiesFileNamePath) throws ParsingException, EEPException {
		Collection<ProtonParseException> exceptions = initializeMetadata(propertiesFileNamePath, EepFacade.getInstance());
    	logger.info("init: done initializing metadata, returned the following exceptions: ");
    	for (ProtonParseException protonParseException : exceptions) {
			logger.info(protonParseException.toString());
		}    	 

	    
	          
    	        
	}


		
	public static synchronized void initializeMetadataFacade(String jsonSerialization) 
	{
		try{
			if (null == instance){
				logger.fine("initializeMetadataFacade: parsing json file and initializing metadata singletones...");
				instance = new MetadataFacade(jsonSerialization);
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.severe("Could not initialize metadata facade, reason: "+e.getMessage());
			throw new RuntimeException(e);
		}
		
	}
	
	public static synchronized MetadataFacade getInstance()
	{
		return instance;
	}
	
	public IEventInstance createEventFromFlatTuple(Tuple input)
	{
		logger.fine("<==========================inside Proton createEventFromTuple method=========>");
		List<String> tupleFields = input.getFields().toList();
		//assuming each tuple has a "name" field		
		String eventTypeName = (String)input.getValueByField(EventHeader.NAME_ATTRIBUTE);
		logger.fine("convertTuple:getting event name: "+eventTypeName);
		IEventType eventType = EventMetadataFacade.getInstance().getEventType(eventTypeName);
		Map<String,Object> attributes = new HashMap<String,Object>();		
		
		Collection<TypeAttribute> typeAttributes = eventType.getTypeAttributes();
		logger.fine("convertTuple: iterating over attributes of event type" );
		for (Iterator iterator = typeAttributes.iterator(); iterator.hasNext();) {
			TypeAttribute typeAttribute = (TypeAttribute) iterator.next();
			String attributeName = typeAttribute.getName();
			if (input.contains(attributeName)){
				Object fieldValue = input.getValueByField(attributeName);
				logger.fine("convertTuple: got value for attribute " +attributeName+" ,value: "+fieldValue );
				attributes.put(attributeName,fieldValue);
			}
			
		}
		//TODO add additional fields here as in adapters
		IEventInstance eventInstance = new EventInstance(eventType, attributes);
		eventInstance.setDetectionTime(Calendar.getInstance().getTimeInMillis());
		logger.fine("convertTuple: built event instance"+ eventInstance );
		return eventInstance;
	}
	
	public IEventInstance createEventFromTuple(Tuple input)
	{
		logger.fine("<==========================inside Proton createEventFromTuple method=========>");
		List<String> tupleFields = input.getFields().toList();
		//assuming each tuple has a "name" field		
		String eventTypeName = (String)input.getValueByField(EventHeader.NAME_ATTRIBUTE);
		logger.fine("convertTuple:getting event name: "+eventTypeName);
		IEventType eventType = EventMetadataFacade.getInstance().getEventType(eventTypeName);
		Map<String,Object> attributes = new HashMap<String,Object>();		
		
		Collection<TypeAttribute> typeAttributes = eventType.getTypeAttributes();
		Map<String,Object> tupleAttributes = (Map<String,Object>)input.getValueByField(MetadataFacade.ATTRIBUTES_FIELD);
		logger.fine("convertTuple: iterating over attributes of event type" );
		//TODO: this is for validation - talk to Alex if required
		for (Iterator iterator = typeAttributes.iterator(); iterator.hasNext();) {
			TypeAttribute typeAttribute = (TypeAttribute) iterator.next();
			String attributeName = typeAttribute.getName();
			if (tupleAttributes.containsKey(attributeName)){
				Object fieldValue = tupleAttributes.get(attributeName);
				logger.fine("convertTuple: got value for attribute " +attributeName+" ,value: "+fieldValue );
				attributes.put(attributeName,fieldValue);
			}
			
		}
		//TODO add additional fields here as in adapters
		IEventInstance eventInstance = new EventInstance(eventType, attributes);
		eventInstance.setDetectionTime(Calendar.getInstance().getTimeInMillis());
		logger.fine("convertTuple: built event instance"+ eventInstance );
		return eventInstance;
	}
	
	public  List<Object> createOutputTuple(IEventInstance eventInstance) {
		logger.fine("<==========================inside Proton createOutputTuple method=========>");
		logger.fine("createOutputTuple: converting "+eventInstance+" to tuple");
	    List<Object> tuple = new ArrayList<Object>();	   
	    tuple.add(eventInstance.getEventType().getName()); //adding event name
	    tuple.add(eventInstance.getAttributes()); //adding attributes
	    
	    logger.fine("createOutputTuple: created tuple values :"+tuple);
	    return tuple;
	}
	
	
	private  Collection<ProtonParseException> initializeMetadata(String jsonTxt, EepFacade eep) throws ParsingException{
 	       MetadataParser metadataParser  = new MetadataParser(eep);
	       Collection<ProtonParseException> exceptions = metadataParser.parseEPN(jsonTxt);
	       return exceptions;
	             
	  }
	
	
	
}
