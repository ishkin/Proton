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
package com.ibm.hrl.proton.webapp.providers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.wink.json4j.JSONObject;

import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;
import com.ibm.hrl.proton.webapp.exceptions.ResponseException;


@Provider
@Consumes("application/json")
public class EventJSONMessageReader implements MessageBodyReader<EventInstance> {

	
	
	private static final Logger logger = Logger.getLogger(EventJSONMessageReader.class.getName());
	
	@Override
	public boolean isReadable(Class<?> type, Type generic, Annotation[] annotation,MediaType media) {
		return EventInstance.class.isAssignableFrom(type);
		//return true;
	}

	@Override
	public EventInstance readFrom(Class<EventInstance> type, Type generic, Annotation[] annotation,
			MediaType media, MultivaluedMap<String, String> map, InputStream eventStream)
			throws IOException, WebApplicationException {

		logger.info("started event message body reader");
		
		EventInstance event = null;
		// the InputStream event contains event in JSON format
		// we should parse the object, create IEventInstance and return it -
		// the resource's put/post function will be invoked
		
		try {
            // the below trick is from here -
            // http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string			
			String eventString = new Scanner(eventStream).useDelimiter("\\A").next();
			JSONObject eventJson = new JSONObject(eventString);
					    		    
			String nameValue = (String)eventJson.get(EventHeader.NAME_ATTRIBUTE);			
			logger.info("name value: " + nameValue + " looking for: " + EventHeader.NAME_ATTRIBUTE);						
			IEventType eventType= EventMetadataFacade.getInstance().getEventType(nameValue);
			
			// get all pairs of attribute name and value
			Map<String,Object> attrValues = new HashMap<String,Object>();	
			Iterator<Object> keysItr = eventJson.keys(); 
			
			
			while(keysItr.hasNext()) {
				String attrName = (String)keysItr.next(); 
				String attrStringValue = (String)eventJson.get(attrName);
				TypeAttribute eventTypeAttribute = eventType.getTypeAttributeSet().getAttribute(attrName);
				AttributeTypesEnum attrType = eventTypeAttribute.getTypeEnum();
				if (attrType.equals(AttributeTypesEnum.STRING) || eventTypeAttribute.getDimension()>0) {
					attrStringValue = "'"+attrStringValue+"'";
				}
				
				Object attrValueObject;	        	      
		        try {
		        	//logger.info("current attribute: " + attrStringValue);
					attrValueObject = TypeAttribute.parseConstantValue(attrStringValue,attrName,eventType,null);
			        attrValues.put(attrName,attrValueObject);
				} catch (Exception e) {
				    String msg = "Could not parse json event " + eventJson + ", reason: " + e.getMessage();
				    logger.severe(msg);
				}			
			}
			event = new EventInstance(eventType,attrValues);
	       	event.setDetectionTime(System.currentTimeMillis());
		}
       	catch (Exception e) {
		    String msg = "Could not parse json event " + e + ", reason: " + e.getMessage();
		    logger.severe(msg);	
		    
		    throw new ResponseException(msg);
		}
       	
       	logger.info("finished event message body reader");
		return event;
	}

}
