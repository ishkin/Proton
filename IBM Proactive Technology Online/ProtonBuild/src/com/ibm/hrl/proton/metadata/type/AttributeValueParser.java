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
package com.ibm.hrl.proton.metadata.type;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.ibm.eep.exceptions.ParseException;
import com.ibm.hrl.proton.expression.facade.EEPException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObjectMeta;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.EventInstance;

public class AttributeValueParser {
	
	public static Object parseConstantValue(String attrValue,String attrName,IDataObjectMeta objectMetadata,DateFormat dateFormatter) throws EEPException{
		Object value;
		ArrayList<IDataObjectMeta> signature = new ArrayList<IDataObjectMeta>();
		signature.add(objectMetadata);
		IDataObject instance = new EventInstance((IEventType)objectMetadata, new HashMap<String,Object>());
		
		IExpression defaultValueExpression;
		try {
			defaultValueExpression = EepFacade.getInstance().createExpression(attrValue, signature);
			value = defaultValueExpression.evaluate(instance);
		} catch (Exception e) {
			//TODO temporal workaround
			//In the future extend EEP to allow different date formats		
			//perhaps the value is a Date expression in a format EEP cannot read, recheck
			if (objectMetadata.getFieldMetaData(attrName).getType().toUpperCase().equals(AttributeTypesEnum.DATE.getName().toUpperCase()) && dateFormatter != null)
			{
				//try parsing with provided formatter
				try {
					value = dateFormatter.parse(attrValue).getTime();
				} catch (java.text.ParseException e1) {
					//try normal timestamp
					try{
						value = Long.parseLong(attrValue);
					}catch(NumberFormatException e2){
						throw new EEPException("Error parsing date string: "+ attrValue+", reason: wrong format");
					}
					
				}
			}else
			{
				throw new EEPException("Error parsing attribute value"+attrValue+", reason: "+e.getMessage());
			}
		}						
		
		
		return value;
	}
}
