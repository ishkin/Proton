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
package com.ibm.hrl.proton.epa.simple.abstractOperators;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.hrl.proton.epa.interfaces.IStandardProcessingFunction;
import com.ibm.hrl.proton.epa.state.IOperatorData;
import com.ibm.hrl.proton.epa.state.OperatorData;
import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.StatefulEventProcesingAgentType;
import com.ibm.hrl.proton.metadata.epa.enums.CardinalityPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.ConsumptionPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.InstanceSelectionPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IMatchingSchema;
import com.ibm.hrl.proton.metadata.epa.schemas.StandardMatchingSchema;
import com.ibm.hrl.proton.runtime.epa.IState;
import com.ibm.hrl.proton.runtime.epa.MatchingSets;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

public abstract class AbstractStandardOperator implements IStandardProcessingFunction {

	protected IOperatorData operatorData;
    protected StatefulEventProcesingAgentType agentType;
	
    public AbstractStandardOperator()
    {
        matchingSets = new MatchingSets();
    }
	
	/**
	 * The candidates that are marked for consumption
	 */
	protected List<IEventInstance>[] consumeCandidates;
	
	/**
	 * The matched sets ready for derivation 
	 */
	protected MatchingSets matchingSets;
	
	/** Selected candidates for pattern matching- but only the event instances, without indexes */
	protected IEventInstance[] instances;

		
	

	@Override
	public IState getInternalState() {
		return operatorData;
	}

	@Override
	public  MatchingSets match()
	{
		compose(); //runs the matching logic and returns matching sets
	    MatchingSets returnedMatchingSets =  new MatchingSets(matchingSets);
	    clear();  //clear internal data structures for next matching round
	    
	    return returnedMatchingSets;
	}

	

	public IMatchingSchema getMatchingSchema()
	{
		return agentType.getMatchingSchema();
	}
	

	
	/**
     * Clear lifespan data's instances and candidates.
     */
    public final void clear () {  
        for (int i=0 ; i < agentType.getNumOfOperands(); i++)
        {
            instances[i] = null;
            consumeCandidates[i] = null;
        }        
        matchingSets.clear();
        
       
    }
    
	/**
     * Consume instances that were used in last situation composition.
     * These instances are in consumedCandidates set.
     */
    protected void consume() 
    {
        
        for (int i = 0; i < consumeCandidates.length; i++)
        {
            if (consumeCandidates[i] != null &&
            		((OperatorData)operatorData).getCandidatesPerOp(i) != null)
            {
            	((OperatorData)operatorData).getCandidatesPerOp(i).removeAll(consumeCandidates[i]);
            }
        }

        
    } 
    
    public LinkedList<IEventInstance> getCandidatesPerOp(int operandNumber) {
    	return new LinkedList<IEventInstance>();
    }
    
	/**
	 * TODO: usually the default value is true
	 * @return
	 */
	protected abstract boolean operatorCompose(); 

	/**
	 * 
	 * Calls the recursive process() method which iterates over all the operands and candidate instances for those operands, 
	 * and according to the policies creates matching sets. If the process finds at least one matching set and the cardinality policy is SINGLE
	 * we make sure this agent will not perform matching again by setting a flag in the state object.
	 * If the consumption policy is CONSUME we also delete the instances which already participated in matching set.
	 */
	protected void compose() 
	{
        int opNum = -1;
    
		if (process(opNum) && operatorCompose()) 
		{			           
            if (agentType.getCardinality().equals(CardinalityPolicyEnum.SINGLE))
            {
                
                /* situation composed - cardinality is single - we need to set a flag indicating that no more matching will be performed */                 
               operatorData.setReject(true);
            } else {
                
                /* if cardinality policy is not SINGLE - we need for future matching to consume the instances that were marked for consumption
                 * during the processing phase (consume instances- delete them from operator's internal state */
                consume();
            }            
        } 
		
		
	}
	
	/**
	 * The main processing function  - recursively iterate over the candidate list and 
	 * @return
	 */
	protected boolean process(int opNum)
	 {                
	      
	        Boolean returnValueBool = false;
	        
	        boolean processValue = false;
	        
	        opNum++;
	        
	        /* recursion stop condition */
	        if(opNum == agentType.getNumOfOperands()) 
	        {
	        	opNum--;
	        	
	        	IExpression parsedAssertion =  agentType.getParsedAssertion();
	        	List<IEventInstance> instancesAsList = Arrays.asList(instances);
	        	//calculate the value based on assertion - if such is defined
	        	if (null == parsedAssertion)
	        	{
	        	    returnValueBool = true;
	        	}
	        	else
	        	{
	        	    returnValueBool = (Boolean) parsedAssertion.evaluate(instancesAsList); // situation where condition
	        	}
	        	
	        	if (returnValueBool == null) 
	        	{
	        	    returnValueBool = false;
	        	}
	        	

	        	if (returnValueBool) 
	        	{

	        		matchingSets.addMatchingSet(instancesAsList);
        	    
	        	    //consuming the instances in case of appropriate non-retention policy	               
	                for (int i=0; i < agentType.getNumOfOperands(); i++) 
	                {
	                	Operand operand = agentType.getEventInputOperand(i);
	                	ConsumptionPolicyEnum consumptionPolicy = agentType.getMatchingSchema().getConsumption(operand);

	                	IEventInstance instanceForOperand = instances[i];
	                	if (consumptionPolicy.equals(ConsumptionPolicyEnum.CONSUME) &&  instanceForOperand != null)
	                	{
	                	    
	                	    List<IEventInstance> operandConsumedInstances = consumeCandidates[i];
	                	    if (operandConsumedInstances == null)
	                	    {
	                	        operandConsumedInstances = new LinkedList<IEventInstance>(); //TODO : supposed to be set
	                	        consumeCandidates[i]=operandConsumedInstances; 
	                	    }
	                		operandConsumedInstances.add(instanceForOperand);
	                	}
	                }
	        	}
	        return returnValueBool;
	        }
	        
	        /* recursion step */ //on the operand index 
	        ICandidateIterator candIter = getCandidateIterator(opNum);	       
	        

	        IEventInstance instance = null;
	        do {
	        	if (instance != null || candIter.hasNext()) 
	        	{
	        	    instance = candIter.next();
	        	}
	            	            
	            if (instance == null)
	            {
	            	instances[opNum]= null;
	            }else{
	            	instances[opNum]= instance;
	            }
	            
	            if (checkCandidates(opNum)) 
	            {
	                processValue = process(opNum);
	                
	                returnValueBool = returnValueBool || processValue;
	                //if found a match and instance selection policy is not EVERY stop trying to find another match
	                if (processValue && (agentType.getMatchingSchema().getRepeatedType(agentType.getEventInputOperand(opNum)) != InstanceSelectionPolicyEnum.EVERY)) 
	                {
	                	break;
	                }
	            }    
	            
	           
	        } while (candIter.hasNext());
	        
	        opNum--;
	        return returnValueBool;
	    }
	 
	 
	/**
	 * Relevant for joining operators - All, Sequence - they should check that this operand is not null
	 * @return
	 */
	protected boolean checkCandidates(int opNum) 
	{
		return true;
	}

	@Override
	//Get all the operands whose type matches this instance, iterate over all the operands
	//and do something
	public boolean addInstancetoState(IEventInstance eventInstance,Set<Operand> filteringResults) {
		//checking what to do according to cardinality policy - perhaps need not perform any processing since already detected the situation
	    //TODO: should optimize this - this data structure should be on EPA instance level, not operator level - we do not want to run the filter either
		if (operatorData.isReject()) return false;
		
		for (Operand operand : filteringResults) 
		{
			//only if the threshold condition on this operand is true
		    //TODO: can only pass the operands which are evaluated to true
			
			    //if the instance should be overriden than prepare the state (clear it)
				if (checkOverride(eventInstance, operand)) 
				{
					clearingForOverride(operand);
				}
				
				addInstance(eventInstance,operand);
			
			
		}
		
		return true;
	}

	/**
	 * Add event instance to the candidate list of the specified operand
	 * @param eventInstance
	 * @param operand
	 */
	protected void addInstance(IEventInstance eventInstance, Operand operand) {
	        int operandIndex = operand.getOperandIndex();
	        addInstanceByPolicy(eventInstance,operand);
		
	}
	
	/**
	 * Add instance by instance selection policy -add it as LAST or FIRST candidate 
	 * @param candidate
	 * @param operand
	 */
	protected void addInstanceByPolicy(IEventInstance candidate,Operand operand)
	{
		StandardMatchingSchema  matchingSchema = (StandardMatchingSchema)getMatchingSchema();
		InstanceSelectionPolicyEnum repeatedTypePolicy = matchingSchema.getRepeatedType(operand);
		if (repeatedTypePolicy.equals(InstanceSelectionPolicyEnum.LAST)){
			addCandidateLast(candidate, operand);
		}else{
			addCandidateFirst(candidate, operand);
		}
	}

	
	/**
	 * If candidate list for the specified operand exist clear it
	 * @param operand
	 */
	protected void clearingForOverride(Operand operand) {
	    int operandIndex = operand.getOperandIndex();
		if (operatorData.checkCandidateExistence(operandIndex)) {    
        	operatorData.clearCandidate(operandIndex);
    	}
		
	}
	
	/**
	 * The instance selection policy is FIRST - the last instance that arrived is added at the end of the list
	 * @param candidate
	 * @param operand
	 */
	 protected void addCandidateFirst(final IEventInstance candidate, final Operand operand) {
	    	
		 int operandIndex = operand.getOperandIndex();		 
		 if (!operatorData.checkCandidateExistence(operandIndex)) {
				operatorData.initiateCandidate(operandIndex);
		 }
		 operatorData.addLast(candidate,operandIndex);	
		 
	 }
	 
	 /**
	  * The instance selection policy is LAST - choosing the last instance that arrived when matching - add it as first in the candidate list
	  * @param candidate
	  * @param operand
	  */
	 protected void addCandidateLast(final IEventInstance candidate, final Operand operand) {
		 int operandIndex = operand.getOperandIndex();
		 if (!operatorData.checkCandidateExistence(operandIndex)) {
				operatorData.initiateCandidate(operandIndex);
		 }
		 operatorData.addFirst(candidate,operandIndex);	
	 }
	
	

	 /**
	  * Check if the event instance is supposed to override existing state instances for the same operand.
	  * @param eventInstance
	  * @param operand
	  * @return
	  */
	protected  boolean checkOverride(IEventInstance eventInstance, Operand operand)
	{
	    StandardMatchingSchema matchingSchema = (StandardMatchingSchema)getMatchingSchema();
		InstanceSelectionPolicyEnum repeatedTypePolicy = matchingSchema.getRepeatedType(operand);
		if (repeatedTypePolicy.equals(InstanceSelectionPolicyEnum.OVERRIDE)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return An iterator over this operand's candidates
	 */
	protected ICandidateIterator getCandidateIterator(int operatorNumber) 
	{
		return new GeneralCandidateIterator(((OperatorData)operatorData).getCandidatesPerOp(
				operatorNumber));
	}
	
	
	public void setOperatorData(IOperatorData operatorData)
    {
	    this.operatorData = operatorData;
    }

    public void setAgentType(StatefulEventProcesingAgentType agentType)
    {
        this.agentType = agentType;
        instances = new IEventInstance[agentType.getNumOfOperands()];
        consumeCandidates = new List[agentType.getNumOfOperands()];
    }
    
	/**
     * <code>GeneralCandidateIterator</code>.
     * 
     * @author yonit
     * 
     */
    public class GeneralCandidateIterator implements ICandidateIterator {
    	private Iterator<IEventInstance> candsIter;
    	
    	/**
    	 * Constructor
    	 * @param candidatesList
    	 */
    	public GeneralCandidateIterator(List<IEventInstance> candidatesList) {
    		if (candidatesList != null) {
    			candsIter = candidatesList.iterator();
    		} else {
    			candsIter = Collections.EMPTY_LIST.iterator();
    		}
    	}
    	
    	/* (non-Javadoc)
		 * @see com.ibm.amit.situationManager.runtimeData.ICandidateIterator#hasNext()
		 */
		public boolean hasNext() {
			return candsIter.hasNext();
		}
		
		/* (non-Javadoc)
		 * @see com.ibm.amit.situationManager.runtimeData.ICandidateIterator#next()
		 */
		public IEventInstance next() {
			return (IEventInstance)candsIter.next();
		}
    }
    
}
