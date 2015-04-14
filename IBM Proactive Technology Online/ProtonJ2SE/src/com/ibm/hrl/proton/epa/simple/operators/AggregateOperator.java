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

import java.util.*;

import com.ibm.hrl.proton.epa.aggregationCalculators.*;
import com.ibm.hrl.proton.epa.simple.abstractOperators.AbstractStandardOperator;
import com.ibm.hrl.proton.epa.state.SequenceOperatorData;
import com.ibm.hrl.proton.metadata.computedVariable.IComputedVariableType;
import com.ibm.hrl.proton.metadata.epa.*;
import com.ibm.hrl.proton.metadata.epa.enums.CardinalityPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.schemas.*;
import com.ibm.hrl.proton.metadata.epa.schemas.AggregationSchema.AggregationTypeEnum;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.runtime.computedVariable.ComputedVariableInstance;
import com.ibm.hrl.proton.runtime.epa.MatchingSets;
import com.ibm.hrl.proton.runtime.epa.functions.IAggregationProcessingFunction;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

/**
 * <code>CountingOperator</code>.
 * 
 * @author gall
 */
public class AggregateOperator extends AbstractStandardOperator implements IAggregationProcessingFunction {
	private Map<String, AggregationCalculator>	calculators	= new HashMap<String, AggregationCalculator>();
    private Map<Integer, List<IEventInstance>> participants = new HashMap<Integer,List<IEventInstance>>();
	 
	
    private void useCandidate(IEventInstance eventInstance, Operand operand) {
		// calculate candidate on all calculators
		for (AggregationCalculator calc : calculators.values()) {
			calc.addInstance(eventInstance, operand);
		}
		
		//add this instance to the participants list
		if (this.agentType.getDerivationSchema().isReportingParticipants())
		{
			//add the instance to the list
			List<IEventInstance> participantsList = participants.get(operand.getOperandIndex());
			if (participantsList == null)
			{
				participantsList = new ArrayList<IEventInstance>();
				participants.put(operand.getOperandIndex(), participantsList);
			}
			participantsList.add(eventInstance);
		}
	}

	private void setMatchingSets(ComputedVariableInstance computedVariables) {
		matchingSets.addMatchingSet(Arrays.asList(computedVariables));
	}

	/**
	 * Clean all consumables after a match
	 */
	private void cleanAfterMatch() {
		// clean all consumables from all calculators
		for (AggregationCalculator calc : calculators.values()) {
			calc.cleanConsumables();
		}
	}

	/**
	 * Check if the assertion is true
	 * @param computedVariables The computed variables to check
	 * @return True iff the assertion is true
	 */
	private boolean isAssertionTrue(ComputedVariableInstance computedVariables) {
		IExpression parsedAssertion = agentType.getParsedAssertion();

		// no assertion, always true
		if (parsedAssertion == null) {
			return true;
		}
		
		return (Boolean)parsedAssertion.evaluate(computedVariables); 
	}

	/**
	 * In case of aggregation there is no meaning to the ORDER policy - all
	 * operands are treated as with EACH policy
	 * 
	 * @param candidate
	 * @param operand
	 */
	@Override
	protected void addInstanceByPolicy(IEventInstance candidate, Operand operand) {
		addCandidateFirst(candidate, operand);
	}

	/**
	 * The instance selection policy is FIRST - the last instance that arrived
	 * is added at the end of the list
	 * 
	 * @param candidate
	 * @param operand
	 */
	@Override
	protected void addCandidateFirst(final IEventInstance candidate, final Operand operand) {
		useCandidate(candidate, operand);
	}

	/**
	 * In case of aggregation there is no meaning on override policy
	 * 
	 * @param eventInstance
	 * @param operand
	 * @return
	 */
	@Override
	protected boolean checkOverride(IEventInstance eventInstance, Operand operand) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.hrl.proton.epa.simple.abstractOperators.AbstractStandardOperator
	 * #operatorCompose()
	 */
	@Override
	protected boolean operatorCompose() {		
		return true;
	}

	@Override
	public boolean determineTerminationInstantiation() {
		return false;
	}

	@Override
	public void setAgentType(StatefulEventProcesingAgentType agentType) {
		super.setAgentType(agentType);
		setOperatorData(new SequenceOperatorData(agentType.getNumOfOperands()));
		AggregationSchema schema = getMatchingSchema();
		
		// create all calculators
		for (Map.Entry<String, AggregationTypeEnum> entry : schema.getCalculatedVariableAggregationType()
				.entrySet()) {
			AggregationCalculator calculator = null;

			switch (entry.getValue()) {
			case AVERAGE:
				calculator = new AverageCalculator(schema, entry.getKey());
				break;
			case COUNT:
				calculator = new CountCalculator(schema, entry.getKey());
				break;
			case MAX:
				calculator = new MaxCalculator(schema, entry.getKey());
				break;
			case MIN:
				calculator = new MinCalculator(schema, entry.getKey());
				break;
			case SUM:
				calculator = new SumCalculator(schema, entry.getKey());
			}

			calculators.put(entry.getKey(), calculator);
		}
	}

	@Override
	public AggregationSchema getMatchingSchema() {
		return (AggregationSchema)super.getMatchingSchema();
	}

	@Override
	public MatchingSets match() {
		matchingSets.clear();		
		
		// gather all results from calculators
		Map<String, Double> results = new HashMap<String, Double>();
		for (Map.Entry<String, AggregationCalculator> entry : calculators.entrySet()) {
			results.put(entry.getKey(), entry.getValue().getResult());
		}
		
		HashMap<String,Object> resultsMap  = new HashMap<String, Object>(results);
		//add the participants to the computed variables attribute list
		ComputedVariableInstance computedVariables = new ComputedVariableInstance(getMatchingSchema()
						.getComputedVariableType(),resultsMap);
		//check the assertion - it should be based only on computed variables
		// if assertion is true, set the matches and clean consumables
		if (isAssertionTrue(computedVariables)) 
		{
			//in case we need to report the situation- if some derivation expression
			//is based on participants we need to fill those attributes within the computed variable
			if (this.agentType.getDerivationSchema().isReportingParticipants())
			{
				//prepare the data structure for fetching input events attribute's arrays
				Map<Integer,HashMap<String,List<Object>>> participantAttributes = prepareParticipantsArrays(getMatchingSchema().getComputedVariableType(),results.keySet());
				//go over the computed variable type defs, for any attribute which name is not 
				//in the calculated variable result set get the array attribute from participating
				//input events
				for (Map.Entry<Integer,HashMap<String,List<Object>>> operandData : participantAttributes.entrySet()) 
				{
					String attributeNamePrefix = "operand"+operandData.getKey().toString();
					for (Map.Entry<String, List<Object>> attributeValuesArray : operandData.getValue().entrySet()) {
						String fullAttrName = attributeNamePrefix+"_"+attributeValuesArray.getKey();
						List<Object> attributeValues = attributeValuesArray.getValue();
						
						resultsMap.put(fullAttrName, attributeValues);
					}
				}
				computedVariables = new ComputedVariableInstance(getMatchingSchema()
						.getComputedVariableType(),resultsMap);
				
			}
			
			setMatchingSets(computedVariables);
			if (agentType.getCardinality().equals(CardinalityPolicyEnum.SINGLE))
            {
                
                /* situation composed - cardinality is single - we need to set a flag indicating that no more matching will be performed */                 
               operatorData.setReject(true);
            } else {
                
                /* if cardinality policy is not SINGLE - we need for future matching to consume the instances that were marked for consumption
                 * during the processing phase (consume instances- delete them from operator's internal state */
            	cleanAfterMatch();
            }        
			
		}
		
		return matchingSets;
	}

	private Map<Integer,HashMap<String,List<Object>>> prepareParticipantsArrays(IComputedVariableType computedVariableType, Set<String> calculatedVariables) {
		Map<Integer,HashMap<String,List<Object>>> attributes = new HashMap<Integer,HashMap<String,List<Object>>>();
		/*for (Map.Entry<Operand, List<IEventInstance>> operandsList : participants.entrySet()) 
		{
			Integer operandIndex = operandsList.getKey().getOperandIndex();			
			attributes.put(operandIndex, new HashMap<String,List<Object>>());			
		}*/
		
		//iterate over the type attributes and see which of them should be filled from the operator participants
		Collection<TypeAttribute> typeAttributes = computedVariableType.getTypeAttributes();
		for (TypeAttribute typeAttribute : typeAttributes) {
			String attributeName = typeAttribute.getName();
			if (!calculatedVariables.contains(attributeName))
			{
				//this attribute is not a computed variable but based on participants
				//parse it and see on which operands it depends and which attribute it is based on
				String delimeter = "_";
				int delimeterIndex = attributeName.indexOf(delimeter);
				Integer operandIndex = Integer.valueOf(attributeName.substring(7, delimeterIndex));  //starting from index 6 since the syntax of attr name is "operand<N>_<attrName>"
				String attribute = attributeName.substring(delimeterIndex+1);
				
				HashMap<String,List<Object>> participantsAttributesMap = attributes.get(operandIndex);
				if (participantsAttributesMap == null)
				{
					participantsAttributesMap =  new HashMap<String,List<Object>>();
					attributes.put(operandIndex, participantsAttributesMap);
				}
				participantsAttributesMap.put(attribute, new ArrayList());
			}
		}
		
		//iterate over the operands and see which attributes we need, populate the lists
		for (Map.Entry<Integer, HashMap<String,List<Object>>> operandsList : attributes.entrySet()) 
		{
			Integer operandIndex = operandsList.getKey();
			List<IEventInstance> eventsListPerOperand = participants.get(operandIndex);
			if (eventsListPerOperand == null) //no such entry - no participants for this operand
			{
				//do nothing, everything was already prepared
				continue;
			}
						
			HashMap<String,List<Object>> eventAttributes = attributes.get(operandIndex);
			for (IEventInstance eventInstance : eventsListPerOperand) 
			{
				for (String attribute : eventAttributes.keySet()) 
				{
					Object attrValue = eventInstance.getEventAttribute(attribute);
					eventAttributes.get(attribute).add(attrValue);
				}
			}
		}
		
		return attributes;
		
	}

}
