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
package com.ibm.hrl.proton.router;

import java.util.Collection;

import com.ibm.hrl.proton.agentQueues.exception.AgentQueueException;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;

/**
 * Used for routing raw and derived events to their agent destinations according to agent and context 
 * definitions (which events types participate in agent and context logic)
* <code>IEventRouter</code>.
* 
*
 */
public interface IEventRouter
{
    /**
     * Decide which agents queues to route the event to, and perform the routing asynchronously 
     * @param eventInstance
     * @param channels
     * @throws AgentQueueException
     */
    public void routeTimedObject(ITimedObject timedObject) throws AgentQueueException , DataSenderException;
   
    /**
     * Route the specified event instances to the relevant agent queues
     * @param eventInstances
     * @param channels
     * @throws AgentQueueException 
     */
    public void routeTimedObjects(Collection<? extends ITimedObject> timedObjects) throws AgentQueueException,DataSenderException;
    
}

