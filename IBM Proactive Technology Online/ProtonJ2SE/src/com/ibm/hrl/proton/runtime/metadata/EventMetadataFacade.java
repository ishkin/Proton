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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.ibm.hrl.proton.metadata.event.IEventType;

/**
 * <code>EventMetadataFacade</code>.
 * 
 * 
 */
public class EventMetadataFacade implements Serializable
{
    Map<String,IEventType> eventTypes;
   
    private static final Logger logger = Logger.getLogger("EventMetadataFacade");
    
    /**Ctor - Initialize the internal data structure*/
    public EventMetadataFacade(Map<String,IEventType> eventTypes)
    {
        this.eventTypes= new HashMap<String,IEventType>(eventTypes);
    }
    
   
    
   
    public IEventType getEventType(String eventTypeName)
    {       
        return eventTypes.get(eventTypeName);
    }
    
    public synchronized void clear() {
    	eventTypes.clear();
    }
}
