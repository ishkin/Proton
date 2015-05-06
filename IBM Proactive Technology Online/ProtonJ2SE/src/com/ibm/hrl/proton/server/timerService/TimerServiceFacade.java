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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.ibm.hrl.proton.server.executorServices.ExecutorUtils;
import com.ibm.hrl.proton.utilities.timerService.ITimerListener;
import com.ibm.hrl.proton.utilities.timerService.ITimerServices;
import com.ibm.hrl.proton.utilities.timerService.TimerServiceException;

/**
 * <code>TimerServiceFacade</code>.
 * 07/2012 - timers implementation was changed to support timers for temporal sliding windows.
 * Sliding temporal partition manages all (multiple) initiation and termination timers and is a listener:
 * for a single listener we need to maintain multiple timers.
 * 
 * The Map<String,ScheduledFuture> was changed to Map<String,List<ScheduledFuture>> and algorithms
 * for creation, destroying and notification were modified accordingly.
 * 
 * 
 */
public class TimerServiceFacade
    implements ITimerServices
{

   
   
    private Map<String,List<ScheduledFuture>> timersMap;
    
    private static Logger logger = Logger.getLogger(TimerServiceFacade.class.getName());
    
    
    
    public TimerServiceFacade()
    {
        timersMap = new ConcurrentHashMap<String,List<ScheduledFuture>>();
        
    }
    
  
    
    @Override
    public void createTimer(ITimerListener timerListener,
            Object additionalInfo, boolean repetitive, long duration,
            long repetitionPeriod)
        throws TimerServiceException
    {
        TimerInfo timerInfo = new TimerInfo(timerListener,additionalInfo,repetitive);
        ScheduledFuture timer; 
        if (repetitive)
        {
            timer = ExecutorUtils.scheduleAtFixedRate(new CallbackTask(timerInfo,this), duration,
            		repetitionPeriod, TimeUnit.MILLISECONDS);
        }else
        {
            timer = ExecutorUtils.schedule(new CallbackTask(timerInfo,this), duration, TimeUnit.MILLISECONDS);
        }
        
        if (!timersMap.containsKey((timerListener.getListenerId()))) {
        	ArrayList timers = new ArrayList<ScheduledFuture>();
        	timers.add(timer);
        	
        	timersMap.put(timerListener.getListenerId(),timers);
        }
        else { // there exist list of timers for this listener
        	timersMap.get(timerListener.getListenerId()).add(timer);
        }   
    }

    @Override
    // destroy all timers for a specific listener
    public void destroyTimers(ITimerListener callbackObject)
    {
        logger.fine("destroyTimers: destroying timers on: " + callbackObject);
        
        Object additionalInfo = null;
        String listenerId = callbackObject.getListenerId();

        // cancel all timers for this listener
        ArrayList<ScheduledFuture> timers = (ArrayList)timersMap.get(listenerId);
        if (timers != null) {            
            for (ScheduledFuture timer: timers) {
            	timer.cancel(false);
            }
        }
        //remove timers list from the map
        timersMap.remove(listenerId);       
    }
    
    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.timerService.ITimerServices#notifyListener(java.lang.Object)
     */
    @Override
    public void notifyListener(Object timerObject) throws TimerServiceException {

    	TimerInfo timerInfo = (TimerInfo)timerObject;  
        boolean repetitiveTimer = timerInfo.isRepetitive();
        // timer listener can be or context partition (listening for termination) or temporal context wrapper
        ITimerListener listener = timerInfo.getTimerListener();
        // the additional information serves on initialization timers to hold the pointer to the parent partition
        Object additionalInformation = timerInfo.getAdditionalInformation();
        
        try
        {
            listener.onTimer(additionalInformation);
            
            // if the timer has completed and it is not repetitive - we can remove it
            if (!repetitiveTimer)
            {
            	// TODO - we should remove only this timer from timers list
            	// this listener can still maintain additional timers; at this point we don't remove
            	// the timer since it can not be identified by unique id, it worth extending the ScheduledFuture
            	// with this capability
                //timersMap.remove(listener.getListenerId());	
            }
        }
        catch (Exception e)
        {
            throw new TimerServiceException("Could not execute on timer notification of listener" + 
            		listener + ", reason: " + e.getMessage());
        }
    }

	@Override
	public void destroyTimers() throws TimerServiceException {
		logger.info("TimerServiceFacade: destroying remaining timers...");
		for (Map.Entry<String,List<ScheduledFuture>> entry : timersMap.entrySet()) {
			for (ScheduledFuture scheduledFeature : entry.getValue()) {
				scheduledFeature.cancel(true);
			};
		}
		logger.info("TimerServiceFacade: destroyed remaining timers...");
		
	}
	
	 public synchronized void cleanUp()
	 {
	    
	    		for (Map.Entry<String, List<ScheduledFuture>> entry : timersMap.entrySet()) 
	        	{
	    			for (ScheduledFuture timer : entry.getValue()) {
	    				timer.cancel(false);
	    			}
	    		}
	        	timersMap.clear();
	        	
	    	
	 }

}
