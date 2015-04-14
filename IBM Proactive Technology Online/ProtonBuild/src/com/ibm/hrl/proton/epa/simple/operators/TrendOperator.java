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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.hrl.proton.epa.simple.abstractOperators.AbstractStandardOperator;
import com.ibm.hrl.proton.epa.state.TrendOperatorData;
import com.ibm.hrl.proton.metadata.computedVariable.ComputedVariableType;
import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.StatefulEventProcesingAgentType;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.epa.enums.ConsumptionPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.TrendRelationEnum;
import com.ibm.hrl.proton.metadata.epa.schemas.TrendMatchingSchema;
import com.ibm.hrl.proton.runtime.computedVariable.ComputedVariableInstance;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

public class TrendOperator extends AbstractStandardOperator {
	
	LinkedList<IEventInstance> trendConsumedCandidates;
	LinkedList<IEventInstance> mockMatchingSet;
	
	/*public TrendOperator(StatefulEventProcesingAgentType agentType) {
		super();
		setOperatorData(new TrendOperatorData(agentType.getNumOfOperands()));
		trendConsumedCandidates = new LinkedList<IEventInstance>();
		setAgentType(agentType);
	}*/
	
	@Override
	public void setAgentType(StatefulEventProcesingAgentType agentType) {		
		super.setAgentType(agentType);		
		trendConsumedCandidates = new LinkedList<IEventInstance>();
		mockMatchingSet = new LinkedList<IEventInstance>();
		for (int i=0; i < agentType.getNumOfOperands(); i++){
			mockMatchingSet.add(new EventInstance(agentType.getEventInputOperand(i).getEventType(),new HashMap<String,Object>()));
		}
			
	}
    
	@Override
    protected void consume() {
    	TrendOperatorData internalSate = (TrendOperatorData)getInternalState();
        for (int i = 0; i < trendConsumedCandidates.size(); i++) {
        	int candidateIndex = internalSate.getCandidates().indexOf(trendConsumedCandidates.get(i));
        	internalSate.getCandidates().remove(candidateIndex);
        }
        // clean the trendConsumedCandidates for next invocation
        trendConsumedCandidates.clear();
    } 
	
    @Override
    protected boolean process(int operandIndex) {
    	
    	// this function takes the entire internal state and checks if there exists a trend
    	// each event type is tested against its expression, in case there is a trend -
    	// the entire internal state is reported as matching set
    	
    	// different event types can have different consumption policies
    	
    	
    	boolean firstCandidateCheck = true;    	
    	TrendOperatorData internalSate = (TrendOperatorData)getInternalState();
    	LinkedList<IEventInstance> candidates = internalSate.getCandidates();
    	
    	double currentTrendValue = 0;
    	matchingSets.setPatternDetected(false);
    	int trendCount = 1;
    	
    	TrendRelationEnum relation = ((TrendMatchingSchema)agentType.
    			getMatchingSchema()).getTrendRelation();
    	Integer trendTreshold = ((TrendMatchingSchema)agentType.
    			getMatchingSchema()).getTrendTreshold();
    	
    	
    	for (IEventInstance candidate: candidates) {
    		List<Operand> operand = agentType.getEventInputOperands(candidate.getEventType());
    		TrendMatchingSchema matchingSchema = (TrendMatchingSchema)agentType.getMatchingSchema();     		
    		//String expression = matchingSchema.getExpression(operand);
    		IExpression parsedExpression = matchingSchema.getParsedExpression(operand.get(0));
    		
    		// calculate the expression value
    		// at this point it only works for numeric expression values (int, double...)
    		// need to make a more generic implementation (any object implementing comparable interface)
    		double value = (Double)parsedExpression.evaluate(candidate);
    		if (!firstCandidateCheck) {
    			// check if trend is satisfied according to trend relation
    			switch (relation) {
    				case INCREASE: { // we are looking for increasing values
    					if (value <= currentTrendValue) {
    						currentTrendValue = value;
    						trendCount =1;
    						continue;
    					} 
    					trendCount++;
    					break;
    				}
    				case DECREASE: { // we are looking for decreasing values
    					if (value >= currentTrendValue) {
    						currentTrendValue = value;
    						trendCount =1;
    						continue;
    					}    
    					trendCount++;
    					break;
    				}
    				case STABLE: { // we are looking for equal values
    					if (value != currentTrendValue) {
    						currentTrendValue = value;
    						trendCount =1;
    					}
    					trendCount++;
    					break;
    				}
    				default: {
						//throw new OperatorsException("Unknown terminator policy");
    					System.out.println("Unknown Trend relation");
					}    				
    			}
    		} else { // firstCandidateCheck
    			firstCandidateCheck = false;
    		}    		
    		currentTrendValue = value;    		
    	}
    	
    	// trend pattern is detected
    	if (trendCount !=1 && trendCount >= trendTreshold){
    		matchingSets.setPatternDetected(true);
    		//TODO : workaround
    		//TODO: the candidates list maintain a list of all candidate instances of all types in the arrival order
    		//this is like in aggregation - you cannot acccess a specific instance data since you do not 
    		//know how many instances to access
    		//therefore for now we do not allow to access instance information, therefore it doesn't matter
    		//what is in matchign set as long as its size matches operand size    		
    		ComputedVariableType computedVariableType = ((TrendMatchingSchema)agentType.
        			getMatchingSchema()).getComputedVariableType();
    		Map<String,Object> attributes = new HashMap<String,Object>();
    		attributes.put(TrendMatchingSchema.TREND_COMPUTED_VARIABLE_ATTR_NAME, trendCount);
    		List<IDataObject> matchingSet = new ArrayList<IDataObject>();
    		matchingSet.add(new ComputedVariableInstance(computedVariableType, attributes));
    		matchingSets.addMatchingSet(matchingSet);
        	// create list of candidates for consumption
        	createCandidatesListForConsumption();
        	
        	return true;
    	}else
    	{
    		return false;
    	}
    	
    }
	
	private void createCandidatesListForConsumption() {

		// all internal state events should potentially be consumed
		// we decide on consumption based on consumption policy per operand (event type)
		TrendOperatorData internalSate = (TrendOperatorData)getInternalState();
    	LinkedList<IEventInstance> candidates = internalSate.getCandidates();
    	
    	for (IEventInstance event: candidates) {
    		List<Operand> operands = agentType.getEventInputOperands(event.getEventType());
    		// operands list has a single operand since operand-event type is 1:1 in trend pattern
    		ConsumptionPolicyEnum consumption = agentType.getMatchingSchema().getConsumption(
    				operands.get(0));
    		    		
    		if (consumption == ConsumptionPolicyEnum.CONSUME) {
    			trendConsumedCandidates.add(event);
    		}
    	}		
	}

	@Override
	// in trend pattern we don't allow single event type to act as several operands
	// passing Operand as parameter for processing is redundant here (and in all other functions),
	// still working with it for compatibility
	public boolean addInstancetoState(IEventInstance eventInstance,Set<Operand> filteringResults) {
		if (operatorData.isReject()) return false;
		
		for (Operand operand: filteringResults) 
		{
			//only if the threshold condition on this operand is true
		    //TODO: can only pass the operands which are evaluated to true
			addInstance(eventInstance,operand);
		}		
		return true;
	}
	
	protected void addInstance(IEventInstance eventInstance,Operand operand) {
		// operand index is not relevant (we maintain a signle candidates list)
		operatorData.addLast(eventInstance,-1);	
	}
			
	@Override
	public boolean determineTerminationInstantiation() {
		return false;
	}
	
	@Override
	protected boolean operatorCompose() {
		return true;
	}

}

