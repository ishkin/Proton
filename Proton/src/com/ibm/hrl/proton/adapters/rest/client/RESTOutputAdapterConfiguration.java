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
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter.TextFormatterType;

public class RESTOutputAdapterConfiguration implements
		IOutputAdapterConfiguration {

	
	
	private String url;
	private String contentType; 
	private TextFormatterType formatterType;

	public RESTOutputAdapterConfiguration(String consumerURL,
			String contentType, TextFormatterType formatterType) {
		this.url = consumerURL;
		this.contentType = contentType;
		this.formatterType = formatterType;
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

}
