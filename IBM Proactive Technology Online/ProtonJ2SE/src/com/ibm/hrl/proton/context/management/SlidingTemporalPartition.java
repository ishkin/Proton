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
package com.ibm.hrl.proton.context.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.hrl.proton.context.exceptions.ContextServiceException;
import com.ibm.hrl.proton.context.facade.ContextServiceFacade;
import com.ibm.hrl.proton.context.facade.IContextService;
import com.ibm.hrl.proton.context.management.AdditionalInformation.NotificationTypeEnum;
import com.ibm.hrl.proton.context.metadata.ComposedSegmentation;
import com.ibm.hrl.proton.context.metadata.ITemporalContextBound;
import com.ibm.hrl.proton.runtime.context.notifications.IContextNotification;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.utilities.containers.Pair;
import com.ibm.hrl.proton.utilities.timerService.ITimerListener;
import com.ibm.hrl.proton.utilities.timerService.TimerServiceException;

/**
 * This class represents sliding temporal partition for sliding window.
 * It contains internal sliding partitions, which in turn maintain internal partitions representing
 * local segmentation; the latter actually contain events.
 * <code>SlidingTemporalPartition</code>.
 * 
 */

public class SlidingTemporalPartition extends TemporalPartition implements ITimerListener {
	
	private static final long serialVersionUID = 1L;
	public static final Logger logger = LoggerFactory.getLogger(SlidingTemporalPartition.class);
	protected long duration; // sliding window length	
	protected long period;   // sliding window repeating period
	protected ArrayList<SlidingTemporalInternalPartition> slidingInternalPartitions;
	protected String contextName;
	protected String agentName;
	protected String compositeContextName;
	private ContextServiceFacade facade;
		
	
	public SlidingTemporalPartition(ArrayList<ITemporalContextBound> initiators,
			long duration, long period, String contextName,String compositeContextName,SegmentationValue globalSegmentation,String agentName,IContextService contextServiceFacade) throws ContextServiceException {
		
		
		super(initiators,globalSegmentation);
		this.facade = (ContextServiceFacade)contextServiceFacade;
		this.period = period;
		this.duration = duration;
		this.contextName = contextName;
		this.compositeContextName = compositeContextName;
		this.agentName = agentName;
		slidingInternalPartitions = new ArrayList<SlidingTemporalInternalPartition>();
		logger.debug("Creating new SlidingTemporalPartition");
		
		try {
			// first sliding internal partition is initiated
			initiateSlidingTemporalPartition();
			// creating repeating timer for each consequent partition
			createRepeatingInitiationTimer();			
		}
		catch (Exception e) {
    		throw (new ContextServiceException(e.getMessage(),e.getCause()));
		}
	}
	
	private void createRepeatingInitiationTimer() throws TimerServiceException {

		
		// add repeating timers for initiation of consequent sliding internal partitions	
		AdditionalInformation initiationInfo = new SlidingPartitionAdditionalInformation(contextName,
				null,null,NotificationTypeEnum.INITIATOR,getPartitionUUID(),null);
		
		// creating sliding internal partition(s) initiation timer
		// this timer is responsible for repeating initiation of new sliding partitions
		// each time we get a notification from this timer - we create a new partition
		// timer for termination of this partition is generated upon its creation
		facade.getTimerServices().createTimer(this,initiationInfo,true,period,period);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || this.getClass() != other.getClass()) return false;
		
		SlidingTemporalPartition otherPartition = (SlidingTemporalPartition)other;
		
		// if super.equals() == true then check duration and period
		if (super.equals(other) && duration == otherPartition.duration &&
				period == otherPartition.period) {
			return true;
		}		
		return false;
	}

	public ArrayList<SlidingTemporalInternalPartition> getSlidingInternalPartitions() {				
		 return slidingInternalPartitions;
	}
	
	
	@Override
	public Collection<Pair<String,Map<String,Object>>> terminate() {

		System.out.println("terminate - sliding temporal partition at " + 
				System.currentTimeMillis());
		
		Collection<Pair<String,Map<String,Object>>> terminatedPartitions= new HashSet<Pair<String,Map<String,Object>>>();
		// terminate all sliding temporal internal partitions
		for (SlidingTemporalInternalPartition partition: slidingInternalPartitions) {			
			terminatedPartitions.addAll(partition.terminate());
		}
		
		// destroy internal partitions initiation and termination timers
		
		facade.getTimerServices().destroyTimers(this);
		
		return terminatedPartitions;
	}
	
	@Override
	public Collection<Pair<String,Map<String,Object>>> findInternalPartition(IEventInstance event,
			ComposedSegmentation localSegmentation) {
		// at this point we know that event falls into this temporal partition
		// now we lookup the internal partition this event falls into, and if it does not exist -
		// we create a new partition(s); this methods returns a collection of partition ids
		logger.debug("findInternalPartition: for event"+event+", segmentation: "+localSegmentation);	
		//boolean foundPartition = false;	
		Collection<Pair<String,Map<String,Object>>> participating = new HashSet<Pair<String,Map<String,Object>>>();
		SegmentationValue eSegmentation = localSegmentation.getSegmentationValue(event);
		logger.debug("findInternalPartition: eSegmentation"+eSegmentation);
		// go over all internal partitions and find partitions with relevant value
		// basically for each existing sliding partition there should be relevant internal one
		for (SlidingTemporalInternalPartition partition: slidingInternalPartitions) {
			logger.debug("findInternalPartition: search for matching internal partitions for event "+event+ "inside sliding partition: "+partition.getId().toString());
			Collection participatingInsideSliding = partition.findInternalPartition(eSegmentation);
			
			//if we cannot find any - means no such internal partition for this segmentation - create one
			if (participatingInsideSliding.isEmpty()){
				logger.debug("findInternalPartition: didn't find matching internal parititons for event" +event+"inside sliding partition: "+partition.getId().toString()+" , adding a new internal partition...");
				participatingInsideSliding.add(partition.addInternalPartition(eSegmentation));
			}else
			{
				logger.debug("findInternalPartition: found matching internal parititons for event" +event+"...");
			}
			
			participating.addAll(participatingInsideSliding);			
		}
		
		/*if (!participating.isEmpty()) {
			System.out.println("findInternalPartition: found matching internal parititons for event" +event+" , returning...");
			return participating;
		}
				
		// at this point we know that there is no internal partition with event's segment
		// we need to create a new one in every sliding temporal partition and return their id	
		for (SlidingTemporalInternalPartition partition: slidingInternalPartitions) {			
			System.out.println("findInternalPartition: didn't find matching internal parititons for event" +event+" , adding a new internal partition...");
			participating.add(partition.addInternalPartition(eSegmentation));						
		}*/
	
		return participating;
	}

	@Override
	public Object onTimer(Object info) throws Exception {
		
		// two types of timers will fire here - initiation of new sliding internal partition
		// and termination of sliding internal existing partition; the first one can be processed
		// entirely here and the second one should be sent to the system as notification
		// and then processed as terminator (return internal partitions ids)

		String contextBoundId = null;
		//String contextBoundId = ((AdditionalInformation)info).getContextBoundId().toString();
		NotificationTypeEnum notificationType = ((AdditionalInformation)info).getNotificationType();
		
		logger.info("on timer - sliding temporal partition, with " +
				notificationType + " at " + System.currentTimeMillis());
		System.out.println("on timer - sliding temporal partition, with " +
				notificationType + " at " + System.currentTimeMillis());
		
		
		if (notificationType == NotificationTypeEnum.INITIATOR) {
			// initiate new sliding temporal partition
			initiateSlidingTemporalPartition();			
		}
		else { // notificationType == NotificationTypeEnum.terminator
			UUID internalPartitionId = ((SlidingPartitionAdditionalInformation)
					info).getInternalPartitionId();
			IContextNotification notification;
			
			if (compositeContextName!= null)
			{
				logger.info("Creating composite context termination notification");
				notification = new SlidingWindowTerminationNotification(compositeContextName,((SlidingPartitionAdditionalInformation)info).getAgentName(),
						System.currentTimeMillis(),System.currentTimeMillis(),contextBoundId,
						getPartitionUUID(),internalPartitionId);
				System.out.println("Creating composite context termination notification");
				
			}else
			{
				logger.info("Creating sliding context termination notification");
				System.out.println("Creating sliding context termination notification");
				notification = new SlidingWindowTerminationNotification(contextName,((SlidingPartitionAdditionalInformation)info).getAgentName(),
						System.currentTimeMillis(),System.currentTimeMillis(),contextBoundId,
						getPartitionUUID(),internalPartitionId);
				
			}
			
			facade.getEventHandler().routeContextNotification(notification);
		}				
		return null;
	}
	
	private  UUID initiateSlidingTemporalPartition() throws ContextServiceException {
		
		logger.debug("SlidingTemporalPartition: initiateSlidingTemporalPartition: creating new sliding partition , initiating termination timer on new sliding temporal partition");
		System.out.println("SlidingTemporalPartition: initiateSlidingTemporalPartition: creating new sliding partition , initiating termination timer on new sliding temporal partition");
		SlidingTemporalInternalPartition partition = new SlidingTemporalInternalPartition(this.globalSegmentation);
		logger.debug("SlidingTemporalPartition: initiateSlidingTemporalPartition:created new sliding partition : "+partition.getId().toString());
		slidingInternalPartitions.add(partition);
		
		try { 	
			
			// add timer for termination of this newly created partition (triggers epa evaluation);
			// timer trigger will be converted into notification that will processed as timer notification
			// and will terminate this partition; the additional info type along with partition id
			// will draw the exact partition to terminate
			AdditionalInformation terminationInfo = new SlidingPartitionAdditionalInformation(
					contextName,agentName,null,NotificationTypeEnum.TERMINATOR,
					getPartitionUUID(),partition.getId());		

			facade.getTimerServices().createTimer(this,terminationInfo,false,duration,0);
		}
		catch (Exception e) {
			throw (new ContextServiceException(e.getMessage(),e.getCause()));
		}
		return partition.getId();
	}

	@Override
	public String getListenerId() {
		return getPartitionUUID().toString();
	}

	public  Collection<Pair<String,Map<String,Object>>> terminate(UUID internalPartition) {
		logger.debug("SlidingTemporalPartition:: terminate - sliding temporal partition, with UUID" +
				internalPartition + " at " + System.currentTimeMillis());
		Collection<Pair<String,Map<String,Object>>> terminatedPartitions = new HashSet<Pair<String,Map<String,Object>>>();
		// terminate all sliding temporal internal partitions
		ArrayList<SlidingTemporalInternalPartition> terminatedSlidingPartitions = new ArrayList<SlidingTemporalInternalPartition>();
		for (SlidingTemporalInternalPartition partition: slidingInternalPartitions) {
			if (partition.getId().toString().equals(internalPartition.toString())) {
				logger.debug("SlidingTemporalPartition:terminate: terminating sliding partition with id: "+partition.getId().toString());
				terminatedPartitions.addAll(partition.terminate());
				terminatedSlidingPartitions.add(partition);
				// remove this sliding partition
				//slidingInternalPartitions.remove(partition);
			}else{
				logger.debug("SlidingTemporalPartition:terminate: sliding partition with id: "+partition.getId().toString()+" doesnt match the termination id: "+internalPartition.toString()+"leaving it as is");
			}
			
		}
		slidingInternalPartitions.removeAll(terminatedSlidingPartitions);
		logger.debug("SlidingTemporalPartition:terminate: terminate - terminated internal partitions" +
				terminatedPartitions + " at " + System.currentTimeMillis()+", left with sliding partitions: "+slidingInternalPartitions);
		for (SlidingTemporalInternalPartition partition : slidingInternalPartitions) {
			logger.debug("SlidingTemporalPartition:terminate: left with sliding partitions: "+partition.getId().toString());
		}
		return terminatedPartitions;
	}
	

}
