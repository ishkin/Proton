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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.hrl.proton.context.exceptions.ContextServiceException;
import com.ibm.hrl.proton.context.facade.ContextServiceFacade;
import com.ibm.hrl.proton.context.management.AdditionalInformation.NotificationTypeEnum;
import com.ibm.hrl.proton.context.metadata.EventInitiator;
import com.ibm.hrl.proton.context.metadata.ITemporalContextBound;
import com.ibm.hrl.proton.context.metadata.TemporalContextInitiator;
import com.ibm.hrl.proton.metadata.context.ContextAbsoluteTimeTerminator;
import com.ibm.hrl.proton.metadata.context.ContextRelativeTimeTerminator;
import com.ibm.hrl.proton.metadata.context.TemporalContextType;
import com.ibm.hrl.proton.metadata.context.enums.ContextInitiatorPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.timerService.ITimerListener;

/**
 * Represents a single temporal context member, and maintains all its active segments.
 * ActiveTemporalContextSegment(s) are kept in a map, mapping segmentation value to a segment.
 * Each such a ActiveTemporalContextSegment maintains a list of initiators arrived to
 * this temporal context member (and not removed yet). 
 * <code>TemporalContextWindow</code>. 
 * 
 */
public class TemporalContextWindow {

	protected TemporalContextType temporalContextType;	
	//protected ComposedSegmentation segmentationContextType;
	protected Map<SegmentationValue,ActiveTemporalContextSegment> activeSegments;
	ContextServiceFacade facade;
	

	public TemporalContextWindow(TemporalContextType temporalContextType,ContextServiceFacade facade) {
		this.temporalContextType = temporalContextType;
		this.facade = facade;
		activeSegments = new HashMap<SegmentationValue,ActiveTemporalContextSegment> ();
	
	}
	
	public ActiveTemporalContextSegment getSegment(SegmentationValue key) {
		if (!activeSegments.containsKey(key)) {
			return null;
		}
		return (activeSegments.get(key));
	}
		
	public TemporalContextType getContextType() {
		return temporalContextType;
	}
	
	public Map<SegmentationValue,ActiveTemporalContextSegment> getActiveSegments() {
		return activeSegments; 	
	}
	
	public ActiveTemporalContextSegment[] getActiveSegmentsValues() {
		return (activeSegments.values().toArray(new ActiveTemporalContextSegment[0]));
	}

	public boolean handleInitiatorInstance(TemporalContextInitiator initiator,
			ITimedObject object, SegmentationValue segmentValue) {

		// check if there exists ActiveTemporalContextSegment with this segment value
		if (!activeSegments.containsKey(segmentValue)) {
			activeSegments.put(segmentValue,new ActiveTemporalContextSegment(segmentValue));
			activeSegments.get(segmentValue).addInitiator(initiator);
			
			return true;
		}
		
		// activeSegments contains the key (at least one initiator at this segment)
		// when a temporal window at a segment is terminated, its active segment is closed
		// we need to check what it our initiation policy - add/ignore
		
		// TODO consider moving initiation policy to a {single, composite} context type level
		// then it can be initiated at the ctor and not looked up dynamically here...
		ContextInitiatorPolicyEnum policy = temporalContextType.getInitiationPolicy(object);
		
		assert (policy != null);
		
		if (policy == ContextInitiatorPolicyEnum.ADD) {
			activeSegments.get(segmentValue).addInitiator(initiator);
			return true;
		}		
		
		return false;
	}
	
	public boolean handleTerminatorInstance(ITemporalContextBound terminatorInstance,
			ITimedObject event, SegmentationValue segmentValue) {
		
		// at this point we know that this event instance initiates this window
		// we need to find all active segment(s) that can be terminated by this event
		// can be multiple segments since the event can be wild card...
		
		boolean addedAsTerminator  = false;
		for (SegmentationValue currentSegment: activeSegments.keySet()) {			
			if (currentSegment.compliesWith(segmentValue)) {
				//if we have a complex expression - evaluate it now
				//at this point we know (due to validation) that if it is a complex expression
				//than we have only one initiator instance - only IGNORE policy is supported
				ActiveTemporalContextSegment segment = activeSegments.get(currentSegment);
				if (event instanceof IEventInstance) 					
					//only in the case the terminator is an event
					//and only in the case the expression is a complex one
				{
					IEventType eventType = ((IEventInstance) event).getEventType();										
					IExpression predicate = temporalContextType.getTerminatorPredicate(eventType);
					if (predicate!= null && predicate.isComplexExpression(eventType))
					{
						
						//at this point we know that we have only one initiator (complex
						//predicates allowed only in the case of IGNORE policy and only 
						//if we have only an event initiator type and only one event initiator type
						assert(segment.getInitiators().size() == 1);					
						ITemporalContextBound initiator = segment.getInitiators().get(0); //get the initiator - at this point we know can be only one
						assert (initiator instanceof EventInitiator);
						List<IDataObject> dataInstances = new ArrayList<IDataObject>();
						dataInstances.add((IEventInstance)event);
						dataInstances.add(((EventInitiator)initiator).getEventInstance());
						Boolean evaluationResult = (Boolean)predicate.evaluate(dataInstances);
						if (!evaluationResult) continue;
					}
					
				}
				
				segment.addTerminator(terminatorInstance);
				addedAsTerminator = true;
			}			
		}
		return addedAsTerminator;
	}
	

	public boolean hasActiveSegments() {
		return (!activeSegments.isEmpty());
	}

	public TemporalContextType getTemporalContextType() {		
		return temporalContextType;
	}

	public void handleAbsoluteAndRelaiveTerminators(ITimerListener listener,
			IEventProcessingAgent agentType,SegmentationValue segmentValue) throws ContextServiceException {
		// for absolute or relative terminator - generate a timer
		List<ContextAbsoluteTimeTerminator> aTer = temporalContextType.getAbsoluteTimeTerminators();
		List<ContextRelativeTimeTerminator> rTer = temporalContextType.getRelativeTimeTerminators();
		
		
		
		try {
			// handle absolute time terminators
			for (ContextAbsoluteTimeTerminator terminator: aTer) {
				AdditionalInformation info = new AdditionalInformation(temporalContextType.getName(),
						agentType.getName(),terminator.getId(),NotificationTypeEnum.TERMINATOR,segmentValue);
	
				long terminationTime = terminator.getTerminationTime().getTime();
				long duration = terminationTime - System.currentTimeMillis();				
				facade.getTimerServices().createTimer(listener,info,false,duration,0);					
			}
			// handle relative time terminators
			for (ContextRelativeTimeTerminator terminator: rTer) {
				AdditionalInformation info = new AdditionalInformation(temporalContextType.getName(),
						agentType.getName(),terminator.getId(),NotificationTypeEnum.TERMINATOR,segmentValue);
	
				long duration = terminator.getRelativeTerminationTime();				
				facade.getTimerServices().createTimer(listener,info,false,duration,0);					
			}
		}
		catch (Exception e) {
    		throw (new ContextServiceException(e.getMessage(),e.getCause()));
    	}		
	}

	/**
	 * @param event
	 * @return
	 */
	public boolean satisfiesTerminatorPredicate(IEventInstance event) {	
		
		//TODO: check if the expression is only dependant on terminator attributes or 
		//on terminator and initiator
		//in case it is a simple terminator-based expression evalute, 
		//otherwise return true - the expression will be evaluated later 
		//when the proper segment is fetched
		IExpression predicate = temporalContextType.getTerminatorPredicate(event.getEventType());
		if (predicate == null) { 
			return true;
		}
		//if the expression is complex expression do not evaluate and return true -
		//will evaluate later for each segment based on initiator value
		//otherwise evaluate now
		if (predicate.isComplexExpression(event.getMetadata())) return true;
		
		return (Boolean)predicate.evaluate(event);
	}

	/**
	 * @param event
	 * @return
	 */
	public boolean satisfiesInitiatorPredicate(IEventInstance event) {
		IExpression predicate = temporalContextType.getInitiatorPredicate(event.getEventType());
		if (predicate == null) { 
			return true;
		}
		return (Boolean)predicate.evaluate(event);
	}
	
}
