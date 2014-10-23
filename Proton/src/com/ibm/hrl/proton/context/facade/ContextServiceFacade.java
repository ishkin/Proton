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
package com.ibm.hrl.proton.context.facade;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.ibm.hrl.proton.context.exceptions.ContextCreationException;
import com.ibm.hrl.proton.context.exceptions.ContextServiceException;
import com.ibm.hrl.proton.context.management.AdditionalInformation;
import com.ibm.hrl.proton.context.management.CompositeContextInstance;
import com.ibm.hrl.proton.context.management.ContextInitiationNotification;
import com.ibm.hrl.proton.context.management.AdditionalInformation.NotificationTypeEnum;
import com.ibm.hrl.proton.context.state.ContextStateManager;
import com.ibm.hrl.proton.eventHandler.IEventHandler;
import com.ibm.hrl.proton.metadata.context.ContextAbsoluteTimeInitiator;
import com.ibm.hrl.proton.metadata.context.TemporalContextType;
import com.ibm.hrl.proton.metadata.context.enums.EventRoleInContextEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.runtime.context.notifications.IContextInitiationNotification;
import com.ibm.hrl.proton.runtime.context.notifications.IContextNotification;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.ContextMetadataFacade;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.containers.Pair;
import com.ibm.hrl.proton.utilities.timerService.ITimerServices;

/**
 * Context service facade - providing event handling and timer services,
 * as well as processing event instance for certain agent in a certain context.
 * <code>ContextServiceFacade</code>.
 * 
 */
public class ContextServiceFacade implements IContextService
{
    
    private static ContextServiceFacade instance;
    ITimerServices timerServices;   
    IEventHandler eventHandler;
    
    Logger logger = Logger.getLogger(getClass().getName());
    
    private ContextServiceFacade(ITimerServices timerService, IEventHandler eventHandler) 
    	throws ContextCreationException {       

        this.eventHandler = eventHandler;
    	this.timerServices = timerService;
    }
    
    public synchronized void  cleanUpState(){
    	if (instance != null){
    		instance = null;
    	}
    }
    /**
     * Initializes CompositeContextInstance for each context-agent pair.
     * For contexts with system startup creates ContextInitiationNotification which is immediately
     * processed, for contexts with absolute time initiator(s) generates timers
     * @throws	ContextCreationException
     */    
    public synchronized void initializeCompositeContextInstances() throws ContextCreationException {
    	
        // create CompositeContextInstance(s) for all context-agent pairs
 
    	Map<String,Collection<IEventProcessingAgent>> contexts =
    		ContextMetadataFacade.getInstance().getContextDefinitions();
    	ContextMetadataFacade contextMetadata = ContextMetadataFacade.getInstance();
    	ContextStateManager stateManager = ContextStateManager.getInstance();

    	try {
	    	for (String contextName: contexts.keySet()) {	    			    		
	    		IContextType contextType = contextMetadata.getContext(contextName);
	    		Collection<IEventProcessingAgent> agents = contexts.get(contextName);
	    		for (IEventProcessingAgent agentType: agents) {
		    		CompositeContextInstance context = new CompositeContextInstance(
		    				contextType,agentType);	    		
		    		stateManager.addContextInstance(context);
		    		
		    		// make unique initiation arrangements (startup and absolute time start)
		    		
		    		// if starts at systems startup - create notification and call processInstance
		    		// single temporal dimension that starts at startup is enough
		    		if (context.hasSystemStartupInitiator()) {
		    			IContextNotification notification = new ContextInitiationNotification(
		    					contextType.getName(), System.currentTimeMillis(),System.currentTimeMillis(),
		    					TemporalContextType.atStartupId,agentType.getName());
		    			
		    			processEventInstance(notification,contextType.getName(),agentType.getName());
		    		}
		    		
		    		// if there is absolute time initiator we create a timer
		    		if (context.hasAbsoluteTimeInitiator()) {
		    			Collection<ContextAbsoluteTimeInitiator> initiators =
		    				context.getAbsoluteTimeInitiators();
		
		    			for (ContextAbsoluteTimeInitiator initiator: initiators) {
		    				// create timer for each repeating absolute time initiator
		    				AdditionalInformation info = new AdditionalInformation(contextType.getName(),
		    						agentType.getName(),initiator.getId(),
		    						NotificationTypeEnum.INITIATOR);
	
		    				long initiationTime = initiator.getInitiationTime().getTime();
		    				long duration = initiationTime - System.currentTimeMillis();
		    				long repetitionPeriod = initiator.getRepeatingInterval();
		    				boolean isRepetitive = initiator.isRepeating();
		    				
		    				timerServices.createTimer(context,info,isRepetitive,
		    						duration,repetitionPeriod);	
		    			}
		    		}
		    	}  
	    	}
    	}
    	catch (Exception e) {
    		throw (new ContextCreationException(e.getMessage(),e.getCause()));
    	}
    }

    /**
     * Returns the instance of context service facade
	 * @param 	timerService
	 * @param 	eventHandler
     * @throws 	ContextServiceException
     * @return 	ContextServiceFacade
     */   
    public synchronized static ContextServiceFacade initializeInstance(
    		ITimerServices timerService,IEventHandler eventHandler) throws ContextServiceException {
    	
        if (null == instance){
            instance = new ContextServiceFacade(timerService,eventHandler);
            instance.initializeCompositeContextInstances();
        }
        return instance;
    }
    
    public synchronized static ContextServiceFacade getInstance()
    {
        return instance;
    }
    
    /**
     * The main interface of the ContextServiceFacade - processing event instance in a given
     * context and for given agent. First event instance roles in this context and for agent are
     * calculated (e.g., initiator and participant), then the relevant context instance is retrieved,
     * and finally cintext's functions are invoked according to roles calculated.
     * 
     * Currently, this method assumes half-open temporal context interval (which determines the
     * processing order - terminate, initiate, participate); that should be configurable.
     * 
	 * @param 	timedObject - can be either event instance or context notification
	 * @param 	contextName - context the object should be processed in
	 * @param 	agentName - agent associated with the context, in case the timedObject is event instance,
	 * it can be this agent's participant
     * @throws 	ContextServiceException
     * @return 	Pair<Collection<String>,Collection<String>> - list of partition ids this object
     * participates in (falls into), and list of partition ids this object terminates
     */            
    public Pair<Collection<Pair<String,Map<String,Object>>>,Collection<Pair<String,Map<String,Object>>>> processEventInstance(ITimedObject timedObject,
    		String contextName, String agentName) throws ContextServiceException {
           	
    	//logger.fine("processEventInstance: "+timedObject+" for context: "+
    	//		contextName+" agent name: "+agentName);
    	    	
    	Collection<Pair<String,Map<String,Object>>> terminatedPartitions = new HashSet<Pair<String,Map<String,Object>>>();
    	Collection<Pair<String,Map<String,Object>>> participatingPartitions = new HashSet<Pair<String,Map<String,Object>>>();    	
    	ContextMetadataFacade metadata = ContextMetadataFacade.getInstance();
    	
    	assert (timedObject instanceof IContextNotification ||
    			timedObject instanceof IEventInstance);
    	
    	Set<EventRoleInContextEnum> roles = new HashSet<EventRoleInContextEnum>();
    	// if the received object is a context notification on timers
    	if (timedObject instanceof IContextNotification) {
    	    if (timedObject instanceof IContextInitiationNotification) {
    	        roles.add(EventRoleInContextEnum.INITIATOR);               
            }
    	    else { // not initiator but context notification - for surely terminator
                roles.add(EventRoleInContextEnum.TERMINATOR);
            }
    	} else { // event is a regular event (IEventInstance)    	    
    	    roles = metadata.calculateEventRoles(contextName,agentName,
    	    		(IEventInstance)timedObject);
    	}    	
    	
    	ContextStateManager stateManager = ContextStateManager.getInstance();
    	CompositeContextInstance cInstance = stateManager.getContextInstance(
    			contextName,agentName);
    	
    	// we currently enforce processing order: terminate, initiate, participate
    	if (roles.contains(EventRoleInContextEnum.TERMINATOR)) {
    		terminatedPartitions = cInstance.processContextTerminator(timedObject);
    	}

    	if (roles.contains(EventRoleInContextEnum.INITIATOR)) {
    		// we only return terminated and participating partitions
    		cInstance.processContextInititiator(timedObject);    	
    	}
    	
    	if (roles.contains(EventRoleInContextEnum.PARTICIPANT)) {
    		assert (timedObject instanceof IEventInstance);
    		participatingPartitions = cInstance.processContextParticipant(
    				(IEventInstance)timedObject);
    	}    	
    	
    	return new Pair<Collection<Pair<String,Map<String,Object>>>,Collection<Pair<String,Map<String,Object>>>>(terminatedPartitions,
    			participatingPartitions);        
    }
    
	public ITimerServices getTimerServices()
    {
        return timerServices;
    }

    public IEventHandler getEventHandler()
    {
        return eventHandler;
    }
}
