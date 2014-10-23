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
package com.ibm.hrl.proton.adapters.interfaces;

import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;

public interface IOutputAdapter extends IAdapter {
	
	/**
	 * Get events from server connector and publish them to consumer 
	 * @throws AdapterException
	 */
	public void writeObject(IDataObject instance) throws AdapterException;
		
	
	/**
	 * Decide if to send the event to consumer based on event list and/or conditions defined on the event
	 * @param eventInstance
	 * @return
	 */
	public boolean sendInstance(IDataObject dataInstance);
	
	
}
