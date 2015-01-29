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
package com.ibm.hrl.proton.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.RoutingMetadataFacade;
import com.ibm.hrl.proton.utilities.containers.Pair;

public class RoutingBolt extends BaseRichBolt {
	
	OutputCollector _collector;	
	String jsonTxt;
	private static final Logger logger = Logger.getLogger(RoutingBolt.class.getName());	 
	
		
	public RoutingBolt(String jsonTxt) {
		super();
		this.jsonTxt = jsonTxt;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
			_collector = collector;
			logger.fine("prepare: initializing RoutingBolt with task id..."+context.getThisTaskId());
			// make sure the metadata is parsed, only once per JVM and all singeltones initiated
			MetadataFacade.initializeMetadataFacade(this.jsonTxt);
			logger.fine("prepare: done initializing RoutingBolt with task id..."+context.getThisTaskId());
	}

	@Override
	public void execute(Tuple input) {
		  
	      //get the information - to which context and agent to route
	      Set<Pair<String,String>> routingInfo= getRoutingInfo(input);
	     
		      //for each agent/context pair, add this information to the tuple and emit the tuple
		      String eventTypeName = (String)input.getValueByField(EventHeader.NAME_ATTRIBUTE);
		      Map<String,Object> attributes = (Map<String,Object>)input.getValueByField(MetadataFacade.ATTRIBUTES_FIELD);
		    
			  List<Object> tuple = new ArrayList<Object>();	   			 
		      tuple.add(eventTypeName);
		      tuple.add(attributes);
		      
		      if (isConsumerEvent(eventTypeName))
		      {
		    	  _collector.emit(MetadataFacade.CONSUMER_EVENTS_STREAM,tuple);
		      }
		      
		      if (routingInfo != null)
		      {
			      //add the agent/context routing info
			      for (Iterator iterator = routingInfo.iterator(); iterator.hasNext();) 
			      {
			    	List<Object> eventTuple = new ArrayList<Object>();
			    	eventTuple.addAll(tuple);
					Pair<String,String> routingEntry = (Pair<String,String>) iterator.next();			
					eventTuple.add(routingEntry.getFirstValue());
					eventTuple.add(routingEntry.getSecondValue());
					logger.fine("RoutingBolt: execute : emitting tuple on stream: "+eventTypeName+" with values: "+tuple);
					_collector.emit(MetadataFacade.EVENT_STREAM,eventTuple);
				
			      }
		      }
	     	      
	      _collector.ack(input);

	}

	private boolean isConsumerEvent(String eventTypeName) {
		 return RoutingMetadataFacade.getInstance().isConsumerEvent(eventTypeName);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {		
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(EventHeader.NAME_ATTRIBUTE);
		fieldNames.add(MetadataFacade.ATTRIBUTES_FIELD);
		declarer.declareStream(MetadataFacade.CONSUMER_EVENTS_STREAM, new Fields(fieldNames));
		
		//add the agentName and contextName fields
		List<String> eventFieldNames = new ArrayList<String>();
		eventFieldNames.addAll(fieldNames);
		eventFieldNames.add(MetadataFacade.AGENT_NAME_FIELD);
		eventFieldNames.add(MetadataFacade.CONTEXT_NAME_FIELD);

		logger.fine("RoutingBolt: declareOutputFields:declaring stream for RoutingBolt: stream name: " +MetadataFacade.EVENT_STREAM+ "with fields "+fieldNames);
		declarer.declareStream(MetadataFacade.EVENT_STREAM, new Fields(eventFieldNames));
		
		
	}
		
	
	
	/**
	 * Return routing info for a tuple. For each tuple determines what agents/context pairs it should be 
	 *	routed to
	 * @param input
	 * @return
	 */
	public Set<Pair<String,String>> getRoutingInfo(Tuple input)
	{
		logger.fine("RoutingBolt: getRoutingInfo: getting routing info for tuple: "+input);
		Set<Pair<String,String>> routingInfo = null;		
		List<String> tupleFields = input.getFields().toList();
		
		//assuming each tuple has a "name" field		
		String eventTypeName = (String)input.getValueByField(EventHeader.NAME_ATTRIBUTE);
		if (null != eventTypeName)
		{			
			IEventType eventType = EventMetadataFacade.getInstance().getEventType(eventTypeName);
			Map<String,Object> attributes = new HashMap<String,Object>();		
			
			if (null != eventType)
			{
				//the content of the tuple is not important, need to get routing info, and it is only based on the event
				//name				
				IEventInstance eventInstance = new EventInstance(eventType, attributes);						
				//determine routing 
				routingInfo= RoutingMetadataFacade.getInstance().determineRouting(eventInstance);
			}
			
		}
		logger.fine("RoutingBolt: getRoutingInfo: got routing info for tuple: "+input+", routing info: "+routingInfo);
		return routingInfo;
	}
	


}
