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
package com.ibm.hrl.proton.epa.aggregationCalculators;

import java.util.Collection;

import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.enums.ConsumptionPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.schemas.AggregationSchema;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

/**
 * The main abstract calculator
 * 
 * @author gall
 * 
 */
public abstract class AggregationCalculator {
	private final AggregationSchema	schema;
	private final String			calculateVariableName;
	// computed operand index -> value
	protected double[]				operandsArrays;

	protected AggregationCalculator(AggregationSchema schema, String calculatedVariableName) {
		this.schema = schema;
		this.calculateVariableName = calculatedVariableName;
		Collection<Operand> operands = schema.getOperands();
		operandsArrays = new double[operands.size()];

		// initialize values
		for (int i = 0; i < operands.size(); i++) {
			resetIndex(i);
		}
	}

	/**
	 * Gets the instance value
	 * @param instance
	 * @param operand
	 * @return The value assossicated with the instance, or null if none is
	 */
	protected Double getValueForCalculatedVariable(IEventInstance instance, Operand operand) {
		Object obj = schema.getCalculatedVariablesExpressions().get(calculateVariableName).get(operand).evaluate(instance);
		if (obj instanceof Integer) {
			// convert integers to double
			return ((Integer)obj).doubleValue();
		} else {
			return (Double)obj;
		}
	}
	
	/**
	 * Add a new instance to the calculator
	 * @param event
	 * @param operand
	 */
	public void addInstance(IEventInstance event, Operand operand) {
		calculate(getValueForCalculatedVariable(event, operand), operand.getOperandIndex());
	}

	/**
	 * Resets the array in the request location
	 * @param index The index to reset the array in 
	 */
	protected void resetIndex(int index) {
		operandsArrays[index] = 0.0;
	}

	/**
	 * Clean all consumable operands
	 */
	public void cleanConsumables() {
		for (Operand operand : schema.getOperands()) {
			if (schema.getConsumption(operand) == ConsumptionPolicyEnum.CONSUME) {
				resetIndex(operand.getOperandIndex());
			}
		}
	}

	/**
	 * Gets the results calculated so far
	 * @return The result calculated so far
	 */
	public abstract double getResult();

	/**
	 * Calculate a new value
	 * @param value The value to calculate
	 * @param index The index to updated with the calculation
	 */
	protected abstract void calculate(double value, int index);
}
