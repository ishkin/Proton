/**
 * 
 */
package com.ibm.hrl.proton.adapters.formatters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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

/**
 * @author itaip
 *
 */
public class CSVMultipleEventsTextFormatter extends AbstractTextFormatter {
	
	protected static final String ATTRIBUTED_DELIMITER = ",";
	protected static final String DELIMITER = "delimiter";	
	protected static final String CSV_ATTRIBUTES = "csvAttributeNames";
	protected static final String EVENT_TYPE_NAME_INDEX = "eventTypeNameIndex";
	protected static final String EVENT_TYPE_NAME_ATTRIBUTE = "eventTypeNameAttribute";
		 
	protected static final String NULL_STRING = "null";
	
	protected String delimiter;
	protected Integer indexNameAttribute;
	protected String  eventTypeNameAttribute;
	// It's important that attributeNames will be a list ordered by order of insertion, because of the format of CSV file.
	protected ArrayList<String> attributeNames;
	//protected String eventTypeName;
	
	public CSVMultipleEventsTextFormatter(Map<String,Object> properties,EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException {
		
		super((String) properties.get(MetadataParser.DATE_FORMAT), eventMetadata, eep);
		String delimiter = (String) properties.get(DELIMITER);
		if (delimiter == null) {
			delimiter = ",";
		}
		this.delimiter = delimiter;
		
		this.indexNameAttribute = Integer.valueOf((String) properties.get(EVENT_TYPE_NAME_INDEX));
		
				
		//parse the attributes string
		this.attributeNames = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer((String)properties.get(CSV_ATTRIBUTES),ATTRIBUTED_DELIMITER);
		while (tokenizer.hasMoreElements()) {
			this.attributeNames.add(tokenizer.nextToken());
		}
		this.eventTypeNameAttribute = (String) this.attributeNames.get(this.indexNameAttribute);	

	}
	
	

	@Override
	public String formatInstance(IDataObject instance) throws AdapterException {
		
		StringBuffer stringBuffer = new StringBuffer();
		Map<String, Object> instanceAttributes = instance.getFieldValues();
		for(String attributeName : attributeNames) {

			Object attributeValue;
			if (attributeName.equals(this.eventTypeNameAttribute)) 
			{
				attributeValue = instance.getMetadata().getName();
			}
			else {
			 attributeValue = instanceAttributes.get(attributeName);
			}
			
			
			if (attributeValue == null) {
				attributeValue = "";
			}
			
			if (attributeValue != null && (attributeValue instanceof Long) && (instance.getFieldMetaData(attributeName).getType().equals(AttributeTypesEnum.DATE.toString())))
			{
				//convert this long to Date using formatter's date format
				String dateString = formatTimestamp((Long) attributeValue);
				stringBuffer.append(dateString);
			} else {
				stringBuffer.append(attributeValue);
			}
			stringBuffer.append(delimiter);
		}
		
		//remove the last delimiter, because it will look like there is another attribute value after it
		stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		
		return stringBuffer.toString();
	}

	@Override
	public IEventInstance parseText(String eventText) throws AdapterException {

		// limit -1 to cancel avoiding trailing empty slots
		String[] attributeStringValues = eventText.split(delimiter, -1);
		
		if (attributeStringValues.length != this.attributeNames.size()) {
			throw new AdapterException("Could not parse the event string " + eventText + ", reason: Mismatch between CSV file attributes and number of values: number of values:"+attributeStringValues.length+", attribute declarations: "+this.attributeNames.size());
		}
		
		Map<String,Object> attributeValues = new HashMap<String,Object>();
		
				
		IEventType eventType = eventMetadata.getEventType(attributeStringValues[indexNameAttribute]);
		if (eventType == null) {
			throw new AdapterException("Could not parse the event string " + eventText + ", reason: event type by the name"+attributeStringValues[indexNameAttribute]+" does not exist");
		}
		
		int index = -1;
		for (String attributeName : attributeNames) {
			
			++index;
			attributeStringValues[index] = attributeStringValues[index].trim();
			
			TypeAttribute eventTypeAttribute = eventType.getTypeAttributeSet().getAttribute(attributeName);
			if (eventTypeAttribute == null){
				continue;
			}
			
			if (attributeStringValues[index].equals("") || attributeStringValues[index].equals(NULL_STRING)) {
				attributeValues.put(attributeName, null);
				continue;
			}
			
			
			String attributeStringValue = attributeStringValues[index];
			attributeStringValue = getAttributeStringValue(eventTypeAttribute, attributeStringValue);
						
			Object attributeValueObject;
			try {
				attributeValueObject = TypeAttribute.parseConstantValue(attributeStringValue, attributeName, eventType, dateFormatter,eep);
			} catch (Exception e) {
				throw new AdapterException("Could not parse the event string " + eventText + ", reason: " + e.getMessage());
			} 
			attributeValues.put(attributeName, attributeValueObject);
		}
		return new EventInstance(eventType, attributeValues);
	}
	
	protected String getAttributeStringValue(TypeAttribute eventTypeAttribute, String attrStringValue) {		
		AttributeTypesEnum attrType = eventTypeAttribute.getTypeEnum();
		if (attrType.equals(AttributeTypesEnum.STRING) || eventTypeAttribute.getDimension()>0) {
			return "'"+attrStringValue+"'";
		}
		return attrStringValue;
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
