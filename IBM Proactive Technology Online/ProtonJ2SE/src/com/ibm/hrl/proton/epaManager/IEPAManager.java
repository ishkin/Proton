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
import java.util.Map;

import com.ibm.hrl.proton.epaManager.exceptions.EPAManagerException;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.utilities.containers.Pair;


/**
 * Interface for the EPA manager. EPA manager is responsible for receiving an event instance,
 * a list of context partitions relevant for the event instance within the certain context, and the agent name which should process
 * the event instance. The EPA manager is responsible for locating the relevant agent instances (responsible for the 
 * specified partitions),  and passing the event instance to those agent instances. 
* <code>IEPAManager</code>.
* 
*
 */
public interface IEPAManager
{
    /**
     * Process the specified event instance by the relevant instances of the specified agent
     * @param eventInstance
     * @param agentName
     * @param contextPartitions
     * @throws EPAManagerException 
     */
    public void processEvent(IEventInstance eventInstance, String agentName,  Collection<Pair<String,Map<String,Object>>> contextPartitions) throws EPAManagerException;
    
    /**
     * When a context is terminated a deffered agent should be notified on the partition termination so it can perform it's calculation
     * @param agentName
     * @param contextPartition
     * @throws EPAManagerException 
     */
    public void processDeffered(String agentName, String contextPartition,Map<String,Object> segmentationValues) throws EPAManagerException;
    
    /**
     * Process the termination of all deffered partitions
     * @param agentName
     * @param partitions
     * @throws EPAManagerException
     */
    public void processDefferedPartitions(String agentName, Collection<Pair<String,Map<String,Object>>> partitions) throws EPAManagerException;
}
