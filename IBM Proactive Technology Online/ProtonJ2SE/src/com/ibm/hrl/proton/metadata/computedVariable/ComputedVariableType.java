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
package com.ibm.hrl.proton.metadata.computedVariable;

import java.util.ArrayList;
import java.util.List;

import com.ibm.hrl.proton.metadata.type.AbstractType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;

/**
 * Represents all computed variables used in a single aggregation EPA
 * The name of the computed variable type is always the same as the name of the aggregation EPA
 * it is assigned to , the field names are the same as the names of computed variables defined
 * by the user
* <code>ComputedVariableType</code>.
* 
*
 */
public class ComputedVariableType extends AbstractType implements
		IComputedVariableType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComputedVariableType()
	{
		super();
	}
	
	public ComputedVariableType(String name,List<TypeAttribute> payload)
	{		
	    super(name,payload);
	    
	}
	
	public ComputedVariableType(List<TypeAttribute> payload)
	{
		super(payload);
	}
	
	@Override
	public List<TypeAttribute> getHeaderAttributes() {
		return new ArrayList<TypeAttribute>();
	}

}
