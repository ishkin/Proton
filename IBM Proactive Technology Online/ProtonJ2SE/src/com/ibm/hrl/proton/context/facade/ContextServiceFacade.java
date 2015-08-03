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





import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.hrl.proton.context.exceptions.ContextCreationException;
import com.ibm.hrl.proton.context.exceptions.ContextServiceException;
import com.ibm.hrl.proton.context.management.AdditionalInformation;
import com.ibm.hrl.proton.context.management.AdditionalInformation.NotificationTypeEnum;
import com.ibm.hrl.proton.context.management.CompositeContextInstance;
import com.ibm.hrl.proton.context.management.ContextInitiationNotification;
import com.ibm.hrl.proton.context.management.SegmentationValue;
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
    
   
    ITimerServices timerServices;   
    IEventHandler eventHandler;
    ContextMetadataFacade contextMetadata;
    ContextStateManager contextStateManager;
    
    private static Logger logger = LoggerFactory.getLogger(ContextServiceFacade.class);
    
    public ContextServiceFacade(ITimerServices timerService, IEventHandler eventHandler,ContextMetadataFacade contextMetadata) 
    	throws ContextCreationException {       

        this.eventHandler = eventHandler;
    	this.timerServices = timerService;
    	this.contextMetadata = contextMetadata;
    	this.contextStateManager = new ContextStateManager(null);
    	initializeCompositeContextInstances();
    }
    
   
    /**
     * Initializes CompositeContextInstance for each context-agent pair.
     * For contexts with system startup creates ContextInitiationNotification which is immediately
     * processed, for contexts with absolute time initiator(s) generates timers
     * @throws	ContextCreationException
     */    
    public synchronized void initializeCompositeContextInstances() throws ContextCreationException {
    	
        // create CompositeContextInstance(s) for all context-agent pairs
 
    	logger.debug("initializeCompositeContextInstances: getting context definitions from context metadata facade...");
    	Map<String,Collection<IEventProcessingAgent>> contexts =
    		contextMetadata.getContextDefinitions();
    	
    	logger.debug("initializeCompositeContextInstances:  context definitions from context metadata facade: "+contexts);
    	ContextStateManager stateManager = this.contextStateManager;
    	logger.debug("initializeCompositeContextInstances: the context state manager is: "+contextStateManager);

    	try {
	    	for (String contextName: contexts.keySet()) {	    			    		
	    		IContextType contextType = contextMetadata.getContext(contextName);
	    		Collection<IEventProcessingAgent> agents = contexts.get(contextName);
	    		for (IEventProcessingAgent agentType: agents) {
	    			logger.debug("initializeCompositeContextInstances: creating new context instance for context"+contextName+" and agent"+agentType);
		    		CompositeContextInstance context = new CompositeContextInstance(
		    				contextType,agentType,this);	    		
		    		stateManager.addContextInstance(context);
		    		logger.debug("initializeCompositeContextInstances: created new context instance for context"+contextName+" and agent"+agentType+" and added to state");
		    		// make unique initiation arrangements (startup and absolute time start)
		    		
		    		// if starts at systems startup - create notification and call processInstance
		    		// single temporal dimension that starts at startup is enough
		    		if (context.hasSystemStartupInitiator()) {
		    			IContextNotification notification = new ContextInitiationNotification(
		    					contextType.getName(), System.currentTimeMillis(),System.currentTimeMillis(),
		    					TemporalContextType.atStartupId,agentType.getName());
		    		
		    			logger.debug("initializeCompositeContextInstances: the context"+contextType.getName()+" has system startup initiator: creating initiation notification: "+notification);
		    			processEventInstance(notification,contextType.getName(),agentType.getName());
		    			logger.debug("initializeCompositeContextInstances: submitted context initiation notification for context: "+contextType.getName()+", "+notification);
		    		}
		    		
		    		// if there is absolute time initiator we create a timer
		    		if (context.hasAbsoluteTimeInitiator()) {
		    			logger.debug("initializeCompositeContextInstances: the context"+contextType.getName()+" has absolute time initiator, creating new timer");
		    			Collection<ContextAbsoluteTimeInitiator> initiators =
		    				context.getAbsoluteTimeInitiators();
		
		    			for (ContextAbsoluteTimeInitiator initiator: initiators) {
		    				// create timer for each repeating absolute time initiator
		    				AdditionalInformation info = new AdditionalInformation(contextType.getName(),
		    						agentType.getName(),initiator.getId(),
		    						NotificationTypeEnum.INITIATOR,new SegmentationValue());
	
		    				long initiationTime = initiator.getInitiationTime().getTime();
		    				long duration = initiationTime - System.currentTimeMillis();
		    				long repetitionPeriod = initiator.getRepeatingInterval();
		    				boolean isRepetitive = initiator.isRepeating();
		    				
		    				timerServices.createTimer(context,info,isRepetitive,
		    						duration,repetitionPeriod);	
		    				logger.debug("initializeCompositeContextInstances: created new timer for absolute time initiator for context"+context);
		    			}
		    		}
		    	}  
	    	}
    	}
    	catch (Exception e) {
    		logger.error("Error initializing Context");
    		throw (new ContextCreationException(e.getMessage(),e.getCause()));
    	}
    	logger.debug("initializeCompositeContextInstance: exit");
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
           	
    	logger.debug("processEventInstance: "+timedObject+" for context: "+
    			contextName+" agent name: "+agentName);
    	    	
    	Collection<Pair<String,Map<String,Object>>> terminatedPartitions = new HashSet<Pair<String,Map<String,Object>>>();
    	Collection<Pair<String,Map<String,Object>>> participatingPartitions = new HashSet<Pair<String,Map<String,Object>>>();    	
    	ContextMetadataFacade metadata = this.contextMetadata;
    	
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
    	
    	ContextStateManager stateManager = this.contextStateManager;
    	logger.debug("processEventInstance: getting context instnace for context: "+
    			contextName+" agent name: "+agentName);
    	CompositeContextInstance cInstance = stateManager.getContextInstance(
    			contextName,agentName);
    	
    	// we currently enforce processing order: terminate, initiate, participate
    	if (roles.contains(EventRoleInContextEnum.TERMINATOR)) {
    		logger.info("processEventInstance: the role of the event is terminator, terminating partitions...");
    		terminatedPartitions = cInstance.processContextTerminator(timedObject);
    		logger.info("processEventInstance: terminated partitions: "+terminatedPartitions);
    	}

    	if (roles.contains(EventRoleInContextEnum.INITIATOR)) {
    		logger.debug("processEventInstance: the role of the event is initiator, initiating partitions...");
    		// we only return terminated and participating partitions
    		cInstance.processContextInititiator(timedObject);    	
    	}
    	
    	if (roles.contains(EventRoleInContextEnum.PARTICIPANT)) {
    		logger.debug("processEventInstance: the role of the event is participant");
    		assert (timedObject instanceof IEventInstance);
    		participatingPartitions = cInstance.processContextParticipant(
    				(IEventInstance)timedObject);
    		logger.debug("processEventInstance: the object"+timedObject+" participates in partitions: "+participatingPartitions);
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
    
    public void clearState(){
    	contextStateManager.clearState();
    }
}
