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


import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ibm.hrl.proton.agentQueues.queuesManagement.AgentQueuesManager;
import com.ibm.hrl.proton.metadata.context.enums.ContextIntervalPolicyEnum;
import com.ibm.hrl.proton.utilities.timerService.ITimerServices;
import com.ibm.hrl.proton.utilities.timerService.TimerServiceException;

/**
 * Extension of agent abstract queue based on non sorted queue.
* <code>ChannelNonSortedQueue</code>.
* 
*
 */
public class AgentlNonSortedQueue extends AgentAbstractQueue
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;



    public AgentlNonSortedQueue(String contextName, String agentName, long bufferingTime,ContextIntervalPolicyEnum initiationPolicy,ContextIntervalPolicyEnum terminationPolicy)
    {
        super( contextName, agentName, bufferingTime,initiationPolicy,terminationPolicy,
                new ConcurrentLinkedQueue<QueueElement>());        
    }

   

    @Override
    public synchronized boolean offer(Object o)
    {       
        
        ITimerServices timerService = AgentQueuesManager.getInstance().getTimerServices();
        
      //gets the head element of the queue and compares it with the current element
        logger.entering(getClass().getName(), "offer");
        QueueElement headElement = (QueueElement)this.peek();
        QueueElement eventInstance = (QueueElement)o;
        long eventTimestamp = eventInstance.getTimestamp();
                                  
        underlyingQueue.add(eventInstance);
        logger.fine("Adding event instance "+eventInstance+" to unsorted queue");
       
        if (headElement  == null) //the first element in the queue - create the timer for the queue
        {             
            //create the timer for the queue
            long currentTimestamp = Calendar.getInstance().getTimeInMillis();
            long timeDifference = bufferingTime - (currentTimestamp - eventTimestamp);
            if (timeDifference > 0)
             try
             {
                 logger.fine("Head element do not exist, entering event instance "+eventInstance+ "as head of the queue and setting timer for "+timeDifference+ " ms");
                 timerService.createTimer(timerListener, Calendar.getInstance().getTime(), false, timeDifference, -1);
             }
             catch (TimerServiceException e)
             {
                 throw new RuntimeException("BUG : couldn't deal with events in the channel due to TimerServiceException: "+e.getMessage());
             }
         else
             try
             {
                 logger.fine("Head element do not exist, entering event instance "+eventInstance+ "as head of the queue and passing for handling since waiting time for this instance is over");
                 onTimer(null);
             }
             catch (Exception e)
             {
                 throw new RuntimeException("BUG : couldn't deal with events in the channel due to ChannelException: "+e.getMessage());
             }
             
        }
        
        logger.exiting(getClass().getName(), "offer");
        return true;
    }


}
