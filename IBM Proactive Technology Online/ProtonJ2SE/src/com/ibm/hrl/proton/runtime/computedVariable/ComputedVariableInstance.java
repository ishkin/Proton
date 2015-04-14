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
package com.ibm.hrl.proton.runtime.computedVariable;

import java.util.Map;

import com.ibm.hrl.proton.metadata.computedVariable.IComputedVariableType;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObjectMeta;
import com.ibm.hrl.proton.metadata.epa.basic.IFieldMeta;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.runtime.computedVariable.interfaces.IComputedVariableInstance;
import com.ibm.hrl.proton.runtime.type.AttributeValues;

public class ComputedVariableInstance implements IComputedVariableInstance {
    protected IComputedVariableType computedVariableType;
    protected AttributeValues attributeValues;
    
    public ComputedVariableInstance(IComputedVariableType computedVariableType, Map<String, Object> attributes)
	{
		this.computedVariableType = computedVariableType;
		// need to fill in header attribute values which are not given by user like id etc.		
		attributeValues = new AttributeValues(computedVariableType.getTypeAttributeSet());
		attributeValues.assignValue(EventHeader.NAME_ATTRIBUTE, computedVariableType.getTypeName());			
		attributeValues.assignAll(attributes);
	}
	
	
	@Override
	public Object getFieldValue(String fieldName) {
		return attributeValues.getAttributeValue(fieldName);
	}

	@Override
	public IDataObjectMeta getMetadata() {
		return computedVariableType;
	}

	@Override
	public IFieldMeta getFieldMetaData(String fieldName) {
		return computedVariableType.getFieldMetaData(fieldName);
	}

	@Override
	public Map<String, Object> getFieldValues() {
		return getAttributes();
	}


	@Override
	public IComputedVariableType getComputedVariableType() {
		return computedVariableType;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributeValues.getAttributeValues();
	}

	@Override
	public String getObjectName() {
		return computedVariableType.getTypeName();
	}


	@Override
	public void setAttributes(Map<String, Object> attributes) {					
		attributeValues.assignAll(attributes);
		
	}

}
