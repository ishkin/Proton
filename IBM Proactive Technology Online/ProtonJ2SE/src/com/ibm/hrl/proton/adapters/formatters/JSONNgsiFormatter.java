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
/**
 * 
 */
package com.ibm.hrl.proton.adapters.formatters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import json.java.JSON;
import json.java.JSONArray;
import json.java.JSONObject;

import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;
import com.ibm.hrl.proton.utilities.containers.Pair;


/**
 * @author tali
 * 
 */
public class JSONNgsiFormatter extends AbstractTextFormatter {
	private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy-HH:mm:ss";
	static final String EVENT_NAME_SUFFIX = "ContextUpdate"; 
	static final String ENTITY_ID_ATTRIBUTE = "entityId";
	static final String ENTITY_TYPE_ATTRIBUTE = "entityType";
	private static final Logger logger = Logger.getLogger(JSONNgsiFormatter.class.getName());


	
	public JSONNgsiFormatter(Map<String,Object> properties,EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException
	{
		this((String)properties.get(MetadataParser.DATE_FORMAT),eventMetadata,eep);
	}
	
	private JSONNgsiFormatter(String dateFormat,EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException 
	{
		super(dateFormat,eventMetadata,eep);
	}
	
	
	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.adapters.formatters.ITextFormatter#formatInstance(com.ibm.hrl.proton.metadata.epa.proactive.IDataObject)
	 */
	@Override
	public Pair<String,String> formatInstance(IDataObject instance){
		//see expected result example at the end of this file
		
				String jsonString = "{ \n";
				Map<String,Object> instanceAttrs = instance.getFieldValues();
				
				String entityType = String.valueOf(instanceAttrs.get(ENTITY_TYPE_ATTRIBUTE));
				if(entityType == null){
				    String msg = "Missing "+ ENTITY_TYPE_ATTRIBUTE + 
			    		     " attribute in the event. NGSI update context is sent with no " + ENTITY_TYPE_ATTRIBUTE;
				    logger.severe(msg);	
				}
				
				String entityID = String.valueOf(instanceAttrs.get(ENTITY_ID_ATTRIBUTE));
				if(entityID == null){
				    String msg = "Missing "+ ENTITY_ID_ATTRIBUTE + 
				    		     " attribute in the event. NGSI update context is sent with no " + ENTITY_ID_ATTRIBUTE;
				    logger.severe(msg);	
				}
				
				String urlExtension = entityID+"/attrs?type="+entityType;
				boolean  firstAttribute = true; 
				
				for (Map.Entry<String, Object> attributeEntry : instanceAttrs.entrySet()) {
					String attrName = attributeEntry.getKey();
					if (attributeEntry.getKey().equals(ENTITY_TYPE_ATTRIBUTE)) continue;
					if (attributeEntry.getKey().equals(ENTITY_ID_ATTRIBUTE)) continue;

					Object value = attributeEntry.getValue();
					if(value != null)
					{
						String attrValue = String.valueOf(value);
						if (attrValue != null && !attrValue.isEmpty()){
							if ((value instanceof Long) && (instance.getFieldMetaData(attrName).getType().equals(AttributeTypesEnum.DATE.toString())))
							{
								//convert this long to Date using formatter's date format
								attrValue = formatTimestamp((Long)value);
							}
							if (firstAttribute == false) jsonString = jsonString.concat(", \n");
							String jsonAttr = "\""+attrName + "\" : { \n"
									+         "\"value\" : \""+value+"\" \n"
									+		  "} \n";
							jsonString = jsonString.concat(jsonAttr);
							firstAttribute = false;
						}
					}
				}
				
				String jsonEnd = "}"; 
				jsonString = jsonString.concat(jsonEnd);
				
				Pair<String,String> returnedResults = new Pair<String,String>(urlExtension,jsonString);
				return returnedResults;
	}

	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.adapters.formatters.ITextFormatter#parseText(java.lang.String)
	 */
	@Override
	public IEventInstance parseText(String eventText) throws AdapterException {
		EventInstance event = null;
		try {
			
			JSONObject eventJson = (JSONObject)JSON.parse(eventText);
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

		 
		}catch(Exception e){
  		  String msg = "Could not parse JSON NGSI event " + e + ", reason: " + e.getMessage();
  		  logger.severe(msg);	
  		  throw new AdapterException(msg);
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
			attrValueObject = TypeAttribute.parseConstantValue(attrStringValue,attrName,eventType,dateFormatter,eep);
			attrMap.put(attrName,attrValueObject);
		} catch (Exception e) {
			String msg = "Could not convert JSON input attribute " + attrName + " to event attribute " + e + ", reason: " + e.getMessage();
			logger.severe(msg);	
			//throw new ResponseException(msg);
		}

	}

	private EventInstance parseVTwoFormat(JSONObject eventJson) throws AdapterException
	{
		EventInstance event = null;

		JSONObject data = (JSONObject)((JSONArray)eventJson.get("data")).get(0);
		String entityType = (String)data.get("type");
		String entityId = (String)data.get("id");
		String eventName = entityType+EVENT_NAME_SUFFIX;

		IEventType eventType= eventMetadata.getEventType(eventName);
		logger.info("Event: " + eventName );
		Map<String,Object> attrValues = new HashMap<String,Object>();

		DateFormat dateFormatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		addAttribute(ENTITY_ID_ATTRIBUTE, entityId, eventType, attrValues, dateFormatter);
		addAttribute(ENTITY_TYPE_ATTRIBUTE, entityType, eventType, attrValues, dateFormatter);

		//iterate over all the rest of attributes and add them to attribute map
		
		Iterator<String> attributes = data.keySet().iterator();
		while(attributes.hasNext()){
			String attributeName = attributes.next();
			String attrStringValue = null;

			if (!attributeName.equals("id") && !attributeName.equals("type")){
				try{
					JSONObject value = (JSONObject)data.get(attributeName);
					String attributeType = (String)value.get("type");
					attrStringValue = value.get("value").toString();

					if(attrStringValue!=null){
						addAttribute(attributeName, attrStringValue, eventType, attrValues, dateFormatter);
					}
				}catch(Exception e){
					String msg = "Could not parse JSON NGSI event " + e + ", reason: " + e.getMessage() + 
							"\n last attribute name: " +  attributeName + " last value: " + attrStringValue;
					logger.severe(msg);	
					throw new AdapterException(msg);
				}		        			   
			}
		}

		event = new EventInstance(eventType,attrValues);
		event.setDetectionTime(System.currentTimeMillis());

		return event;
	}

	private EventInstance parseVOneFormat(JSONObject eventJson) throws AdapterException{
		EventInstance event = null;


		JSONObject element = (JSONObject)((JSONArray)eventJson.get("contextResponses")).get(0);
		JSONObject contextElement = (JSONObject)element.get("contextElement");
		String entityType = (String)contextElement.get("type");
		String entityId = (String)contextElement.get("id");
		String eventName = entityType+EVENT_NAME_SUFFIX;

		IEventType eventType= eventMetadata.getEventType(eventName);
		logger.info("Event: " + eventName );
		Map<String,Object> attrValues = new HashMap<String,Object>();

		DateFormat dateFormatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		addAttribute(ENTITY_ID_ATTRIBUTE, entityId, eventType, attrValues, dateFormatter);
		addAttribute(ENTITY_TYPE_ATTRIBUTE, entityType, eventType, attrValues, dateFormatter);

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




