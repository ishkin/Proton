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
package com.ibm.hrl.proton.metadata.type;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zoharf
 *
 */
public class TypeAttributeSet implements Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Map<String, TypeAttribute> typeAttributes;
	
	public TypeAttributeSet()
	{
		this(null);
	}
	public TypeAttributeSet(List<TypeAttribute> atts)
	{
		typeAttributes = new HashMap<String, TypeAttribute>();
		addAllAttributes(atts);
	}
	
	public void addAttribute(TypeAttribute att)
	{
		typeAttributes.put(att.getName(), att);
	}
	
	public void addAllAttributes(List<TypeAttribute> atts)
	{
		for (TypeAttribute att : atts)
			addAttribute(att);
	}
	
	public TypeAttribute getAttribute(String attName)
	{
		assert (!typeAttributes.containsKey(attName));
		
		return typeAttributes.get(attName);
	}
	
	public Collection<TypeAttribute> getAllAttributes()
	{
		return typeAttributes.values();
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
	    String attrDefsString = "";
	    Collection<TypeAttribute> attributes = getAllAttributes();
	    for (TypeAttribute typeAttribute : attributes)
        {
            attrDefsString += typeAttribute+";";
        }
	    
	    return attrDefsString;
	}
}
