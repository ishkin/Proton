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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import com.ibm.hrl.proton.agentQueues.exception.AgentQueueException;
import com.ibm.hrl.proton.agentQueues.queuesManagement.AgentQueuesManager;
import com.ibm.hrl.proton.context.facade.ContextServiceFacade;
import com.ibm.hrl.proton.eventHandler.IEventHandler;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.routing.STORMMetadataFacade;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.utilities.containers.Pair;
import com.ibm.hrl.proton.utilities.facadesManager.FacadesManager;



public class ContextBolt extends BaseRichBolt {

	OutputCollector _collector;
	private static final Logger logger = LoggerFactory.getLogger(ContextBolt.class);	
	private FacadesManager facadesManager;
	private STORMMetadataFacade metadataFacade;
	
	public ContextBolt(FacadesManager facadesManager,STORMMetadataFacade metadataFacade) {		
		super();
		logger.debug("ContextBolt constructor start");
		this.facadesManager = facadesManager;
		this.metadataFacade = metadataFacade;
		logger.debug("ContextBolt constructor end");
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		_collector = collector;
		
		// make sure the metadata is parsed, only once per JVM and all singeltones initiated
		logger.debug("prepare: initializing ContextBolt with task id..."+context.getThisTaskId());
		
		
		IEventHandler stormEventHandler = new StormEventHandler(collector,metadataFacade,facadesManager);
		facadesManager.setEventHandler(stormEventHandler);
		AgentQueuesManager agentQueuesManager = new AgentQueuesManager(facadesManager.getTimerServiceFacade(), stormEventHandler, facadesManager.getWorkManager(),metadataFacade.getMetadataFacade());
		facadesManager.setAgentQueuesManager(agentQueuesManager);
	    try {
			ContextServiceFacade contextServiceFacade = new ContextServiceFacade(facadesManager.getTimerServiceFacade(), stormEventHandler, metadataFacade.getMetadataFacade().getContextMetadataFacade());
	    	facadesManager.setContextServiceFacade(contextServiceFacade);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Could not initialize Context bolt, reason : "+e.getMessage());
			throw new RuntimeException(e);
		} 
	    logger.debug("prepare: done initializing ContextBolt with task id..."+context.getThisTaskId());
		
	}

	@Override
	public void execute(Tuple input) {
		//get the tuple, remove the agent and context information
		logger.debug("ContextBolt: execute : passing tuple : "+ input+" for processing...");
		IEventInstance eventInstance = metadataFacade.createEventFromTuple(input);		
		try {
			String contextName = (String)input.getValueByField(STORMMetadataFacade.CONTEXT_NAME_FIELD);
			String agentName = (String)input.getValueByField(STORMMetadataFacade.AGENT_NAME_FIELD);
			Set<Pair<String,String>> agentContextSet = new HashSet<Pair<String,String>>();
			Pair<String,String> agentContextPair = new Pair<String,String>(agentName,contextName);
			agentContextSet.add(agentContextPair);
			logger.debug("ContextBolt: execute : created event instnace: "+eventInstance+" from tuple: "+input+"passing to agent queues for"+agentContextSet+"for processing...");
			facadesManager.getAgentQueuesManager().passEventToQueues(eventInstance, agentContextSet);
			//AgentQueuesManager.getInstance().passEventToQueues(eventInstance);
		} catch (AgentQueueException e) {
			e.printStackTrace();
			logger.error("Could not pass event for processing of context service, reason: " +e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		logger.debug("ContextBolt: execute : done processing tuple "+input);
		 _collector.ack(input);
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(EventHeader.NAME_ATTRIBUTE);
		fieldNames.add(STORMMetadataFacade.ATTRIBUTES_FIELD);
		//add the agentName and contextName fields
		fieldNames.add(STORMMetadataFacade.AGENT_NAME_FIELD);
		fieldNames.add(STORMMetadataFacade.CONTEXT_PARTITION_FIELD);
		fieldNames.add(STORMMetadataFacade.CONTEXT_SEGMENTATION_VALUES);

		logger.debug("ContextBolt: declareOutputFields:declaring stream " +STORMMetadataFacade.EVENT_STREAM+ "with fields "+fieldNames);
		declarer.declareStream(STORMMetadataFacade.EVENT_STREAM, new Fields(fieldNames));

		
		//also declare a stream for termination
		/*List<String> terminationFields = new ArrayList<String>();
		terminationFields.add(MetadataFacade.AGENT_NAME_FIELD);
		terminationFields.add(MetadataFacade.CONTEXT_PARTITION_FIELD);
		terminationFields.add(MetadataFacade.CONTEXT_SEGMENTATION_VALUES);
		logger.fine("ContextBolt: declareOutputFields:declaring termination stream " +MetadataFacade.TERMINATION_EVENTS_STREAM+ "with fields "+terminationFields);
		declarer.declareStream(MetadataFacade.TERMINATION_EVENTS_STREAM, new Fields(terminationFields));*/
	}

}
