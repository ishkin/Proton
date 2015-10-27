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
package com.ibm.hrl.proton.webapp;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.ibm.hrl.proton.webapp.exceptions.ResponseExceptionMapper;
import com.ibm.hrl.proton.webapp.providers.EventJSONMessageReader;
import com.ibm.hrl.proton.webapp.providers.EventPlainTextMessageReader;
import com.ibm.hrl.proton.webapp.providers.EventXmlNgsiMessageReader;
import com.ibm.hrl.proton.webapp.resources.EventResource;

import deprecated.EventResourceJSONDeprecated;
import deprecated.EventResourcePlainTextDeprecated;

public class WebApplication extends Application {

	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(EventResource.class);
		classes.add(EventResourceJSONDeprecated.class);
		classes.add(EventResourcePlainTextDeprecated.class);
		classes.add(EventPlainTextMessageReader.class);
		classes.add(EventJSONMessageReader.class);
		classes.add(EventXmlNgsiMessageReader.class);
		classes.add(ResponseExceptionMapper.class);
		
		return classes;
	}
}
