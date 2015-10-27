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
package com.ibm.hrl.proton.runtime.epa.interfaces;

import java.util.Set;

import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.interfaces.IMatchingSchema;
import com.ibm.hrl.proton.runtime.epa.IState;
import com.ibm.hrl.proton.runtime.epa.MatchingSets;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

public interface IProcessingFunction {

	/**
	 * Performs matching according to the current state and the policies,
	 * returns matched sets in a MatchingSets object, they are intended for the 
	 * derivation stage.
	 * @return
	 */
	public MatchingSets match(); // executed on internal state
	
	/**
	 * Adds the event instance to internal state - to the relevant operands.
	 * Relevant operands are the ones whose expression was evaluated to true.
	 * Returns false when this situation is no longer accepting new event instances (cardinality
	 * policy is single) - the EPA can store and use this flag not to do any filtering on next event
	 * instances
	 * @param eventInstance
	 * @param filteringResults
	 * @return
	 */
	public boolean addInstancetoState(IEventInstance eventInstance, Set<Operand> filteredResults);
	public IState getInternalState();
	
	public IMatchingSchema getMatchingSchema();
	
	public boolean determineTerminationInstantiation();
}
