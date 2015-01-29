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
package com.ibm.hrl.proton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;

import com.ibm.hrl.proton.agents.EPAManagerBolt;
import com.ibm.hrl.proton.context.ContextBolt;
import com.ibm.hrl.proton.routing.MetadataFacade;
import com.ibm.hrl.proton.routing.RoutingBolt;

public class ProtonTopologyBuilder {
	private static final String INPUT_NAME="input";
	private static final String ROUTING_BOLT_NAME ="routingBolt";
	private static final String CONTEXT_BOLT_NAME = "contextBolt";
	private static final String EPA_MANAGER_BOLT_NAME = "epaManagerBolt";

	private static Logger logger = LoggerFactory.getLogger(ProtonTopologyBuilder.class);

	//TODO: add here properties: parallelism for each bolt (routing, context, EPA manager)
	public void buildProtonTopology(TopologyBuilder topologyBuilder,BaseRichSpout inputSpout, BaseRichBolt outputBolt, String outputBoltName,String jsonFileName){
		logger.info("Building topology with EPN from " + jsonFileName);
		
		String jsonTxt = buildJSON(jsonFileName);
		
		logger.debug("\nEPN JSON:\n" + jsonTxt + "\n");

		MetadataFacade.initializeMetadataFacade(jsonTxt);
		
		logger.info("Proton metadata initialized successfully");
		
		topologyBuilder.setSpout(ProtonTopologyBuilder.INPUT_NAME, inputSpout);		   
		topologyBuilder.setBolt(ProtonTopologyBuilder.ROUTING_BOLT_NAME, new RoutingBolt(jsonTxt)).shuffleGrouping(ProtonTopologyBuilder.INPUT_NAME).shuffleGrouping(ProtonTopologyBuilder.EPA_MANAGER_BOLT_NAME, MetadataFacade.EVENT_STREAM);
		topologyBuilder.setBolt(ProtonTopologyBuilder.CONTEXT_BOLT_NAME, new ContextBolt(jsonTxt)).fieldsGrouping(ProtonTopologyBuilder.ROUTING_BOLT_NAME, MetadataFacade.EVENT_STREAM,new Fields(MetadataFacade.AGENT_NAME_FIELD,MetadataFacade.CONTEXT_NAME_FIELD));
		topologyBuilder.setBolt(ProtonTopologyBuilder.EPA_MANAGER_BOLT_NAME, new EPAManagerBolt(jsonTxt)).fieldsGrouping(ProtonTopologyBuilder.CONTEXT_BOLT_NAME, MetadataFacade.EVENT_STREAM, new Fields(MetadataFacade.AGENT_NAME_FIELD,MetadataFacade.CONTEXT_PARTITION_FIELD));
		topologyBuilder.setBolt(outputBoltName, outputBolt).shuffleGrouping(ProtonTopologyBuilder.ROUTING_BOLT_NAME,MetadataFacade.CONSUMER_EVENTS_STREAM);
		
		logger.info("Building topology completed.");
	}
	
	private String buildJSON(String metadataFileName)
	{
		String line;
		 StringBuilder sb = new StringBuilder();
	     BufferedReader in = null;
	     String jsonFileName = metadataFileName;
	     try
	     {
	    	 in = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFileName), "UTF-8"));	    
	    	 while ((line = in.readLine()) != null)
	    	 {
	    		 sb.append(line);
	    	 }

	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }finally
	     {
	    	 try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     }
	     
	     String jsonTxt  = sb.toString();
	     return jsonTxt;
	}
}
