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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

/**
 * @author tali
 * 
 */
public class XmlNgsiFormatter extends AbstractTextFormatter {
	
	static final String EVENT_NAME_SUFFIX = "ContextUpdate"; 
	static final String ENTITY_ID_ATTRIBUTE = "entityId";
	static final String ENTITY_TYPE_ATTRIBUTE = "entityType";
	private static final Logger logger = Logger.getLogger(XmlNgsiFormatter.class.getName());


	
	public XmlNgsiFormatter(Map<String,Object> properties) throws AdapterException
	{
		this((String)properties.get(MetadataParser.DATE_FORMAT));
	}
	
	private XmlNgsiFormatter(String dateFormat) throws AdapterException 
	{
		super(dateFormat);
	}
	
	
	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.adapters.formatters.ITextFormatter#formatInstance(com.ibm.hrl.proton.metadata.epa.proactive.IDataObject)
	 */
	@Override
	public String formatInstance(IDataObject instance) {
		
		//see expected result example at the end of this file
		
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

		String ngsiXml = 
				 "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+"<updateContextRequest>\n"
			    +"   <contextElementList>\n"
			    +"    <contextElement>\n"
			    +"		 <entityId type=\"" + entityType + "\" isPattern=\"false\">\n"
			    +"			<id>" + entityID + "</id>\n"
			    +"		 </entityId>\n"
			    +"		<contextAttributeList>\n";

		
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
					String xmlAttr = 
							 "   	   <contextAttribute>\n"
							+"            <name>"+ attrName + "</name>\n"
							+"    		  <contextValue>" + attrValue + "</contextValue>\n"
							+"         </contextAttribute>\n";
					ngsiXml = ngsiXml.concat(xmlAttr);				
				}
			}
		}
		String xmlEnd = 
				 "      </contextAttributeList>\n"
				+"    </contextElement>\n"
				+"   </contextElementList>\n"
				+"   <updateAction>UPDATE</updateAction>\n"
				+"</updateContextRequest>";
		
		ngsiXml = ngsiXml.concat(xmlEnd);
		return ngsiXml;
	}

	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.adapters.formatters.ITextFormatter#parseText(java.lang.String)
	 */
	@Override
	public IEventInstance parseText(String eventText) throws AdapterException {
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(eventText);
			doc.getDocumentElement().normalize();

			Node entityIdNode = doc.getElementsByTagName("entityId").item(0);
			String entityType = entityIdNode.getAttributes().getNamedItem("type").getNodeValue();
			String entityId = getNodeValue(((Node)doc.getElementsByTagName("id").item(0)));
			System.out.println("Entity type: " + entityType + " Entity id:"	+ entityId);
			String eventName = entityType+EVENT_NAME_SUFFIX;

			IEventType eventType= EventMetadataFacade.getInstance().getEventType(eventName);
			Map<String,Object> attrValues = new HashMap<String,Object>();
			
			addAttribute(ENTITY_TYPE_ATTRIBUTE, entityType, eventType, attrValues);
			addAttribute(ENTITY_ID_ATTRIBUTE, entityId, eventType, attrValues);

			// get all pairs of attribute name and value
			NodeList attrNodes = doc.getElementsByTagName("contextAttribute");

			for (int i=0; i<attrNodes.getLength(); i++) {
				Element attr = (Element)attrNodes.item(i); 
				String attrName = getNodeValue(attr.getElementsByTagName("name").item(0));
				String attrStringValue = getNodeValue(attr.getElementsByTagName("contextValue").item(0));
				System.out.println("Attribute[" + i + "] name: " + attrName +
									" value:" + attrStringValue);
				
				addAttribute(attrName, attrStringValue, eventType, attrValues);
			}

			IEventInstance event = new EventInstance(eventType,attrValues);
	       	event.setDetectionTime(System.currentTimeMillis());
	       	
	       	return event;


		} catch (Exception e) {
			String msg = "Could not parse XML NGSI event " + e + ", reason: " + e.getMessage();
			throw new AdapterException(msg);
		}

	}
	
	private void addAttribute(String attrName, String attrStringValue, IEventType eventType, Map<String,Object> attrMap) throws AdapterException{

		TypeAttribute eventTypeAttribute = eventType.getTypeAttributeSet().getAttribute(attrName);
		AttributeTypesEnum attrType = eventTypeAttribute.getTypeEnum();
		if (attrType.equals(AttributeTypesEnum.STRING) || eventTypeAttribute.getDimension()>0) {
			attrStringValue = "'"+attrStringValue+"'";
		}
		
		Object attrValueObject;	     
		try {
	       	attrValueObject = TypeAttribute.parseConstantValue(attrStringValue,attrName,eventType,dateFormatter);
		    attrMap.put(attrName,attrValueObject);
		} catch (Exception e) {
		    throw new AdapterException("Could not convert XML input attribute " + attrName + " to event attribute");
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

//Expected result example for updateContextRequest (sent as output to the consumer):
//	<?xml version="1.0" encoding="UTF-8"?>
//	<updateContextRequest>
//	    <contextElementList>
//	   	 <contextElement>
//	   		 <entityId type="Lamp" isPattern="false">
//	   			 <id>Lamp5</id>
//	   		 </entityId>
//	   		 <contextAttributeList>
//	   			 <contextAttribute>
//	   				 <name>ligthlevel</name>
//	   				 <type>percentage</type>
//	   				 <contextValue>45</contextValue>
//	   			 </contextAttribute>
//	   		 </contextAttributeList>
//	   	 </contextElement>
//	    </contextElementList>
//	    <updateAction>UPDATE</updateAction>
//	</updateContextRequest>


