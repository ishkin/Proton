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

import com.ibm.hrl.proton.adapters.configuration.IInputAdapterConfiguration;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter.TextFormatterType;

public class RESTInputAdapterConfiguration implements
		IInputAdapterConfiguration {


	
	private String url;
	private String contentType; 
	private TextFormatterType formatterType;
	private long delay;
	private long pollingDelay;
	private InputAdapterPullModeEnum pollMode;
	

	public RESTInputAdapterConfiguration(String url, String contentType,
			TextFormatterType formatterType,long delay,long pollingDelay,InputAdapterPullModeEnum pollMode) {
		super();
		this.url = url;
		this.contentType = contentType;
		this.formatterType = formatterType;
		this.delay  = delay;
		this.pollingDelay = pollingDelay;
		this.pollMode = pollMode;
		 
	}
	
	public String getUrl() {
		return url;
	}

	public String getContentType() {
		return contentType;
	}

	public TextFormatterType getFormatterType() {
		return formatterType;
	}
	


	@Override
	public InputAdapterPullModeEnum getPollMode() {
		return pollMode;
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
