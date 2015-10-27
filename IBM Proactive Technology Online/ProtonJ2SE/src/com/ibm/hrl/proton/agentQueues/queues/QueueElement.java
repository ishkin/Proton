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

import java.util.Set;
import java.util.logging.Logger;

import com.ibm.hrl.proton.metadata.context.enums.ContextIntervalPolicyEnum;
import com.ibm.hrl.proton.metadata.context.enums.EventRoleInContextEnum;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;


/**
* Element within the agent queue
* <code>QueueElement</code>.
* 
*
 */
public class QueueElement implements Comparable {
    /**Event instance */
	private ITimedObject timedObject;
	
	/**Timestamp according to which the sorting occures */
	private long timestamp; //the occurence/detection time of the element (depends on queue configuration)
	
	/**
	 * Inititiation interval policy for the queue representing the context.
	 * Will be null if not relevant (no initiation by event)
	 */
	private ContextIntervalPolicyEnum initiationInterPolicy;

     /**
     * Termination interval policy for the queue representing the context.
     * Will be null if not relevant (no termination by event)
     */

	private ContextIntervalPolicyEnum termIntervalPolicy;
	
	/**
	 * Context event roles - affect the ordering of simualtaneous events within a sorted queue
	 */
	private Set<EventRoleInContextEnum> eventRoles;
	
	private  static Logger logger = Logger.getLogger(QueueElement.class.getName());
	

    public QueueElement(ITimedObject timedObject, long timestamp,ContextIntervalPolicyEnum initiationInterPolicy,ContextIntervalPolicyEnum termIntervalPolicy,Set<EventRoleInContextEnum> eventRoles)
	{
	    this.timedObject = timedObject;
	    this.timestamp = timestamp;
	    this.initiationInterPolicy = initiationInterPolicy;
	    this.termIntervalPolicy = termIntervalPolicy;
	    this.eventRoles = eventRoles;
	}
	
	public ITimedObject getTimedObject() {
		return timedObject;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	@Override
	public int compareTo(Object o) {	
	        
	    QueueElement otherElement = (QueueElement)o;
		long diffElementTimestamp = otherElement.getTimestamp();
		
		logger.fine("compareTo: Comparing two elements in the queue: current event instance "+this.timedObject+" and other instance "+otherElement.getTimedObject());
		
		if (this.timestamp == diffElementTimestamp)
		{	
		    logger.fine("compareTo: the two elements are with equal timestamp, placing in the queue according to the context interval definitions and their context roles");
		    Set<EventRoleInContextEnum> thisElementRoles = getEventRoles();
            Set<EventRoleInContextEnum> otherElementRoles = otherElement.getEventRoles();
            logger.fine("compareTo: this event roles are: "+thisElementRoles+", second element roles: "+otherElementRoles);
            
		    //check according to context interval policy and event roles
		    if (initiationInterPolicy != null)
		    {
		        logger.fine("compareTo: initiation policy of the queue is not null, checking the initiation policy type...");
		        //interval open on initiation
		        if (initiationInterPolicy.equals(ContextIntervalPolicyEnum.OPEN))		            
		        {	
		            logger.fine("compareTo: initiation policy of the queue is OPEN...");	            
		            
		            //for interval open on initiation we first process the participant and then the initiator
		            if (thisElementRoles.contains(EventRoleInContextEnum.INITIATOR)){
		                logger.fine("The initiation interval policy is open, this element is the initiator and therefore returning '1'");
		                return 1;
		            }
		          //for interval open on initiation we first process the participant and then the initiator
                    if (otherElementRoles.contains(EventRoleInContextEnum.INITIATOR)){
                        logger.fine("The initiation interval policy is open, the other element is the initiator and therefore returning '-1'");
                        return -1;
                    }                  
		        }else
		            //interval closed on initiation
		        {
		          //for interval closed on initiation we first process the initiator and then the participant
		            logger.fine("compareTo: initiation policy of the queue is CLOSED...");
                    if (thisElementRoles.contains(EventRoleInContextEnum.INITIATOR))
                    {
                        logger.fine("The initiation interval policy is closed, this element is the initiator and therefore returning '-1'");
                        return -1;
                    }
                  //for interval open on initiation we first process the participant and then the initiator
                    if (otherElementRoles.contains(EventRoleInContextEnum.INITIATOR)){
                        logger.fine("The initiation interval policy is closed, the other element is the initiator and therefore returning '1'");
                        return 1;
                    }                    
		        }		        
		    }
		    
		    //check according to context interval policy and event roles
            if (termIntervalPolicy != null)
            {
                logger.fine("compareTo: termination policy of the queue is not null, checking the termination policy type...");
                //interval open on initiation
                if (termIntervalPolicy.equals(ContextIntervalPolicyEnum.OPEN))
                {
                    logger.fine("compareTo: termination policy of the queue is OPEN...");
                    //for interval open on initiation we first process the participant and then the initiator
                    if (thisElementRoles.contains(EventRoleInContextEnum.TERMINATOR)){
                        logger.fine("The termination interval policy is open, this element is the terminator and therefore returning '-1'");
                        return -1;
                    }
                  //for interval open on initiation we first process the participant and then the initiator
                    if (otherElementRoles.contains(EventRoleInContextEnum.TERMINATOR)){
                        logger.fine("The termination interval policy is open, the other element is the terminator and therefore returning '1'");
                        return 1;
                    }
                    
                }else
                    //interval closed on initiation
                {
                  //for interval closed on initiation we first process the initiator and then the participant
                    logger.fine("compareTo: termination policy of the queue is CLOSE...");
                    
                    if (thisElementRoles.contains(EventRoleInContextEnum.TERMINATOR)){
                        logger.fine("The termination interval policy is closed, this element is the terminator and therefore returning '1'");
                        return 1;
                    }
                  //for interval open on initiation we first process the participant and then the initiator
                    if (otherElementRoles.contains(EventRoleInContextEnum.TERMINATOR)){
                        logger.fine("The termination interval policy is closed, the other element is the terminator and therefore returning '-1'");
                        return -1;
                    }
                   
                }               
            }
		    return 0;
		}
		    
		
		if (this.timestamp < diffElementTimestamp)
		{
			return -1;
		}else
		{
			return 1;
		}			
		
	}
	
	public Set<EventRoleInContextEnum> getEventRoles()
    {
        return eventRoles;
    }

	@Override
	public String toString()
	{
	    return "QueueElement: event "+timedObject+", timestamp: "+timestamp;
	}
}
