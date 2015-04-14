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


public class ProtonParseException extends Exception {
	private static final long		serialVersionUID	= -8861082984884618430L;

	public static final int			DEFAULT_INDEX		= -1;

	private final String			_message;
	private final ParseErrorEnum	_errorEnum;
	private final String			_definitionInstance;
	private final DefinitionType		_definitionType;
	private final ErrorType			_errorType;
	private final ErrorElement		_elementEnum;
	private final int				_rowNumber;								
	private final int				_tableNumber;									
	private final boolean			_stopIteration;

	public ProtonParseException(ParseErrorEnum errorEnum, String definitionInstance,
			DefinitionType definitionType, ErrorType errorType, ErrorElement elementEnum) {
		this(errorEnum, definitionInstance, definitionType, errorType, elementEnum, DEFAULT_INDEX,
				DEFAULT_INDEX, true, null);
	}

	public ProtonParseException(ParseErrorEnum errorEnum, String definitionInstance,
			DefinitionType definitionType, ErrorType errorType, ErrorElement elementEnum, int rowNumber) {
		this(errorEnum, definitionInstance, definitionType, errorType, elementEnum, rowNumber, DEFAULT_INDEX,
				true, null);
	}

	public ProtonParseException(ParseErrorEnum errorEnum, String definitionInstance,
			DefinitionType definitionType, ErrorType errorType, ErrorElement elementEnum, int rowNumber,
			String message) {
		this(errorEnum, definitionInstance, definitionType, errorType, elementEnum, rowNumber, DEFAULT_INDEX,
				true, message);
	}
	
	public ProtonParseException(ParseErrorEnum errorEnum, String definitionInstance,
			DefinitionType definitionType, ErrorType errorType, ErrorElement elementEnum, int rowNumber,
			int tableNumber, String message) {
		this(errorEnum, definitionInstance, definitionType, errorType, elementEnum, rowNumber, tableNumber, true,
				message);
	}

	public ProtonParseException(ParseErrorEnum errorEnum, String definitionInstance,
			DefinitionType definitionType, ErrorType errorType, ErrorElement elementEnum, int rowNumber,
			int tableNumber) {
		this(errorEnum, definitionInstance, definitionType, errorType, elementEnum, rowNumber, tableNumber,
				true, null);
	}

	public ProtonParseException(ParseErrorEnum errorEnum, String definitionInstance,
			DefinitionType definitionType, ErrorType errorType, ErrorElement elementEnum, int rowNumber,
			int tableNumber, boolean stopIteration) {
		this(errorEnum, definitionInstance, definitionType, errorType, elementEnum, rowNumber, tableNumber,
				stopIteration, null);
	}
	
	public ProtonParseException(ParseErrorEnum errorEnum, String definitionInstance,
			DefinitionType definitionType, ErrorType errorType, ErrorElement elementEnum, String message) {
		this(errorEnum, definitionInstance, definitionType, errorType, elementEnum, DEFAULT_INDEX,
				DEFAULT_INDEX, true, message);
	}

	public ProtonParseException(ParseErrorEnum errorEnum, String definitionInstance,
			DefinitionType definitionType, ErrorType errorType, ErrorElement elementEnum, int rowNumber,
			int tableNumber, boolean stopIteration, String message) {
		_errorEnum = errorEnum;
		_definitionInstance = definitionInstance;
		_definitionType = definitionType;
		_errorType = errorType;
		_elementEnum = elementEnum;
		_rowNumber = rowNumber;
		_tableNumber = tableNumber;
		_stopIteration = stopIteration;
		_message = message;
	}


	public boolean stopIteration() {
		return _stopIteration;
	}

	@Override
	public String toString() {
		return _errorEnum + "@" + _definitionInstance
				+ (_rowNumber != DEFAULT_INDEX ? ". at row: " + _rowNumber : "");
	}

	public String toJsonString() {
		JSONObject result = new JSONObject();
		result.put("errorEnum", _errorEnum.toString());
		result.put("definitionInstance", _definitionInstance);
		result.put("definitionType", _definitionType.toString());
		result.put("errorType", _errorType.toString());
		result.put("elementEnum", _elementEnum.toString());
		if (_rowNumber != DEFAULT_INDEX) {
			result.put("rowNumber", _rowNumber);
		}
		if (_tableNumber != DEFAULT_INDEX) {
			result.put("tableNumber", _tableNumber);
		}
		if (_message != null) {
			result.put("message", _message);
		}

		return result.toString();
	}
}
