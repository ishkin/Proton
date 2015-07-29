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
package com.ibm.hrl.proton.metadata.epa;

import java.io.Serializable;

import com.ibm.hrl.proton.metadata.event.IEventType;

public class Operand implements Comparable,Serializable {

	private int operandIndex; // Operand index
	private IEventType eventType; // Event Type
	private String alias = null; //in case we would like to store the alias name
	
	public Operand(int operandIndex, IEventType eventType) {
		this.operandIndex = operandIndex;
		this.eventType = eventType;
	}

	
	
	public String getAlias() {
		return alias;
	}

	public int getOperandIndex() {
		return operandIndex;
	}

	public IEventType getEventType() {
		return eventType;
	}
	
	@Override
	public boolean equals(Object o) {

	    if(this == o)
	        return true;
	    if((o == null) || (o.getClass() != this.getClass()))
	        return false;
	    // object must be Operand at this point
	    Operand otherOperand = (Operand)o;
	    return operandIndex == otherOperand.getOperandIndex()&&
	    (eventType == otherOperand.getEventType() || (eventType != null && eventType.equals(otherOperand.getEventType())));


	}
	
	@Override
	public int hashCode() {
	    int hash = 5;
	    hash = 31 * hash + operandIndex;
	    hash = 31 * hash + (null == eventType ? 0 : eventType.hashCode());
	    return hash;
	}

	
	@Override
	public int compareTo(Object o) {		 	
		    if((o == null) || (o.getClass() != this.getClass()))
		       throw new ClassCastException();
		    // object must be Operand at this point
		    Operand otherOperand = (Operand)o;
		    int otherOperandIndex = otherOperand.getOperandIndex();
		    if (operandIndex < otherOperandIndex) return -1;
		    if (operandIndex == otherOperandIndex) return 0;
		    return 1;
	}
	
	
}
