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

import json.java.JSONObject;


/**
 * Checks if the argument parsed is null
 * @param <T> The type of the element parsed
 */
class NullChecker<T> extends ActionParser<T> {


	public NullChecker(String key, JSONObject jsonObject, String name, DefinitionType defintionType,
			ErrorElement type, IParser<T> parser, int rowNumber, int tableNumber) {
		super(key, jsonObject, name, defintionType, type, parser, rowNumber, tableNumber);
		// TODO Auto-generated constructor stub
	}

	public NullChecker(String key, JSONObject jsonObject, String name, DefinitionType defintionType,
			ErrorElement type, IParser<T> parser, int rowNumber) {
		super(key, jsonObject, name, defintionType, type, parser, rowNumber);
		// TODO Auto-generated constructor stub
	}

	public NullChecker(String key, JSONObject jsonObject, String name, DefinitionType defintionType,
			ErrorElement type, IParser<T> parser) {
		super(key, jsonObject, name, defintionType, type, parser);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkElementParsed(T element) {
		return _element != null;
	}

	@Override
	protected ParseErrorEnum getExceptionEnum() {
		return ParseErrorEnum.NULL_ELEMENT;
	}
	
}
