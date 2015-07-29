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



abstract class ActionParser<T> implements IActionParser<T> {
	protected T _element; // the element parsed
	protected final String _key; // the key to the json object
	protected final JSONObject _jsonObject; // the json object to parse

	protected String _definitionInstance;
	private final DefinitionType _definitionType;
	private final ErrorElement _elementType;
	private final int _rowNumber;
	private final int _tableNumber;
	private final IParser<T> _parser; 
	
	protected boolean _failedCast = false;

	public ActionParser(String key, JSONObject jsonObject, String name, DefinitionType defintionType,
			ErrorElement type, IParser<T> parser) {
		this(key, jsonObject, name, defintionType, type, parser, ProtonParseException.DEFAULT_INDEX,
				ProtonParseException.DEFAULT_INDEX);
	}

	public ActionParser(String key, JSONObject jsonObject, String name, DefinitionType defintionType,
			ErrorElement type, IParser<T> parser, int rowNumber) {
		this(key, jsonObject, name, defintionType, type, parser, rowNumber, ProtonParseException.DEFAULT_INDEX);
	}

	public ActionParser(String key, JSONObject jsonObject, String name, DefinitionType defintionType,
			ErrorElement type, IParser<T> parser, int rowNumber, int tableNumber) {
		_key = key;
		_jsonObject = jsonObject;
		_definitionInstance = name;
		_definitionType = defintionType;
		_elementType = type;
		_rowNumber = rowNumber;
		_tableNumber = tableNumber;
		_parser = parser;
	}

	@Override
	public T tryParse() {
		try {
			_element = _parser.parse((_jsonObject.get(_key)));
		} catch (ClassCastException e) {
			_element = null;
			_failedCast = true;
		}
		
		return _element;
	}

	@Override
	public ProtonParseException getException() {
		return new ProtonParseException(getFailedCastOrOtherException(), _definitionInstance, _definitionType,
				getErrorType(), _elementType, _rowNumber, _tableNumber, stopIteration());
	}
	
	private ParseErrorEnum getFailedCastOrOtherException() {
		if (_failedCast) {
			return ParseErrorEnum.INVALID_JSON_FORMAT;
		} else {
			return getExceptionEnum();
		}
	}
	protected abstract ParseErrorEnum getExceptionEnum();
	
	/**
	 * Gets the error type (error or warning or something else)
	 */
	protected ErrorType getErrorType() {
		return ErrorType.ERROR;
	}
	
	/**
	 * Return true iff the current parsing iteration should stop if an exception occured
	 */
	protected boolean stopIteration() {
		// default implementation is to stop on errors only
		return getErrorType() == ErrorType.ERROR;
	}
}
