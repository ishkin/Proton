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
package com.ibm.hrl.proton.agents;

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

import com.ibm.hrl.proton.epaManager.EPAManagerFacade;
import com.ibm.hrl.proton.epaManager.exceptions.EPAManagerException;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.routing.STORMMetadataFacade;
import com.ibm.hrl.proton.routing.StormEventRouter;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.utilities.containers.Pair;
import com.ibm.hrl.proton.utilities.facadesManager.FacadesManager;

public class EPAManagerBolt extends BaseRichBolt {
	private static final Logger logger = LoggerFactory.getLogger(EPAManagerBolt.class);	 
	OutputCollector _collector;
	String jsonTxt;
	private FacadesManager facadesManager;
	private STORMMetadataFacade metadataFacade;
	
	
	public EPAManagerBolt(FacadesManager facadesManager,STORMMetadataFacade metadataFacade) {
		super();		
		this.facadesManager = facadesManager;
		this.metadataFacade = metadataFacade;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		_collector = collector;
		logger.debug("prepare: initializing EPAManagerBolt with task id..."+context.getThisTaskId());
		StormEventRouter eventRouter = new StormEventRouter(_collector,metadataFacade);
		EPAManagerFacade epaManager = new EPAManagerFacade(facadesManager.getWorkManager(), eventRouter, null,metadataFacade.getMetadataFacade());
		facadesManager.setEventRouter(eventRouter);
		facadesManager.setEpaManager(epaManager);
		logger.debug("prepare: done initializing EPAManagerBolt with task id..."+context.getThisTaskId());

	}

	@Override
	public void execute(Tuple input) {
		//decide what kind of tuple received - whether it is a regular tuple or termination tuple
		//call appropriate method of EPAManagerFacade
		logger.debug("EPAManagerBolt: execute : passing tuple : "+ input+" for processing...");
		String agentName = (String)input.getValueByField(STORMMetadataFacade.AGENT_NAME_FIELD);
		String contextPartition = (String)input.getValueByField(STORMMetadataFacade.CONTEXT_PARTITION_FIELD);
		Map<String,Object> segmentationValues = (Map<String,Object>)input.getValueByField(STORMMetadataFacade.CONTEXT_SEGMENTATION_VALUES);

		String eventTypeName = (String)input.getValueByField(EventHeader.NAME_ATTRIBUTE);
		
		try{
			if (!eventTypeName.equals(STORMMetadataFacade.TERMINATOR_EVENT_NAME))
			{
				//real event and not just termination notification
				IEventInstance event = metadataFacade.createEventFromTuple(input);
				logger.debug("EPAManagerBolt: execute : created event instance  : "+ event+" from tuple "+input+", for agent: "+agentName+" and context partition "+contextPartition+", passing for processing to EPA manager...");
				Set<Pair<String,Map<String,Object>>> partitionsToProcess = new HashSet<Pair<String,Map<String,Object>>>();
				partitionsToProcess.add(new Pair<String,Map<String,Object>>(contextPartition,segmentationValues));
				facadesManager.getEpaManager().processEvent(event, agentName, partitionsToProcess);
			}else
			{
				logger.debug("EPAManagerBolt: execute : received termination notification for agent: "+ agentName+" and contextPartitition: "+contextPartition+", passing for processing...");
				facadesManager.getEpaManager().processDeffered(agentName, contextPartition, segmentationValues);
			}

		}catch (EPAManagerException e) {		
			e.printStackTrace();
			logger.error("Could not pass event for processing to EPAManager, reason: "+e.getMessage());
			throw new RuntimeException(e);
		}
		_collector.ack(input);

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {	
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(EventHeader.NAME_ATTRIBUTE);
		fieldNames.add(STORMMetadataFacade.ATTRIBUTES_FIELD);
		logger.debug("EPAManagerBolt: declareOutputFields:declaring stream " +STORMMetadataFacade.EVENT_STREAM+ "with fields "+fieldNames);
		declarer.declareStream(STORMMetadataFacade.EVENT_STREAM, new Fields(fieldNames));
		
	}
}
