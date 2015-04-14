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

import java.util.HashMap;
import java.util.Map;

import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

public abstract class BaseTextFormatter extends AbstractTextFormatter {


	
	protected static final String DELIMITER = "delimiter";
	protected static final String TAG_DATA_SEPARATOR = "tagDataSeparator";
	 
	protected static final String NULL_STRING = "null";
	
	protected String delimeter;
	protected String tagDataSeparator;

	protected BaseTextFormatter(String delimeter, String tagDataSeparator,String dateFormat) throws AdapterException
	{
		super(dateFormat);
		this.delimeter = delimeter;
		this.tagDataSeparator = tagDataSeparator;
	}
	
	@Override
	public abstract String formatInstance(IDataObject instance);
	
	protected abstract String getAttributeStringValue(TypeAttribute eventTypeAttribute, String attrStringValue);
		
	
	@Override
	public IEventInstance parseText(String eventText) throws AdapterException 
	{		
		//search the Name attribute depicting the event type name
		int nameIndex  = eventText.indexOf(EventHeader.NAME_ATTRIBUTE);
		String substring = eventText.substring(nameIndex);
		int delimiterIndex = substring.indexOf(delimeter);
		int tagDataSeparatorIndex = substring.indexOf(tagDataSeparator);
		String nameValue = substring.substring(tagDataSeparatorIndex+1,delimiterIndex);
		IEventType eventType= EventMetadataFacade.getInstance().getEventType(nameValue);
		
		//search for all pairs of tag-data by using delimiter
		Map<String,Object> attrValues = new HashMap<String,Object>();
		
		String[] tagValuePairs = eventText.split(this.delimeter);
		for (String tagValue : tagValuePairs) 
		{
			//separate the tag from the value using the tagDataSeparator
			String[] separatedPair = tagValue.split(tagDataSeparator);
			String attrName = separatedPair[0]; //the tag is always the first in the pair
			
			if (attrName != null) {
				attrName = attrName.trim();
			}

			//some attributes might not have value specified at all
			if (separatedPair.length < 2)
			{
	        	attrValues.put(attrName, null);
	        	continue;

			}

			String attrStringValue = separatedPair[1];
			if (attrStringValue != null) {
				attrStringValue = attrStringValue.trim();
			}

			if (attrStringValue.equals(NULL_STRING))
	        {
	        	//the attribute has a value of null
	        	attrValues.put(attrName, null);
	        	continue;
	        }
			
			TypeAttribute eventTypeAttribute = eventType.getTypeAttributeSet()
					.getAttribute(attrName);

			
			attrStringValue = getAttributeStringValue(eventTypeAttribute, attrStringValue);			

			Object attrValueObject;	        
	      
	        try {
				attrValueObject = TypeAttribute.parseConstantValue(attrStringValue, attrName,eventType,dateFormatter);
			} catch (Exception e) {
				throw new AdapterException("Could not parse the event string"+eventText+", reason: "+e.getMessage());
			} 
	        attrValues.put(attrName,attrValueObject);
	        
	        /*try {
				switch (attrType)
				{
				case INTEGER:
					attrValueObject =  Integer.valueOf(attrStringValue);
					attrValues.put(attrName, (Integer)attrValueObject);
					break;
				case LONG:
					attrValueObject =  Long.valueOf(attrStringValue);
					attrValues.put(attrName, (Long)attrValueObject);
					break;                    
				case FLOAT:
					attrValueObject =  Float.valueOf(attrStringValue);
					attrValues.put(attrName, (Float)attrValueObject);
					break;                    
				case DOUBLE:                        
					//can either be a simple double or a distribution
					attrValueObject = TypeAttribute.parseDouble(attrStringValue);
					if (attrValueObject instanceof Double){
						attrValues.put(attrName, (Double)attrValueObject);
					}else
					{
						attrValues.put(attrName, (AbstractDistribution)attrValueObject);
					}                        
					break;
				case DATETIME:
					try {
						attrValueObject = df.parse(attrStringValue);
						attrValues.put(attrName, (Date)attrValueObject);
					} catch (Exception e) {
						//assume that if unparsable data format it is a long value representing millisecs
						attrValues.put(attrName, Long.valueOf(attrStringValue));
					}
					break;
				case STRING:
					attrValues.put(attrName, attrStringValue);
					break;
				case BOOLEAN:
					attrValueObject =  Boolean.valueOf(attrStringValue);
					attrValues.put(attrName, (Boolean)attrValueObject);
					break;
				case TIME:
					attrValueObject = Time.valueOf(attrStringValue);
					attrValues.put(attrName, (Time)attrValueObject);
					break;               
				case UUID:
					attrValueObject = UUID.fromString(attrStringValue);
					attrValues.put(attrName, (UUID)attrValueObject);
					break;            
				default:
					attrValues.put(attrName, attrStringValue);
					break;
				}
			} catch (Exception e) {
				throw new AdapterException("Could not parse the event string"+eventText+", reason: "+e.getMessage());
			} */
	                			
	      
		}
		
		return new EventInstance(eventType, attrValues);
		
		
	}
}
