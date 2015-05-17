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

import java.util.Map;

import json.java.JSONObject;

import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;

/**
 * @author kofman
 *
 */
public class JSONFormatter extends BaseJsonFormatter {


	
	public JSONFormatter(Map<String,Object> properties) throws AdapterException
	{
		this((String)properties.get(MetadataParser.DATE_FORMAT));
	}
	
	private JSONFormatter(String dateFormat) throws AdapterException 
	{
		super(dateFormat);
	}
	
	
	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.adapters.formatters.ITextFormatter#formatInstance(com.ibm.hrl.proton.metadata.epa.proactive.IDataObject)
	 */
	@Override
	public String formatInstance(IDataObject instance) {
		
		JSONObject json = new JSONObject();
		
		String nameTag = EventHeader.NAME_ATTRIBUTE;
		
		
		String typeName = instance.getMetadata().getName();

		json.put(nameTag, typeName);
		
		Map<String,Object> instanceAttrs = instance.getFieldValues();
		for (Map.Entry<String, Object> attributeEntry : instanceAttrs.entrySet()) {
			String name = attributeEntry.getKey();
			if (attributeEntry.getKey().equals(EventHeader.NAME_ATTRIBUTE)) continue;

			Object value = attributeEntry.getValue();
			if(value != null)
			{
				String entryValue = String.valueOf(value);
				if ((value instanceof Long) && (instance.getFieldMetaData(name).getType().equals(AttributeTypesEnum.DATE.toString())))
				{
					//convert this long to Date using formatter's date format
					entryValue = formatTimestamp((Long)value);
				}
				json.put(name, entryValue);
			}
			
		}
		
		return json.toString();
	}

	

}
