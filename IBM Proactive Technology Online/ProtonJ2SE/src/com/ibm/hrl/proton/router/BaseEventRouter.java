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

import java.util.Collection;
import java.util.logging.Logger;

import com.ibm.hrl.proton.agentQueues.exception.AgentQueueException;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;

public abstract class BaseEventRouter implements IEventRouter{
	
	private static final Logger logger = Logger.getLogger("BaseEventRouter");
	protected final IDataSender eventSender;
	
	protected BaseEventRouter(IDataSender eventSender)
    {       
        this.eventSender = eventSender;
    }
	
	public abstract void routeTimedObject(ITimedObject timedObject) throws AgentQueueException, DataSenderException;
	
	/* (non-Javadoc)
     * @see com.ibm.hrl.proton.agentQueue.eventRouter.IEventRouter#routeTimedObjects(java.util.Collection)
     */
    @Override
    public void routeTimedObjects(
            Collection<? extends ITimedObject> timedObjects)
        throws AgentQueueException, DataSenderException
    {
        for (ITimedObject timedObject : timedObjects)
        {
            routeTimedObject(timedObject);
        }
        
    }
    
    protected void forwardEventsToConsumer(IEventInstance derivedEvent) throws DataSenderException
    {
       
        eventSender.sendDataToConsumer(derivedEvent);
      
    }
}
	
