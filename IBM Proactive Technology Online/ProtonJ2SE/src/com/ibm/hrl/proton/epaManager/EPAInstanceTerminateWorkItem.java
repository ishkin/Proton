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

import com.ibm.hrl.proton.agentQueues.exception.AgentQueueException;
import com.ibm.hrl.proton.router.DataSenderException;
import com.ibm.hrl.proton.router.IEventRouter;
import com.ibm.hrl.proton.runtime.epa.interfaces.IEventProcessingAgentInstance;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.asynchronousWork.IWorkItem;

/**
 * <code>EPAInstanceTerminateWorkItem</code>.
 * 
 * 
 */
public class EPAInstanceTerminateWorkItem implements IWorkItem
{
    /**Agent instance queue */
    IEventProcessingAgentInstance eventProcessingAgentInstance;
    /**Event router for routing derived events back to the system or to the external consumers */
    IEventRouter eventRouter;
    
    
    public EPAInstanceTerminateWorkItem(
            IEventProcessingAgentInstance eventProcessingAgentInstance, IEventRouter eventRouter)
    {
        super();
        this.eventProcessingAgentInstance = eventProcessingAgentInstance;
        this.eventRouter = eventRouter;
    }


    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.asynchronousWork.IWorkItem#run()
     */
    @Override
    public void run()
    {
    
        List<ITimedObject> derivedEvents = eventProcessingAgentInstance.terminate();
        try
        {
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
