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

import com.ibm.hrl.proton.metadata.epa.schemas.AggregationSchema;

public class MinCalculator extends AggregationCalculator {
	public MinCalculator(AggregationSchema schema, String variableName) {
		super(schema, variableName);
	}

	@Override
	public double getResult() {
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < super.operandsArrays.length; i++) {
			min = Math.min(min, super.operandsArrays[i]);
		}

		return min;
	}

	@Override
	protected void calculate(double value, int index) {
		super.operandsArrays[index] = Math.min(super.operandsArrays[index], value);
	}
	
	@Override
	protected void resetIndex(int index) {
		super.operandsArrays[index] = Double.POSITIVE_INFINITY;
	} 
}
