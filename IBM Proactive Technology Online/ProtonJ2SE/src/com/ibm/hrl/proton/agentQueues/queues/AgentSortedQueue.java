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
import java.util.PriorityQueue;

import com.ibm.hrl.proton.agentQueues.queuesManagement.AgentQueuesManager;
import com.ibm.hrl.proton.metadata.context.enums.ContextIntervalPolicyEnum;
import com.ibm.hrl.proton.utilities.timerService.ITimerServices;
import com.ibm.hrl.proton.utilities.timerService.TimerServiceException;


/**
 * Implementation of agent abstract queue based on priority (sorted) queue
* <code>ChannelSortedQueue</code>.
* 
*
 */
public class AgentSortedQueue extends AgentAbstractQueue {  
    
	/**
     * 
     */
    private static final long serialVersionUID = 1L;


    public AgentSortedQueue(String contextName, String agentName,long bufferingTime,ContextIntervalPolicyEnum initiationPolicy,ContextIntervalPolicyEnum terminationPolicy,AgentQueuesManager queueManager)
	{
	    super(contextName,agentName,bufferingTime,initiationPolicy,terminationPolicy,new PriorityQueue<QueueElement>(),queueManager);
	}
	

    /**
     * Inserts the element to the priority queue.
     * 
     * @return true
     * @throws ClassCastException if the element cannot be compared with the
     *         elements in the priority queue using the ordering of the priority
     *         queue.
     * @throws NullPointerExcepiton if the element is null.
     */
 
    public synchronized boolean offer(Object o) 
    {
       ITimerServices timerService = queueManager.getTimerServices();
        
       logger.entering(getClass().getName(), "offer");
       //gets the head element of the queue and compares it with the current element
       QueueElement headElement = (QueueElement)this.peek();
       QueueElement eventInstance = (QueueElement)o;
       long eventTimestamp = eventInstance.getTimestamp();
                                 
       underlyingQueue.add(eventInstance);
       logger.fine("Adding event instance "+eventInstance+" to sorted queue");
       
       if (headElement!= null && (headElement.getTimestamp()> eventTimestamp)){          
         //remove the timer on the queue, insert the new element, recalculate the timer on the queue
           timerService.destroyTimers(timerListener);       
           long currentTime = Calendar.getInstance().getTimeInMillis(); 
           long timeDifference = (eventTimestamp+bufferingTime) - currentTime;
           
           if (timeDifference > 0)
           {
               logger.fine("Event instance "+eventInstance+ "is going into the queue before the head element: "+headElement+", recreating the queue timer for "+timeDifference+" ms");
               //set the new timer
            try
            {
                timerService.createTimer(timerListener, Calendar.getInstance().getTime(), false, timeDifference, -1);
            }
            catch (TimerServiceException e)
            {                               
                throw new RuntimeException();
            }
           }
           else
           {
            //add event to the queue and take care of it 
            
             try
            {
                logger.fine("Event instance "+eventInstance+ "is going into the queue before the head element: "+headElement+" passing directly to processing since the waiting time for this event instance is over");
                onTimer(null);
            }
            catch (Exception e)
            {
                throw new RuntimeException("BUG : couldn't deal with events in the channel due to ChannelException: "+e.getMessage());
            }
             return true;
           }
               
               
       }              
       
       if (headElement  == null)
       {             
           
           //create the timer for the queue
           long currentTimestamp = Calendar.getInstance().getTimeInMillis();
           long timeDifference = bufferingTime - (currentTimestamp - eventTimestamp);
           if (timeDifference > 0)
            try
            {
                logger.fine("No previous entries in the queue, event instance: "+eventInstance+ "goes to the head of the queue, setting timer to "+timeDifference+" ms");
                timerService.createTimer(timerListener, Calendar.getInstance().getTime(), false, timeDifference, -1);
            }
            catch (TimerServiceException e)
            {
                throw new RuntimeException("BUG : couldn't deal with events in the channel due to TimerServiceException: "+e.getMessage());
            }
        else
            try
            {
                logger.fine("No previous entries in the queue, event instance: "+eventInstance+ "goes to the head of the queue, processing directly since waiting time for this instance is over");
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
