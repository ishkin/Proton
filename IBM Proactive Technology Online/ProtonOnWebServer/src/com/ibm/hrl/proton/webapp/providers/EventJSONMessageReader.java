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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.hrl.proton.adapters.formatters.JSONComposerFormatter;
import com.ibm.hrl.proton.adapters.formatters.JSONNgsiFormatter;
import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.webapp.WebFacadesManager;
import com.ibm.hrl.proton.webapp.WebMetadataFacade;
import com.ibm.hrl.proton.webapp.exceptions.ResponseException;


@Provider
@Consumes("application/json")
public class EventJSONMessageReader implements MessageBodyReader<EventInstance> {
	private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy-HH:mm:ss";
//	static final String EVENT_NAME_SUFFIX = "ContextUpdate"; 
//	static final String ENTITY_ID_ATTRIBUTE = "entityId";
//	static final String ENTITY_TYPE_ATTRIBUTE = "entityType";
	


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
		String eventString = new Scanner(eventStream).useDelimiter("\\A").next();
		// the InputStream event contains event in JSON format
		// we should parse the object, create IEventInstance and return it -
		// the resource's put/post function will be invoked

		try {
			// the below trick is from here -
			// http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string			
			
			//try the NGSI format
			event = parseNGSIJSON(eventString);
		}
		catch (Exception e) {
			
			try{
				event = parseComposerJSON(eventString);
			}catch(Exception e1){
				
				
				try{
					event = parseRegularJSON(eventString);
				}catch (Exception e2){
					String msg3 = "Could not parse json as any of supported types (NGSI, Composer,regular) " + e2 + ", reason: " + e2.getMessage();
					logger.severe(msg3);
					throw new ResponseException(msg3);
					
				}
			}
			
		}

		logger.info("finished event message body reader");
		return event;
	}

	private EventInstance parseComposerJSON(String eventString) throws IOException, WebApplicationException{
		JSONObject eventJson;
		EventInstance event = null;
		try {
			 eventJson = new JSONObject(eventString);
			//get the class object and extract the name of the event
			String className = (String)eventJson.get(JSONComposerFormatter.CLASS_ATTRIBUTE_VALUE);
			int lastIndexOf = className.lastIndexOf(JSONComposerFormatter.NAMESPACE_DELIMETER);
			String eventTypeName = className.substring(lastIndexOf+1);


			IEventType eventType= WebMetadataFacade.getInstance().getEventMetadataFacade().getEventType(eventTypeName);

			Map<String,Object> attrValues = new HashMap<String,Object>();
			for (Object key: eventJson.keySet()) {
				String attrName = (String)key; 
				
				TypeAttribute eventTypeAttribute = eventType.getTypeAttributeSet().getAttribute(attrName);
				if (eventTypeAttribute == null){
					continue;
				}
				
				String attrStringValue = (String)eventJson.get(attrName);
				AttributeTypesEnum attrType = eventTypeAttribute.getTypeEnum();
				if (attrType.equals(AttributeTypesEnum.STRING) || eventTypeAttribute.getDimension()>0) {
					attrStringValue = "'"+attrStringValue+"'";
				}

				Object attrValueObject;	        	      
				try {
					attrValueObject = TypeAttribute.parseConstantValue(attrStringValue,attrName,eventType,null,WebFacadesManager.getInstance().getEepFacade());
					attrValues.put(attrName,attrValueObject);
				} catch (Exception e) {
					throw new AdapterException("Could not convert JSON string to event object");
				}						
			}

			event = new EventInstance(eventType,attrValues);
			event.setDetectionTime(System.currentTimeMillis());

		}
		catch (Exception e) {
			String msg = "Could not parse json event " + e + ", reason: " + e.getMessage();		

			throw new ResponseException(msg);
		}

		logger.info("finished event message body reader");
		return event;


	}

	private EventInstance parseRegularJSON(String eventString) throws IOException, WebApplicationException
	{
		logger.info("started event message body reader");

		EventInstance event = null;
		// the InputStream event contains event in JSON format
		// we should parse the object, create IEventInstance and return it -
		// the resource's put/post function will be invoked

		try {
			JSONObject eventJson = new JSONObject(eventString);

			String nameValue = (String)eventJson.get(EventHeader.NAME_ATTRIBUTE);			
			logger.info("name value: " + nameValue + " looking for: " + EventHeader.NAME_ATTRIBUTE);						
			IEventType eventType= WebMetadataFacade.getInstance().getEventMetadataFacade().getEventType(nameValue);

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
					attrValueObject = TypeAttribute.parseConstantValue(attrStringValue,attrName,eventType,null,WebFacadesManager.getInstance().getEepFacade());
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

			throw new ResponseException(msg);
		}

		logger.info("finished event message body reader");
		return event;
	}
	
	private EventInstance parseNGSIJSON(String eventString) throws IOException, WebApplicationException
	{
		logger.info("started event message body reader");

		EventInstance event = null;
		// the InputStream event contains event in JSON format
		// we should parse the object, create IEventInstance and return it -
		// the resource's put/post function will be invoked

		try {
			JSONObject eventJson = new JSONObject(eventString);
			
			//get the entry for the entity
			//distinguish between NGSI v1 and v2 formats
						
			
			if (eventJson.containsKey("contextResponses")){
				//parse as NGSI v1 format		
				event= parseVOneFormat(eventJson);
			}
			else
			{
				//parse as NGSI v2 format
				event= parseVTwoFormat(eventJson);
			}

		}
		catch (Exception e) {
			String msg = "Could not parse json NGSI event " + e + ", reason: " + e.getMessage();			

			throw new ResponseException(msg);
		}

		logger.info("finished event message body reader");
		return event;
	}

	private void addAttribute(String attrName, String attrStringValue, IEventType eventType, 
			Map<String,Object> attrMap, DateFormat dateFormatter) throws AdapterException{

		TypeAttribute eventTypeAttribute = eventType.getTypeAttributeSet().getAttribute(attrName);
		AttributeTypesEnum attrType = eventTypeAttribute.getTypeEnum();
		if (attrType.equals(AttributeTypesEnum.STRING) || eventTypeAttribute.getDimension()>0) {
			attrStringValue = "'"+attrStringValue+"'";
		}

		Object attrValueObject;	     
		try {
			attrValueObject = TypeAttribute.parseConstantValue(attrStringValue,attrName,eventType,dateFormatter,WebFacadesManager.getInstance().getEepFacade());
			attrMap.put(attrName,attrValueObject);
		} catch (Exception e) {
			String msg = "Could not convert JSON input attribute " + attrName + " to event attribute " + e + ", reason: " + e.getMessage();
			logger.severe(msg);	
			//throw new ResponseException(msg);
		}

	}

	private EventInstance parseVTwoFormat(JSONObject eventJson) throws JSONException, AdapterException
	{
		EventInstance event = null;

		JSONObject data = (JSONObject)((JSONArray)eventJson.get("data")).get(0);
		String entityType = (String)data.get("type");
		String entityId = (String)data.get("id");
		String eventName = entityType+JSONNgsiFormatter.EVENT_NAME_SUFFIX;

		IEventType eventType= WebMetadataFacade.getInstance().getEventMetadataFacade().getEventType(eventName);
		logger.info("Event: " + eventName );
		Map<String,Object> attrValues = new HashMap<String,Object>();

		DateFormat dateFormatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		addAttribute(JSONNgsiFormatter.ENTITY_ID_ATTRIBUTE, entityId, eventType, attrValues, dateFormatter);
		addAttribute(JSONNgsiFormatter.ENTITY_TYPE_ATTRIBUTE, entityType, eventType, attrValues, dateFormatter);

		//iterate over all the rest of attributes and add them to attribute map
		Iterator<String> attributes = data.keys();
		while(attributes.hasNext()){
			String attributeName = attributes.next();
			String attrStringValue = null;

			if (!attributeName.equals("id") && !attributeName.equals("type")){
				try{
					JSONObject value = data.getJSONObject(attributeName);
					String attributeType = (String)value.get("type");
					attrStringValue = value.get("value").toString();

					if(attrStringValue!=null){
						addAttribute(attributeName, attrStringValue, eventType, attrValues, dateFormatter);
					}
				}catch(Exception e){
					String msg = "Could not parse JSON NGSI event " + e + ", reason: " + e.getMessage() + 
							"\n last attribute name: " +  attributeName + " last value: " + attrStringValue;						
					new ResponseException(msg);
				}		        			   
			}
		}

		event = new EventInstance(eventType,attrValues);
		event.setDetectionTime(System.currentTimeMillis());

		return event;
	}

	private EventInstance parseVOneFormat(JSONObject eventJson) throws AdapterException, JSONException{
		EventInstance event = null;


		JSONObject element = (JSONObject)((JSONArray)eventJson.get("contextResponses")).get(0);
		JSONObject contextElement = element.getJSONObject("contextElement");
		String entityType = (String)contextElement.get("type");
		String entityId = (String)contextElement.get("id");
		String eventName = entityType+JSONNgsiFormatter.EVENT_NAME_SUFFIX;

		IEventType eventType= WebMetadataFacade.getInstance().getEventMetadataFacade().getEventType(eventName);
		logger.info("Event: " + eventName );
		Map<String,Object> attrValues = new HashMap<String,Object>();

		DateFormat dateFormatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		addAttribute(JSONNgsiFormatter.ENTITY_ID_ATTRIBUTE, entityId, eventType, attrValues, dateFormatter);
		addAttribute(JSONNgsiFormatter.ENTITY_TYPE_ATTRIBUTE, entityType, eventType, attrValues, dateFormatter);

		//iterate over all the rest of attributes and add them to attribute map
		JSONArray attributes = (JSONArray)contextElement.get("attributes");
		Iterator<JSONObject> attributesIter = attributes.iterator();

		while(attributesIter.hasNext()){
			JSONObject attribute = attributesIter.next();
			String name = (String)attribute.get("name");
			String value = attribute.get("value").toString();

			addAttribute(name, value, eventType, attrValues, dateFormatter);


		}


		event = new EventInstance(eventType,attrValues);
		event.setDetectionTime(System.currentTimeMillis());

		return event;
	}

}
