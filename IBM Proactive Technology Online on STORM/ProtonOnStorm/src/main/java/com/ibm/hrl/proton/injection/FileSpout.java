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
 * 
 * IBM-Review-Requirement: Art30.3 - DO NOT TRANSFER OR EXCLUSIVELY LICENSE THE FOLLOWING CODE UNTIL Jan 31, 2022!
 * Please note that the code in this file  was developed for the PSYMBIOSYS project in HRL funded by the European Union  under the Horizon 2020 Program.
 * The project started on Feb 1, 2015 until Jan 31, 2018. Thus, in accordance with article 30.3 of the Multi-Beneficiary General Model Grant Agreement of the Program, 
 * the above limitations are in force until Jan 31, 2022.
 * For further details please contact the developers lead Inna Skarbovsky (inna@il.ibm.com), or IP attorney Udi Einhorn (UDIE@il.ibm.com).
 ******************************************************************************/

package com.ibm.hrl.proton.injection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.ibm.hrl.proton.adapters.formatters.CSVTextFormatter;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter.TextFormatterType;
import com.ibm.hrl.proton.adapters.formatters.JSONFormatter;
import com.ibm.hrl.proton.adapters.formatters.TagTextFormatter;
import com.ibm.hrl.proton.adapters.formatters.XmlNgsiFormatter;
import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.inout.ProducerMetadata;
import com.ibm.hrl.proton.metadata.inout.ProducerMetadata.ProducerType;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.routing.STORMMetadataFacade;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;
import com.ibm.hrl.proton.utilities.containers.Pair;
import com.ibm.hrl.proton.utilities.facadesManager.FacadesManager;

public class FileSpout extends BaseRichSpout{
	FacadesManager facadesManager;
	STORMMetadataFacade metadataFacade;
	SpoutOutputCollector _collector;
	private BufferedReader eventReader;
	private ITextFormatter fileFormatter;
	
	
	private boolean done;
	
	//producer properties
	String fileName;
	TextFormatterType fileFormatterType;
	ProducerType producerType;
	long delay;
	long pollingDelay;
	
	private long prevTimestamp =0;
	

	public FileSpout(FacadesManager facadesManager,STORMMetadataFacade metadataFacade) {
		super();
		this.facadesManager = facadesManager;
		this.metadataFacade = metadataFacade;
	}
	
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		_collector = collector;
		try {
			Collection<ProducerMetadata> producers = metadataFacade.getMetadataFacade().getConsumerProducerMetadata().getProducers();
			//we assume that this producer is used for testing purposes and therefore there is just one..
			ProducerMetadata producer = producers.iterator().next();
			
			//making sure it is indeed file producer
			producerType = producer.getProducerType();
			boolean fileProducer = (producerType == ProducerType.FILE || producerType == ProducerType.TIMED) ? true : false;
			if (!fileProducer ) throw new RuntimeException("the designated producer"+ producer.getProducerName()+" is a not a file producer");
			
			//opening for reading
			fileName = (String)producer.getProducerProperty(MetadataParser.FILENAME);
			fileFormatterType = TextFormatterType.valueOf(((String)producer.getProducerProperty(MetadataParser.FORMATTER)).toUpperCase());
			
			
			EepFacade eep = facadesManager.getEepFacade();
			EventMetadataFacade eventMetadata = metadataFacade.getMetadataFacade().getEventMetadataFacade();
			switch (fileFormatterType) 
			{
			case XML:
				fileFormatter = new XmlNgsiFormatter(producer.getProducerProperties(),eventMetadata,eep);
				break;
			case JSON:
				fileFormatter = new JSONFormatter(producer.getProducerProperties(),eventMetadata,eep);
				break;
			case CSV:
				fileFormatter = new CSVTextFormatter(producer.getProducerProperties(),eventMetadata,eep);
				break;
			case TAG:
				fileFormatter = new TagTextFormatter(producer.getProducerProperties(),eventMetadata,eep);
				break;
			default:
				throw new AdapterException("Could not initialize file input adapter:"+this+" unrecognised formatter type");
			}
			
			delay = Long.valueOf((String)producer.getProducerProperty(MetadataParser.DELAY));
			pollingDelay = Long.valueOf((String)producer.getProducerProperty(MetadataParser.POLLING_DELAY));
			eventReader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("could not open input file: " + fileName
					+ " for reading, file not found");
		} catch (AdapterException e) {
			throw new RuntimeException("could not create text formatter for file: " + fileName
					+ " for reading, reason: "+e.getMessage());
		}
		
	}
	
	
	private Pair<Values, Long> nextEvent() throws Exception {
		String line = eventReader.readLine();
		if (line != null) {
			String eventLine = new String(line.getBytes(Charset
					.forName("UTF-8")), "UTF-8");
			IEventInstance eventInstance = fileFormatter.parseText(eventLine);
			
			//set the delay time - either timed via occurrence time or just standard delay			
			return new Pair<Values, Long>(new Values(eventInstance.getEventType().getName(), eventInstance.getAttributes()),
						eventInstance.getOccurenceTime());    			
			
		} else {
			return null;
		}
	}

	@Override
	public void nextTuple() {
		
		long delayMicroseconds = 0;
		try {
			if (!done) {
				Pair<Values, Long> event = nextEvent();

				if (event == null) {
					done = true;
				
					eventReader.close();
					return;
				}

				long timestamp = event.getSecondValue();
				//according to the type of the producer calculate the needed time for delay
				
				if (producerType.equals(ProducerType.TIMED))
				{
					delayMicroseconds = prevTimestamp > 0 ?timestamp - prevTimestamp : 0;

					if (delayMicroseconds >= 0) {
						prevTimestamp = timestamp;
					} else {
								delayMicroseconds = 0;
								// leave prevTimestamp as its last value - either this
								// is the 1st event, or the current event has earlier
								// timestamp than the previous one
							}

				}else
				{
					delayMicroseconds = delay;
				}
								Thread.sleep(delayMicroseconds);					
				_collector.emit(event.getFirstValue());
			}
		} catch (Exception e) {
			System.out.println("error reading next event, reason: "+ e.getMessage());
		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("Name",
				STORMMetadataFacade.ATTRIBUTES_FIELD));
		
	}

}
