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

package com.ibm.hrl.proton.server.timerService;

import com.ibm.hrl.proton.utilities.timerService.TimerServiceException;
import com.ibm.hrl.proton.utilities.timerService.ITimerServices.TimerInfo;

/**
 * <code>CallbackTask</code>.
 * @param <V>
 * 
 * 
 */
public class CallbackTask implements Runnable
{

	
	private TimerInfo timerObject;
    
    public CallbackTask(TimerInfo timerObject)
    {
        this.timerObject = timerObject;
    }
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        try
        {
            TimerServiceFacade.getInstance().notifyListener(timerObject);
        }
        catch (TimerServiceException e)
        {
            throw new RuntimeException("Problem executing on timer ,reason: " + e.getMessage());
        }
        
    }

}
