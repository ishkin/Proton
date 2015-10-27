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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.webapp.WebFacadesManager;
import com.ibm.hrl.proton.webapp.WebMetadataFacade;
import com.ibm.hrl.proton.webapp.exceptions.ResponseException;

@Provider
@Consumes(MediaType.TEXT_PLAIN)
public class EventPlainTextMessageReader implements MessageBodyReader<IEventInstance> {

	
	
	private static final String DELIMITER = ";";
	private static final String TAG_DATA_SEPARATOR = "=";
	private static final Logger logger = Logger.getLogger(EventPlainTextMessageReader.class.getName());
	
	@Override
	public boolean isReadable(Class<?> type, Type generic, Annotation[] annotation, MediaType media) {
		//return String.class.isAssignableFrom(type);
		return true;
	}

	@Override
	public IEventInstance readFrom(Class<IEventInstance> type, Type generic, Annotation[] annotation,
			MediaType media, MultivaluedMap<String, String> map, InputStream eventStream)
			throws IOException, WebApplicationException {

		logger.info("started plain text message body reader");
		
		// the InputStream event contains event in plain text format
		// we should parse the event text, create IEventInstance and return it -
		// the resource's put/post function will be invoked
		
		//String eventString = getEventString(eventStream);
		String eventString = "Name=StockBuy;id=111;amount=100;price=5000";
		logger.info("extracted event string: " + eventString);
		
		// the eventString consists of name=value pairs separated by delimiter		
		int nameAttrIndex = eventString.indexOf(EventHeader.NAME_ATTRIBUTE);
		String nameSubstring = eventString.substring(nameAttrIndex);
		
		int delimiterIndex = nameSubstring.indexOf(DELIMITER);
		int tagDataSeparatorIndex = nameSubstring.indexOf(TAG_DATA_SEPARATOR);
		String nameValue = nameSubstring.substring(tagDataSeparatorIndex+1,delimiterIndex);		
		IEventType eventType= WebMetadataFacade.getInstance().getEventMetadataFacade().getEventType(nameValue);
		
		// search for all pairs of tag-data by using delimiter
		Map<String,Object> attrValues = new HashMap<String,Object>();
		
		String[] tagValuePairs = eventString.split(DELIMITER);
		for (String tagValue: tagValuePairs) 
		{
			// separate the tag from the value using the tagDataSeparator
			String[] separatedPair = tagValue.split(TAG_DATA_SEPARATOR);
			String attrName = separatedPair[0]; //the tag is always the first in the pair
			
			//some attributes might not have value specified at all
			if (separatedPair.length < 2) {
	        	attrValues.put(attrName, null);
	        	continue;
			}
			String attrStringValue = separatedPair[1];
			if (attrStringValue.equals("null")) {
	        	//the attribute has a value of null
	        	attrValues.put(attrName,null);
	        	continue;
	        }
			
			TypeAttribute eventTypeAttribute = eventType.getTypeAttributeSet().getAttribute(attrName);
			AttributeTypesEnum attrType = eventTypeAttribute.getTypeEnum();
			if (attrType.equals(AttributeTypesEnum.STRING) || eventTypeAttribute.getDimension()>0) {
				attrStringValue = "'"+attrStringValue+"'";
			}
			
			Object attrValueObject;	        	      
	        try {
				attrValueObject = TypeAttribute.parseConstantValue(attrStringValue,attrName,eventType,null,WebFacadesManager.getInstance().getEepFacade());
		        attrValues.put(attrName,attrValueObject);
			} catch (Exception e) {
			    String msg = "Could not parse the event string" + eventString +
						", reason: " + e.getMessage();
			    logger.severe(msg);
			    
			    throw new ResponseException(msg);
			}
		}
		
		IEventInstance event = new EventInstance(eventType,attrValues);
       	event.setDetectionTime(System.currentTimeMillis());
       	
       	logger.info("finished message body reader");
		return event;
	}

	private String getEventString(InputStream eventStream) throws IOException {
		if (eventStream != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
            	int bytesRead;
                Reader reader = new BufferedReader(new InputStreamReader(eventStream,"UTF-8"));                
                while ((bytesRead = reader.read(buffer)) != -1) {
                    writer.write(buffer,0,bytesRead);
                }
            } finally {
            	eventStream.close();
            }
            return writer.toString();
        }               
		return "";		
	}

}
