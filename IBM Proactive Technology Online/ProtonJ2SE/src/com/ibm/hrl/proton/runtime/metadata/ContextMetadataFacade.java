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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.ibm.hrl.proton.metadata.context.CompositeContextType;
import com.ibm.hrl.proton.metadata.context.ContextEventInitiator;
import com.ibm.hrl.proton.metadata.context.ContextEventTerminator;
import com.ibm.hrl.proton.metadata.context.enums.EventRoleInContextEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;
import com.ibm.hrl.proton.metadata.context.interfaces.ITemporalContextType;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
/**
 * Metadata class holding all context definitions in the system.
 * <code>ContextMetadataFacade</code>.
 * 
 *
 */
public class ContextMetadataFacade
{
        
    private static ContextMetadataFacade instance = null;
    /** Mapping between context type name and its definition */
    private Map<String,IContextType> contexts;
    
    private Map<String,Collection<IEventProcessingAgent>> contextAgents;
    
    private Map<String,Map<String,Set<EventRoleInContextEnum>>> eventRoles;
    Logger logger = Logger.getLogger(getClass().getName());
    
    /** Ctor - initialize the internal data structure */
    private ContextMetadataFacade(Map<String,IContextType> contextTypes,
    		Map<String,Collection<IEventProcessingAgent>> contextAgents)
    {
        this.contexts = new HashMap<String,IContextType>(contextTypes);
        this.contextAgents = contextAgents;
        
        eventRoles = new HashMap<String,Map<String,Set<EventRoleInContextEnum>>>();
        initializeEventRoles();
    }
    
    
    public void setContextAgents(Map<String,Collection<IEventProcessingAgent>> contextAgents)
    {
    	 this.contextAgents = contextAgents;
    }
    /**
     * Iterate over all defined context types, for temporal contexts determine if there are event
     * initiators/terminators and if there are add the relevant types to the list 
     */
    private void initializeEventRoles()    
    {
        logger.entering("ContextMetadataFacade", "initializeEventRoles");
        for (Map.Entry<String,IContextType> context: this.contexts.entrySet())
        {       
            IContextType rootContext = context.getValue();
            logger.finest("initializeEventRoles: analyzing context" + rootContext.getName());
            String contextName = context.getKey();
            
            
            if (rootContext instanceof ITemporalContextType)
            {
            	
            	checkEventRoles(contextName,rootContext);
            }
            else if (rootContext instanceof CompositeContextType)
            {
                for (IContextType contextType: ((CompositeContextType)rootContext).getMemberContexts())
                {
                	
                	// it is only relevant for temporal context (not segmentation)
    	            if (contextType instanceof ITemporalContextType)                
    	            {
    	            	checkEventRoles(contextName,contextType);
    	            }
    	        }

            }
            
            
        }
        logger.exiting("ContextMetadataFacade", "initializeEventRoles");        
    }

    /**
     * Extract event roles for the specified temporal context
     * @param contextName
     * @param contextType
     */
	private void checkEventRoles(String contextName, IContextType contextType) {
		logger.finest("checkEventRoles: The context "+ contextName+
				" is of type ITemporalContextType, checking event initiators/terminators...");
		
		ITemporalContextType temporalContext = (ITemporalContextType)contextType;
		List<ContextEventInitiator> eventInitiators = temporalContext.getEventInitiators();
		List<ContextEventTerminator> eventTerminators = temporalContext.getEventTerminators();
		logger.finest("initializeEventRoles: Context initiators: "+eventInitiators);
		logger.finest("initializeEventRoles: Context terminators: "+eventTerminators);
		if (temporalContext.getEventInitiators().size() != 0)
		{
		    //there are event initiators for this context, add them to the list
		    for (ContextEventInitiator contextEventInitiator : eventInitiators)
		    {
		        String eventName =  contextEventInitiator.getEventTypeName();
		        logger.finest("initializeEventRoles: event "+eventName+
		        		" is INITIATOR for context "+contextName);
		        addEventRole(eventName,contextName,EventRoleInContextEnum.INITIATOR);                        
		    } 
		}
		
		if (temporalContext.getEventTerminators().size() != 0)
		{
		    //there are event terminators for this context, add them to the list
		    for (ContextEventTerminator contextEventTerminator : eventTerminators)
		    {
		        String eventName =  contextEventTerminator.getEventTypeName();
		        logger.finest("initializeEventRoles: event "+eventName+
		        		" is TERMINATOR for context "+contextName);
		        addEventRole(eventName,contextName,EventRoleInContextEnum.TERMINATOR);                        
		    } 
		}
	}
    
    /**
     * Adding the given role of a specific event for a specific context to the data structure
     * @param eventName
     * @param contextName
     * @param contextRole
     */
    private void addEventRole(String eventName, String contextName, EventRoleInContextEnum contextRole)
    {
        if (!eventRoles.containsKey(eventName))
        {                            
            eventRoles.put(eventName, new HashMap<String,Set<EventRoleInContextEnum>>());
        }
        
        //get the map for the current event type
        Map<String,Set<EventRoleInContextEnum>> eventMap  = eventRoles.get(eventName);
        if (!eventMap.containsKey(contextName))
        {
            //the key for the current context do not exist yet, enter it now
            eventMap.put(contextName, new HashSet<EventRoleInContextEnum>());
        }
        Set<EventRoleInContextEnum> eventRole = eventMap.get(contextName);
        eventRole.add(contextRole);
    }

    /**
     * This method is called at system startup when parsing the definitions -
     * to initialize this singleton with context metadata
     * @param metadata
     */
    public static synchronized void initializeContext(Map<String,IContextType> contextTypes,
    		Map<String,Collection<IEventProcessingAgent>> contextAgents) {
        if (instance == null){
            instance = new ContextMetadataFacade(contextTypes,contextAgents); 
        }
        
    }
    
    
    public synchronized void cleanUpState()
    {
    	if (instance != null){
    		instance = null;
    	}
    }
    
    /**
     * Get instance of this singleton
     * @return
     */
    public static ContextMetadataFacade getInstance(){
        return instance;
    }
    
    /**
     * Return the context type object for the context by the specified name
     * @param contextName
     * @return
     */
    public IContextType getContext(String contextName){
        return contexts.get(contextName);
    }
    
    /**
     * Decide on event role of the given event in a given context
     * Determine event instance role in the context.
     * @param contextName
     * @param eventInstance
     * @return
     */
    public Set<EventRoleInContextEnum> calculateEventRoles(String contextName, String agentName,
    		IEventInstance eventInstance)
    {           
        //initialize to empty set
        Set<EventRoleInContextEnum> contextEventRoles = new HashSet<EventRoleInContextEnum>();
        String eventTypeName = eventInstance.getObjectName();
        
        logger.finest("calculateEventRoles: calculating event roles for event "+eventTypeName+
        		" and context: "+contextName);
        
        if (eventRoles.containsKey(eventTypeName))
        {
            Map<String,Set<EventRoleInContextEnum>> contextRoles = eventRoles.get(eventTypeName);
            if (contextRoles.containsKey(contextName)){
                contextEventRoles = contextRoles.get(contextName);
            }
        }
        
        // at this point we've calculated context related roles
        // now we check if this event is also agent participant (participant role)
        Collection<IEventProcessingAgent> agents = contextAgents.get(contextName);
        for (IEventProcessingAgent agent: agents) {
        	if (agent.getName().equals(agentName)) { // it is our agent
        		if (agent.getInputEvents().contains(eventInstance.getEventType())) {
        			contextEventRoles.add(EventRoleInContextEnum.PARTICIPANT);
        		}
        	}
        }
                
        logger.finest("calculateEventRoles: event roles for event "+
        		eventTypeName+" are: "+contextEventRoles);
        
        return contextEventRoles;
    }

	public Map<String,Collection<IEventProcessingAgent>> getContextDefinitions() {
		return contextAgents;
	}

    /**
     * Clear previous contexts data (used by the parser before parsing a new project definition)
     */
	public static synchronized void clear() {
		instance = null;
		
	}
    
}
