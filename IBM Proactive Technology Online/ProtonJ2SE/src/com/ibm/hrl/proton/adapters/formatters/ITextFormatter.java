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

import java.util.List;

import javax.mail.MethodNotSupportedException;

import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;


public interface ITextFormatter 
{
	public enum TextFormatterType {CSV, TAG,XML,JSON,JSON_NGSI,JSON_COMPOSER};
	/**
	 * Format the given instance into text message given this formatter's type and properties
	 * The instance might represent both events or actions
	 * @param instance
	 * @return
	 * @throws AdapterException 
	 */
	public Object formatInstance(IDataObject instance) throws AdapterException;
	
	/**
	 * Parse the given text line , representing event instance, according to this formatter's type and properties
	 * @param eventText
	 * @return
	 * @throws AdapterException 
	 */
	public IEventInstance parseText(String eventText) throws AdapterException;
	
	/**
	 * Format the given timestamp into date string using the configured date formatter
	 * @param timestamp
	 * @return
	 */
	public String formatTimestamp(long timestamp);
	
	/**
	 * Parse the provided date string and create a timestamp value
	 * @param dateString
	 * @return
	 * @throws AdapterException 
	 */
	public long parseDate(String dateString) throws AdapterException;

	public boolean isArray(String eventInstanceText) throws AdapterException;

	public List<String> returnInstances(String eventInstanceText) throws AdapterException;
	
	
}
