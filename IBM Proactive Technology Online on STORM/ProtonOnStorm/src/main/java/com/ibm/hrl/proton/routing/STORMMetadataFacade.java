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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.tuple.Tuple;

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
import com.ibm.hrl.proton.runtime.metadata.MetadataFacade;

public class STORMMetadataFacade implements Serializable {
	

	
	public static final String ATTRIBUTES_FIELD = "attributes";
	public static final String AGENT_NAME_FIELD = "agentName";
	public static final String CONTEXT_NAME_FIELD = "contextName";
	public static final String CONTEXT_PARTITION_FIELD = "contextPartition";	
	public static final String CONTEXT_SEGMENTATION_VALUES = "segmentationValues";	
	
	public static final String EVENT_STREAM = "events";
	public static final String CONSUMER_EVENTS_STREAM = "consumerEvents";
	public static final String TERMINATOR_EVENT_NAME = "contextTerminatorEventStream";
	
	private static final Logger logger = LoggerFactory.getLogger(STORMMetadataFacade.class.getName());
	private MetadataFacade metadataFacade;


	public STORMMetadataFacade(String propertiesFileNamePath,EepFacade eep) throws ParsingException, EEPException {
		
		this.metadataFacade = new MetadataFacade();
		Collection<ProtonParseException> exceptions = initializeMetadata(propertiesFileNamePath,metadataFacade,eep);
    	logger.info("init: done initializing metadata, returned the following exceptions: ");
    	for (ProtonParseException protonParseException : exceptions) {
			logger.info(protonParseException.toString());
		}    	 

	    
	          
    	        
	}

	

	public MetadataFacade getMetadataFacade() {
		return metadataFacade;
	}

	
	public IEventInstance createEventFromFlatTuple(Tuple input)
	{
		logger.debug("<==========================inside Proton createEventFromTuple method=========>");
		List<String> tupleFields = input.getFields().toList();
		//assuming each tuple has a "name" field		
		String eventTypeName = (String)input.getValueByField(EventHeader.NAME_ATTRIBUTE);
		logger.debug("convertTuple:getting event name: "+eventTypeName);
		IEventType eventType = metadataFacade.getEventMetadataFacade().getEventType(eventTypeName);
		Map<String,Object> attributes = new HashMap<String,Object>();		
		
		Collection<TypeAttribute> typeAttributes = eventType.getTypeAttributes();
		logger.debug("convertTuple: iterating over attributes of event type" );
		for (Iterator iterator = typeAttributes.iterator(); iterator.hasNext();) {
			TypeAttribute typeAttribute = (TypeAttribute) iterator.next();
			String attributeName = typeAttribute.getName();
			if (input.contains(attributeName)){
				Object fieldValue = input.getValueByField(attributeName);
				logger.debug("convertTuple: got value for attribute " +attributeName+" ,value: "+fieldValue );
				attributes.put(attributeName,fieldValue);
			}
			
		}
		//TODO add additional fields here as in adapters
		IEventInstance eventInstance = new EventInstance(eventType, attributes);
		eventInstance.setDetectionTime(Calendar.getInstance().getTimeInMillis());
		logger.debug("convertTuple: built event instance"+ eventInstance );
		return eventInstance;
	}
	
	public IEventInstance createEventFromTuple(Tuple input)
	{
		logger.debug("<==========================inside Proton createEventFromTuple method=========>");
		List<String> tupleFields = input.getFields().toList();
		//assuming each tuple has a "name" field		
		String eventTypeName = (String)input.getValueByField(EventHeader.NAME_ATTRIBUTE);
		logger.debug("convertTuple:getting event name: "+eventTypeName);
		IEventType eventType = metadataFacade.getEventMetadataFacade().getEventType(eventTypeName);
		Map<String,Object> attributes = new HashMap<String,Object>();		
		
		Collection<TypeAttribute> typeAttributes = eventType.getTypeAttributes();
		Map<String,Object> tupleAttributes = (Map<String,Object>)input.getValueByField(STORMMetadataFacade.ATTRIBUTES_FIELD);
		logger.debug("convertTuple: iterating over attributes of event type" );
		//TODO: this is for validation - talk to Alex if required
		for (Iterator iterator = typeAttributes.iterator(); iterator.hasNext();) {
			TypeAttribute typeAttribute = (TypeAttribute) iterator.next();
			String attributeName = typeAttribute.getName();
			if (tupleAttributes.containsKey(attributeName)){
				Object fieldValue = tupleAttributes.get(attributeName);
				logger.debug("convertTuple: got value for attribute " +attributeName+" ,value: "+fieldValue );
				attributes.put(attributeName,fieldValue);
			}
			
		}
		//TODO add additional fields here as in adapters
		IEventInstance eventInstance = new EventInstance(eventType, attributes);
		eventInstance.setDetectionTime(Calendar.getInstance().getTimeInMillis());
		logger.debug("convertTuple: built event instance"+ eventInstance );
		return eventInstance;
	}
	
	public  List<Object> createOutputTuple(IEventInstance eventInstance) {
		logger.debug("<==========================inside Proton createOutputTuple method=========>");
		logger.debug("createOutputTuple: converting "+eventInstance+" to tuple");
	    List<Object> tuple = new ArrayList<Object>();	   
	    tuple.add(eventInstance.getEventType().getName()); //adding event name
	    tuple.add(eventInstance.getAttributes()); //adding attributes
	    
	    logger.debug("createOutputTuple: created tuple values :"+tuple);
	    return tuple;
	}
	
	
	private  Collection<ProtonParseException> initializeMetadata(String jsonTxt,MetadataFacade metadataFacade,EepFacade eep) throws ParsingException{		   
 	       MetadataParser metadataParser;
	
			metadataParser = new MetadataParser(eep,metadataFacade);
			 Collection<ProtonParseException> exceptions = metadataParser.parseEPN(jsonTxt);
		     return exceptions;
		
	      
	             
	  }
	
	
	
}
