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
 * Object parsed can be null if condition holds
 */
public class NullCheckerUnderCondition<T> extends NullChecker<T> {
	private final boolean _condition;

	public NullCheckerUnderCondition(String key, JSONObject jsonObject, String name,
			DefinitionType defintionType, ErrorElement type, IParser<T> parser, int rowNumber,
			int tableNumber, boolean condition) {
		super(key, jsonObject, name, defintionType, type, parser, rowNumber, tableNumber);
		_condition = condition;
	}

	public NullCheckerUnderCondition(String key, JSONObject jsonObject, String name,
			DefinitionType defintionType, ErrorElement type, IParser<T> parser, int rowNumber,
			boolean condition) {
		super(key, jsonObject, name, defintionType, type, parser, rowNumber);
		_condition = condition;
	}

	public NullCheckerUnderCondition(String key, JSONObject jsonObject, String name,
			DefinitionType defintionType, ErrorElement type, IParser<T> parser, boolean condition) {
		super(key, jsonObject, name, defintionType, type, parser);
		_condition = condition;
	}

	@Override
	public boolean checkElementParsed(T element) {
		if (_condition == false) {
			return super.checkElementParsed(element);
		}

		// if condition holds, object can be null
		return true;
	};
	
	@Override
	protected ErrorType getErrorType() {
		return ErrorType.WARNING;
	}
}
