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

import com.ibm.hrl.proton.epa.simple.abstractOperators.AbstractStandardOperator;
import com.ibm.hrl.proton.epa.state.OperatorData;
import com.ibm.hrl.proton.runtime.epa.IState;
import com.ibm.hrl.proton.runtime.epa.functions.IAbsenceProcessingFunction;

public class AbsenceOperator extends AbstractStandardOperator implements
		IAbsenceProcessingFunction {

	
	/*@Override
	public MatchingSets match() {
		// TODO Auto-generated method stub
		return null;
	}*/
		
	@Override
	/* 
	 * This function is invoked from compose() and returns whether there was a match
	 * (the internal MatchingSet variable is updated with the result); in Absence we do not generate
	 * a matching set but only boolean indication regarding the match (empty internal state - true,
	 * otherwise - false).  
	 */
	protected boolean process(int opNum) {
		IState internalState = getInternalState();
		OperatorData data = (OperatorData)internalState;		

		// currently the entire processing (call hierarchy) does not work with empty
		// matching set, which is the case with the Absence pattern; should be special treatment
		// another alternative is to push dummy event into matching set to support all places
		// and this event would be ignored by derivation (trivial "empty" derivation for
		// the Absence pattern is enforced by metadata)

		
		// put a dummy matching set into matching sets
		matchingSets.addMatchingSet(new ArrayList());
		;
		
		if (!data.isEmpty()) {
			matchingSets.setPatternDetected(false);
		}
		return data.isEmpty();
	}

	/*@Override
	public boolean addInstancetoState(IEventInstance eventInstance,Set<Operand> filteringResults) {
		return false;

	}

	@Override
	protected boolean checkOverride(IEventInstance eventInstance,
			Operand operand) {
		return false;
	}

	@Override
	protected void addInstanceByPolicy(IEventInstance candidate,Operand operand)
	{
		addCandidateFirst(candidate, operand);
	}*/

	@Override
	protected boolean operatorCompose() {
		// TODO What this function should return for absence...
		return false;
	}

	@Override
	public boolean determineTerminationInstantiation() {
		return true;
	}

}
