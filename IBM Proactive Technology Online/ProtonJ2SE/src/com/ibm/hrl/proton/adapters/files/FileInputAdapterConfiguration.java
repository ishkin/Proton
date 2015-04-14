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

import com.ibm.hrl.proton.adapters.configuration.IInputAdapterConfiguration;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter.TextFormatterType;

public class FileInputAdapterConfiguration implements IInputAdapterConfiguration {

		
	private String inputFileName;
	private long delay; //in milisecs
	private TextFormatterType fileFormatterType;
	private long pollingDelay;
	
	


	public FileInputAdapterConfiguration(String inputFileName,long delay,TextFormatterType fileFormatterType,long pollingDelay) {
		super();
		this.inputFileName = inputFileName;
		this.delay = delay;
		this.pollingDelay = pollingDelay;
		this.fileFormatterType = fileFormatterType;
		
	}

		


	public String getInputFileName() {
		return inputFileName;
	}
	
	public TextFormatterType getFileFormatterType() {
		return fileFormatterType;
	}


	@Override
	public InputAdapterPullModeEnum getPollMode() {
		return InputAdapterPullModeEnum.SINGLE;
	}


	@Override
	public long getPollingDelay() {
		return pollingDelay;
	}


	@Override
	public long getSendingDelay() {
		return delay;
	}






}
