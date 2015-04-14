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

package com.ibm.hrl.proton.epa.state;

import java.util.LinkedList;

import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

/**
 * <code>GeneralLifespanData</code> Lifespan Data for general lifespans.
 * 
 */

public class OperatorData extends AbstractOperatorData{
	/** This ID identifies the class upon serialization/deserialization
	 * and explicitly defining it allows deserialization to work properly
	 * even when the class has changed.
	 */
	public static final long serialVersionUID = 0;

   
    /**
     * A candidate list is a two dimensional array.  The list of operand i is candidateList[i][] where  operand i is the ith operand of the composed event.          Example: A candidate list of two operands operand 0: e1 with quantifier first operand 1: e2 with quantifier last opNum = 0           opNum = 1 quantifier = first   quantifier = last  +---------+         +---------+ |  HEAD   |         |  HEAD   |   +---------+         +---------+ |                   |      |                   | \|/                 \|/ +---------+         +---------+ |   e11   |         |   e23   |   +---------+         +---------+ n  |    /|\ v       n  |    /|\ v e  |     |  e       e  |     |  e x  |     |  r       x  |     |  r t \|/    |  p       t \|/    |  p +---------+         +---------+ |   e12   |         |   e22   |   +---------+         +---------+ n  |    /|\ v       n  |    /|\ v e  |     |  e       e  |     |  e x  |     |  r       x  |     |  r t \|/    |  p       t \|/    |  p +---------+         +---------+ |   e13   |         |   e21   |   +---------+         +---------+ /|\                 /|\      |                   | |                   | +---------+         +---------+ |  TAIL   |         |  TAIL   |   +---------+         +---------+ quantifier = first   quantifier = last           opNum = 0           opNum = 1         
     */
    LinkedList<IEventInstance> candidateList[];
   

    /**
     * Constructor
     * @param sit the situation definition
     */
    public OperatorData(int operandsNumber) {    	
        super();
        candidateList = new LinkedList[operandsNumber];
    }

    
	public void clearCandidate(int operandIndex){
		LinkedList<IEventInstance> candidate = candidateList[operandIndex]; 
    	if (candidate != null) candidate.clear();
    }
    
    public boolean checkCandidateExistence(int operandIndex)
    {       
    	return !(candidateList[operandIndex] == null);
    }
	


	public void initiateCandidate(int operandIndex) {	    
		if (candidateList[operandIndex] == null) {
			candidateList[operandIndex] = new LinkedList<IEventInstance>();	
		}
	}


	public void addLast(IEventInstance eventInstance, int operandIndex) {	   
		candidateList[operandIndex].addLast(eventInstance);
	}
	

	public void addFirst(IEventInstance IEventInstance, int operandIndex) {	   
		candidateList[operandIndex].addFirst(IEventInstance);
		
	}
	
	public LinkedList<IEventInstance> getCandidatesPerOp(int operandNumber)
	{
		return candidateList[operandNumber];
	}

	@Override
	public void clean() 
	{
		for (int i = 0; i < candidateList.length; i++) {
			clearCandidate(i);
		}
		
	}

	@Override
	// checks if all candidate lists are empty
	public boolean isEmpty() {
		for (LinkedList<IEventInstance> candidates: candidateList) {
			if (candidates != null && !candidates.isEmpty()) {
				return false;
			}
		}
		return true;		
	}
}
