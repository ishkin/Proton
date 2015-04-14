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
package com.ibm.hrl.proton.runtime.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.runtime.context.notifications.IContextNotification;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.epa.AgentQueueMetadata;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.containers.Pair;

/**
 * A view into metadata - routing information
* <code>RoutingMetadataFacade</code>.
* 
*
 */
public class RoutingMetadataFacade{

	private static  RoutingMetadataFacade instance = null;
	/**A Map where the key is the event type name, the value is a set of objects consisting of agent name and context name */
	/**For initiator/terminator the context name is the one they initiate/terminate, the agent name is all
	 * the agents defined on this context
	 * For participant this is the agent name whom this event is an input to, and the context name this 
	 * agent is defined on */
	private Map<String,Set<Pair<String,String>>> eventsRoutingInfo;
	
	/** A map where the key is the agent name, and the value is the queue definitions for the agent (buffering time, priority ordering)*/
	private Map<String,AgentQueueMetadata> agentQueueInfo;
	
	/**
	 * Mapping between context name to all agent names defined on this context
	 */
	private Map<String,Set<Pair<String,String>>> contextAgentMapping;
	
	/**
	 * Events which are intended for being routed to consumers
	 */
	private Set<String> consumerEvents;

	Logger logger = Logger.getLogger(getClass().getName());
	
	
	private RoutingMetadataFacade(Map<String,Set<Pair<String,String>>> channelAgentContext,
	                              Map<String,AgentQueueMetadata> agentChannelInfo,
	                              Set<String> consumerEvents,
	                              Map<String,Collection<IEventProcessingAgent>> contextAgentMapping)
	{
		this.eventsRoutingInfo = new HashMap<String,Set<Pair<String,String>>>(channelAgentContext);		
		this.agentQueueInfo = new HashMap<String,AgentQueueMetadata>( agentChannelInfo);
		this.consumerEvents = new HashSet<String>(consumerEvents);
		deriveContextAgentMapping(contextAgentMapping);
		
	}
	
	/**
     * @param contextAgentMapping2
     */
    private void deriveContextAgentMapping(
            Map<String, Collection<IEventProcessingAgent>> contextToAgents)
    {
       //create the same mapping as for events
        this.contextAgentMapping = new HashMap<String,Set<Pair<String,String>>>();
        for (Map.Entry<String, Collection<IEventProcessingAgent>> entry : contextToAgents.entrySet())
        {
            String contextName = entry.getKey();
            Collection<IEventProcessingAgent> agentsPerContext = entry.getValue();
            
            Set<Pair<String,String>> contextAgentPairs = new HashSet<Pair<String,String>>();
            contextAgentMapping.put(contextName, contextAgentPairs);
            for (IEventProcessingAgent agent : agentsPerContext)
            {
                Pair<String,String> agentContextPair = new Pair<String,String>(agent.getName(),contextName);
                contextAgentPairs.add(agentContextPair);
            }
        }
        
    }

    /**
	 * This method is called at system startup when parsing the definitions  - to initialize this singleton 
	 * with routing metadata
	 * @param metadata
	 */
	public static void initializeChannels(Map<String,Set<Pair<String,String>>> channelAgentContext,
	                                    Map<String,AgentQueueMetadata> agentChannelInfo,
	                                    Set<String> consumerEvents,
	                                    Map<String,Collection<IEventProcessingAgent>> contextAgentMapping){
		instance = new RoutingMetadataFacade(channelAgentContext,agentChannelInfo,consumerEvents,contextAgentMapping);
	}
	
	/**
	 * Get instance of this singleton
	 * @return
	 */
	public static RoutingMetadataFacade getInstance(){
		return instance;
	}
	

	

	public AgentQueueMetadata getAgentQueueDefinitions(String agentName)
	{
	    return agentQueueInfo.get(agentName);
	}
	
	

	public boolean isConsumerEvent(String eventTypeName)
	{
	    return consumerEvents.contains(eventTypeName);
	}
	

	
	public Set<Pair<String,String>> determineRouting(ITimedObject timedObject)
	{
	    Set<Pair<String,String>> resultSet = null;
	    if (timedObject instanceof IContextNotification){
	        String contextName = ((IContextNotification)timedObject).getContextName();
	        String agentName = ((IContextNotification)timedObject).getAgentName();
	        resultSet = new HashSet<Pair<String,String>>();
	        resultSet.add(new Pair<String,String>(agentName,contextName));
	        //resultSet = contextAgentMapping.get(contextName);
	    }else
	    {
	        resultSet = eventsRoutingInfo.get(((IEventInstance)timedObject).getObjectName());
	    }
	    
	    return resultSet;
	}

    /**
     * Clear previous Action data (used by the parser before parsing a new project definition)
     */
	public static synchronized void clear() {
		instance = null;
	}
   
	

}
