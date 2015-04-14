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

import com.ibm.hrl.proton.metadata.epa.enums.InstanceSelectionPolicyEnum;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

/**
 * <code>SequenceOperatorData</code>.
 * 
 * 
 */
public class SequenceOperatorData extends AbstractOperatorData
{
	public static final long serialVersionUID = 0;
	
    LinkedList<SequenceOperatorCandidate> candidateList[];
    int operandsNumber;
    
    /**
     * @param numOfOperands
     */
    public SequenceOperatorData(int numOfOperands) {
        super();
        candidateList = new LinkedList[numOfOperands];
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
    	System.out.println("this function should not be called for sequence...");
    }

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.AbstractOperatorData#addLast
     * (com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance, int)
     */
    @Override
    public void addLast(IEventInstance eventInstance, int operandIndex) {
    	// create SequenceOperatorCandidate from event instance, add last and update links
    	SequenceOperatorCandidate candidate = new SequenceOperatorCandidate(eventInstance); 
    	
    	if (operandIndex > 0) 
    	{
    		LinkedList<SequenceOperatorCandidate> prevCandidates = candidateList[operandIndex-1];
    		if (prevCandidates != null)
    		{
	    		for (int i=prevCandidates.size()-1; i>= 0 &&
	    			prevCandidates.get(i).nextConsequentCandidate == null; i--) {
	    			prevCandidates.get(i).nextConsequentCandidate = candidate;
	    			candidate.previousLinkedCandidate = prevCandidates.get(i); 
	    		}
    		}
    		
    	}    	    	
    	candidateList[operandIndex].addLast(candidate);    	
    }

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.AbstractOperatorData#checkCandidateExistence(int)
     */
    @Override
    public boolean checkCandidateExistence(int operandIndex) {
    	return (!(candidateList[operandIndex] == null));
    }

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.AbstractOperatorData#clearCandidate(int)
     */
    @Override
    public void clearCandidate(int operandIndex) {
    	if (candidateList[operandIndex] != null) {
        	candidateList[operandIndex].clear();	
    	}
    }

    public LinkedList<SequenceOperatorCandidate> getCandidatesPerOp(int operandNumber) {
		return candidateList[operandNumber];
	}

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.AbstractOperatorData#initiateCandidate(int)
     */
    @Override
    public void initiateCandidate(int operandIndex) {
		if (candidateList[operandIndex] == null) {
			candidateList[operandIndex] = new LinkedList<SequenceOperatorCandidate>();	
		}
    }

	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.epa.state.IOperatorData#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		for (LinkedList<SequenceOperatorCandidate> candidates: candidateList) {
			if (!candidates.isEmpty()) {
				return false;
			}
		}
		return true;		
	}

	@Override
	public void clean() {
		for (int i = 0; i < candidateList.length; i++) {
			clearCandidate(i);
		}
	}
	
	public class SequenceCandidateIterator implements ICandidateIterator {

		private boolean isFirstPolicy;
		private SequenceOperatorCandidate currentCandidate;
		private LinkedList<SequenceOperatorCandidate> candidates;
		private int earliestAllowedCandidateIndex;
		private int currentCandidateIndex;
		
		public SequenceCandidateIterator(LinkedList<SequenceOperatorCandidate> list,
				SequenceOperatorCandidate previous, int currentOperand,
				InstanceSelectionPolicyEnum policy) {
					
			candidates = list;
			isFirstPolicy = true;
			if (policy == InstanceSelectionPolicyEnum.LAST) {
				isFirstPolicy = false;
			}			

			currentCandidate = null;
			if (candidates != null && !candidates.isEmpty()) { // candidates list is not empty
				if (previous == null) { // it is the very first candidates list
					currentCandidate = (isFirstPolicy? list.getFirst() : list.getLast());
				} else { // previous != null
					if (isFirstPolicy) {
						currentCandidate = previous.nextConsequentCandidate;
					} else if (previous.nextConsequentCandidate != null) { // last policy
						currentCandidate = candidates.getLast();
						earliestAllowedCandidateIndex = candidates.indexOf(previous.nextConsequentCandidate);
					} else { // last policy and previous.nextConsequentCandidate==null
						currentCandidate = null;
					}
				}
			}
						
			currentCandidateIndex = -1;
			if (currentCandidate != null) {
				// SequenceOperatorCandidate implements equals()
				currentCandidateIndex = candidates.indexOf(currentCandidate);
			}			
			//earliestAllowedCandidateIndex = currentCandidateIndex; 
			
		}
		
		/* (non-Javadoc)
		 * @see com.ibm.hrl.proton.epa.state.ICandidateIterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return (currentCandidate != null);
		}

		/* (non-Javadoc)
		 * @see com.ibm.hrl.proton.epa.state.ICandidateIterator#next()
		 */
		@Override
		public SequenceOperatorCandidate next() {
			SequenceOperatorCandidate returnedCandidate = currentCandidate;
			if (returnedCandidate != null) {
				// set the new current candidate
				if (isFirstPolicy) {
					if (currentCandidateIndex < candidates.size()-1) {
						currentCandidate = candidates.get(currentCandidateIndex+1);
						currentCandidateIndex++;					
					}
					else {
						currentCandidateIndex = -1;
						currentCandidate = null;
					}
				}
				if (!isFirstPolicy) { // last policy
					if (currentCandidateIndex > earliestAllowedCandidateIndex &&
							currentCandidateIndex > 0) {
						currentCandidate = candidates.get(currentCandidateIndex-1);
						currentCandidateIndex--;
					}
					else {
						currentCandidateIndex = -1;
						currentCandidate = null;
					}
				}			
			}
			return returnedCandidate;
		}		
	}

}
