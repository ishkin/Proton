package com.ibm.hrl.proton.adapters.formatters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import json.java.JSON;
import json.java.JSONObject;

import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;
import com.ibm.hrl.proton.utilities.containers.Pair;

public class JSONComposerFormatter extends AbstractTextFormatter {	
	public static final String CLASS_ATTRIBUTE_VALUE = "$class";
	public static final String CLASS_ATTRIBUTE = "class";
	public static final String NAMESPACE_DELIMETER=".";
	private String namespace;
	private HashSet<String> headerAttributesNames;

	
	public JSONComposerFormatter(Map<String,Object> properties,EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException
	{
		this((String)properties.get(MetadataParser.DATE_FORMAT),(String)properties.get(CLASS_ATTRIBUTE), eventMetadata,eep);		
	}
	
	private JSONComposerFormatter(String dateFormat,String namespace,EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException 
	{
		super(dateFormat,eventMetadata,eep);
		this.namespace = namespace;						
		this.headerAttributesNames = new HashSet<String>();
		
		//removing header attributes
		List<TypeAttribute> headerAttributes = EventHeader.getAttributes();		
		
		for (TypeAttribute eventAttribute : headerAttributes) {
			this.headerAttributesNames.add(eventAttribute.getName());
		}
		
		
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.adapters.formatters.ITextFormatter#formatInstance(com.ibm.hrl.proton.metadata.epa.proactive.IDataObject)
	 */
	@Override
	public Pair<String,String> formatInstance(IDataObject instance){
		//see expected result example at the end of this file		
				
				JSONObject json = new JSONObject();				
				String typeName = instance.getMetadata().getName();
				
				//put the class name
				String urlExtension;
				if (namespace.equals("")){
					urlExtension = typeName;
				}else{
					urlExtension = namespace+NAMESPACE_DELIMETER+typeName;
					json.put(CLASS_ATTRIBUTE_VALUE,urlExtension );	
				}
				
										
				
				//fetch the list of relevant attributes and iterate over attributes to build the list
				Map<String,Object> instanceAttrs = instance.getFieldValues();
				for (Map.Entry<String, Object> attributeEntry : instanceAttrs.entrySet()) {
					String name = attributeEntry.getKey();
					if (attributeEntry.getKey().equals(EventHeader.NAME_ATTRIBUTE)) continue;
					if (headerAttributesNames.contains(name)) continue ; //do not want to format header attribute

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
				
				String jsonString = json.toString();
				System.out.println("Created event: "+jsonString);
				Pair<String,String> returnedResults = new Pair<String,String>(urlExtension,jsonString);
				return returnedResults;
	}

	@Override
	public IEventInstance parseText(String eventText) throws AdapterException {		
		
			JSONObject eventJson;
			try {
				eventJson = (JSONObject)JSON.parse(eventText);
				System.out.println("json event: " + eventJson.toString());
			} catch (IOException e) {
				throw new AdapterException("Could not convert JSON string to event object");
			}
			
			
			
			//get the class object and extract the name of the event
			String className = (String)eventJson.get(CLASS_ATTRIBUTE_VALUE);
			int lastIndexOf = className.lastIndexOf(NAMESPACE_DELIMETER);
			String eventTypeName = className.substring(lastIndexOf+1);
			
			
			IEventType eventType= eventMetadata.getEventType(eventTypeName);
						
			Map<String,Object> attrValues = new HashMap<String,Object>();
			for (Object key: eventJson.keySet()) {
				String attrName = (String)key; 				
				TypeAttribute eventTypeAttribute = eventType.getTypeAttributeSet().getAttribute(attrName);
				if (eventTypeAttribute == null){
					continue;
				}
				String attrStringValue = eventJson.get(attrName).toString();
				AttributeTypesEnum attrType = eventTypeAttribute.getTypeEnum();
				if (attrType.equals(AttributeTypesEnum.STRING) || eventTypeAttribute.getDimension()>0) {
					attrStringValue = "'"+attrStringValue+"'";
				}
				
				Object attrValueObject;	        	      
		        try {
					attrValueObject = TypeAttribute.parseConstantValue(attrStringValue,attrName,eventType,dateFormatter,eep);
			        attrValues.put(attrName,attrValueObject);
				} catch (Exception e) {
				    throw new AdapterException("Could not convert JSON string to event object");
				}						
			}
			
			IEventInstance event = new EventInstance(eventType,attrValues);
	       	event.setDetectionTime(System.currentTimeMillis());
	       	
	       	return event;
	

	}
	
	@Override
	public boolean isArray(String eventInstanceText) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> returnInstances(String eventInstanceText)  {
		return new ArrayList<String>();
	}

}
