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
package com.ibm.hrl.proton.adapters.formatters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

public abstract class AbstractTextFormatter implements ITextFormatter {
	private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy-HH:mm:ss";

	
	protected DateFormat dateFormatter;	
	protected EventMetadataFacade eventMetadata;
	protected EepFacade eep;
	
	AbstractTextFormatter(String dateFormat,EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException
	{
		try{
			if (dateFormat == null)
			{
				dateFormatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
			}
			else
			{
				dateFormatter = new SimpleDateFormat(dateFormat);
			}
		
			this.eventMetadata = eventMetadata;
			this.eep =eep;
		}
		catch(IllegalArgumentException e)
		{
			//wrong date format string
			throw new AdapterException("Error creating text formatter, wrong date format: "+dateFormat+",error: "+e.getMessage());
		}
	}
	
	@Override
	public String formatTimestamp(long timestamp) {
		return dateFormatter.format(new Date(timestamp));
	}

	@Override
	public long parseDate(String dateString) throws AdapterException {
		try {
			return dateFormatter.parse(dateString).getTime();
		} catch (ParseException e) {			
			e.printStackTrace();
			throw new AdapterException("Error formatting date: "+dateString+"reason: "+e.getMessage());
		}
	}

	

}
