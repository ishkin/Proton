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

package com.ibm.hrl.proton.context.state;

import java.util.HashMap;
import java.util.Map;

import com.ibm.hrl.proton.context.management.CompositeContextInstance;
import com.ibm.hrl.proton.utilities.persistence.IPersistenceManager;

/**
 * <code>ContextStateManager</code>.
 * 
 * 
 */
public class ContextStateManager implements IContextStateManager {

	private static ContextStateManager instance;
    private IPersistenceManager persistenceManager;
    
    // the key is context name + agent name
    private Map<String,CompositeContextInstance> contexts;
    
    public synchronized static ContextStateManager getInstance() {
        if (instance == null){
            instance = new ContextStateManager(null);            
        }
        return instance;
    }

    public IPersistenceManager getPersistenceManager() {
    	return persistenceManager;
    }
    
    public ContextStateManager(IPersistenceManager persistenceManager)
    {       
        this.persistenceManager = persistenceManager;
        contexts = new HashMap<String,CompositeContextInstance>();        
    }
    
    public CompositeContextInstance getContextInstance(String contextName, String agentName) {
    	String key = contextName.concat(agentName);
    	return (contexts.get(key));
    }
    
    public void addContextInstance(CompositeContextInstance context) {

    	// make sure this entry does not exist yet
    	String key = context.getContextName().concat(context.getAgentName());
    	assert(!contexts.containsKey(key));    	    	
    	contexts.put(key,context);    	
    }
}
