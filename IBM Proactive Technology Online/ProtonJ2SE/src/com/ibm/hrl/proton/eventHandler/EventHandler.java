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

package com.ibm.hrl.proton.eventHandler;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.ibm.hrl.proton.agentQueues.exception.AgentQueueException;
import com.ibm.hrl.proton.agentQueues.exception.EventHandlingException;
import com.ibm.hrl.proton.context.exceptions.ContextServiceException;
import com.ibm.hrl.proton.context.facade.ContextServiceFacade;
import com.ibm.hrl.proton.epaManager.EPAManagerFacade;
import com.ibm.hrl.proton.epaManager.exceptions.EPAManagerException;
import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.router.EventRouter;
import com.ibm.hrl.proton.router.DataSenderException;
import com.ibm.hrl.proton.runtime.context.notifications.IContextNotification;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EPAManagerMetadataFacade;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.containers.Pair;

/**
 * <code>EventHandler</code>.
 * 
 * 
 */
public class EventHandler
    implements IEventHandler
{
    private static EventHandler instance;    
    private Logger logger = Logger.getLogger("EventHandler");
    
    
    private EventHandler(){
        
    }
    
    public static synchronized IEventHandler getInstance(){
        if (null == instance)
        {
            instance = new EventHandler(); 
        }
        
        return instance;
    }
   

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.agentQueue.eventHandler.IEventHandler#handleEventInstance(com.ibm.hrl.proton.runtime.timedObjects.ITimedObject, java.lang.String, java.lang.String)
     */
    @Override
    public void handleEventInstance(ITimedObject timedObject, String agentName,
            String contextName)
        throws EventHandlingException
    {
      //upon reply pass it to agent using the relevant partitions
        logger.fine("handleEventInstance: timed object: "+timedObject+", agent: "+agentName+" ,context: "+contextName);        
        Pair<Collection<Pair<String,Map<String,Object>>>, Collection<Pair<String,Map<String,Object>>>> partitions;
        try
        {
            partitions = ContextServiceFacade.getInstance().processEventInstance(timedObject, contextName, agentName);
        }
        catch (ContextServiceException e)
        {
            throw new EventHandlingException("Could not pass event instance "+timedObject+" to context service for agent"+agentName+" and context "+contextName+" ,reason: "+e.getMessage());
        }
        catch(Exception e){
        	System.out.println("Error in event handler: handleEventInstance");
        	e.printStackTrace();
        	throw new EventHandlingException("Could not pass event instance "+timedObject+" to context service for agent"+agentName+" and context "+contextName+" ,reason: "+e.getMessage());
        }
        Collection<Pair<String,Map<String,Object>>> terminatedPartitions = partitions.getFirstValue();
        Collection<Pair<String,Map<String,Object>>> participatingPartitions = partitions.getSecondValue();       
        logger.fine("handleEventInstance: context service returned the following partitions: "+ partitions);
       
        
        //if the timed object is context notification its job is done - it was passed to context service. 
        //however we need to make sure if as the result of that notification some partitions were actually
        //terminated - we need to inform the relevant agent instances
        
        if (timedObject instanceof IContextNotification) {
            if (terminatedPartitions != null && !terminatedPartitions.isEmpty())
            {
                try
                {
                    EPAManagerFacade.getInstance().processDefferedPartitions(agentName, terminatedPartitions);
                    return;
                }
                catch (EPAManagerException e)
                {
                    throw new EventHandlingException("Could not terminate partitions whose termination was timer-driven  for agent "+agentName+" and context "+contextName+" ,reason: "+e.getMessage());
                }
            }
            return;
        }
        
        IEventInstance event = (IEventInstance)timedObject;
        IEventProcessingAgent agentInstanceDef = EPAManagerMetadataFacade.getInstance().getAgentDefinition(agentName);
        List<Operand> eventOperands = agentInstanceDef.getEventInputOperands(event.getEventType());
        if (!(eventOperands == null || eventOperands.isEmpty()))
        {
            //this event is participant in the agent - pass the participation information
            if ((participatingPartitions != null) && !(participatingPartitions.isEmpty()))
            {
                try
                {
                    logger.fine("handleEventInstance: passing event for handling to EPA manager");
                    EPAManagerFacade.getInstance().processEvent(event, agentName, participatingPartitions);
                    
                    logger.fine("handleEventInstance: passed event for handling to EPA manager");
                    
                }
                catch (EPAManagerException e)
                {
                    throw new EventHandlingException("Could not pass event instance "+event+" to agent "+agentName+" and context "+contextName+" ,reason: "+e.getMessage());
                }
            }
        }
        
        if (terminatedPartitions != null && !terminatedPartitions.isEmpty())
        {
            try
            {
                EPAManagerFacade.getInstance().processDefferedPartitions(agentName, terminatedPartitions);
            }
            catch (EPAManagerException e)
            {
                throw new EventHandlingException("Could not terminate partitions whose termination initiated by event "+event+" for agent "+agentName+" and context "+contextName+" ,reason: "+e.getMessage());
            }
        }

    }

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.agentQueue.eventHandler.IEventHandler#routeContextNotification(com.ibm.hrl.proton.runtime.context.notifications.IContextNotification)
     */
    @Override
    public void routeContextNotification(
            IContextNotification contextNotification)
        throws EventHandlingException
    {
        try
        {
            EventRouter.getInstance().routeTimedObject(contextNotification);
        }
        catch (AgentQueueException e)
        {
            throw new EventHandlingException("Could not pass context notification on context"+ contextNotification.getContextName()+" to buffering queues, reason: "+e.getMessage());
        }
        catch (DataSenderException e)
        {
            throw new EventHandlingException("Could not pass context notification on context"+ contextNotification.getContextName()+" to buffering queues, reason: "+e.getMessage());
        }
        

    }

}
