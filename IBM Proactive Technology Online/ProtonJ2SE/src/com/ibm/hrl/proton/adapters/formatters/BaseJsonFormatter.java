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
package com.ibm.hrl.proton.adapters.formatters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import json.java.JSON;
import json.java.JSONObject;

import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

public abstract class BaseJsonFormatter extends AbstractTextFormatter{

	
		
	protected BaseJsonFormatter(String dateFormat) throws AdapterException 
	{
		super(dateFormat);
	}
	
	
	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.adapters.formatters.ITextFormatter#formatInstance(com.ibm.hrl.proton.metadata.epa.proactive.IDataObject)
	 */
	@Override
	public abstract String formatInstance(IDataObject instance);
		
		
	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.adapters.formatters.ITextFormatter#parseText(java.lang.String)
	 */
	@Override
	public IEventInstance parseText(String eventText) throws AdapterException {
		
		JSONObject eventJson;
		try {
			eventJson = (JSONObject)JSON.parse(eventText);
			System.out.println("json event: " + eventJson.toString());
		} catch (IOException e) {
			throw new AdapterException("Could not convert JSON string to event object");
		}
	    
		String nameValue = (String)eventJson.get(EventHeader.NAME_ATTRIBUTE);
		IEventType eventType= EventMetadataFacade.getInstance().getEventType(nameValue);
		if (eventType==null){
			throw new AdapterException("Event type ".concat(nameValue).concat(" is not defined"));
		}
		
		// get all pairs of attribute name and value
		Map<String,Object> attrValues = new HashMap<String,Object>();
		for (Object key: eventJson.keySet()) {
			String attrName = (String)key; 
			String attrStringValue = (String)eventJson.get(attrName);
			TypeAttribute eventTypeAttribute = eventType.getTypeAttributeSet().getAttribute(attrName);
			if (eventTypeAttribute == null){
				throw new AdapterException("Attibute ".concat(attrName).concat(" of event ").concat(nameValue).concat(" is not defined"));
			}
			AttributeTypesEnum attrType = eventTypeAttribute.getTypeEnum();
			if (attrType.equals(AttributeTypesEnum.STRING) || eventTypeAttribute.getDimension()>0) {
				attrStringValue = "'"+attrStringValue+"'";
			}
			
			Object attrValueObject;	        	      
	        try {
				attrValueObject = TypeAttribute.parseConstantValue(attrStringValue,attrName,eventType,dateFormatter);
		        attrValues.put(attrName,attrValueObject);
			} catch (Exception e) {
			    throw new AdapterException("Could not convert JSON string to event object");
			}						
		}
		
		IEventInstance event = new EventInstance(eventType,attrValues);
       	event.setDetectionTime(System.currentTimeMillis());
       	
       	return event;
	}
}
