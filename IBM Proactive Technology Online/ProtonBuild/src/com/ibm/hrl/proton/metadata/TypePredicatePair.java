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
package com.ibm.hrl.proton.metadata;

import com.ibm.hrl.proton.metadata.type.interfaces.IBasicType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

public class TypePredicatePair {

	protected IBasicType type;
    protected IExpression parsedCondition;
	protected String condition;
	
    public TypePredicatePair(IBasicType type,
            IExpression parsedCondition, String condition)
    {
        super();
        this.type = type;
        this.parsedCondition = parsedCondition;
        this.condition = condition;
    }

	public IBasicType getType() {
		return type;
	}
	public void setType(IBasicType basicType) {
		this.type = basicType;
	}
	public IExpression getParsedCondition() {
		return parsedCondition;
	}
	public void setParsedCondition(IExpression parsedCondition) {
		this.parsedCondition = parsedCondition;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	
}
