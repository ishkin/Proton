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

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.ibm.hrl.proton.metadata.computedVariable.IComputedVariableType;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;
import com.ibm.hrl.proton.metadata.context.interfaces.ISegmentationContextType;
import com.ibm.hrl.proton.metadata.epa.enums.CardinalityPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.EPATypeEnum;
import com.ibm.hrl.proton.metadata.epa.enums.EvaluationPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IDerivationSchema;
import com.ibm.hrl.proton.metadata.epa.interfaces.IFilteringSchema;
import com.ibm.hrl.proton.metadata.epa.interfaces.IMatchingSchema;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

public class StatefulEventProcesingAgentType extends EventProcessingAgentType {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	public IMatchingSchema matchingSchema;
	protected EvaluationPolicyEnum evaluation;
	protected CardinalityPolicyEnum cardinality;
	protected IExpression parsedAssertion;
	protected String assertion;	
	protected Collection<ISegmentationContextType> epaSegmentation;
	protected IComputedVariableType agentContextInformation;
	
	

	public StatefulEventProcesingAgentType(UUID id, String name, EPATypeEnum epaType,
			List<IEventType> inputEvents,
			IMatchingSchema matchingSchema,
			IFilteringSchema filteringSchema,
			IDerivationSchema derivationSchema, IContextType context,
			boolean isFair){
		super(id, name, epaType, inputEvents,filteringSchema, derivationSchema, context,isFair);
		this.matchingSchema = matchingSchema;
	}

	/**
	 * The order of input and output events in the input and derivation lists matter - should be as operands order
	 * @param id
	 * @param name
	 * @param epaType
	 * @param inputEvents
	 * @param derivedEvents
	 * @param matchingSchema
	 * @param filteringSchema
	 * @param derivationSchema
	 * @param context
	 * @param cardinality
	 * @param reportParticipants
	 * @param isFair
	 */
	public StatefulEventProcesingAgentType(
			UUID id, 
			String name,
			EPATypeEnum epaType, List<IEventType> inputEvents,			
			IMatchingSchema matchingSchema,
			IFilteringSchema filteringSchema,
			IDerivationSchema derivationSchema, IContextType context,
			CardinalityPolicyEnum cardinality,
			boolean isFair,String assertion, IExpression parsedExpression,EvaluationPolicyEnum evaluationPolicy,
			Collection<ISegmentationContextType> epaSegmentation) {
		super(id, name, epaType, inputEvents,  filteringSchema,
				derivationSchema, context, isFair);
		this.matchingSchema = matchingSchema;
		this.cardinality = cardinality;
		this.parsedAssertion = parsedExpression;
		this.evaluation = evaluationPolicy;
		this.epaSegmentation = epaSegmentation;
		// TODO Auto-generated constructor stub
	}
	
	public void setEpaSegmentation(Collection<ISegmentationContextType> epaSegmentation) {
		this.epaSegmentation = epaSegmentation;
	}
	
	public EvaluationPolicyEnum getEvaluation() {
		return evaluation;
	}
	public CardinalityPolicyEnum getCardinality() {
		return cardinality;
	}
	public IExpression getParsedAssertion() {
		return parsedAssertion;
	}
		
	public IMatchingSchema getMatchingSchema()
    {
        return matchingSchema;
    }
	
	public void setMatchingSchema(IMatchingSchema matchingSchema) {
		this.matchingSchema = matchingSchema;
	}

	public void setEvaluation(EvaluationPolicyEnum evaluation) {
		this.evaluation = evaluation;
	}

	public void setCardinality(CardinalityPolicyEnum cardinality) {
		this.cardinality = cardinality;
	}

	public void setParsedAssertion(IExpression parsedAssertion) {
		this.parsedAssertion = parsedAssertion;
	}

	public void setAssertion(String assertion) {
		this.assertion = assertion;
	}
	
	public IComputedVariableType getAgentContextInformation() {
		return agentContextInformation;
	}

	public void setAgentContextInformation(
			IComputedVariableType agentContextInformation) {
		this.agentContextInformation = agentContextInformation;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
	    return "EPA '"+getName()+"' of type: "+getType();
	}
		

	@Override
	public Collection<ISegmentationContextType> getLocalSegmentation() {
		return epaSegmentation;
	}		
}
