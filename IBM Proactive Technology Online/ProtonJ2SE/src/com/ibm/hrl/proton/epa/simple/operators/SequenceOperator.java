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

package com.ibm.hrl.proton.epa.simple.operators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.hrl.proton.epa.simple.abstractOperators.AbstractStandardOperator;
import com.ibm.hrl.proton.epa.state.SequenceOperatorCandidate;
import com.ibm.hrl.proton.epa.state.SequenceOperatorData;
import com.ibm.hrl.proton.epa.state.SequenceOperatorData.SequenceCandidateIterator;
import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.StatefulEventProcesingAgentType;
import com.ibm.hrl.proton.metadata.epa.enums.ConsumptionPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.InstanceSelectionPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.schemas.StandardMatchingSchema;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

/**
 * <code>SequenceOperator</code>.
 * 
 * 
 */
public class SequenceOperator extends AbstractStandardOperator {
	
	private SequenceOperatorCandidate sequenceInstances[];
	private SequenceOperatorCandidate sequenceConsumedCandidates[];	
		
	/*public SequenceOperator(StatefulEventProcesingAgentType agentType) {
		super();
		sequenceInstances = new SequenceOperatorCandidate[agentType.getNumOfOperands()];
		sequenceConsumedCandidates = new SequenceOperatorCandidate[agentType.getNumOfOperands()];
		setOperatorData(new SequenceOperatorData(agentType.getNumOfOperands()));
		setAgentType(agentType);
	}*/
    
	@Override
	public void setAgentType(StatefulEventProcesingAgentType agentType) {		
		super.setAgentType(agentType);		
		//setOperatorData(new SequenceOperatorData(agentType.getNumOfOperands()));
		sequenceInstances = new SequenceOperatorCandidate[agentType.getNumOfOperands()];
		sequenceConsumedCandidates = new SequenceOperatorCandidate[agentType.getNumOfOperands()];
	}
	
	@Override
    protected void consume() {
        for (int i = 0; i < sequenceConsumedCandidates.length; i++) {
            if (sequenceConsumedCandidates[i] != null) {
            	if (((SequenceOperatorData)operatorData).getCandidatesPerOp(i) != null) {
                	// handle all the required links
            		LinkedList<SequenceOperatorCandidate> candidates =
            			((SequenceOperatorData)operatorData).getCandidatesPerOp(i);
            		int candidateIndex = candidates.indexOf(sequenceConsumedCandidates[i]);
            		
            		handleCandidateLinks(sequenceConsumedCandidates[i],i,candidateIndex);            		
            		
            		((SequenceOperatorData)operatorData).getCandidatesPerOp(i).remove(
            				sequenceConsumedCandidates[i]);
            	}            		
            }
        }
    } 
    
	private void handleCandidateLinks(SequenceOperatorCandidate candidate,
			int operandIndex,int candidateIndex) {

		LinkedList<SequenceOperatorCandidate> candidates = 
    			((SequenceOperatorData)operatorData).getCandidatesPerOp(operandIndex);
		
		// if its not the very first candidates queue
		if (operandIndex > 0 && candidate.previousLinkedCandidate != null) {
			// next consecutive candidate - can be null or existing one
			SequenceOperatorCandidate newNextConsecutiveCandidate = null;
			if (candidateIndex < candidates.size()-1) {
				newNextConsecutiveCandidate = candidates.get(candidateIndex+1); 
			}
			
			LinkedList<SequenceOperatorCandidate> prevCandidates = 
    			((SequenceOperatorData)operatorData).getCandidatesPerOp(operandIndex-1);
			for (int i = prevCandidates.indexOf(candidate.previousLinkedCandidate);
				i < prevCandidates.size() && prevCandidates.get(i).nextConsequentCandidate ==
					candidate; i++) {

				prevCandidates.get(i).nextConsequentCandidate =
					newNextConsecutiveCandidate;
			}

			if (newNextConsecutiveCandidate != null) {
				newNextConsecutiveCandidate.previousLinkedCandidate =
					candidate.previousLinkedCandidate;
			}
		}
		
		// if its not the very last participant queue		
		if (operandIndex < agentType.getNumOfOperands()-1 && candidate.nextConsequentCandidate != null) {
			// if the next consecutive candidate points back at the consumed event - 
			// put null there (consumed event was the first one to point at it...), otherwise
			// do nothing (some earlier event point at it as consecutive)
			if (candidate.nextConsequentCandidate.previousLinkedCandidate == candidate) {				
				candidate.nextConsequentCandidate.previousLinkedCandidate = null;
				if (candidateIndex < candidates.size()-1) {
					SequenceOperatorCandidate oneAfterCand = candidates.get(candidateIndex+1);
					if (oneAfterCand.nextConsequentCandidate == candidate.nextConsequentCandidate ) {
						candidate.nextConsequentCandidate.previousLinkedCandidate =
							oneAfterCand;
					}
				}				
			}
		}
	}

	@Override
    protected boolean operatorCompose() {        
        return true;
    }

    @Override
    protected boolean process(int operand) {
    	
    	// this function looks for first/last matching set
    	// updates AbstractStandardOperator local data members: matchingSets, consumeCandidates
    	// operand is initially passed as -1, and is incremented at each iteration
    	
    	findMatchingCombination(0,null);
    	
    	return (matchingSets.isPatternDetected());
    }
	 
	private void findMatchingCombination(int operand, SequenceOperatorCandidate previous) {
		// make sure matchingSets is cleared before process() invocation
		if (matchingSets.isPatternDetected() && !checkIfToContinue()) 
		{ // recursion possible exit point
			return;
		}
		if (operand == ((SequenceOperatorData)operatorData).getOperandsNumber()) {
			testSingleCombination(sequenceInstances);			
		}
		else {			
			// get SequenceCandidateIterator for current operand
			LinkedList<SequenceOperatorCandidate> candidates =
				((SequenceOperatorData)operatorData).getCandidatesPerOp(operand);
			InstanceSelectionPolicyEnum policy = agentType.getMatchingSchema().getRepeatedType(
					agentType.getEventInputOperand(operand));
			SequenceCandidateIterator iterator =
				((SequenceOperatorData)operatorData).new SequenceCandidateIterator(
					candidates,previous,operand,policy);
			
			while (iterator.hasNext()) {
				sequenceInstances[operand] = iterator.next();
				// invoke findMatchingCombination with next operand and current instance
				findMatchingCombination(operand+1,sequenceInstances[operand]);				
			}			
		}
	}

	private boolean checkIfToContinue() {
		//iterate over the operands, if for one of them the consumption policy is REUSE and the instance selection
		//policy is EVERY that means there might be more matches, continue to check
		for (int i=0; i< ((SequenceOperatorData)operatorData).getOperandsNumber();i++)
		{
			Operand operand = agentType.getEventInputOperand(i);
			InstanceSelectionPolicyEnum instanceSelectionPolicy = agentType.getMatchingSchema().getRepeatedType(operand);
			ConsumptionPolicyEnum consumptionPolicy = agentType.getMatchingSchema().getConsumption(operand);
			if (instanceSelectionPolicy.equals(InstanceSelectionPolicyEnum.EVERY) && consumptionPolicy.equals(ConsumptionPolicyEnum.REUSE))
				return true;
		}
		
		return false;
	}

	private boolean testSingleCombination(SequenceOperatorCandidate[] instances) {

		IExpression parsedAssertion =  agentType.getParsedAssertion();
    	List<IEventInstance> instancesAsList = new ArrayList<IEventInstance>();
    	
    	boolean detected = false;
    	for (SequenceOperatorCandidate candidate: instances) {
    		if (candidate == null) { // candidate(s) are missing
    			return false;
    		}
    		instancesAsList.add(candidate.getEventInstance());
    	}
    	if (parsedAssertion == null) {
    		detected = true;
    	} else {
    		// evaluate pattern assertion
    		detected = (Boolean)parsedAssertion.evaluate(instancesAsList);
    	}
    	
    	if (detected) {
    		matchingSets.setPatternDetected(true);
    		matchingSets.addMatchingSet(instancesAsList);    		
    	    // consuming the instances in case of appropriate consumption policy	               
            for (int i=0; i < agentType.getNumOfOperands(); i++) {
            	Operand operand = agentType.getEventInputOperand(i);
            	ConsumptionPolicyEnum consumptionPolicy =
            		agentType.getMatchingSchema().getConsumption(operand);

            	IEventInstance instanceForOperand = instances[i].getEventInstance();
            	if (consumptionPolicy.equals(ConsumptionPolicyEnum.CONSUME) && instanceForOperand != null) {            	    
            		sequenceConsumedCandidates[i]=instances[i]; 
            	}
            }
    	}
    	return detected;		
	}

	@Override
	// will work with IOperatorData...
	public boolean addInstancetoState(IEventInstance eventInstance,Set<Operand> filteringResults) {
		if (operatorData.isReject()) return false;
		
		for (Operand operand : filteringResults) 
		{
			//only if the threshold condition on this operand is true
		    //TODO: can only pass the operands which are evaluated to true
			
		    //if the instance should be overridden than prepare the state (clear it)
			if (checkOverride(eventInstance,operand)) 
			{
				clearingForOverride(operand);
			}				
			addInstance(eventInstance,operand);
		}		
		return true;
	}
	
	protected void addInstance(IEventInstance eventInstance,Operand operand) {
        //int operandIndex = operand.getOperandIndex();
        addInstanceByPolicy(eventInstance,operand);	
	}
	
	protected void addInstanceByPolicy(IEventInstance candidate,Operand operand) {
		StandardMatchingSchema matchingSchema = (StandardMatchingSchema)getMatchingSchema();
		InstanceSelectionPolicyEnum repeatedTypePolicy = matchingSchema.getRepeatedType(operand);
		if (repeatedTypePolicy.equals(InstanceSelectionPolicyEnum.LAST)){
			addCandidateLast(candidate,operand);
		} else { // it is "first" policy
			addCandidateFirst(candidate,operand);
		}
	}
	
	protected void addCandidateFirst(IEventInstance candidate, Operand operand) {	    	
		int operandIndex = operand.getOperandIndex();		 
		if (!operatorData.checkCandidateExistence(operandIndex)) {
			operatorData.initiateCandidate(operandIndex);
		}
		// we are maintaining the same data structure for first and last policy
		// in case it is last policy, the list of candidates to traverse should be inverted
		// we have iterator that returns relevant candidates inside recursion,
		// it will differ for first and last policies
		
		operatorData.addLast(candidate,operandIndex);
	 }
	 
	 protected void addCandidateLast(final IEventInstance candidate, final Operand operand) {
		int operandIndex = operand.getOperandIndex();
		if (!operatorData.checkCandidateExistence(operandIndex)) {
			operatorData.initiateCandidate(operandIndex);
		}
		operatorData.addLast(candidate,operandIndex);	
	 }

	@Override
	public boolean determineTerminationInstantiation() {
		return false;
	}
	
}
