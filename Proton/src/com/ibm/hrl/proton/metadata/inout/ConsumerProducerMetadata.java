/*******************************************************************************
 * Copyright 2014 IBM
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
package com.ibm.hrl.proton.metadata.inout;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConsumerProducerMetadata {

	private static ConsumerProducerMetadata instance;
	private Map<String,ConsumerMetadata> consumers;
	private Map<String,ProducerMetadata> producers;
	
	private ConsumerProducerMetadata()
	{
		this.consumers = new HashMap<String,ConsumerMetadata>();
		this.producers = new HashMap<String,ProducerMetadata>();
	}
	
	public static ConsumerProducerMetadata initializeInstance()
	{
		instance = new ConsumerProducerMetadata();
		return instance;
	}
	
	public static ConsumerProducerMetadata getInstance()
	{
		return instance;
	}
	
	public ConsumerMetadata getConsumer(String name){
		return consumers.get(name);
	}
	
	public ProducerMetadata getProducer(String name){
		return producers.get(name);
	}
	
	public Collection<ConsumerMetadata> getConsumers(){
		return consumers.values();
	}
	
	public Collection<ProducerMetadata> getProducers(){
		return producers.values();
	}
	
	public void addConsumer(String consumerName, ConsumerMetadata consumerMetadata){
		consumers.put(consumerName, consumerMetadata);
	}
	
	public void addProducer(String producerName, ProducerMetadata producerMetadata){
		producers.put(producerName, producerMetadata);
	}

}
