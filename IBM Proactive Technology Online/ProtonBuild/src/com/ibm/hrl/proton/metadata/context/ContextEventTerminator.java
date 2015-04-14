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
package com.ibm.hrl.proton.metadata.context;

import com.ibm.hrl.proton.metadata.context.enums.ContextTerminationTypeEnum;
import com.ibm.hrl.proton.metadata.context.enums.ContextTerminatorPolicyEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextEventBound;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

public class ContextEventTerminator extends ContextTerminator implements IContextEventBound {

	protected IEventType terminator;
	protected String predicate;
	protected IExpression parsedPredicate;

	
	public ContextEventTerminator(IEventType eventType,IExpression parsedCondition,
			String condition, ContextTerminatorPolicyEnum terminatorPolicy,
			ContextTerminationTypeEnum terminationType)
    {
		super(terminatorPolicy,terminationType);
        this.terminator=  eventType;
        this.predicate = condition;
        this.parsedPredicate = parsedCondition;        
    }
    
    /**
     * Return the event type of the initiator
     * @return
     */
    public IEventType getEventType(){
        return terminator;
    }
    
    public IEventType getTerminatorType(){
        return terminator;
    }
    
    /**
     * Return the event type name
     * @return
     */
    public String getEventTypeName()
    {
        return getEventType().getTypeName();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "Context event terminator of type: "+getEventTypeName();
    }

    @Override
	public IExpression getParsedPredicate() {
		return parsedPredicate;
	}

    @Override
	public String getPredicate() {
		return predicate;
	}
	
}
