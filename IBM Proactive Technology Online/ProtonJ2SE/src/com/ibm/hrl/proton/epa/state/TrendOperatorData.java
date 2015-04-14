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


public class TrendOperatorData extends AbstractOperatorData {

	public static final long serialVersionUID = 0;
	
    LinkedList<IEventInstance> candidateList;
    int operandsNumber;
    
    public TrendOperatorData(int numOfOperands) {
        super();
        candidateList = new LinkedList<IEventInstance>();
        operandsNumber = numOfOperands;
    }
    
    public int getOperandsNumber() {
    	return operandsNumber;
    }

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.AbstractOperatorData#addFirst
     * (com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance, int)
     */
    @Override
    public void addFirst(IEventInstance IEventInstance, int operandIndex)
    {        
    	System.out.println("this function should not be called for trend...");
    }

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.AbstractOperatorData#addLast
     * (com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance, int)
     */
    @Override
    public void addLast(IEventInstance eventInstance, int operandIndex) {
    	candidateList.addLast(eventInstance);    	
    }

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.AbstractOperatorData#checkCandidateExistence(int)
     */
    @Override
    public boolean checkCandidateExistence(int operandIndex) {
    	return (!(candidateList == null));
    }

    public void clearCandidates() {
    	candidateList.clear();
    }

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.AbstractOperatorData#clearCandidate(int)
     */
    @Override
    public void clearCandidate(int operandIndex) {
    	candidateList.clear();
    }
    
    public LinkedList<IEventInstance> getCandidates() {
		return candidateList;
	}

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.AbstractOperatorData#initiateCandidate(int)
     */
    @Override
    public void initiateCandidate(int operandIndex) {
		if (candidateList == null) {
			candidateList = new LinkedList<IEventInstance>();	
		}
    }

	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.epa.state.IOperatorData#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return (candidateList.isEmpty());	
	}

	@Override
	public void clean() {
		clearCandidates();
	}
	
}
