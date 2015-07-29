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

import com.ibm.hrl.proton.adapters.configuration.IOutputAdapterConfiguration;
import com.ibm.hrl.proton.adapters.connector.IOutputConnector;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter.TextFormatterType;
import com.ibm.hrl.proton.adapters.formatters.JSONFormatter;
import com.ibm.hrl.proton.adapters.formatters.TagTextFormatter;
import com.ibm.hrl.proton.adapters.formatters.XmlNgsiFormatter;
import com.ibm.hrl.proton.adapters.interfaces.AbstractOutputAdapter;
import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.inout.ConsumerMetadata;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

public class RESTOutputAdapter extends AbstractOutputAdapter {


	
	private static final String PRODUCER_URL = "producerURL";
	private static final String CONTENT_TYPE = "contentType";
	
	private String url;
	private String contentType;
	ITextFormatter textFormatter;
	private String authToken;

	public RESTOutputAdapter(ConsumerMetadata consumerMetadata,
			IOutputConnector serverConnector,EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException {	
		super(consumerMetadata, serverConnector,eventMetadata);
		this.url = ((RESTOutputAdapterConfiguration)configuration).getUrl();
		this.contentType = ((RESTOutputAdapterConfiguration)configuration).getContentType();
		this.authToken = ((RESTOutputAdapterConfiguration)configuration).getAuthToken();
		TextFormatterType formatterType = ((RESTOutputAdapterConfiguration)configuration).getFormatterType();
		
		switch (formatterType) {
		case XML:
			textFormatter = new XmlNgsiFormatter(consumerMetadata.getConsumerProperties(),eventMetadata,eep);
			break;
		case CSV:			
			 throw new UnsupportedOperationException("CSV format is not supported");
		case JSON:
			textFormatter = new JSONFormatter(consumerMetadata.getConsumerProperties(),eventMetadata,eep);
			break;
		case TAG:
			textFormatter = new TagTextFormatter(consumerMetadata.getConsumerProperties(),eventMetadata,eep);
			break;
		default:
			throw new AdapterException("Could not initialize REST output adapter: "+this+", unrecognised formatter type");
		}
	}

	@Override
	public void writeObject(IDataObject instance) throws AdapterException {
		try {
			//out.write(eventInstance.toString()+LINE_SEPARATOR);			
			RestClient.putEventToConsumer(url, textFormatter.formatInstance(instance), contentType,authToken);
			
		} catch (RESTException e) {			
			throw new AdapterException(e);
		}
		

	}

	@Override
	public void initializeAdapter() throws AdapterException {
		super.initialize();
	}

	@Override
	public void shutdownAdapter() throws AdapterException {
		super.shutdown();

	}

	@Override
	public IOutputAdapterConfiguration createConfiguration(
			ConsumerMetadata consumerMetadata) {
		String consumerURL= (String)consumerMetadata.getConsumerProperty(MetadataParser.URL);
		String contentType= (String)consumerMetadata.getConsumerProperty(MetadataParser.CONTENT_TYPE);
		TextFormatterType formatterType = TextFormatterType.valueOf(((String)consumerMetadata.getConsumerProperty(MetadataParser.FORMATTER)).toUpperCase());
		String authToken = (String)consumerMetadata.getConsumerProperty(MetadataParser.AUTH_TOKEN);
		return new RESTOutputAdapterConfiguration(consumerURL,contentType,formatterType,authToken);
	}

}
