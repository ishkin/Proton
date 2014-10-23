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
import java.util.Map;

import com.ibm.hrl.proton.context.exceptions.ContextServiceException;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.containers.Pair;

/**
 * Interface for context service facade.
 * <code>IContextService</code>.
 * 
 */
public interface IContextService
{
    /**
     * Process the given timed object - either a real event instance, or a notification created
     * by context on timer (context partition termination or initiation)

	 * @param timedObject - can be either event instance or context notification
	 * @param contextName - context the object should be processed in
	 * @param agentName - agent associated with the context, in case the timedObject is event instance,
	 * it can be this agent's participant
     * @throws ContextServiceException
     * @return Pair<Collection<String>,Collection<String>> - list of partition ids this object
     * participates in (falls into), and list of partition ids this object terminates
     */
    public Pair<Collection<Pair<String, Map<String, Object>>>, Collection<Pair<String, Map<String, Object>>>> processEventInstance(ITimedObject timedObject,
    		String contextName, String agentName) throws ContextServiceException;
}
