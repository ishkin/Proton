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

import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.schemas.AggregationSchema;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

public class CountCalculator extends SumCalculator {

	public CountCalculator(AggregationSchema schema, String variableName) {
		super(schema, variableName);
	}

	@Override
	protected Double getValueForCalculatedVariable(IEventInstance instance, Operand operand) {
		// default value is 1.0
		try {
			Double superResult = super.getValueForCalculatedVariable(instance, operand);
			if (superResult == null) {
				return 1.0; // default value
			}
			return superResult;
		} catch (NullPointerException e) {
			return 1.0;
		}
	}
}
