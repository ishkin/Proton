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
 * Similiar to NullChecker, except only a warning is issued
 * @param <T> The type of the element parsed
 */
class WarningNullChecker<T> extends NullChecker<T> {
	public WarningNullChecker(String key, JSONObject jsonObject, String name, DefinitionType defintionType,
			ErrorElement type, IParser<T> parser, int rowNumber, int tableNumber) {
		super(key, jsonObject, name, defintionType, type, parser, rowNumber, tableNumber);
		// TODO Auto-generated constructor stub
	}

	public WarningNullChecker(String key, JSONObject jsonObject, String name, DefinitionType defintionType,
			ErrorElement type, IParser<T> parser, int rowNumber) {
		super(key, jsonObject, name, defintionType, type, parser, rowNumber);
		// TODO Auto-generated constructor stub
	}

	public WarningNullChecker(String key, JSONObject jsonObject, String name, DefinitionType defintionType,
			ErrorElement type, IParser<T> parser) {
		super(key, jsonObject, name, defintionType, type, parser);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ErrorType getErrorType() {
		return ErrorType.WARNING;
	}
}
