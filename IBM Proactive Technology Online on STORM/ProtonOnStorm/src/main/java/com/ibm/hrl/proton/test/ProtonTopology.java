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

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

import com.ibm.hrl.proton.ProtonTopologyBuilder;


public class ProtonTopology {
	 public static void main(String[] args) throws Exception {
		 	String jsonFileName = "D:\\EP\\Projects\\Proton\\OpenSourceWorkspace\\ProtonStandalone\\sample\\DistributedCounterVariation.json"; 
		    TopologyBuilder builder = new TopologyBuilder();

		    new ProtonTopologyBuilder().buildProtonTopology(builder,new ProtonSpout(),new OutputBolt(),"outputBolt",jsonFileName);
		    

		    Config conf = new Config();
		    conf.setDebug(true);
		    conf.setNumWorkers(2);
		    

		    if (args != null && args.length > 0) {
		      conf.setNumWorkers(3);

		      StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
		    }
		    else {

		      LocalCluster cluster = new LocalCluster();
		      cluster.submitTopology("test", conf, builder.createTopology());
		      Utils.sleep(10000);
		      //cluster.killTopology("test");
		      //cluster.shutdown();
		    }
		  }
}
