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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import com.ibm.hrl.proton.adapters.configuration.IInputAdapterConfiguration;
import com.ibm.hrl.proton.adapters.connector.IInputConnector;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter.TextFormatterType;
import com.ibm.hrl.proton.adapters.formatters.JSONFormatter;
import com.ibm.hrl.proton.adapters.formatters.TagTextFormatter;
import com.ibm.hrl.proton.adapters.interfaces.AbstractInputAdapter;
import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.inout.ProducerMetadata;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

public class FileInputAdapter extends AbstractInputAdapter {

	public static final Logger logger = Logger.getLogger(FileInputAdapter.class.getName());
		
	private String inputFileName;	
	RandomAccessFile file;
	long filePointer;
	private static Calendar calendar  = Calendar.getInstance();
	private ITextFormatter fileFormatter;
	
	
	public FileInputAdapter(ProducerMetadata producerMetadata,IInputConnector serverConnector,EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException {
		super(producerMetadata,serverConnector,eventMetadata);		
		this.inputFileName = ((FileInputAdapterConfiguration)configuration).getInputFileName();				
		TextFormatterType formatterType = ((FileInputAdapterConfiguration)configuration).getFileFormatterType();
		
		switch (formatterType) 
		{
		case CSV:
			throw new UnsupportedOperationException("Currently CVS formatter is not supported");
		case XML:
			throw new UnsupportedOperationException("Currently XML formatter is not supported");
		case JSON:
			fileFormatter = new JSONFormatter(producerMetadata.getProducerProperties(),eventMetadata,eep);
			break;
		case TAG:
			fileFormatter = new TagTextFormatter(producerMetadata.getProducerProperties(),eventMetadata,eep);
			break;
		default:
			throw new AdapterException("Could not initialize file input adapter:"+this+" unrecognised formatter type");
		}

	}

	

	public IEventInstance readData() throws AdapterException
	{
		String line = null;
		IEventInstance eventInstance = null;
		
		try {
			long fileLength = this.file.length();
			if( fileLength > filePointer ) 
			{
				// There is data to read
				file.seek( filePointer );
				line = file.readLine();
				filePointer = file.getFilePointer();							
				
				if (line == null) return eventInstance;

				//eventInstance = EventInstance.parseFlatInstance(line);
				eventInstance = fileFormatter.parseText(line);
				//eventInstance.setDetectionTime(calendar.getTimeInMillis());	
				eventInstance.setDetectionTime(Calendar.getInstance().getTimeInMillis());
			}
			
			return eventInstance;
		} catch (IOException e) {
			if (running != false)
				logger.info("Cannot read from the input file, reason: "+e.getMessage());
			return null;
		} 
	}
	
	@Override
	public void initializeAdapter() throws AdapterException
	{
		super.initialize();
	
		try
		{
		  File inputFile = new File(inputFileName);
		  file = new RandomAccessFile(inputFile, "r");
		  filePointer = 0;		  
		}
		catch(Exception e)
		{
			throw new AdapterException(e.getMessage());
		}	
	}

	@Override
	public void shutdownAdapter() throws AdapterException {		
		super.shutdown();
		if (file != null)
		{
			try
			{
				file.close();
			}
			catch (IOException e)
			{
				throw new AdapterException(e.getMessage());
			}
		}
	}

	
	public IInputAdapterConfiguration createConfiguration(
			ProducerMetadata producerMetadata) {
		String inputFileName= (String)producerMetadata.getProducerProperty(MetadataParser.FILENAME);
		long delay = Long.valueOf((String)producerMetadata.getProducerProperty(MetadataParser.DELAY));
		long pollingDelay = Long.valueOf((String)producerMetadata.getProducerProperty(MetadataParser.POLLING_DELAY));
		TextFormatterType fileFormatterType = TextFormatterType.valueOf(((String)producerMetadata.getProducerProperty(MetadataParser.FORMATTER)).toUpperCase());
		return new FileInputAdapterConfiguration(inputFileName,delay,fileFormatterType,pollingDelay);
	}



	@Override
	public List<IEventInstance> readBatchedData() throws AdapterException {
		throw new UnsupportedOperationException("File input adapter doesn't support the 'readBatchedData' operation since even in SINGLE mode the updates are processed in a raw as batched");
	}
	
	
	 
	
	
}
