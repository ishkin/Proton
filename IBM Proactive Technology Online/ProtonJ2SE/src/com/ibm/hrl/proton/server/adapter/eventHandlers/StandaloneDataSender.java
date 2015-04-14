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

package com.ibm.hrl.proton.server.adapter.eventHandlers;

import java.util.ArrayList;
import java.util.List;

import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.router.DataSenderException;
import com.ibm.hrl.proton.router.IDataSender;

/**
 * <code>StandaloneEventSender</code>.
 * 
 * 
 */
public class StandaloneDataSender implements IDataSender
{

	
    private static StandaloneDataSender instance;
    private List<IDataSender> dataSendersList;
   
    private StandaloneDataSender() throws DataSenderException
    {       

    	dataSendersList = new ArrayList<IDataSender>();
        
    }
    
    public static synchronized void initializeInstance() throws DataSenderException
    {
        
        instance = new StandaloneDataSender();
    }
    
    public static StandaloneDataSender getInstance()
    {
        return instance;
    }
    
    
    
    public void addDataSender(IDataSender dataSender)
    {
    	dataSendersList.add(dataSender);
    }
    
    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.router.IEventSender#sendEventToConsumer(com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance)
     */
    @Override
    public void sendDataToConsumer(IDataObject instance) throws DataSenderException
    {
    	
        for (IDataSender dataSender : dataSendersList) 
        {
        	
        	dataSender.sendDataToConsumer(instance);
			
		}


    }

    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.router.IEventSender#shutdownEventSender()
     */
    @Override
    public void shutdownDataSender() throws DataSenderException
    {
        //iterate and shutdowns all event senders
    	 for (IDataSender dataSender : dataSendersList) 
         {
    		 dataSender.shutdownDataSender(); 			
 		 }
        
    }

	

}
