/*******************************************************************************
 * Copyright 2015 IBM
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
package com.ibm.hrl.proton.routing;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;

import com.ibm.hrl.proton.agentQueues.exception.AgentQueueException;
import com.ibm.hrl.proton.router.DataSenderException;
import com.ibm.hrl.proton.router.IEventRouter;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;

public class StormEventRouter implements IEventRouter {
	OutputCollector outputCollector;
	private static final Logger logger = LoggerFactory.getLogger("StormEventRouter");
	private STORMMetadataFacade metadataFacade;
	
	public StormEventRouter(OutputCollector _collector,STORMMetadataFacade metadataFacade) {
		this.outputCollector = _collector;
		this.metadataFacade = metadataFacade;
	}

	@Override
	public void routeTimedObject(ITimedObject timedObject)
			throws AgentQueueException, DataSenderException {
		logger.debug("routeTimedObject: routing "+timedObject+" to outside of EPAManagerBolt");
		//create tuple from event instance
		IEventInstance eventInstance = (IEventInstance)timedObject;
		List<Object> tupleFields = metadataFacade.createOutputTuple(eventInstance);
		this.outputCollector.emit(STORMMetadataFacade.EVENT_STREAM, tupleFields);

	}

	@Override
	public void routeTimedObjects(
			Collection<? extends ITimedObject> timedObjects)
			throws AgentQueueException, DataSenderException {
		for (ITimedObject timedObject : timedObjects)
        {
            routeTimedObject(timedObject);
        }

	}

}
