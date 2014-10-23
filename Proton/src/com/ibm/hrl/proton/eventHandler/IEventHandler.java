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
package com.ibm.hrl.proton.eventHandler;

import com.ibm.hrl.proton.agentQueues.exception.EventHandlingException;
import com.ibm.hrl.proton.runtime.context.notifications.IContextNotification;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;


/**
 * Handler for event instances that has been buffered to the specified amount of time and now ready to be passed to processing to 
 * context service and EPAs.
* <code>IChannelEventHandler</code>.
* 
*
 */
public interface IEventHandler
{
    /**
     * This method is called by the agent queue manager when a timed object (either real event instance or notifications simulated by context) is 
     * ready to be processed
     * @param event
     * @param agentName
     * @param contextName
     * @throws EventHandlingException 
     */
    public void handleEventInstance(ITimedObject timedObject,String agentName, String contextName) throws EventHandlingException;
    
   
    
    
    /**
     * Context service uses this method to route the context notifications (context partition termination on timer
     * and context initiation on timer) to the agent queues,where they will wait in a buffering queue,
     * after which they will be passed back to context service to complete the action (of termination and initialization of relevant partitions)
     * @param contextNotification
     * @throws EventHandlingException 
     */
    public void routeContextNotification(IContextNotification contextNotification) throws EventHandlingException;
}
