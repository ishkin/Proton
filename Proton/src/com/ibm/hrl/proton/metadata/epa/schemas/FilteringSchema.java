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
package com.ibm.hrl.proton.metadata.epa.schemas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.interfaces.IFilteringSchema;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

public class FilteringSchema implements IFilteringSchema {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4090741302506158236L;
	/** If there is no filtering expression per operand then the string and the parsed expressions per this operand are NULL */
	protected Map<Operand, String> filters; // Textual representation of the filter expression
	protected Map<Operand, IExpression> parsedExpresions; // run-time expression evaluate
	protected Map<IEventType,List<Operand>> eventsOperandMap;
    
	
    public FilteringSchema(Map<Operand, String> filters,
            Map<Operand, IExpression> parsedExpresions)
    {
        this.filters = filters;
        this.parsedExpresions = parsedExpresions;
        mapEventsToOperands();
        
    }
    
   
    
	//protected List<EventPredicatePair> filters; --> TODO: need to replace in the future
	
	/**
     * @return
     */
    private void mapEventsToOperands()
    {
        eventsOperandMap = new HashMap<IEventType, List<Operand>>();
        for (Operand operand : parsedExpresions.keySet())
        {
            IEventType eventType = operand.getEventType();
            List<Operand> eventOperandList;
            if (!eventsOperandMap.containsKey(eventType))
            {
                eventsOperandMap.put(eventType, new ArrayList<Operand>());                
            }
            eventOperandList = eventsOperandMap.get(eventType);
            eventOperandList.add(operand);
        }
                
    }

    public Map<Operand, String> getFilters() {
		return filters;
	}
	public void setFilters(Map<Operand, String> filters) {
		this.filters = filters;
	}
	public Map<Operand, IExpression> getParsedExpresions() {
		return parsedExpresions;
	}
	public void setParsedExpresions(Map<Operand, IExpression> parsedExpresions) {
		this.parsedExpresions = parsedExpresions;
	}
	
	public Map<Operand,IExpression> getEventExpressions(IEventType eventType)
	{
	    Map<Operand,IExpression> operandExpressionsMap = new HashMap<Operand,IExpression>();
	   
	     List<Operand> eventOperands = eventsOperandMap.get(eventType);
	     
	     if (eventOperands != null)
	     {
	         for (Operand operand : eventOperands)
	         {            
	              operandExpressionsMap.put(operand, parsedExpresions.get(operand));
	         }
	     }
	     	     
	     return operandExpressionsMap;
	      
	}
	
}
