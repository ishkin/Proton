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

import java.util.AbstractQueue;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.logging.Logger;

import com.ibm.hrl.proton.agentQueues.exception.AgentQueueException;
import com.ibm.hrl.proton.agentQueues.queuesManagement.AgentQueuesManager;
import com.ibm.hrl.proton.eventHandler.IEventHandler;
import com.ibm.hrl.proton.metadata.context.enums.ContextIntervalPolicyEnum;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.timerService.ITimerListener;
import com.ibm.hrl.proton.utilities.timerService.ITimerServices;
import com.ibm.hrl.proton.utilities.timerService.TimerServiceException;


/**
 * Abstract queue for the agent queues - we provide Sorted and Non Sorted implementation.
 * Upon reception of a new event for the given queue, it is inserted into the appropriate place at the queue,
 * and in some cases the buffering timer on the queue is created/changed (when this new event instance is the head of the queue)
 * When the timer on queue expires, we processes all "expired" events in the queue and create a new timer on the queue. 
 * 
 * The buffering time of the queue can be updated - in current implementation the buffering time will be updated when the old
 * timer on the queue expires (at most the head of the queue will be treated with the old buffering time)
* <code>AgentAbstractQueue</code>.
* 
*
 */
public abstract class AgentAbstractQueue  implements ITimerListener,Queue
{
    protected AbstractQueue<QueueElement> underlyingQueue;   
    protected String contextName;
    protected String agentName;
    protected long bufferingTime;    
    protected QueueTimerListener timerListener;
    private ContextIntervalPolicyEnum initiationInterPolicy;
    private ContextIntervalPolicyEnum terminationInterPolicy;
   
    protected static Logger logger = Logger.getLogger(AgentAbstractQueue.class.getName());
    
    public AgentAbstractQueue(  String contextName, String agentName,long bufferingTime,
            ContextIntervalPolicyEnum initiationInterPolicy, ContextIntervalPolicyEnum terminationInterPolicy,AbstractQueue<QueueElement> underLyingQueue)
    {
        
        this.contextName = contextName;
        this.agentName = agentName;      
        this.bufferingTime = bufferingTime;
        this.underlyingQueue = underLyingQueue;
        String queueName = AgentQueuesManager.getQueueName(agentName, contextName);
        timerListener = new QueueTimerListener(queueName);
        this.initiationInterPolicy = initiationInterPolicy;
        this.terminationInterPolicy = terminationInterPolicy;
        
    }
  
    
    @Override
    public Iterator<QueueElement> iterator()
    {
       return underlyingQueue.iterator();
    }

    @Override
    public int size()
    {
        return underlyingQueue.size();
    }

    @Override
    public abstract boolean offer(Object o);

    @Override
    public QueueElement peek()
    {
        return underlyingQueue.peek();
    }

    @Override
    public QueueElement poll()
    {
        return underlyingQueue.poll();
    }
    
    /**
     * Removes the specified object of the priority queue.
     * 
     * @param o the object to be removed.
     * @return true if the object is in the priority queue, false if the object
     *         is not in the priority queue.
     */
    @Override    
    public synchronized boolean remove(Object o) {
       throw new UnsupportedOperationException("Cannot remove an object from the queue, only by getting the head of the queue an object can be removed");
    }

    public long getBufferingTime()
    {
        return bufferingTime;
    }


    public synchronized void setBufferingTime(long bufferingTime)
    {
        this.bufferingTime = bufferingTime;
    }
    
    @Override
    public String getListenerId()
    {
        return AgentQueuesManager.getQueueName(agentName, contextName);
    }
    
    @Override
    public synchronized Object onTimer(Object additionalInformation) throws Exception
    {
        AgentQueuesManager queueManager = AgentQueuesManager.getInstance();
        IEventHandler eventHandler = queueManager.getEventHandler();
        ITimerServices timerService = queueManager.getTimerServices();
        
        
        logger.entering(getClass().getName(), "onTimer");
        // remove the head element, check the timer, pass to processing
        //do so until meet a first element who is not supposed to be processed yet
        //create the new timer for the queue        
        logger.fine("Checking in a loop if the queue: "+agentName+";"+contextName+"with buff time" + bufferingTime+" head element timer has expired...");
        QueueElement headElement = this.peek();
        long currentTimestamp = Calendar.getInstance().getTimeInMillis();
        while ((headElement != null) && (headElement.getTimestamp()+bufferingTime <= currentTimestamp))
        {
            
            QueueElement head = this.poll(); //remove the element from the queue
            ITimedObject timedObject;
           
            timedObject = head.getTimedObject();
            logger.fine("The head element is timed object: "+timedObject+" passing for handling ...");
            eventHandler.handleEventInstance(timedObject, agentName, contextName);
            
            headElement = this.peek();
            currentTimestamp = Calendar.getInstance().getTimeInMillis();
        }
        
        
        //in case some items are left in the queue, create a new timer on the queue
        if (headElement != null)
        {            
          //create a new timer on the remaining element
            long nextEventTimestamp = headElement.getTimestamp();
            //create the timer for the queue
            currentTimestamp = Calendar.getInstance().getTimeInMillis();
            long timeDifference = bufferingTime - (currentTimestamp- nextEventTimestamp);
            logger.fine("Some elements are still left in the queue: "+headElement+", creating a new timer for "+timeDifference+ "ms");
            try
            {
                timerService.createTimer(timerListener, Calendar.getInstance().getTime(), false, timeDifference, -1);
            }
            catch (TimerServiceException e)
            {
                throw new AgentQueueException("Could not create a timer for the channel queue: "+getListenerId()+", reason: "+e.getMessage());
            }
        }
        
        logger.exiting(getClass().getName(), "onTimer");
        return null; //the return value is not important in  case of this listener
    }
    
    
    public ContextIntervalPolicyEnum getInitiationInterPolicy()
    {
        return initiationInterPolicy;
    }


    public ContextIntervalPolicyEnum getTerminationInterPolicy()
    {
        return terminationInterPolicy;
    }
    
    /* (non-Javadoc)
     * @see java.util.Queue#element()
     */
    @Override
    public Object element()
    {
        return underlyingQueue.element();
    }


    /* (non-Javadoc)
     * @see java.util.Queue#remove()
     */
    @Override
    public Object remove()
    {
        throw new UnsupportedOperationException("Cannot remove an object from the queue, only by getting the head of the queue an object can be removed");
    }


    /* (non-Javadoc)
     * @see java.util.Collection#add(java.lang.Object)
     */
    @Override
    public boolean add(Object object)
    {
        return offer(object);
    }


    /* (non-Javadoc)
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection collection)
    {
        if (null == collection) {
            throw new NullPointerException();
        }
        if (this == collection) {
            throw new IllegalArgumentException();
        }
        boolean result = false;
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            if (add(it.next())) {
                result = true;
            }
        }
        return result;
    }


    /* (non-Javadoc)
     * @see java.util.Collection#clear()
     */
    @Override
    public void clear()
    {
        underlyingQueue.clear();
        
    }


    /* (non-Javadoc)
     * @see java.util.Collection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object object)
    {
        return underlyingQueue.contains(object);
    }


    /* (non-Javadoc)
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection collection)
    {
        return underlyingQueue.containsAll(collection);
    }


    /* (non-Javadoc)
     * @see java.util.Collection#isEmpty()
     */
    @Override
    public boolean isEmpty()
    {
        return underlyingQueue.isEmpty();
    }


    /* (non-Javadoc)
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection collection)
    {
        throw new UnsupportedOperationException("Cannot remove an object from the queue, only by getting the head of the queue an object can be removed");
    }


    /* (non-Javadoc)
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection collection)
    {
        throw new UnsupportedOperationException("Cannot remove an object from the queue, only by getting the head of the queue an object can be removed");
    }


    /* (non-Javadoc)
     * @see java.util.Collection#toArray()
     */
    @Override
    public Object[] toArray()
    {
        return underlyingQueue.toArray();
    }


    /* (non-Javadoc)
     * @see java.util.Collection#toArray(T[])
     */
    @Override
    public Object[] toArray(Object[] array)
    {
        return underlyingQueue.toArray(array);
    }
}
