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

package com.ibm.hrl.proton.metadata.epa.schemas;

import java.util.*;

import com.ibm.hrl.proton.metadata.computedVariable.*;
import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.enums.ConsumptionPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IMatchingSchema;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

/**
 * <code>AggregationSchema</code>.
 * 
 * 
 */
public class AggregationSchema extends StandardMatchingSchema implements IMatchingSchema
{

	public enum AggregationTypeEnum {MAX,MIN,AVERAGE,SUM,COUNT};
	public Map<String,Map<Operand,IExpression>> calculatedVariablesExpressions;
	public Map<String,AggregationTypeEnum> calculatedVariableAggregationType;	
	public IComputedVariableType computedVariableType;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;    

	public AggregationSchema() {
		super();
		this.calculatedVariablesExpressions = new HashMap<String,Map<Operand,IExpression>>();
		this.calculatedVariableAggregationType = new HashMap<String,AggregationTypeEnum>();
		this.computedVariableType = new ComputedVariableType();
	}


	public AggregationSchema(
			Map<String, Map<Operand, IExpression>> calculatedVariablesExpressions,
			Map<String, AggregationTypeEnum> calculatedVariableAggregationType,
			Map<Operand,ConsumptionPolicyEnum> consumptionPolicy,
			IComputedVariableType computedVariableType) {
		super(consumptionPolicy);
		this.calculatedVariablesExpressions = calculatedVariablesExpressions;
		this.calculatedVariableAggregationType = calculatedVariableAggregationType;
		this.computedVariableType = computedVariableType;
	}


	public IComputedVariableType getComputedVariableType() {
		return computedVariableType;
	}


	public void setComputedVariableType(IComputedVariableType computedVariableType) {
		this.computedVariableType = computedVariableType;
	}


	public Map<String, Map<Operand, IExpression>> getCalculatedVariablesExpressions() {
		return calculatedVariablesExpressions;
	}


	public void setCalculatedVariablesExpressions(
			Map<String, Map<Operand, IExpression>> calculatedVariablesExpressions) {
		this.calculatedVariablesExpressions = calculatedVariablesExpressions;
	}


	public Map<String, AggregationTypeEnum> getCalculatedVariableAggregationType() {
		return calculatedVariableAggregationType;
	}


	public void setCalculatedVariableAggregationType(
			Map<String, AggregationTypeEnum> calculatedVariableAggregationType) {
		this.calculatedVariableAggregationType = calculatedVariableAggregationType;
	}
	
	public void addCalculatedVariableAggregationType(String calculatedVariableName, AggregationTypeEnum aggregationType)
	{
		this.calculatedVariableAggregationType.put(calculatedVariableName, aggregationType);
	}
	
	public void addCalculatedVariableOperandExpression(String calculatedVariableName,Operand operand, IExpression parsedExpression)
	{
		Map<Operand,IExpression> expressionsPerVariable;
		if ((expressionsPerVariable = calculatedVariablesExpressions.get(calculatedVariableName)) == null)
		{
			expressionsPerVariable = new HashMap<Operand,IExpression>();
			calculatedVariablesExpressions.put(calculatedVariableName, expressionsPerVariable);
			
		}
		expressionsPerVariable.put(operand, parsedExpression);
	}

	public Collection<Operand> getOperands() {
		return super.consumption.keySet();
	}


}
