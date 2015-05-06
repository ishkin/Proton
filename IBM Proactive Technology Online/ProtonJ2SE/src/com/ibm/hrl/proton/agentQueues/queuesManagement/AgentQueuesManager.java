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
package com.ibm.hrl.proton.agentQueues.queuesManagement;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.ibm.hrl.proton.agentQueues.async.AgentQueueWorkItem;
import com.ibm.hrl.proton.agentQueues.exception.AgentQueueException;
import com.ibm.hrl.proton.agentQueues.queues.AgentAbstractQueue;
import com.ibm.hrl.proton.agentQueues.queues.AgentSortedQueue;
import com.ibm.hrl.proton.agentQueues.queues.AgentlNonSortedQueue;
import com.ibm.hrl.proton.eventHandler.IEventHandler;
import com.ibm.hrl.proton.metadata.context.enums.ContextIntervalPolicyEnum;
import com.ibm.hrl.proton.metadata.context.enums.EventRoleInContextEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;
import com.ibm.hrl.proton.metadata.context.interfaces.ITemporalContextType;
import com.ibm.hrl.proton.runtime.context.notifications.IContextInitiationNotification;
import com.ibm.hrl.proton.runtime.context.notifications.IContextNotification;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.ContextMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.IMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.RoutingMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.epa.AgentQueueMetadata;
import com.ibm.hrl.proton.runtime.metadata.epa.AgentQueueMetadata.SortingPolicy;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.asynchronousWork.AsynchronousExecutionException;
import com.ibm.hrl.proton.utilities.asynchronousWork.IWorkManager;
import com.ibm.hrl.proton.utilities.containers.Pair;
import com.ibm.hrl.proton.utilities.timerService.ITimerServices;

public class AgentQueuesManager {
    
    
    private static final String QUEUE_NAME_DELIMETER=";";
    
	
	/**The datastructure where it maps the agent name to priority queue */
	private Map<String,AgentAbstractQueue> registeredQueues;
	
	ITimerServices timerServices;	
    IEventHandler eventHandler;
	IWorkManager wm;
	ContextMetadataFacade contextMetadata;
	RoutingMetadataFacade routingMetadataFacade;
	private static Logger logger = Logger.getLogger(AgentQueuesManager.class.getName());
	
	/**
	 * 
	 * @param timerService
	 * @param eventHandler
	 * @param workManager
	 */
	public AgentQueuesManager(ITimerServices timerService,IEventHandler eventHandler,IWorkManager workManager,IMetadataFacade metadataFacade){
		//TODO: initialize the map to the agents size, so that resizing will not be required
	    registeredQueues = new ConcurrentHashMap<String,AgentAbstractQueue>();
		this.timerServices = timerService;
		this.eventHandler = eventHandler;
		this.wm = workManager;
		this.contextMetadata = metadataFacade.getContextMetadataFacade();
		this.routingMetadataFacade = metadataFacade.getRoutingMetadataFacade();
		
	}
	
	
	
	public ITimerServices getTimerServices()
    {
        return timerServices;
    }

    public IEventHandler getEventHandler()
    {
        return eventHandler;
    }

	
	public static String getQueueName(String agentName, String contextName){
	    return agentName+QUEUE_NAME_DELIMETER+contextName;
	}
	
	/**
	 * Used on timer expiration notification to fetch already existing queues on which those timers expired 
	 * @param queueName
	 * @return
	 */
	public AgentAbstractQueue getAgentQueue(String queueName)
	{
	    return registeredQueues.get(queueName);
	}
	/**
	 * Get the channel queue for the agent/context pair, create a new queue according to channel's definitions in the 
	 * case such a queue doesn't exist yet
	 * @param agentName
	 * @param contextName
	 * @return
	 */
	public AgentAbstractQueue getAgentQueue(String agentName, String contextName)
	{	
	   
	    String queueName = getQueueName(agentName, contextName);
		if (!registeredQueues.containsKey(queueName))
		{
			synchronized(registeredQueues){
				if (!registeredQueues.containsKey(queueName))
				{				   
				    
				    AgentQueueMetadata agentChannelMeta = routingMetadataFacade.getAgentQueueDefinitions(agentName);
			        long bufferingTime = agentChannelMeta.getBufferingTime();
			        SortingPolicy sortingPolicy = agentChannelMeta.getSortingPolicy();
			       
			        logger.fine("getAgentQueue: Creating a queue for agent: "+agentName+", context:"+contextName+", bufferingTime: "+bufferingTime+", sortingPolicy: "+sortingPolicy);
			        
			        //determine the interval initiaiton and interval termination policy for this queue representing a certain context
			        IContextType contextType = contextMetadata.getContext(contextName);
			        ContextIntervalPolicyEnum initIntervalPolicy = null;
			        ContextIntervalPolicyEnum terminationInterPolicy = null;
			        logger.fine("getAgentQueue: determining if the queue has initiation/termination interval policy");
			        if (contextType instanceof ITemporalContextType)
			        {			            			            
			            initIntervalPolicy = ((ITemporalContextType)contextType).getInitiatorIntervalPolicy();
			            terminationInterPolicy = ((ITemporalContextType)contextType).getTerminatorIntervalPolicy();
			            if (initIntervalPolicy.equals(ContextIntervalPolicyEnum.IRRELEVANT))
			            {
			                initIntervalPolicy = null;
			            }
			            if (terminationInterPolicy.equals(ContextIntervalPolicyEnum.IRRELEVANT))
                        {
			                terminationInterPolicy = null;
                        }
			            logger.fine("getAgentQueue: the queue for agent:"+agentName+" has the initiation interval policy of "+initIntervalPolicy+" and termination interval policy "+terminationInterPolicy);
			        }
			       
			        
				    AgentAbstractQueue channelQueue;
				    if (sortingPolicy.equals(SortingPolicy.SORTED)){
				        channelQueue = new AgentSortedQueue(contextName,agentName,bufferingTime,initIntervalPolicy,terminationInterPolicy,this);
				    }else
				    {
				        channelQueue = new AgentlNonSortedQueue(contextName,agentName,bufferingTime,initIntervalPolicy,terminationInterPolicy,this);   
				    }			
					registeredQueues.put(queueName, channelQueue);
				}
			}
		}
		return registeredQueues.get(queueName);
	}
	
	public void passEventToQueues(ITimedObject timedObject,Set<Pair<String,String>> agentsList) throws AgentQueueException
	{
	    Set<EventRoleInContextEnum> eventRoles = new HashSet<EventRoleInContextEnum>();	              
	    
	    if (agentsList == null) return; //no agents defined for this event
	    
	    //iterate over all the intended channels and add the event to the channel queue
	    logger.fine("passEventToQueues: going to asynchronously pass timed object "+timedObject+" to channel queues");
	    for (Pair<String, String> pair : agentsList)
        {
            String agentName = pair.getFirstValue();
            String contextName = pair.getSecondValue();
            logger.fine("passEventToQueues: submiting the timedObject"+timedObject+" to queue for agent "+agentName+" , context: "+contextName);

            //the submitting of event instance to differnet channel queues are done in parallel using asynchronous work interface
            //if event is simulated by context service (timed context initiation/termination creates event which is supposed to be 
            //submitted to agent queues, the actual initiation/termination will be done when this event reaches context service
            //such event already have context roles assigned by context service
            logger.fine("passEventToQueues: determining the event timedObject"+timedObject+" context roles for context: "+contextName);
            
            if (timedObject instanceof IContextNotification)
            {
                logger.fine("passEventToQueues: the timed object "+timedObject+" is a context notification sent by context service");
                //the object is notification on context initiation/termination
                if (timedObject instanceof IContextInitiationNotification)
                {
                    eventRoles.add(EventRoleInContextEnum.INITIATOR);               
                }
                else //not initiator but context notification - for surely terminator
                {
                    eventRoles.add(EventRoleInContextEnum.TERMINATOR);
                }
            }
            else
            {
                //real event
                eventRoles = contextMetadata.calculateEventRoles(contextName,agentName, (IEventInstance)timedObject);
            }
            AgentQueueWorkItem chWorkItem = new AgentQueueWorkItem(agentName,contextName,timedObject,eventRoles,this,routingMetadataFacade);
            try
            {
                wm.runWork(wm.createWork(chWorkItem));
            }
            catch (AsynchronousExecutionException e)
            {
                throw new AgentQueueException("Could not pass timedObject: "+timedObject+ "to channel queue "+ agentName+" , reason: "+e.getMessage());
            }
                                  
            logger.fine("passEventToQueues: submitted the timed object to queue for agent "+agentName+" , context: "+contextName);
        }
	}
	/**
	 * Asynchronously pass event instance to all relevant channel queues. The queues will place the event instane in the appropriate
	 * place and update their waiting timers if needed.
	 * @param eventInstance
	 * @param agentsList
	 * @throws AgentQueueException
	 */
	public void passEventToQueues(ITimedObject timedObject) throws AgentQueueException
	{
	    Set<Pair<String,String>> agentsList = routingMetadataFacade.determineRouting(timedObject);
	    passEventToQueues(timedObject, agentsList);
	}
	
	
	
	
	
	/**
	 * Update the channel queue buffering time
	 * @param agentName
	 * @param contextName
	 * @param newBufferingTime
	 */
	public void updateBufferingTime(String agentName, String contextName, long newBufferingTime)
	{	    
	    AgentAbstractQueue channelQueue = getAgentQueue(agentName, contextName);
	    channelQueue.setBufferingTime(newBufferingTime);
	}
	
	
	
}
