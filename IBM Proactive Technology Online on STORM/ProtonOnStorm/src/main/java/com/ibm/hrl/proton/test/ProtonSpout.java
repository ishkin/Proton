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
package com.ibm.hrl.proton.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import com.ibm.hrl.proton.routing.STORMMetadataFacade;



public class ProtonSpout extends BaseRichSpout {
	public static Logger LOG = Logger.getLogger(ProtonSpout.class);
	boolean _isDistributed;
	SpoutOutputCollector _collector;
	
	public ProtonSpout() 
	{
	        this(true);
	}
	
	public ProtonSpout(boolean isDistributed) 
	{
	        _isDistributed = isDistributed;
	}
	
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		 _collector = collector;
		 //TODO: connect to real source of intformation
		
	}

	@Override
	public void nextTuple() {
		 Utils.sleep(100);	  
		 /*int min = 0;
		 int max = 100; 
		 double A1 = min + (int)(Math.random() * ((max - min) + 1));
		 String A2 = "test"+A1;*/
		 List<Map<String,Object>> eventList = new ArrayList<Map<String,Object>>();
		 Map<String,Object> attributes1 = new HashMap<String,Object>();
		 //attributes1.put("Name", "AggregatedSensorRead");
		 attributes1.put("phoneNumber", "9");
		 attributes1.put("nodeID", "10");
		 attributes1.put("callDuration", "1");		
		 eventList.add(attributes1);
		 Map<String,Object> attributes2 = new HashMap<String,Object>();
		 //attributes2.put("Name", "AggregatedSensorRead");
		 attributes2.put("phoneNumber", "9");
		 attributes2.put("nodeID", "10");
		 attributes2.put("callDuration", "1");
		 eventList.add(attributes2);
		 Map<String,Object> attributes3 = new HashMap<String,Object>();
		 //attributes3.put("Name", "AggregatedSensorRead");
		 attributes3.put("phoneNumber", "3");
		 attributes3.put("nodeID", "10");
		 attributes3.put("callDuration", "1");			
		 eventList.add(attributes3);
		
		 
		 for (Map<String, Object> event : eventList) {
			   Utils.sleep(100);	  
			  _collector.emit(new Values("CallData",event));
		}

		
		
	}

	
	 
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		 declarer.declare(new Fields("Name",STORMMetadataFacade.ATTRIBUTES_FIELD));
		
	}
	
	public void ack(Object msgId) {
		System.out.println("tupple acked");
    }

    public void fail(Object msgId) {
    	System.out.println("tupple failed");
    }
    
    @Override
    public Map<String, Object> getComponentConfiguration() {
        if(!_isDistributed) {
            Map<String, Object> ret = new HashMap<String, Object>();
            ret.put(Config.TOPOLOGY_MAX_TASK_PARALLELISM, 1);
            return ret;
        } else {
            return null;
        }
    }    

}
