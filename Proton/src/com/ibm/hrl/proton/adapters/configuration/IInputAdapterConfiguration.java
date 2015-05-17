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
package com.ibm.hrl.proton.adapters.configuration;

public interface IInputAdapterConfiguration
{
	public enum InputAdapterPullModeEnum {BATCH,SINGLE};
	
	/**
	 * Get poll mode of this adapter - either getting single event instance each time
	 * it polls the resource or getting a batched set of event instances
	 * @return
	 */
	public InputAdapterPullModeEnum getPollMode();
	
	/**
	 * Get polling delay - the time between two consecutive polls of the resource for updates
	 * @return
	 */
	public long getPollingDelay();
	
	/**
	 * Get the delay for stalling between sending new event instances to the server
	 * @return
	 */
	public long getSendingDelay();
}
