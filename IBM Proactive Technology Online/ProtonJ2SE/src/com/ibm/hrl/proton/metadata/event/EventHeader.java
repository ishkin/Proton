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
package com.ibm.hrl.proton.metadata.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;

public class EventHeader implements Serializable{

	public static final String NAME_ATTRIBUTE = "Name";
	public static final String CHRONON_ATTRIBUTE = "Chronon";
	public static final String EVENT_INSTANCE_ID_ATTRIBUTE = "EventId";
	//sarit
	public static final String OCCURENCE_TIME_ATTRIBUTE = "OccurrenceTime";
	public static final String DETECTION_TIME_ATTRIBUTE = "DetectionTime";
	public static final String EXPIRATION_TIME_ATTRIBUTE = "ExpirationTime";
	public static final String ANNOTATION_ATTRIBUTE = "Annotation";
	public static final String CERTAINTY_ATTRIBUTE = "Certainty";
	public static final String EVENT_SOURCE_ATTRIBUTE = "EventSource";
	public static final String DURATION_ATTRIBUTE = "Duration";
	public static final String COST_ATTRIBUTE = "Cost";
	
	protected static List<TypeAttribute> headerAttributes;
	
	static
	{
		headerAttributes = new ArrayList<TypeAttribute>();
		headerAttributes.add(new TypeAttribute(NAME_ATTRIBUTE, AttributeTypesEnum.STRING));
		headerAttributes.add(new TypeAttribute(CHRONON_ATTRIBUTE, AttributeTypesEnum.CHRONON));
		headerAttributes.add(new TypeAttribute(EVENT_INSTANCE_ID_ATTRIBUTE, AttributeTypesEnum.UUID));
		headerAttributes.add(new TypeAttribute(OCCURENCE_TIME_ATTRIBUTE, AttributeTypesEnum.DATE));
		headerAttributes.add(new TypeAttribute(DETECTION_TIME_ATTRIBUTE, AttributeTypesEnum.DATE));
		headerAttributes.add(new TypeAttribute(ANNOTATION_ATTRIBUTE, AttributeTypesEnum.STRING));
		headerAttributes.add(new TypeAttribute(CERTAINTY_ATTRIBUTE, AttributeTypesEnum.DOUBLE));
		headerAttributes.add(new TypeAttribute(DURATION_ATTRIBUTE, AttributeTypesEnum.DOUBLE));
		headerAttributes.add(new TypeAttribute(EVENT_SOURCE_ATTRIBUTE, AttributeTypesEnum.STRING));
		headerAttributes.add(new TypeAttribute(EXPIRATION_TIME_ATTRIBUTE, AttributeTypesEnum.DATE));
		headerAttributes.add(new TypeAttribute(COST_ATTRIBUTE, AttributeTypesEnum.DOUBLE));
				
	}
	
	public static List<TypeAttribute> getAttributes()
	{
		return headerAttributes;
	}
}
