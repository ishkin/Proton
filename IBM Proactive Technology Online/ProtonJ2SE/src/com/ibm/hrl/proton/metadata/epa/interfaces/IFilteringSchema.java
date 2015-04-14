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
package com.ibm.hrl.proton.metadata.epa.interfaces;

import java.io.Serializable;
import java.util.Map;

import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

/**
 * @author zoharf
 *
 */
public interface IFilteringSchema  extends Serializable{

	/**
	 * Get all the parsed expressions for all event types in this schema
	 * @return
	 */
	public Map<Operand, IExpression> getParsedExpresions();
	
	/**
	 * Get parsed expressions for the specified event type , return a mapping between operands
	 * and their matchign expressions
	 * @param eventInstance
	 * @return
	 */
	public Map<Operand,IExpression> getEventExpressions(IEventType eventType);
	
}
