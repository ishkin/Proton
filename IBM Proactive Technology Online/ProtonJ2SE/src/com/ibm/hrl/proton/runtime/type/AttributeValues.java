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

import com.ibm.hrl.proton.metadata.type.TypeAttributeSet;


public class AttributeValues extends BaseAttributeValues {

		
	public AttributeValues(TypeAttributeSet atts)
	{
		super(atts);
	}
	
	public void assignValue(String attributeName, Object val)
	{
		assert(attributes.getAttribute(attributeName) != null);
		System.out.println(attributeName);
		System.out.println(val.toString());

		assert (val.getClass().equals(attributes.getAttribute(attributeName).getTypeEnum().getTypeClass()));
		attributeValues.put(attributeName, val);
	}
	
}
