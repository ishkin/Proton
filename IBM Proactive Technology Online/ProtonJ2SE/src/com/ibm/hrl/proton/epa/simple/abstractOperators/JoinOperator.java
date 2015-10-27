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
package com.ibm.hrl.proton.epa.simple.abstractOperators;

import com.ibm.hrl.proton.runtime.epa.functions.IJoinProcessingFunction;

public abstract class JoinOperator extends AbstractStandardOperator implements IJoinProcessingFunction{		
	

	 /**
     * Check if the candidates that are selected so far do not violate the
     * Joining condition, which is that all operand instances must be present
     * and different from one another.
     *
     * @return          true if the candidates satisfies the conditions
     */
    protected boolean checkCandidates(int opNum) {
    	return (instances[opNum] != null);
    }
    
    @Override
    protected boolean operatorCompose() {        
        return true;
    }

}
