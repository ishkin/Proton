/*******************************************************************************
 * Copyright 2015 IBM
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
package com.ibm.hrl.proton.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Fields;

import com.ibm.hrl.proton.agentQueues.exception.AgentQueueException;
import com.ibm.hrl.proton.agentQueues.exception.EventHandlingException;
import com.ibm.hrl.proton.agentQueues.queuesManagement.AgentQueuesManager;
import com.ibm.hrl.proton.context.exceptions.ContextServiceException;
import com.ibm.hrl.proton.context.facade.ContextServiceFacade;
import com.ibm.hrl.proton.eventHandler.IEventHandler;
import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.routing.MetadataFacade;
import com.ibm.hrl.proton.runtime.context.notifications.IContextNotification;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EPAManagerMetadataFacade;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.containers.Pair;

public class StormEventHandler implements IEventHandler {

	private static final Logger logger = Logger.getLogger("StormEventHandler");	
	private OutputCollector _collector;
	
	
	protected StormEventHandler(OutputCollector _collector)
	{
		this._collector = _collector;
	}
	
	@Override
	public void handleEventInstance(ITimedObject timedObject, String agentName,
			String contextName) throws EventHandlingException {
		//upon reply pass it to agent using the relevant partitions
        logger.fine("StormEventHandler: handleEventInstance: timed object: "+timedObject+", agent: "+agentName+" ,context: "+contextName);        
        Pair<Collection<Pair<String,Map<String,Object>>>, Collection<Pair<String,Map<String,Object>>>> partitions;
        try
        {
            partitions = ContextServiceFacade.getInstance().processEventInstance(timedObject, contextName, agentName);
        }
        catch (ContextServiceException e)
        {
            throw new EventHandlingException("Could not pass event instance "+timedObject+" to context service for agent"+agentName+" and context "+contextName+" ,reason: "+e.getMessage());
        }
        Collection<Pair<String,Map<String,Object>>> terminatedPartitions = partitions.getFirstValue();
        Collection<Pair<String,Map<String,Object>>> participatingPartitions = partitions.getSecondValue();       
        logger.fine("StormEventHandler: handleEventInstance: context service returned the following partitions: "+ partitions);
       
        
        //if the timed object is context notification its job is done - it was passed to context service. 
        //however we need to make sure if as the result of that notification some partitions were actually
        //terminated - we need to inform the relevant agent instances
        
        if (timedObject instanceof IContextNotification) {
            if (terminatedPartitions != null && !terminatedPartitions.isEmpty())
            {
               
                	Set<List<Object>> tuples = createTerminationTuples(agentName, terminatedPartitions);                	
                	for (List<Object> tupleFields : tuples) 
                	{
						//emit the termination tuples
                		logger.fine("StormEventHandler: received termination notification, created respective output tuple: with fields "+tupleFields+" ,passing for processing to EPAManagerBolt");
                		_collector.emit(MetadataFacade.EVENT_STREAM, tupleFields);
					}                    
                    return;
               
            }
            return;
        }
        
        IEventInstance event = (IEventInstance)timedObject;
        IEventProcessingAgent agentInstanceDef = EPAManagerMetadataFacade.getInstance().getAgentDefinition(agentName);
        List<Operand> eventOperands = agentInstanceDef.getEventInputOperands(event.getEventType());
        if (!(eventOperands == null || eventOperands.isEmpty()))
        {
            //this event is participant in the agent - pass the participation information
            if ((participatingPartitions != null) && !(participatingPartitions.isEmpty()))
            {
               
                    
                    Set<List<Object>> tuples = createParticipationTuples(event, agentName, participatingPartitions);
                    for (List<Object> tuplesFields : tuples) {
                    	logger.fine("StormEventHandler: participation notification, created respective output tuple: event name" + event.getEventType().getName()+" with field values "+tuplesFields+" ,passing for processing to EPAManagerBolt");
                    	_collector.emit(MetadataFacade.EVENT_STREAM, tuplesFields);
					}
                    
                    
                    
                    
               
            }
        }
        
        if (terminatedPartitions != null && !terminatedPartitions.isEmpty())
        {
        	Set<List<Object>> tuples = createTerminationTuples(agentName, terminatedPartitions);                	
        	for (List<Object> tupleFields : tuples) 
        	{
				//emit the termination tuples
        		logger.fine("StormEventHandler: received termination notification, created respective output tuple: with fields "+tupleFields+" ,passing for processing to EPAManagerBolt");
        		_collector.emit(MetadataFacade.EVENT_STREAM, tupleFields);
			}     
        }

	}

	@Override
	public void routeContextNotification(
			IContextNotification contextNotification)
			throws EventHandlingException {
		 try {
			 logger.fine("StormEventHandler: routeContextNotification: routing context notification: "+contextNotification+" back to agent queues");
			AgentQueuesManager.getInstance().passEventToQueues(contextNotification);
		} catch (AgentQueueException e) {
			throw new EventHandlingException("Error passing context notification back to agent queues, reason: "+e.getMessage());
		}

	}
	
	private Set<List<Object>> createTerminationTuples(String agentName, Collection<Pair<String, Map<String,Object>>> terminatedPartitions)
	{
		Set<List<Object>> terminatedTuples = new HashSet<List<Object>>(); 
		//need to create a termination tuple with agent name, context partition name, and serialization values
		for (Pair<String, Map<String, Object>> terminatedPartition : terminatedPartitions) {
			String contextPartition = terminatedPartition.getFirstValue();
			 List<Object> tuple = new ArrayList<Object>();
			 tuple.add(MetadataFacade.TERMINATOR_EVENT_NAME);
			 tuple.add(new HashMap<String,Object>());
			 tuple.add(agentName);
			 tuple.add(contextPartition);
			 tuple.add(terminatedPartition.getSecondValue());
			 terminatedTuples.add(tuple);
		}
		
		return terminatedTuples;
	}
	
	private Set<List<Object>> createParticipationTuples(IEventInstance event, String agentName, Collection<Pair<String, Map<String,Object>>> participatingPartitions ){
		Set<List<Object>> newTuples = new HashSet<List<Object>>(); 
		
		List<Object> tupleFieldsValues = MetadataFacade.getInstance().createOutputTuple(event);	
		tupleFieldsValues.add(agentName);
		
		for (Pair<String, Map<String,Object>> participatingPartition : participatingPartitions) {
			String  contextPartition = participatingPartition.getFirstValue();
			List<Object> participatingPartitionTuple = new ArrayList<Object>(tupleFieldsValues);
			participatingPartitionTuple.add(contextPartition);
			participatingPartitionTuple.add(participatingPartition.getSecondValue());
			newTuples.add(participatingPartitionTuple);
		}
		
		return newTuples;
		
	}

}
