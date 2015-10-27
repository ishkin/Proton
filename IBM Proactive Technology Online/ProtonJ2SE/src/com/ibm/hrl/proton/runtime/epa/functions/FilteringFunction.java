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
package com.ibm.hrl.proton.runtime.epa.functions;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.interfaces.IFilteringSchema;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

public class FilteringFunction implements Serializable
{
    private static Logger logger = Logger.getLogger("FilteringFunction");
    /**
     * Return a list of all operands relevant for this event type, and whose condition was evaluated to TRUE for this event instance
     * @param event
     * @param filteringSchema
     * @return
     */
	public static Set<Operand> evaluate(IEventInstance event,IFilteringSchema filteringSchema)
	{
	    logger.fine("evaluate: evaluating filters for event instance: "+event);
	    Set<Operand> expressionEvalResults = new TreeSet<Operand>();
	    Map<Operand,IExpression> parsedExpressions = filteringSchema.getEventExpressions(event.getEventType());
	    for (Map.Entry<Operand, IExpression> element : parsedExpressions.entrySet())
        {
            Operand operand = element.getKey();            
            IExpression expression = element.getValue();
            logger.fine("evaluate: evaluating expression: "+expression);
            if (expression == null || (Boolean) expression.evaluate(event))
            {
                expressionEvalResults.add(operand);
                logger.fine("evaluate: expression: "+expression+" evaluated to TRUE");
            }
            
        }
		return expressionEvalResults;
	}
}
