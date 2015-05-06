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
package com.ibm.hrl.proton.adapters.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

public class FileOutputAdapter extends AbstractOutputAdapter {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private String outputFileName;	
	BufferedWriter out;		
	ITextFormatter fileFormatter;

	
	public FileOutputAdapter(ConsumerMetadata consumerMetadata,IOutputConnector serverConnector,EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException {
		super(consumerMetadata, serverConnector,eventMetadata);
		this.outputFileName = ((FileOutputAdapterConfiguration)configuration).getOutputFileName();
		TextFormatterType formatterType = ((FileOutputAdapterConfiguration)configuration).getFileFormatterType();
		
		switch (formatterType) {
		case XML:
			fileFormatter = new XmlNgsiFormatter(consumerMetadata.getConsumerProperties(),eventMetadata,eep);
			break;
		case JSON:
			fileFormatter = new JSONFormatter(consumerMetadata.getConsumerProperties(),eventMetadata,eep);
			break;
		case CSV:			
			 throw new UnsupportedOperationException("CSV format is not supported");
		case TAG:
			fileFormatter = new TagTextFormatter(consumerMetadata.getConsumerProperties(),eventMetadata,eep);
			break;
		default:
			throw new AdapterException("Could not initialize file output adapter: "+this+", unrecognised formatter type");
		}
	}

	
	
	public void writeObject(IDataObject instance)
			throws AdapterException {
		try {
			//out.write(eventInstance.toString()+LINE_SEPARATOR);			
			out.write(fileFormatter.formatInstance(instance)+LINE_SEPARATOR);
			out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new AdapterException(e.getMessage());
		}
		
	}

	@Override
	public void initializeAdapter() throws AdapterException
	{
		super.initialize();
	
		try
		{		  		
		  File file = new File(outputFileName);
		  boolean fileExists = file.createNewFile(); //we create a file in case doesn't exist
		  
		  FileWriter fstream = new FileWriter(outputFileName);
		  out = new BufferedWriter(fstream);
		  
		}
		catch(Exception e)
		{
			throw new AdapterException(e.getMessage());
		}	
	}

	@Override
	public void shutdownAdapter() throws AdapterException {		
		super.shutdown();
		if (out != null)
			try
		{
				out.close();
		}
		catch (IOException e)
		{
			throw new AdapterException(e.getMessage());
		}
	}
	
	@Override
	public IOutputAdapterConfiguration createConfiguration(
			ConsumerMetadata consumerMetadata) {
		String outputFileName= (String)consumerMetadata.getConsumerProperty(MetadataParser.FILENAME);
		TextFormatterType fileFormatterType = TextFormatterType.valueOf(((String)consumerMetadata.getConsumerProperty(MetadataParser.FORMATTER)).toUpperCase());
		return new FileOutputAdapterConfiguration(outputFileName,fileFormatterType);
	}

	

}
