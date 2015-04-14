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
package com.ibm.hrl.proton.epaManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.ibm.hrl.proton.epa.state.IEPAStateManager;
import com.ibm.hrl.proton.epaManager.agentInstances.EventProcessingAgentInstance;
import com.ibm.hrl.proton.epaManager.agentInstances.StatefulProcessingAgentInstance;
import com.ibm.hrl.proton.epaManager.exceptions.EPAManagerException;
import com.ibm.hrl.proton.epaManager.exceptions.EPAManagerLogicExecutionException;
import com.ibm.hrl.proton.epaManager.exceptions.EPAManagerSubmitException;
import com.ibm.hrl.proton.epaManager.state.EPAStateManager;
import com.ibm.hrl.proton.metadata.epa.enums.EPATypeEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.router.IEventRouter;
import com.ibm.hrl.proton.runtime.epa.interfaces.IEventProcessingAgentInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EPAManagerMetadataFacade;
import com.ibm.hrl.proton.utilities.asynchronousWork.AsynchronousExecutionException;
import com.ibm.hrl.proton.utilities.asynchronousWork.IWorkManager;
import com.ibm.hrl.proton.utilities.containers.Pair;
import com.ibm.hrl.proton.utilities.persistence.IPersistenceManager;

public abstract class BaseEPAManagerFacade implements IEPAManager{
	
    /**
     * Map for holding the mapping between agent names, and internal map holding agent instances names (per
     * context partitions) and queued event instances. The internal entries are deleted when all event instances
     * in the queue are processed, entries will be created again if event instances for the partition arrive.
     */
    protected Map<String,Map<String,IEventProcessingAgentInstance>> agentInstance;
   
    /**Work manager for creating asynchronous work */
    protected IWorkManager wm;
    /**Event router for routing derived events back into the system */
    private IEventRouter eventRouter;
        
    /** State manager created on top of persistence manager, containing EPA level state management business methods*/
    private IEPAStateManager stateManager;
    private IPersistenceManager persistenceManager;
    
    private Logger logger = Logger.getLogger(getClass().getName());
    protected EPAManagerMetadataFacade metadataFacade;
  
   
    protected BaseEPAManagerFacade(IWorkManager wm, IEventRouter eventRouter,IPersistenceManager persistenceManager)
    {        
        metadataFacade = EPAManagerMetadataFacade.getInstance();
        Collection<String> agentNames = metadataFacade.getAgentNames();
        
        logger.fine("EPAManagerFacade: creating the internal agent list from agent definitions");
        agentInstance = new HashMap<String,Map<String,IEventProcessingAgentInstance>>(agentNames.size());
        //initialize the map with agent names
        for (String agentName : agentNames)
        {
            logger.fine("EPAManagerFacade: adding agent : "+ agentName+" to agent definitions");
            Map<String,IEventProcessingAgentInstance> agentInstances = new HashMap<String,IEventProcessingAgentInstance>();
            agentInstance.put(agentName, agentInstances);
        }
        
        this.wm = wm;
        this.eventRouter = eventRouter;      
        this.stateManager = new EPAStateManager(persistenceManager);
        this.persistenceManager = persistenceManager;
    }
    
     
    protected IEventRouter getEventRouter()
    {
        return eventRouter;
    }
    
    protected void clear(){    	
    	for (Map<String,IEventProcessingAgentInstance> agents : agentInstance.values()) {
    		for (IEventProcessingAgentInstance agent : agents.values()) {
				agent.clearState();
			}
		}
    	this.agentInstance.clear();
    }
    
    /**
     * Process the termination of all the specified partitions
     * @param agentName
     * @param partitions
     * @throws EPAManagerException
     */
    public void processDefferedPartitions(String agentName, Collection<Pair<String,Map<String,Object>>> partitions) throws EPAManagerException
    {
    	
        for (Pair<String,Map<String,Object>> partition : partitions)
        {
            processDeffered(agentName,partition.getFirstValue(),partition.getSecondValue());
        }
    }
     
    
    @Override
    public abstract void processDeffered(String agentName, String partition,Map<String,Object> segmentationValues) throws EPAManagerException;
    
    /**
     * Get 
     * @param agentName
     * @return
     */
    public Map<String,IEventProcessingAgentInstance> getEPAInstances(String agentName){
        return agentInstance.get(agentName);        
    }
    
   
 

    /**
     * @param agentName
     * @param partition
     * @throws EPAManagerSubmitException
     */
    protected void submitEventInstance(IEventProcessingAgentInstance eventProcessingAgentInstance, IEventInstance eventInstance)
        throws EPAManagerSubmitException
    {
        logger.fine("submitEventInstance: submitting event instance "+eventInstance+ " asynchronousyly for processing by "+eventProcessingAgentInstance);
        try
        {
            EPAInstanceWorkItem epaInstanceWork = new EPAInstanceWorkItem(eventProcessingAgentInstance, eventInstance);        
            wm.runWork(wm.createWork(epaInstanceWork));
        }
        catch (AsynchronousExecutionException e)
        {
            throw new EPAManagerSubmitException("Could not submit event instance for EPA processing, reason: "+e.getMessage());
        }
    }
    
    @Override
    public abstract void processEvent(IEventInstance eventInstance, String agentName,
            Collection<Pair<String,Map<String,Object>>> contextPartitions) throws EPAManagerException;
    
    /**
     * Returns the state manager for the EPA manager module
     * @return
     */
    protected IEPAStateManager getStateManager()
    {
        return stateManager;
    }

    /**
     */
    
 
    protected IPersistenceManager getPersistenceManager()
    {
       return persistenceManager;
    }
}
