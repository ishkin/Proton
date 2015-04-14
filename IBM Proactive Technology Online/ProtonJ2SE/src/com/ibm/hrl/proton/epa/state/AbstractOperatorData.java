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

import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

/**
 * <code>AbstractOperatorData</code>.
 * 
 * 
 */
public abstract class AbstractOperatorData implements IOperatorData
{
    /** This ID identifies the class upon serialization/deserialization
     * and explicitly defining it allows deserialization to work properly
     * even when the class has changed.
     */
    public static final long serialVersionUID = 0;
    boolean reject;

    
    public AbstractOperatorData()
    {
        reject = false;
    }
    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.IOperatorData#addFirst(com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance, int)
     */
    @Override
    public abstract void addFirst(IEventInstance eventInstance, int operandIndex);
    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.IOperatorData#addLast(com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance, int)
     */
    @Override
    public abstract void addLast(IEventInstance eventInstance, int operandIndex);
    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.IOperatorData#checkCandidateExistence(int)
     */
    @Override
    public abstract boolean checkCandidateExistence(int operandIndex);

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.IOperatorData#clearCandidate(int)
     */
    @Override
    public abstract void clearCandidate(int operandIndex);
    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.IOperatorData#getCandidatesPerOp(int)
     */
    //@Override
    //public abstract LinkedList<IEventInstance> getCandidatesPerOp(int operandNumber);

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.IOperatorData#initiateCandidate(int)
     */
    @Override
    public abstract void initiateCandidate(int operandIndex);
    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.IOperatorData#isReject()
     */
    @Override
    public boolean isReject()
    {
        return reject;
    }

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.IOperatorData#setReject(boolean)
     */
    @Override
    public void setReject(boolean reject)
    {
        this.reject = reject;

    }

}
