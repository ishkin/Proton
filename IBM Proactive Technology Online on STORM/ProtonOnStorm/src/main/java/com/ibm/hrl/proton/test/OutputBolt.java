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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class OutputBolt extends BaseRichBolt {

	OutputCollector _collector;
	BufferedWriter out;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this._collector = _collector;
		String fileName = "D:\\EP\\Projects\\EU\\STORM\\output.txt";
		File file = new File(fileName);
		try {
			boolean fileExists = file.createNewFile();
			FileWriter fstream = new FileWriter(fileName);
			out = new BufferedWriter(fstream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //we create a file in case doesn't exist*/
		  
		

	}

	@Override
	public void execute(Tuple input) {
		System.out.println("Received tuple: "+input);
		try {
			out.write(input.toString()+LINE_SEPARATOR);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

}
