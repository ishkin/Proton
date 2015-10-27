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
package com.ibm.hrl.proton.utilities.timerService;

import java.io.Serializable;


/**
 * Interface to the timer listener - the listener wil be notified upon timer expiration
* <code>ITimerListener</code>.
* 
*
 */
public interface ITimerListener extends Serializable{
    /**
     * Notification method which will be called when the timer expires
     * @param additionalInformation - an information object which was specified when creating the expired timer
     * @return
     * @throws Exception
     */
	public Object onTimer(Object additionalInformation) throws Exception;

	/**
	 * Return unique listener id
	 * @return
	 */
	public String getListenerId();
   
}
