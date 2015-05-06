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

import java.util.Map;

import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

public class TagTextFormatter extends BaseTextFormatter {


	
	
	public TagTextFormatter(Map<String,Object> properties,EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException
	{
		this((String)properties.get(DELIMITER),(String)properties.get(TAG_DATA_SEPARATOR),(String)properties.get(MetadataParser.DATE_FORMAT),eventMetadata,eep);
	}
	
	private TagTextFormatter(String delimeter, String tagDataSeparator,String dateFormat,EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException
	{
		super(delimeter,tagDataSeparator,dateFormat,eventMetadata,eep);
		
	}

	@Override
	public String formatInstance(IDataObject instance) {
		StringBuffer stringBuffer = new StringBuffer();
		
		//making sure event name appears first - readability purposes
		String nameTag=nameTag = EventHeader.NAME_ATTRIBUTE;
		
		
		String typeName = instance.getMetadata().getName();
		stringBuffer.append(nameTag).append(tagDataSeparator).append(typeName).append(delimeter);
		
		Map<String,Object> instanceAttrs = instance.getFieldValues();
		for (Map.Entry<String, Object> attributeEntry : instanceAttrs.entrySet()) {
			if (attributeEntry.getKey().equals(EventHeader.NAME_ATTRIBUTE)) continue;
			stringBuffer.append(attributeEntry.getKey());
			stringBuffer.append(tagDataSeparator);
			
			if ((attributeEntry.getValue() instanceof Long) && (instance.getFieldMetaData(attributeEntry.getKey()).getType().equals(AttributeTypesEnum.DATE.toString())))
			{
				//convert this long to Date using formatter's date format
				String dateString = formatTimestamp((Long)attributeEntry.getValue());
				stringBuffer.append(dateString);
			}else
			{
				stringBuffer.append(attributeEntry.getValue());
			}
			
			stringBuffer.append(delimeter);
		}
		
		return stringBuffer.toString();
	}

	@Override
	protected String getAttributeStringValue(TypeAttribute eventTypeAttribute, String attrStringValue) {		
		AttributeTypesEnum attrType = eventTypeAttribute.getTypeEnum();
		if (attrType.equals(AttributeTypesEnum.STRING) || eventTypeAttribute.getDimension()>0) {
			return "'"+attrStringValue+"'";
		}
		return attrStringValue;
	}

	

}
