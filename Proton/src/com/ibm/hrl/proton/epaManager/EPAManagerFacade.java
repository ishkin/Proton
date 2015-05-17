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


/**
 * J2SE implementation for EPAManager. 
* <code>EPAManagerFacade</code>.
* 
*
 */
public class EPAManagerFacade extends BaseEPAManagerFacade
{
	private static EPAManagerFacade instance;
	private Logger logger = Logger.getLogger(getClass().getName());
	
    private EPAManagerFacade(IWorkManager wm, IEventRouter eventRouter,IPersistenceManager persistenceManager)
    {        
        super(wm, eventRouter, persistenceManager);
    }
    

    /**
     * Return the instance  of channel queue manager
     */
    public synchronized static EPAManagerFacade initializeInstance(IWorkManager wm,IEventRouter eventRouter,IPersistenceManager persistenceManager)
    {
        if (null == instance)
        {
            instance = new EPAManagerFacade(wm,eventRouter,persistenceManager);  
        }
                   
        return instance;
    }
    
    public static EPAManagerFacade getInstance()
    {
        return instance;
    }
    
    
    public synchronized void clear(){
    	super.clear();
    	if (instance != null){
    		instance = null;
    	}    	
    }
   
    
    
    
    @Override
    public void processDeffered(String agentName, String partition,Map<String,Object> segmentationValues) throws EPAManagerException
    {
        Map<String,IEventProcessingAgentInstance> agentInstances = agentInstance.get(agentName);
        IEventProcessingAgentInstance eventProcessingAgentInstance = null;
        
        synchronized(agentInstances)
        {
            if (agentInstances.containsKey(partition))
            {
                eventProcessingAgentInstance = agentInstances.get(partition);
                agentInstances.remove(partition);
            }
        }
        
        EPATypeEnum agentType = metadataFacade.getAgentDefinition(agentName).getType();
        //in cast there is no agent instance need to determine if one should be created - 
        //for some stateful agents which should be processed in DEFFERED mode
        //we will not even register the instance within EPA manager  - just process the termination notification and finish
        if (eventProcessingAgentInstance == null)
        {
           IEventProcessingAgent agent = metadataFacade.getAgentDefinition(agentName);
     	   boolean needToInstantiate = EventProcessingAgentInstance.checkInstantiationNeed(agent);
     	   if (needToInstantiate)
     	   {     		  
     		  if (!agentType.equals(EPATypeEnum.BASIC)){
     			  eventProcessingAgentInstance = new StatefulProcessingAgentInstance(metadataFacade.getAgentDefinition(agentName), partition, getStateManager(), getPersistenceManager());
     		  }else
     		  {
     			 eventProcessingAgentInstance = new EventProcessingAgentInstance(metadataFacade.getAgentDefinition(agentName),partition,getStateManager(),getPersistenceManager());  
     		  }
     		  
     	   }
        }
        
       if (eventProcessingAgentInstance != null){
    	   EPAInstanceTerminateWorkItem terminatorWorkItem = new EPAInstanceTerminateWorkItem(eventProcessingAgentInstance);
           try
           {        	       
        	   synchronized (agentInstance) 
        	   {
        		   if (!agentType.equals(EPATypeEnum.BASIC) )
                   {
                   	  //update the agent context information
                	   ((StatefulProcessingAgentInstance)eventProcessingAgentInstance).setContextSegmentationValues(segmentationValues);
                   }
        	   }              
               wm.runWork(wm.createWork(terminatorWorkItem));
           }
           catch (AsynchronousExecutionException e)
           {
               throw new EPAManagerException("Could not perform termination for EPA" + agentName+ ", partition "+partition+", reason: "+e.getMessage());
           }
       }
       
             
            
        
    }
    
    
    
   
 

   
    
    @Override
    public void processEvent(IEventInstance eventInstance, String agentName,
            Collection<Pair<String,Map<String,Object>>> contextPartitions) throws EPAManagerException
    {
        logger.fine("processEvent: going to asynchronously pass event instance "+eventInstance+" to relevant agent instances");
        
        Map<String,IEventProcessingAgentInstance> agentInstances = agentInstance.get(agentName);
        synchronized(agentInstances)
        {
        	
            for (Pair<String,Map<String,Object>> partitionInformation : contextPartitions)
            {
            	String partition = partitionInformation.getFirstValue();
            	Map<String,Object> segmentationValues = partitionInformation.getSecondValue();
                logger.fine("passEventAgentInstance: receive event "+eventInstance+" for agent "+agentName+ " partition: "+partition);
                if (!agentInstances.containsKey(partition))
                {
                    logger.fine("passEventAgentInstance: EventProcessingAgent for such a partition do not exist, creating...");
                    IEventProcessingAgentInstance eventProcessingAgentInst;
                    EPATypeEnum agentType = metadataFacade.getAgentDefinition(agentName).getType();
                    try
                    {
                    	if (!agentType.equals(EPATypeEnum.BASIC) ){
                    		eventProcessingAgentInst = new StatefulProcessingAgentInstance(metadataFacade.getAgentDefinition(agentName),partition,getStateManager(),getPersistenceManager());
                    	}else
                    	{
                    		eventProcessingAgentInst = new EventProcessingAgentInstance(metadataFacade.getAgentDefinition(agentName),partition,getStateManager(),getPersistenceManager());
                    	}
                    	
                        agentInstances.put(partition, eventProcessingAgentInst);
                        logger.fine("passEventAgentInstance: EventProcessingAgent created...");
                    }
                    catch (EPAManagerLogicExecutionException e)
                    {
                        throw new EPAManagerSubmitException("Could not submit work for agent " + agentName +" , instance "+ partition+ ", reason: "+e.getMessage());
                    }                                       
                    
                }
                

                IEventProcessingAgentInstance eventProcessingInstance = agentInstances.get(partition);
                EPATypeEnum agentType = eventProcessingInstance.getAgentType().getType();
                synchronized (agentInstance) 
         	   	{
         		   if (!agentType.equals(EPATypeEnum.BASIC) )
                    {
                    	  //update the agent context information
                 	   ((StatefulProcessingAgentInstance)eventProcessingInstance).setContextSegmentationValues(segmentationValues);
                    }
         	   	}     
                submitEventInstance(eventProcessingInstance, eventInstance);

            }
                        
        }
    }
    
   
}
