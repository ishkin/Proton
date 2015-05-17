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

import java.util.ArrayList;
import java.util.List;

import com.ibm.hrl.proton.metadata.context.enums.ContextInitiatorPolicyEnum;
import com.ibm.hrl.proton.metadata.context.enums.ContextIntervalPolicyEnum;
import com.ibm.hrl.proton.metadata.context.enums.ContextTypeEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.ITemporalContextType;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.context.notifications.IContextNotification;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;

public class TemporalContextType extends ContextType implements ITemporalContextType {
	
	private static final long serialVersionUID = 1L;
	public static final String atStartupId = "startup";
	protected List<ContextInitiator> initiators;
	
    // can be single ContextRelativeTimeTerminator terminator
	protected List<ContextTerminator> terminators;
	 
	//protected List<ContextEventTerminator> eventTerminators;
	//protected List<ContextAbsoluteTimeTerminator> absoluteTimeTerminator;
	//protected ContextRelativeTimeTerminator relativeTimeTerminator;
	
	protected ContextIntervalPolicyEnum initiatorIntervalPolicy;
	
	
	public TemporalContextType(String name, List<ContextInitiator> initiators,
            List<ContextTerminator> terminators,
            ContextIntervalPolicyEnum initiatorIntervalPolicy,
            ContextIntervalPolicyEnum terminatorIntervalPolicy,
            boolean atStartUp, boolean neverEnding)
    {
        super(name,ContextTypeEnum.TEMPORAL_INTERVAL);
        if (initiators == null)
        {
        	this.initiators = new ArrayList<ContextInitiator>();
        }else
        {
        	this.initiators = new ArrayList<ContextInitiator>(initiators);
        }
        
        if (terminators == null)
        {
        	this.terminators = new ArrayList<ContextTerminator>();
        }else
        {
        	this.terminators = new ArrayList<ContextTerminator>(terminators);
        }        
        this.initiatorIntervalPolicy = initiatorIntervalPolicy;
        this.terminatorIntervalPolicy = terminatorIntervalPolicy;
        this.atStartUp = atStartUp;
        this.neverEnding = neverEnding;
        
        if (atStartUp && !this.initiators.isEmpty()) {
        	// TODO throw exception - context with startup==true should not have initiators        	
        }
        
        if (neverEnding && !this.terminators.isEmpty()) {
        	// TODO throw exception - context with neverending==true should not have terminators        	
        }        
    }
	
    protected ContextIntervalPolicyEnum terminatorIntervalPolicy;

	protected boolean atStartUp; // can come with event initiators
	protected boolean neverEnding; // forces empty terminators
	
	
	public boolean startsAtSystemStartup() {
		return atStartUp;
	}
	
	@Override
	public List<ContextAbsoluteTimeInitiator> getAbsoluteTimeInitiators() {
		List<ContextAbsoluteTimeInitiator> list = new ArrayList<ContextAbsoluteTimeInitiator>();
		
		for (ContextInitiator initiator: initiators) {
			if (initiator instanceof ContextAbsoluteTimeInitiator) {
				list.add((ContextAbsoluteTimeInitiator)initiator);
			}
		}		
		return list;
	}
		
	@Override
	public List<ContextEventInitiator> getEventInitiators() {
		List<ContextEventInitiator> list = new ArrayList<ContextEventInitiator>();
		
		if (initiators != null)
		{
		    for (ContextInitiator initiator: initiators) {
	            if (initiator instanceof ContextEventInitiator) {
	                list.add((ContextEventInitiator)initiator);
	            }
	        } 
		}
				
		return list;
	}
	
	@Override
	public List<ContextEventTerminator> getEventTerminators() {
		List<ContextEventTerminator> list = new ArrayList<ContextEventTerminator>();
		
		if (terminators != null){
		    for (ContextTerminator terminator: terminators) {
	            if (terminator instanceof ContextEventTerminator) {
	                list.add((ContextEventTerminator)terminator);
	            }
	        } 
		}
				
		return list;
	}

	@Override
	public List<ContextAbsoluteTimeTerminator> getAbsoluteTimeTerminators() {
		List<ContextAbsoluteTimeTerminator> list = new ArrayList<ContextAbsoluteTimeTerminator>();
		
		for (ContextTerminator terminator: terminators) {
			if (terminator instanceof ContextAbsoluteTimeTerminator) {
				list.add((ContextAbsoluteTimeTerminator)terminator);
			}
		}		
		return list;
	}

	@Override
	public List<ContextRelativeTimeTerminator> getRelativeTimeTerminators() {
		List<ContextRelativeTimeTerminator> list = new ArrayList<ContextRelativeTimeTerminator>();
		
		for (ContextTerminator terminator: terminators) {
			if (terminator instanceof ContextRelativeTimeTerminator) {
				list.add((ContextRelativeTimeTerminator)terminator);
			}
		}		
		return list;
	}
		
	@Override
	public List<ContextTerminator> getTerminators() {
		return terminators; 	
	}
	
	@Override
	public boolean hasEventInititators() {
		for (ContextInitiator initiator: initiators) {
			if (initiator instanceof ContextEventInitiator) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean hasAbsoluteTimeInititators() {
		for (ContextInitiator initiator: initiators) {
			if (initiator instanceof ContextAbsoluteTimeInitiator) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean hasEventTerminators() {
		for (ContextTerminator terminator: terminators) {
			if (terminator instanceof ContextEventTerminator) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasAbsoluteTimeTerminators() {
		for (ContextTerminator terminator: terminators) {
			if (terminator instanceof ContextAbsoluteTimeTerminator) {
				return true;
			}
		}
		return false;
	}	
	
	@Override
	public boolean hasRelativeTimeTerminator() {
		for (ContextTerminator terminator: terminators) {
			if (terminator instanceof ContextRelativeTimeTerminator) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isNeverEnding() {
		return neverEnding;
	}
	@Override
	public boolean startsAtStartup() {
		return atStartUp;
	}

	@Override
	public List<ContextInitiator> getInitiators() {
		return initiators;
	}

    @Override
    public ContextIntervalPolicyEnum getInitiatorIntervalPolicy()
    {
        return initiatorIntervalPolicy;
    }

    @Override
    public ContextIntervalPolicyEnum getTerminatorIntervalPolicy()
    {
        return terminatorIntervalPolicy;
    }

	public ContextInitiatorPolicyEnum getInitiationPolicy(ITimedObject object) {

		assert (!initiators.isEmpty());
		
		for (ContextInitiator initiator: initiators) {

			if (object instanceof IEventInstance) {
				if  (initiator instanceof ContextEventInitiator) {
					if (((ContextEventInitiator)initiator).getEventType().equals(
							((EventInstance)object).getEventType())) {
						return initiator.getInitiationPolicy();
					}					
				}
			}
			if (object instanceof IContextNotification) {
				if  (initiator instanceof ContextAbsoluteTimeInitiator) {
					if (((ContextAbsoluteTimeInitiator)initiator).getId().toString().equals(
							((IContextNotification)object).getContextBoundId())) {
						return initiator.getInitiationPolicy();
					}					
				}
			}						
		}
		
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || this.getClass() != this.getClass()) return false;
		
		TemporalContextType otherContext = (TemporalContextType)other;		
		return (this.getId() == otherContext.getId());
	}

	/**
	 * @param eventType
	 */
	public IExpression getInitiatorPredicate(IEventType eventType) {		
		List<ContextEventInitiator> initiators = getEventInitiators();
		for (ContextEventInitiator initiator: initiators) {
			if (initiator.getEventType().equals(eventType)) {
				// found the required initiator, return its predicate
				return initiator.getParsedPredicate();
			}
		}
		return null;
	}

	/**
	 * @param eventType
	 * @return
	 */
	public IExpression getTerminatorPredicate(IEventType eventType) {
		List<ContextEventTerminator> terminators = getEventTerminators();
		for (ContextEventTerminator terminator: terminators) {
			if (terminator.getEventType().equals(eventType)) {
				// found the required initiator, return its predicate
				return terminator.getParsedPredicate();
			}
		}
		return null;
	}	

}
