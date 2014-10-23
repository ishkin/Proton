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

import java.util.List;
import java.util.logging.Logger;

import com.ibm.hrl.proton.agentQueues.exception.AgentQueueException;
import com.ibm.hrl.proton.router.DataSenderException;
import com.ibm.hrl.proton.router.IEventRouter;
import com.ibm.hrl.proton.runtime.epa.interfaces.IEventProcessingAgentInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.asynchronousWork.IWorkItem;


/**
 * Asynchronous work item for reading and processing events from agent instance queue. 
 * The thread will keep reading event instances from the queue as long as there are any. 
 * When there are no more events in the agent instance the queue, the entry for the queue will be 
 * deleted from the map registering active instances and the thread will end its existence. 
 * If in the future additional event instances will be added to the queue, a new entry in the map will be created
 * and a new processing thread spawned.
* <code>EPAManagerWorkItem</code>.
* 
*
 */
public class EPAInstanceWorkItem implements IWorkItem
{
    /**Agent instance queue */
    IEventProcessingAgentInstance eventProcessingAgentInstance;
    IEventInstance eventInstance;
    /**Event router for routing derived events back to the system or to the external consumers */
    IEventRouter eventRouter;
    Logger logger = Logger.getLogger(getClass().getName());
    
    public EPAInstanceWorkItem(IEventProcessingAgentInstance eventProcessingAgentInstance, IEventInstance eventInstance)
    {
        super();
        this.eventProcessingAgentInstance = eventProcessingAgentInstance;
        this.eventInstance = eventInstance;
        eventRouter = EPAManagerFacade.getInstance().getEventRouter();
        
        
    }
    
    @Override
    public void run()
    {       
        runEventProcessing();
    }


    //TODO: decide on "quiet points" where to persist the internal state of the agent - perhaps after/before
    //taking care of each event in the waiting queue
    private void runEventProcessing()
    {           
       
        logger.fine("runEventProcessing:  running event processing instance by agent: "+ eventProcessingAgentInstance+ "for :"+eventInstance);
        List<ITimedObject> derivedEvents = eventProcessingAgentInstance.processEvent(eventInstance);        
        try
        {
            logger.fine("runEventProcessing: routing derived events  by agent: "+ eventProcessingAgentInstance+" for :"+eventInstance);
            eventRouter.routeTimedObjects(derivedEvents);            
            
        }
        catch (AgentQueueException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (DataSenderException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    

}
