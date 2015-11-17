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
package com.ibm.hrl.proton.runtime.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.TypeAttributeSet;

public abstract class BaseAttributeValues implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected TypeAttributeSet attributes;
	protected HashMap<String, Object> attributeValues;
	
	public BaseAttributeValues(TypeAttributeSet atts)
	{
		attributes = atts;
		attributeValues = new HashMap<String, Object>();
		setDefaultValues();
	}
	
	public abstract void assignValue(String attributeName, Object val);
	
	public HashMap<String, Object> getAttributeValues() {
		return attributeValues;
	}

	public void setAttributeValues(HashMap<String, Object> attributeValues) {
		this.attributeValues = attributeValues;
	}

	public void assignAll(Map<String, Object> attributes)
	{
		attributeValues.putAll(attributes);
	}
	
	public Object getAttributeValue(String attribute)
	{
		assert (attributeValues.containsKey(attribute));
		
		return attributeValues.get(attribute);
	}
	
	public void setDefaultValues()
	{
		for (TypeAttribute att : attributes.getAllAttributes())
			attributeValues.put(att.getName(), att.getDefaultValue());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
	    String result = "";
	    for (Map.Entry<String,Object> element : attributeValues.entrySet())
        {
	        String attrName = element.getKey();
	        Object attrValue  = element.getValue();
	        result+=attrName+"="+attrValue+"; ";
        }
	    
	    return result;
	}
}
