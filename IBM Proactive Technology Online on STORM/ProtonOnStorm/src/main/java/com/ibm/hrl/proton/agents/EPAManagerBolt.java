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
import java.util.logging.Logger;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import com.ibm.hrl.proton.epaManager.EPAManagerFacade;
import com.ibm.hrl.proton.epaManager.exceptions.EPAManagerException;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.routing.MetadataFacade;
import com.ibm.hrl.proton.routing.StormEventRouter;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.server.timerService.TimerServiceFacade;
import com.ibm.hrl.proton.server.workManager.WorkManagerFacade;
import com.ibm.hrl.proton.utilities.containers.Pair;

public class EPAManagerBolt extends BaseRichBolt {
	private static final Logger logger = Logger.getLogger(EPAManagerBolt.class.getName());	 
	OutputCollector _collector;
	String jsonTxt;
	
	public EPAManagerBolt(String jsonTxt) {
		super();
		this.jsonTxt = jsonTxt;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		_collector = collector;
		logger.fine("prepare: initializing EPAManagerBolt with task id..."+context.getThisTaskId());
		MetadataFacade.initializeMetadataFacade(jsonTxt);
		TimerServiceFacade.getInstance();
		EPAManagerFacade.initializeInstance(WorkManagerFacade.getInstance(), new StormEventRouter(_collector), null);
		logger.fine("prepare: done initializing EPAManagerBolt with task id..."+context.getThisTaskId());

	}

	@Override
	public void execute(Tuple input) {
		//decide what kind of tuple received - whether it is a regular tuple or termination tuple
		//call appropriate method of EPAManagerFacade
		logger.fine("EPAManagerBolt: execute : passing tuple : "+ input+" for processing...");
		String agentName = (String)input.getValueByField(MetadataFacade.AGENT_NAME_FIELD);
		String contextPartition = (String)input.getValueByField(MetadataFacade.CONTEXT_PARTITION_FIELD);
		Map<String,Object> segmentationValues = (Map<String,Object>)input.getValueByField(MetadataFacade.CONTEXT_SEGMENTATION_VALUES);

		String eventTypeName = (String)input.getValueByField(EventHeader.NAME_ATTRIBUTE);
		
		try{
			if (!eventTypeName.equals(MetadataFacade.TERMINATOR_EVENT_NAME))
			{
				//real event and not just termination notification
				IEventInstance event = MetadataFacade.getInstance().createEventFromTuple(input);
				logger.fine("EPAManagerBolt: execute : created event instance  : "+ event+" from tuple "+input+", for agent: "+agentName+" and context partition "+contextPartition+", passing for processing to EPA manager...");
				Set<Pair<String,Map<String,Object>>> partitionsToProcess = new HashSet<Pair<String,Map<String,Object>>>();
				partitionsToProcess.add(new Pair<String,Map<String,Object>>(contextPartition,segmentationValues));
				EPAManagerFacade.getInstance().processEvent(event, agentName, partitionsToProcess);
			}else
			{
				logger.fine("EPAManagerBolt: execute : received termination notification for agent: "+ agentName+" and contextPartitition: "+contextPartition+", passing for processing...");
				EPAManagerFacade.getInstance().processDeffered(agentName, contextPartition, segmentationValues);
			}

		}catch (EPAManagerException e) {		
			e.printStackTrace();
			logger.severe("Could not pass event for processing to EPAManager, reason: "+e.getMessage());
			throw new RuntimeException(e);
		}
		_collector.ack(input);

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {	
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(EventHeader.NAME_ATTRIBUTE);
		fieldNames.add(MetadataFacade.ATTRIBUTES_FIELD);
		logger.fine("EPAManagerBolt: declareOutputFields:declaring stream " +MetadataFacade.EVENT_STREAM+ "with fields "+fieldNames);
		declarer.declareStream(MetadataFacade.EVENT_STREAM, new Fields(fieldNames));
		
	}
}
