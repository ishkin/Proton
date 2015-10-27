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
package com.ibm.hrl.proton.metadata.context;

import com.ibm.hrl.proton.metadata.context.enums.ContextInitiatorPolicyEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextEventBound;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

public class ContextEventInitiator extends ContextInitiator implements IContextEventBound {

    
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected IEventType initiator;
	protected String predicate;
	protected IExpression parsedPredicate;
	

	public ContextEventInitiator(IEventType eventType,IExpression parsedCondition,
			String condition, ContextInitiatorPolicyEnum initiatorPolicy)
    {	
		super();
        this.initiator = eventType;
        this.predicate = condition;
        this.parsedPredicate = parsedCondition;
        this.initiatorPolicy = initiatorPolicy;
    }
    
    /**
     * Return the event type of the initiator
     * @return
     */
    public IEventType getEventType(){
       return initiator;
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
        return "Context event initiator of type: "+getEventTypeName();
    }

	@Override
	public IEventType getInitiatorType() {
		return initiator;
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
