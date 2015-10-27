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
package com.ibm.hrl.proton.runtime.metadata;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;



/**
 * Metadata for agent operators.
* <code>EPAManagerMetadataFacade</code>.
* 
*
 */
public class EPAManagerMetadataFacade implements Serializable
{
    
    private Map<String,IEventProcessingAgent> agentsDefinitions;
    private static Logger logger = Logger.getLogger(EPAManagerMetadataFacade.class.getName());
    
    public  EPAManagerMetadataFacade(Map<String,IEventProcessingAgent> agentsDefinitions){
        this.agentsDefinitions = new HashMap<String,IEventProcessingAgent>(agentsDefinitions);
    }
    
    
    
    
    /**
     * Return all agents names within the metadata
     */
    public Collection<String> getAgentNames()
    {
        return agentsDefinitions.keySet();
    }
    
    /**
     * Return all agents definitions
     * @return
     */
    public Collection<IEventProcessingAgent> getAgentDefinitions(){
       return agentsDefinitions.values();
    }
    
    /**
     * Return definition of a single agent given by its name
     * @param agentName
     * @return
     */
    public IEventProcessingAgent getAgentDefinition(String agentName)
    {
        return agentsDefinitions.get(agentName);
    }
    
    /**
     * Clear previous EPAs data (used by the parser before parsing a new project definition)
     */
	public  synchronized void clear() {
		agentsDefinitions.clear();	
	}
     
}
