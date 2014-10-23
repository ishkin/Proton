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
package com.ibm.hrl.proton.metadata.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class DateParser implements IParser<Date> {
	private final SimpleDateFormat _df;
	
	public DateParser(SimpleDateFormat df) {
		_df = df;
	}
	
	@Override
	public Date parse(Object object) throws ClassCastException {
		if (object == null) {
			return null;
		}
		try {
			return _df.parse(object.toString());
		} catch (ParseException e) {
			throw new ClassCastException();
		}
	}

}
