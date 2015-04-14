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
package deprecated;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.router.EventRouter;
import com.ibm.hrl.proton.router.IEventRouter;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;
import com.ibm.hrl.proton.webapp.exceptions.ResponseException;

@Path("/events-plain-text")
public class EventResourcePlainTextDeprecated {
		
	
	
	private static final String DELIMITER = ";";
	private static final String TAG_DATA_SEPARATOR = "=";

	private static final Logger logger = Logger.getLogger(EventResourcePlainTextDeprecated.class.getName());
	private static final IEventRouter eventRouter = EventRouter.getInstance();
	
	@POST
	@Consumes("text/plain")
	public String submitNewEvent(String eventString) {
		
		logger.info("starting submitNewEvent");		
		//createAndSendEvents(eventString);
		
		// the eventString consists of name=value pairs separated by delimiter		
		int nameAttrIndex = eventString.indexOf(EventHeader.NAME_ATTRIBUTE);
		String nameSubstring = eventString.substring(nameAttrIndex);
		
		int delimiterIndex = nameSubstring.indexOf(DELIMITER);
		int tagDataSeparatorIndex = nameSubstring.indexOf(TAG_DATA_SEPARATOR);
		String nameValue = nameSubstring.substring(tagDataSeparatorIndex+1,delimiterIndex);
		IEventType eventType = EventMetadataFacade.getInstance().getEventType(nameValue);
		
		//search for all pairs of tag-data by using delimiter
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
			if (attrType.equals(AttributeTypesEnum.STRING)) {
				attrStringValue = "'"+attrStringValue+"'";
			}
			
			Object attrValueObject;	        	      
	        try {
				attrValueObject = TypeAttribute.parseConstantValue(attrStringValue,attrName,eventType,null);
		        attrValues.put(attrName,attrValueObject);
			} catch (Exception e) {
			    String msg = "Could not parse the event string" + eventString +
						", reason: " + e.getMessage();
			    logger.severe(msg);
			}
		}
		
		IEventInstance event = new EventInstance(eventType,attrValues);
       	event.setDetectionTime(System.currentTimeMillis());
       		       	
		try {
			eventRouter.routeTimedObject(event);
		}
		catch (Exception e) {
		    String msg = "Could not send event, reason: " + e + ", message: " + e.getMessage();
		    logger.severe(msg);

		    throw new ResponseException(msg);
		}

		logger.info("events sent to proton runtime...");		
		return "sending event: done";
	}
	
	private void createAndSendEvents(String flag) {
		IEventType eventType= EventMetadataFacade.getInstance().getEventType("StockBuy");		
		Map<String,Object> attrValues = new HashMap<String,Object>();
		
       	attrValues.put("id","111");
		attrValues.put("amount","100");
		attrValues.put("price","5000");
		
       	IEventInstance event = new EventInstance(eventType,attrValues);
       	event.setDetectionTime(System.currentTimeMillis());
       	
		IEventRouter eventRouter = EventRouter.getInstance();

		try {
			eventRouter.routeTimedObject(event);
		}
		catch (Exception e) {
		    String msg = "Could not send event, reason: " + e + ", message: " + e.getMessage();
		    logger.severe(msg);

		    throw new ResponseException(msg);
		}
		
		//-------------------------------------------------------------------------------------
		
		eventType= EventMetadataFacade.getInstance().getEventType("StockSell");		
		attrValues.clear();
		
       	attrValues.put("id","111");
		attrValues.put("amount","100");
		attrValues.put("price","6000");
		
       	event = new EventInstance(eventType,attrValues);
       	event.setDetectionTime(System.currentTimeMillis());
		      	
		try {
			eventRouter.routeTimedObject(event);
		}
		catch (Exception e) {
		    String msg = "Could not send event, reason: " + e + ", message: " + e.getMessage();
		    logger.severe(msg);

		    throw new ResponseException(msg);
		}
	}
	
}
