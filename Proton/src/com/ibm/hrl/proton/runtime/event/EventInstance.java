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
package com.ibm.hrl.proton.runtime.event;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.ibm.hrl.proton.metadata.epa.basic.IDataObjectMeta;
import com.ibm.hrl.proton.metadata.epa.basic.IFieldMeta;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.type.AttributeValues;

public class EventInstance implements IEventInstance{

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected IEventType eventType;
	

    protected AttributeValues attributeValues;
	
	public EventInstance(IEventType eventType, Map<String, Object> attributes)
	{
		this.eventType = eventType;
		// need to fill in header attribute values which are not given by user like id etc.		
		attributeValues = new AttributeValues(eventType.getTypeAttributeSet());
		attributeValues.assignValue(EventHeader.NAME_ATTRIBUTE, eventType.getTypeName());
		attributeValues.assignValue(EventHeader.CHRONON_ATTRIBUTE, eventType.getChronon());
		attributeValues.assignValue(EventHeader.EVENT_INSTANCE_ID_ATTRIBUTE,UUID.randomUUID());
		attributeValues.assignAll(attributes);
	}
	
	public EventInstance(IEventType eventType, long occurenceTime,long detectionTime,Map<String, Object> attributes)
	{
	    this(eventType,attributes);
	    attributeValues.assignValue(EventHeader.DETECTION_TIME_ATTRIBUTE, detectionTime);
	    attributeValues.assignValue(EventHeader.OCCURENCE_TIME_ATTRIBUTE, occurenceTime);
	}
	
	public EventInstance(IEventType eventType,long detectionTime,Map<String, Object> attributes)
	{
	    this(eventType,attributes);
	    attributeValues.assignValue(EventHeader.DETECTION_TIME_ATTRIBUTE, detectionTime);	    
	}
	
	
	public void setDetectionTime(long detectionTime)
	{
	    attributeValues.assignValue(EventHeader.DETECTION_TIME_ATTRIBUTE, detectionTime);
	}
	
	@Override
	public IEventType getEventType() {
		// TODO Auto-generated method stub
		return eventType;
	}

	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return attributeValues.getAttributeValues();
	}

	@Override
	public Object getEventAttribute(String attName) {
		// TODO Auto-generated method stub
		return attributeValues.getAttributeValue(attName);
	}
		
	@Override
	public long getOccurenceTime()
	{
		return (Long) getEventAttribute(EventHeader.OCCURENCE_TIME_ATTRIBUTE);
	}
	
	@Override
	public long getDetectionTime()
	{
		return (Long) getEventAttribute(EventHeader.DETECTION_TIME_ATTRIBUTE);
	}
	
	@Override
	public String getObjectName()
	{
		return (String) getEventAttribute(EventHeader.NAME_ATTRIBUTE);
	}
	
	@Override
	public Double getEventDuration()
	{
		return (Double) getEventAttribute(EventHeader.DURATION_ATTRIBUTE);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
	    String returnString = "";
	    returnString += eventType.getTypeName()+"; ";	   
	    returnString += attributeValues;
	    return returnString;
	    
	}

	@Override
	public Object getFieldValue(String fieldName) {
		// TODO Auto-generated method stub
		return getEventAttribute(fieldName);
	}

	@Override
	public IDataObjectMeta getMetadata() {
		// TODO Auto-generated method stub
		return getEventType();
	}

	@Override
	public IFieldMeta getFieldMetaData(String fieldName) {
		// TODO Auto-generated method stub
		return getEventType().getFieldMetaData(fieldName);
	}

	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance#getEventId()
	 */
			
	@Override
	public UUID getEventId() {
		return (UUID)getEventAttribute(EventHeader.EVENT_INSTANCE_ID_ATTRIBUTE);		
	}
	
	public void setEventType(IEventType eventType)
    {
        this.eventType = eventType;
    }
	
	/**
	 * Method for parsing a string representation of event instance and returning an EventInstance object 
	 * filled with relevant payload and metadata
	 * @param eventInstance
	 * @return
	 */
	/*public static EventInstance parseFlatInstance(String eventInstance)
	{
	    Map<String,Object> attrValues = new HashMap<String,Object>();
	    DateFormat df = DateFormat.getDateInstance();
	    String delims = ";";
        String[] tokens = eventInstance.split(delims);
        String eventTypeName = tokens[0];
        eventTypeName = eventTypeName.trim();
        IEventType eventType = EventMetadataFacade.getInstance().getEventType(eventTypeName);
        for (int i = 1; i < tokens.length; i++)
        {
            String attributeExpression = tokens[i];
            int equalSignIndex = attributeExpression.indexOf("=");
            String attrName = attributeExpression.substring(0, equalSignIndex);
            attrName = attrName.trim();
            String attrValue = attributeExpression.substring(equalSignIndex+1);
            attrValue = attrValue.trim();
            
            //get the attribute type and convert the value to appropriate object
            Object attrValueObject;
            TypeAttribute eventTypeAttribute = eventType.getTypeAttributeSet().getAttribute(attrName);
            AttributeTypesEnum attrType = eventTypeAttribute.getTypeEnum();
            
            try{
                switch (attrType)
                {
                    case INTEGER:
                        attrValueObject =  Integer.valueOf(attrValue);
                        attrValues.put(attrName, (Integer)attrValueObject);
                        break;
                    case LONG:
                        attrValueObject =  Long.valueOf(attrValue);
                        attrValues.put(attrName, (Long)attrValueObject);
                        break;                    
                    case FLOAT:
                        attrValueObject =  Float.valueOf(attrValue);
                        attrValues.put(attrName, (Float)attrValueObject);
                        break;                    
                    case DOUBLE:                        
                        //can either be a simple double or a distribution
                        attrValueObject = TypeAttribute.parseDouble(attrValue);
                        if (attrValueObject instanceof Double){
                            attrValues.put(attrName, (Double)attrValueObject);
                        }else
                        {
                            attrValues.put(attrName, (AbstractDistribution)attrValueObject);
                        }                        
                        break;
                    case DATETIME:
                        attrValueObject = df.parse(attrValue);
                        attrValues.put(attrName, (Date)attrValueObject);
                        break;
                    case STRING:
                        attrValues.put(attrName, attrValue);
                        break;
                    case BOOLEAN:
                        attrValueObject =  Boolean.valueOf(attrValue);
                        attrValues.put(attrName, (Boolean)attrValueObject);
                        break;
                    case TIME:
                        attrValueObject = Time.valueOf(attrValue);
                        attrValues.put(attrName, (Time)attrValueObject);
                        break;               
                    case UUID:
                        attrValueObject = UUID.fromString(attrValue);
                        attrValues.put(attrName, (UUID)attrValueObject);
                        break;            
                    default:
                        attrValues.put(attrName, attrValue);
                        break;
                }

            }catch(Exception e){
                e.printStackTrace();
            }
            
            
        }
        
        EventInstance instance = new EventInstance(eventType, attrValues);
        return instance;
	}*/

	@Override
	public Map<String, Object> getFieldValues() {
		return getAttributes();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;
		EventInstance other = (EventInstance)o;
		if (!other.getEventType().getTypeName().equals(this.getEventType().getTypeName())) return false;
		Map<String,Object> attributes = getAttributes();
		for (Map.Entry<String, Object> attribute : attributes.entrySet()) {
			String attributeName = attribute.getKey();
			Object attributeValue  = attribute.getValue();
			if (attributeName.equals(EventHeader.OCCURENCE_TIME_ATTRIBUTE) ||
				attributeName.equals(EventHeader.DETECTION_TIME_ATTRIBUTE)	||
				attributeName.equals(EventHeader.EVENT_INSTANCE_ID_ATTRIBUTE)) continue;
			if (attributeValue == null && other.getEventAttribute(attributeName) == null) continue;
			if (!other.getEventAttribute(attributeName).equals(attributeValue)) return false;
		}
		return true;
	}
    
}
