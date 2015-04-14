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

import java.util.List;

import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;


public interface IInputAdapter extends IAdapter
{
	/**
	 * Poll input resource,get a new event instance and send the information to proton server
	 * @throws AdapterException
	 */
	public IEventInstance readData() throws AdapterException;
	
	/**
	 * Poll input resource, get a batch of new event instances and send the information
	 * to proton server
	 * @return
	 * @throws AdapterException
	 */
	public List<IEventInstance> readBatchedData() throws AdapterException;	
	
	
	/**
	 * Filter out the outgoing instance based on producer definitions
	 * @param eventInstance
	 * @return
	 */
	public boolean filterInstance(IEventInstance eventInstance);
	
}
