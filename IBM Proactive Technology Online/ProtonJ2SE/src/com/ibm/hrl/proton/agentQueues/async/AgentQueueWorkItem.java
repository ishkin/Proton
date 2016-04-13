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
package com.ibm.hrl.proton.agentQueues.async;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.ibm.hrl.proton.agentQueues.exception.AgentQueueException;
import com.ibm.hrl.proton.agentQueues.queues.QueueElement;
import com.ibm.hrl.proton.agentQueues.queuesManagement.AgentQueuesManager;
import com.ibm.hrl.proton.metadata.context.enums.ContextIntervalPolicyEnum;
import com.ibm.hrl.proton.metadata.context.enums.EventRoleInContextEnum;
import com.ibm.hrl.proton.runtime.metadata.RoutingMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.epa.AgentQueueMetadata;
import com.ibm.hrl.proton.runtime.metadata.epa.AgentQueueMetadata.OrderingPolicy;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.asynchronousWork.IWorkItem;



/**
 * Implementation of work item for the channel - for asynchronously submitting event instance to an agent queue for processing
* <code>ChannelWorkItem</code>.
* 
*
 */
public class AgentQueueWorkItem implements IWorkItem
{

    private String agentName;
    private String contextName;
    private ITimedObject timedObject;  
    
    /** Context event roles - determined the ordering of simultaneous events in sorted queues.  */
    private Set<EventRoleInContextEnum> eventRoles;
    private AgentQueuesManager manager;
    private RoutingMetadataFacade metadataFacade;
    
    public AgentQueueWorkItem(String agentName, String contextName,
            ITimedObject timedObject,Set<EventRoleInContextEnum> eventRoles,AgentQueuesManager manager,RoutingMetadataFacade metadataFacade)
    {
        super();
        this.agentName = agentName;
        this.contextName = contextName;
        this.timedObject = timedObject;        
        this.eventRoles =eventRoles;
        this.manager = manager;
        this.metadataFacade = metadataFacade;
    }


    @Override
    public void run()
    {        
        AgentQueueMetadata agentChannelMeta = metadataFacade.getAgentQueueDefinitions(agentName);
        LinkedBlockingQueue<QueueElement> channelQueue;
		try {
			channelQueue = manager.getAgentQueue(agentName, contextName);
		} catch (AgentQueueException e) {			
			e.printStackTrace();
			throw new RuntimeException("Error adding events to queue for agent:"+agentName+", reason: "+e.getMessage());
		}
                       
        long eventTimestamp;
        if (agentChannelMeta.getOrderingPolicy().equals(OrderingPolicy.DETECTION_TIME)){              
            eventTimestamp = timedObject.getDetectionTime();
        }
        else
        {            
            eventTimestamp = timedObject.getOccurenceTime(); 
        }
        
        QueueElement queueElement = new QueueElement(timedObject,eventTimestamp,ContextIntervalPolicyEnum.IRRELEVANT,ContextIntervalPolicyEnum.IRRELEVANT,eventRoles);
        channelQueue.add(queueElement);
    }

}
