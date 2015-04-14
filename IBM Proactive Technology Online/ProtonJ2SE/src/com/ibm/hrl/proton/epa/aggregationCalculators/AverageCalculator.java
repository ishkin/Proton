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

public class AverageCalculator extends AggregationCalculator {
	private int[]	counts;

	public AverageCalculator(AggregationSchema schema, String variableName) {
		super(schema, variableName);
		counts = new int[super.operandsArrays.length];
	}

	@Override
	public double getResult() {
		double sum = 0;
		int totalCount = 0;
		// calculate count*value for all element and then divide by the total count
		for (int i = 0; i < counts.length; i++) {
			sum += super.operandsArrays[i];
			totalCount += counts[i];
		}
		return sum / totalCount;
	}
	
	@Override
	protected void resetIndex(int index) {
		super.resetIndex(index);
		// The first call from the super constructor will cause a NullPE because
		// Count is still not initialized
		if (counts != null) {
			counts[index] = 0;
		}
	}

	@Override
	protected void calculate(double value, int index) {
		super.operandsArrays[index] += value;
		counts[index]++;
	}

}
