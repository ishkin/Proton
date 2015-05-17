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
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import com.ibm.hrl.proton.context.exceptions.ContextServiceException;
import com.ibm.hrl.proton.context.facade.ContextServiceFacade;
import com.ibm.hrl.proton.context.management.AdditionalInformation.NotificationTypeEnum;
import com.ibm.hrl.proton.context.metadata.ComposedSegmentation;
import com.ibm.hrl.proton.context.metadata.ITemporalContextBound;
import com.ibm.hrl.proton.metadata.context.SegmentationContextType;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
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
	
	protected long duration; // sliding window length	
	protected long period;   // sliding window repeating period
	protected ArrayList<SlidingTemporalInternalPartition> slidingInternalPartitions;
	protected String contextName;
	protected String agentName;
	
	public SlidingTemporalPartition(ArrayList<ITemporalContextBound> initiators,
			long duration, long period, String contextName,SegmentationValue globalSegmentation,String agentName) throws ContextServiceException {
		
		super(initiators,globalSegmentation);
		this.period = period;
		this.duration = duration;
		this.contextName = contextName;
		this.agentName = agentName;
		slidingInternalPartitions = new ArrayList<SlidingTemporalInternalPartition>();
				
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

		ContextServiceFacade facade = ContextServiceFacade.getInstance();
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
		ContextServiceFacade facade = ContextServiceFacade.getInstance();
		facade.getTimerServices().destroyTimers(this);
		
		return terminatedPartitions;
	}
	
	@Override
	public Collection<Pair<String,Map<String,Object>>> findInternalPartition(IEventInstance event,
			ComposedSegmentation localSegmentation) {
		// at this point we know that event falls into this temporal partition
		// now we lookup the internal partition this event falls into, and if it does not exist -
		// we create a new partition(s); this methods returns a collection of partition ids
			
		//boolean foundPartition = false;	
		Collection<Pair<String,Map<String,Object>>> participating = new HashSet<Pair<String,Map<String,Object>>>();
		SegmentationValue eSegmentation = localSegmentation.getSegmentationValue(event);
		// go over all internal partitions and find partitions with relevant value
		// basically for each existing sliding partition there should be relevant internal one
		for (SlidingTemporalInternalPartition partition: slidingInternalPartitions) {
			participating.addAll(partition.findInternalPartition(eSegmentation));			
		}
		
		if (!participating.isEmpty()) {
			return participating;
		}
				
		// at this point we know that there is no internal partition with event's segment
		// we need to create a new one in every sliding temporal partition and return their id	
		for (SlidingTemporalInternalPartition partition: slidingInternalPartitions) {			
			participating.add(partition.addInternalPartition(eSegmentation));						
		}
	
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
		
		System.out.println("on timer - sliding temporal partition, with " +
				notificationType + " at " + System.currentTimeMillis());
		
		if (notificationType == NotificationTypeEnum.INITIATOR) {
			// initiate new sliding temporal partition
			initiateSlidingTemporalPartition();			
		}
		else { // notificationType == NotificationTypeEnum.terminator
			UUID internalPartitionId = ((SlidingPartitionAdditionalInformation)
					info).getInternalPartitionId();
						
			IContextNotification notification = new SlidingWindowTerminationNotification(contextName,((SlidingPartitionAdditionalInformation)info).getAgentName(),
					System.currentTimeMillis(),System.currentTimeMillis(),contextBoundId,
					getPartitionUUID(),internalPartitionId);
			
			ContextServiceFacade.getInstance().getEventHandler().routeContextNotification(notification);
		}				
		return null;
	}
	
	private UUID initiateSlidingTemporalPartition() throws ContextServiceException {
		
		SlidingTemporalInternalPartition partition = new SlidingTemporalInternalPartition(this.globalSegmentation);
		slidingInternalPartitions.add(partition);
		
		try { 	
			ContextServiceFacade facade = ContextServiceFacade.getInstance();
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

	public Collection<Pair<String,Map<String,Object>>> terminate(UUID internalPartition) {
		
		Collection<Pair<String,Map<String,Object>>> terminatedPartitions = new HashSet<Pair<String,Map<String,Object>>>();
		// terminate all sliding temporal internal partitions
		for (SlidingTemporalInternalPartition partition: slidingInternalPartitions) {
			if (partition.getId().toString().equals(internalPartition.toString())) {
				terminatedPartitions.addAll(partition.terminate());
				// remove this sliding partition
				slidingInternalPartitions.remove(partition);
			}
		}		
		return terminatedPartitions;
	}
	

}
