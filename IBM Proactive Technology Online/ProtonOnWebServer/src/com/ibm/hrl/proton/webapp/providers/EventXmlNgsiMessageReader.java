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
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.webapp.WebFacadesManager;
import com.ibm.hrl.proton.webapp.WebMetadataFacade;
import com.ibm.hrl.proton.webapp.exceptions.ResponseException;


@Provider
@Consumes("application/xml")
public class EventXmlNgsiMessageReader implements MessageBodyReader<EventInstance> {


	
	private static final Logger logger = Logger.getLogger(EventXmlNgsiMessageReader.class.getName());
	//private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.S'000'z"; //2013-04-24T18:07:01.000000Z
	private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy-HH:mm:ss";
	static final String EVENT_NAME_SUFFIX = "ContextUpdate"; 
	static final String ENTITY_ID_ATTRIBUTE = "entityId";
	static final String ENTITY_TYPE_ATTRIBUTE = "entityType";

		
	@Override
	public boolean isReadable(Class<?> type, Type generic, Annotation[] annotation,MediaType media) {
		return EventInstance.class.isAssignableFrom(type);
	}

	@Override
	public EventInstance readFrom(Class<EventInstance> type, Type generic, Annotation[] annotation,
			MediaType media, MultivaluedMap<String, String> map, InputStream eventStream)
			throws IOException, WebApplicationException {

		logger.info("started event message body reader");
		EventInstance event = null;
		String attrName = null;
		String attrStringValue = null;
		
		// the InputStream event contains event in XML NGSI format
		// we should parse the object, create IEventInstance and return it -
		// the resource's put/post function will be invoked
		
		try {
			
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(eventStream);
				doc.getDocumentElement().normalize();

				Node entityIdNode = doc.getElementsByTagName("entityId").item(0);
				String entityType = entityIdNode.getAttributes().getNamedItem("type").getNodeValue();
				String entityId = getNodeValue(((Node)doc.getElementsByTagName("id").item(0)));
				//System.out.println("Entity type: " + entityType + " Entity id:"	+ entityId);
				String eventName = entityType+EVENT_NAME_SUFFIX;

				IEventType eventType= WebMetadataFacade.getInstance().getEventMetadataFacade().getEventType(eventName);
				logger.info("Event: " + eventName );
				Map<String,Object> attrValues = new HashMap<String,Object>();

				DateFormat dateFormatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
				addAttribute(ENTITY_ID_ATTRIBUTE, entityId, eventType, attrValues, dateFormatter);
				addAttribute(ENTITY_TYPE_ATTRIBUTE, entityType, eventType, attrValues, dateFormatter);

				// get all pairs of attribute name and value
				NodeList attrNodes = doc.getElementsByTagName("contextAttribute");

				for (int i=0; i<attrNodes.getLength(); i++) {
					Element attr = (Element)attrNodes.item(i); 
					attrName = getNodeValue(attr.getElementsByTagName("name").item(0));
					attrStringValue = getNodeValue(attr.getElementsByTagName("contextValue").item(0));
					//System.out.println("Attribute[" + i + "] name: " + attrName +
					//					" value:" + attrStringValue);
					if(attrStringValue!=null){
						addAttribute(attrName, attrStringValue, eventType, attrValues, dateFormatter);
					}
				}

				event = new EventInstance(eventType,attrValues);
		       	event.setDetectionTime(System.currentTimeMillis());


			} catch (Exception e) {
			    String msg = "Could not parse XML NGSI event " + e + ", reason: " + e.getMessage() + 
			    		      "\n last attribute name: " +  attrName + " last value: " + attrStringValue;
			    logger.severe(msg);	
			    new ResponseException(msg);
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
			String msg = "Could not convert XML input attribute " + attrName + " to event attribute " + e + ", reason: " + e.getMessage();
			logger.severe(msg);	
		    //throw new ResponseException(msg);
		}

	}
	
	private String getNodeValue(Node node){
		
		if (node == null){
			return null;
		}
		Node internalTextNode = node.getFirstChild();
		if (internalTextNode == null){
			return null;
		}
		return internalTextNode.getNodeValue();
	}

}
