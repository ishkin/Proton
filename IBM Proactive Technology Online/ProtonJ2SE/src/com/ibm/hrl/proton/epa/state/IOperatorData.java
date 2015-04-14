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

package com.ibm.hrl.proton.epa.state;

import java.io.Serializable;

import com.ibm.hrl.proton.runtime.epa.IState;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

/**
 * <code>IOperatorData</code>.
 * 
 * 
 */
public interface IOperatorData  extends IState ,Serializable
{
    /**
     * A flag determining if to allow to add new instances to state or not
     * @return
     */
    public boolean isReject();

    /**
     * Setting the flag determining if to allow to add new instances to state or not
     * @return
     */
    public void setReject(boolean reject) ;

    /**
     * Remove all instances from the specified operand
     * @param operandIndex
     */
    public void clearCandidate(int operandIndex);
    
    /**
     * Check if the specified operand was initialized
     * @param operandIndex
     * @return
     */
    public boolean checkCandidateExistence(int operandIndex);

    /**
     * Initiate a list of instances for the specified operand
     * @param operandIndex
     */
    public void initiateCandidate(int operandIndex) ;

    /**
     * Add the given instance as LAST to the specified operand's instances list
     * @param eventInstance
     * @param operandIndex
     */
    public void addLast(IEventInstance eventInstance, int operandIndex) ;
    
    /**
     * Add the given instance as FIRST to the specified operand's instances list
     * @param IEventInstance
     * @param operandIndex
     */
    public void addFirst(IEventInstance eventInstance, int operandIndex);
    
    /**
     * Get all candidate instances for the specified operand
     * @param operandNumber
     * @return
     */
    
    //public LinkedList<IEventInstance> getCandidatesPerOp(int operandNumber);
    
    public boolean isEmpty();

}
