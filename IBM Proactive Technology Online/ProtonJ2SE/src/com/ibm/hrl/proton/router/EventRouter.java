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

package com.ibm.hrl.proton.router;

import java.util.logging.Logger;

import com.ibm.hrl.proton.agentQueues.exception.AgentQueueException;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.IMetadataFacade;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.facadesManager.IFacadesManager;

/**
 * <code>EventRouter</code>.
 * 
 * 
 */
public class EventRouter extends BaseEventRouter
{
      
    private static final Logger logger = Logger.getLogger("EventRouter");
    private IFacadesManager facadesManager;
    private IMetadataFacade metadataFacade;
    
    
    public EventRouter(IDataSender eventSender,IFacadesManager facadesManager2,IMetadataFacade metadataFacade2)
    {  
    	 super(eventSender);
    	this.facadesManager = facadesManager2;
    	this.metadataFacade = metadataFacade2;
      
    }
    
   
    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.agentQueue.eventRouter.IEventRouter#routeTimedObject(com.ibm.hrl.proton.runtime.timedObjects.ITimedObject)
     */
    @Override
    public void routeTimedObject(ITimedObject timedObject)
        throws AgentQueueException, DataSenderException
    {
      //check if event should be routed to consumers and route the event
        //TODO: will be separate component in the future        
        logger.fine("routeTimedObject: timed object "+ timedObject+" determining if to send to consumers...");
        if (timedObject instanceof IEventInstance)
        {
            IEventInstance eventInstance  = (IEventInstance)timedObject;
            if (metadataFacade.getRoutingMetadataFacade().isConsumerEvent(eventInstance.getObjectName()))
            {     
                logger.info("routeTimedObject: forwarding event "+timedObject+" to consumer...");
                forwardEventsToConsumer(eventInstance);
            }
        }
        //logger.fine("routeTimedObject: timed object "+ timedObject+" determining if to submit back to Proton...");
        //route the event back to the system
        facadesManager.getAgentQueuesManager().passEventToQueues(timedObject);
        //logger.fine("routeTimedObject: routed timed object "+ timedObject);
        
    }

    



}
