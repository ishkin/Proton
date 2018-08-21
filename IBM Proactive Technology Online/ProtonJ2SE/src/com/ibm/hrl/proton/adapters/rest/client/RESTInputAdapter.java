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
package com.ibm.hrl.proton.adapters.rest.client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.ibm.hrl.proton.adapters.configuration.IInputAdapterConfiguration;
import com.ibm.hrl.proton.adapters.configuration.IInputAdapterConfiguration.InputAdapterPullModeEnum;
import com.ibm.hrl.proton.adapters.connector.IInputConnector;
import com.ibm.hrl.proton.adapters.formatters.CSVTextFormatter;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter.TextFormatterType;
import com.ibm.hrl.proton.adapters.formatters.JSONFormatter;
import com.ibm.hrl.proton.adapters.formatters.TagTextFormatter;
import com.ibm.hrl.proton.adapters.formatters.XmlNgsiFormatter;
import com.ibm.hrl.proton.adapters.interfaces.AbstractInputAdapter;
import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.inout.ProducerMetadata;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

public class RESTInputAdapter extends AbstractInputAdapter {


		
	private ITextFormatter textFormatter;	
	private String producerURL;
	private String contentType;
	private HttpClient httpClient;
	private GetMethod getMethod;
	
	public RESTInputAdapter(ProducerMetadata producerMetadata,IInputConnector serverConnector,EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException {
		super(producerMetadata,serverConnector, eventMetadata);
		producerURL  = ((RESTInputAdapterConfiguration)configuration).getUrl();
		contentType = ((RESTInputAdapterConfiguration)configuration).getContentType();	
		TextFormatterType formatterType = ((RESTInputAdapterConfiguration)configuration).getFormatterType();
		
		switch (formatterType) 
		{
		case XML:
			textFormatter = new XmlNgsiFormatter(producerMetadata.getProducerProperties(),eventMetadata,eep);
			break;
		case JSON:
			textFormatter = new JSONFormatter(producerMetadata.getProducerProperties(),eventMetadata,eep);
			break;
		case CSV:			
			textFormatter = new CSVTextFormatter(producerMetadata.getProducerProperties(),eventMetadata,eep);
		case TAG:
			textFormatter = new TagTextFormatter(producerMetadata.getProducerProperties(),eventMetadata,eep);
			break;
		default:
			throw new AdapterException("Could not initialize REST input adapter:"+this+" unrecognised formatter type");
		}
		
		

	}
	
	@Override
	public IEventInstance readData() throws AdapterException {
		List<String> eventsList;
		try {
			if (getMethod== null) {
				getMethod = new GetMethod(producerURL);
				this.getMethod.setRequestHeader(
		                "Content-type", contentType+"; charset=ISO-8859-1");
				
			}
			eventsList = RestClient.getEventsFromProducer(httpClient,getMethod, producerURL,contentType);
			
			//in this mode we assume the list will always contain a single event instance
			if (eventsList == null || eventsList.isEmpty()) return null;
			
			String eventInstanceText = eventsList.iterator().next();			
			if (eventInstanceText == null || eventInstanceText.equals("")) return null; 
			
			//in case the event is given with new lines, we need to concatenate the lines
			StringBuffer fullEventData = new StringBuffer(); 
			Iterator<String> iterator = eventsList.iterator();
			while (iterator.hasNext()) {
				fullEventData.append(iterator.next());
			}
			IEventInstance eventInstance = textFormatter.parseText(fullEventData.toString());
			eventInstance.setDetectionTime(Calendar.getInstance().getTimeInMillis());	
			return eventInstance;
		} catch (RESTException e) {
			getMethod.releaseConnection();
			getMethod = null;
			throw new AdapterException(e);			
		} 
		
	}

	@Override
	public void initializeAdapter() throws AdapterException {
		super.initialize();
		this.httpClient = new HttpClient();
		this.getMethod = new GetMethod(producerURL);
		this.getMethod.setRequestHeader(
                "Content-type", contentType+"; charset=ISO-8859-1");
		
	}

	@Override
	public void shutdownAdapter() throws AdapterException {
		try{
			super.shutdown();
		}catch(AdapterException e){
			throw e;
		}finally{
			if (getMethod!= null) getMethod.releaseConnection();
		}
		
		
	}

	@Override
	public IInputAdapterConfiguration createConfiguration(
			ProducerMetadata producerMetadata) {		
		TextFormatterType fileFormatterType = TextFormatterType.valueOf(((String)producerMetadata.getProducerProperty(MetadataParser.FORMATTER)).toUpperCase());
		String url = (String)producerMetadata.getProducerProperty(MetadataParser.URL);
		String contentType = (String)producerMetadata.getProducerProperty(MetadataParser.CONTENT_TYPE);
		long delay = Integer.valueOf((String)producerMetadata.getProducerProperty(MetadataParser.DELAY));
		long pollingDelay = Integer.valueOf((String)producerMetadata.getProducerProperty(MetadataParser.POLLING_DELAY));
		InputAdapterPullModeEnum pollMode = InputAdapterPullModeEnum.valueOf(((String)producerMetadata.getProducerProperty(MetadataParser.POLLING_MODE)).toUpperCase());
		return new RESTInputAdapterConfiguration(url,contentType,fileFormatterType,delay,pollingDelay,pollMode);
	}

	@Override
	public List<IEventInstance> readBatchedData() throws AdapterException {
		List<String> eventsList;
		List<IEventInstance> eventInstances = new ArrayList<IEventInstance>();
		try {
			if (getMethod== null) {
				getMethod = new GetMethod(producerURL);
				this.getMethod.setRequestHeader(
		                "Content-type", contentType+"; charset=ISO-8859-1");
				
			}
			eventsList = RestClient.getEventsFromProducer(httpClient,getMethod,producerURL, contentType);					
			if (eventsList == null || eventsList.isEmpty()) return eventInstances;
			for (String eventInstanceText : eventsList) {
				if (textFormatter.isArray(eventInstanceText)){
					List<String> eventStringList = textFormatter.returnInstances(eventInstanceText);
					for (String eventString : eventStringList) {
						IEventInstance eventInstance = textFormatter.parseText(eventString);
						eventInstance.setDetectionTime(Calendar.getInstance().getTimeInMillis());
						
						eventInstances.add(eventInstance);

					}
				}else{
					IEventInstance eventInstance = textFormatter.parseText(eventInstanceText);
					eventInstance.setDetectionTime(Calendar.getInstance().getTimeInMillis());
					
					eventInstances.add(eventInstance);

				}
			}
				
			
			return eventInstances;
			
		} catch (RESTException e) {
			getMethod.releaseConnection();
			getMethod = null;
			throw new AdapterException(e);
		} 
		
	}

}
