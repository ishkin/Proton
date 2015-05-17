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
package com.ibm.hrl.proton.agentQueues.queues;

import com.ibm.hrl.proton.agentQueues.queuesManagement.AgentQueuesManager;
import com.ibm.hrl.proton.utilities.timerService.ITimerListener;

/**
 * Timer listener for the queue whose name is passed in the ctor.
* <code>QueueTimerListener</code>.
* 
*
 */
public class QueueTimerListener implements ITimerListener
{
    private static final long serialVersionUID = 1L;
    private String queueName;
    
    public QueueTimerListener(String queueName)
    {
        this.queueName= queueName;
    }    
    
    @Override
    public String getListenerId()
    {
        return queueName;
    }

    @Override
    public Object onTimer(Object additionalInformation)
    throws Exception
    {
        AgentAbstractQueue queue = AgentQueuesManager.getInstance().getAgentQueue(queueName);
        return queue.onTimer(additionalInformation);
    }
        
    

}
