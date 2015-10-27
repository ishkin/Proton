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
 * Interface for timer service
* <code>ITimerServices</code>.
* 
*
 */
public interface ITimerServices extends Serializable
{
    /**
     * Create timer according to the specified details
     * @param timerListener  - a timer listener, which will be notified upon timer expiration
     * @param additionalInfo -an information object which will be passed to the timer listener upon the timer expiration
     * @param repetitive - if the timer is repetitive or not
     * @param duration - duration for the timer in ms
     * @param repetitionPeriod - repetition period for the timer in ms
     * @throws TimerServiceException
     */
    public void createTimer(ITimerListener timerListener,Object additionalInfo,boolean repetitive,long duration, long repetitionPeriod) throws TimerServiceException;
    
    /**
     * Destroy all timers for the specified listener object
     * @param callbackObject
     * @return
     */
    public void destroyTimers(ITimerListener callbackObject);
  
    /**
     * Notify the listener on timer expiration
     * @param timerObject - the timer object , containing information about the timer, the listener and additional info
     * @throws TimerServiceException 
     */
    public void notifyListener(Object timerObject) throws TimerServiceException;
    
    
    public void destroyTimers() throws TimerServiceException;
    
    /**
     * Helper class for holding information to save in each timer
    * <code>TimerInfo</code>.
    * 
    *
     */
    public class  TimerInfo implements Serializable
    {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private final Object additionalInformation;
        private final ITimerListener timerListener;
        private final boolean repetitive;
 
        public TimerInfo(ITimerListener listener, Object additionalInfo,boolean repetitive)
        {
            this.timerListener = listener;
            this.additionalInformation = additionalInfo;
            this.repetitive = repetitive;
        }
        
       
        public Object getAdditionalInformation() {
            return additionalInformation;
        }

       
        public ITimerListener getTimerListener() {
            return timerListener;
        }
        
        public boolean isRepetitive()
        {
            return repetitive;
        }

    }
}
