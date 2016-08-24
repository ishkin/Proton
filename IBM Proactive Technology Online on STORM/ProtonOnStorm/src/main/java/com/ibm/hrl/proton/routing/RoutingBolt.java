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
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import com.ibm.hrl.proton.context.metadata.ComposedSegmentation;
import com.ibm.hrl.proton.expression.eep.EepExpression;
import com.ibm.hrl.proton.metadata.context.CompositeContextType;
import com.ibm.hrl.proton.metadata.context.SegmentationContextType;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.utilities.containers.Pair;
import com.ibm.hrl.proton.utilities.facadesManager.FacadesManager;

public class RoutingBolt extends BaseRichBolt {
	
	OutputCollector _collector;		
	private static final Logger logger = LoggerFactory.getLogger(RoutingBolt.class.getName());	 
	FacadesManager facadesManager;
	STORMMetadataFacade metadataFacade;
	Map<String, ComposedSegmentation> contextInfo = new ConcurrentHashMap<String,ComposedSegmentation>();
	
		
	public RoutingBolt(FacadesManager facadesManager,STORMMetadataFacade metadataFacade) {
		super();
		this.facadesManager = facadesManager;
		this.metadataFacade = metadataFacade;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
			_collector = collector;							
			logger.debug("prepare: done initializing RoutingBolt with task id..."+context.getThisTaskId());
	}

	@Override
	public void execute(Tuple input) {
		  
	      //get the information - to which context and agent to route
	      Set<Pair<String,String>> routingInfo= getRoutingInfo(input);
	     
		      //for each agent/context pair, add this information to the tuple and emit the tuple
		      String eventTypeName = (String)input.getValueByField(EventHeader.NAME_ATTRIBUTE);
		      Map<String,Object> attributes = (Map<String,Object>)input.getValueByField(STORMMetadataFacade.ATTRIBUTES_FIELD);
		    
			  List<Object> tuple = new ArrayList<Object>();	   			 
		      tuple.add(eventTypeName);
		      tuple.add(attributes);
		      
		      if (isConsumerEvent(eventTypeName))
		      {
		    	  _collector.emit(STORMMetadataFacade.CONSUMER_EVENTS_STREAM,tuple);
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
					
					//extract the context information and calculate composed segmentation value
					String segmentationValue = calculateSegmentationValue(routingEntry.getSecondValue(), input);
					eventTuple.add(segmentationValue);
					logger.debug("RoutingBolt: execute : emitting tuple on stream: "+eventTypeName+" with values: "+tuple);
					_collector.emit(STORMMetadataFacade.EVENT_STREAM,eventTuple);
				
			      }
		      }
	     	      
	      _collector.ack(input);

	}

	/**
	 * Get the relevant context metadata, retrieve the composed segmentation value and calculate
	 * @param secondValue
	 * @return
	 */
	private String calculateSegmentationValue(String contextName, Tuple input) 
	{
		IContextType contextType = metadataFacade.getMetadataFacade().getContextMetadataFacade().getContext(contextName);
		ComposedSegmentation globalSegmentation = new ComposedSegmentation();
		if (contextInfo.containsKey(contextName))
		{
			return evaluateSegmentation(contextInfo.get(contextName), input);
		}else
			
		{
			if (contextType instanceof CompositeContextType) {
				List<IContextType> members = ((CompositeContextType)contextType).getMemberContexts();
				for (IContextType context: members) {
					if (context instanceof SegmentationContextType) {
						globalSegmentation.add((SegmentationContextType)context);
					}
				}
			}
			// for single context - create a composite one
			if (!(contextType instanceof CompositeContextType)) { // temporal or segmentation
				if (contextType instanceof SegmentationContextType) {					
					globalSegmentation.add((SegmentationContextType)contextType);
				}			
			}
			
			contextInfo.put(contextName, globalSegmentation);
			return  evaluateSegmentation(globalSegmentation, input);
		}

		
		
	}

	private String evaluateSegmentation(
			ComposedSegmentation composedSegmentation, Tuple input) {
		String composedSegmentationValue = "";
		IEventInstance eventInstance = metadataFacade.createEventFromTuple(input);
		if (composedSegmentation.getSegments().size() != 0)
		{
			// evaluate value of this segment for the given event
			// if event does not contain attributes complying with the composite context
			// return an empty SegmentationValue
			//logger.debug("getSegmentationValue: for event"+event);
			// we assume that event attributes comply with given segment (defs parsing check)
			
			for (SegmentationContextType segment: composedSegmentation.getSegments()) {
				//logger.debug("getSegmentationValue: iterating over segments"+segment);
				//logger.debug("getSegmentationValue: getting parsed expression for event type"+ event.getEventType());
				EepExpression expression = (EepExpression)segment.getParsedSegmentationExpression(eventInstance.getEventType());
				
				if (expression != null) { // event can either participate in this segment or not
					// invoke eep to evaluate expression for the given event instance
					Object expressionResult = expression.copyAndEvaluate(eventInstance);
					String expressionValue = expressionResult.toString();
					composedSegmentationValue = composedSegmentationValue.concat(expressionValue+",");
				}
			}				
			
		}
		
		return composedSegmentationValue;
	}

	private boolean isConsumerEvent(String eventTypeName) {
		 return metadataFacade.getMetadataFacade().getRoutingMetadataFacade().isConsumerEvent(eventTypeName);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {		
		List<String> fieldNames = new ArrayList<String>();
		fieldNames.add(EventHeader.NAME_ATTRIBUTE);
		fieldNames.add(STORMMetadataFacade.ATTRIBUTES_FIELD);
		declarer.declareStream(STORMMetadataFacade.CONSUMER_EVENTS_STREAM, new Fields(fieldNames));
		
		//add the agentName and contextName fields
		List<String> eventFieldNames = new ArrayList<String>();
		eventFieldNames.addAll(fieldNames);
		eventFieldNames.add(STORMMetadataFacade.AGENT_NAME_FIELD);
		eventFieldNames.add(STORMMetadataFacade.CONTEXT_NAME_FIELD);
		eventFieldNames.add(STORMMetadataFacade.CONTEXT_SEGMENTATION_VALUES);

		logger.debug("RoutingBolt: declareOutputFields:declaring stream for RoutingBolt: stream name: " +STORMMetadataFacade.EVENT_STREAM+ "with fields "+fieldNames);
		declarer.declareStream(STORMMetadataFacade.EVENT_STREAM, new Fields(eventFieldNames));
		
		
	}
		
	
	
	/**
	 * Return routing info for a tuple. For each tuple determines what agents/context pairs it should be 
	 *	routed to
	 * @param input
	 * @return
	 */
	public Set<Pair<String,String>> getRoutingInfo(Tuple input)
	{
		logger.debug("RoutingBolt: getRoutingInfo: getting routing info for tuple: "+input);
		Set<Pair<String,String>> routingInfo = null;		
		List<String> tupleFields = input.getFields().toList();
		
		//assuming each tuple has a "name" field		
		String eventTypeName = (String)input.getValueByField(EventHeader.NAME_ATTRIBUTE);
		if (null != eventTypeName)
		{			
			IEventType eventType = metadataFacade.getMetadataFacade().getEventMetadataFacade().getEventType(eventTypeName);
			Map<String,Object> attributes = new HashMap<String,Object>();		
			
			if (null != eventType)
			{
				//the content of the tuple is not important, need to get routing info, and it is only based on the event
				//name				
				IEventInstance eventInstance = new EventInstance(eventType, attributes);						
				//determine routing 
				routingInfo= metadataFacade.getMetadataFacade().getRoutingMetadataFacade().determineRouting(eventInstance);								
				
			}
			
		}
		logger.debug("RoutingBolt: getRoutingInfo: got routing info for tuple: "+input+", routing info: "+routingInfo);
		return routingInfo;
	}
	


}
