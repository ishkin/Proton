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


public abstract class NullCheckAndEnum<T extends Enum<T>> extends NullChecker<T> {
	protected String _parsedValue;
	// since tryParse is override, no need to use a parser
	public NullCheckAndEnum(String key, JSONObject jsonObject, String name, DefinitionType defintionType,
			ErrorElement type, int rowNumber, int tableNumber) {
		super(key, jsonObject, name, defintionType, type, null, rowNumber, tableNumber);
		// TODO Auto-generated constructor stub
	}

	public NullCheckAndEnum(String key, JSONObject jsonObject, String name, DefinitionType defintionType,
			ErrorElement type, int rowNumber) {
		super(key, jsonObject, name, defintionType, type, null, rowNumber);
		// TODO Auto-generated constructor stub
	}

	public NullCheckAndEnum(String key, JSONObject jsonObject, String name, DefinitionType defintionType,
			ErrorElement type) {
		super(key, jsonObject, name, defintionType, type, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public T tryParse() {
		_parsedValue = (String)_jsonObject.get(_key);
		_element = null;
		if (_parsedValue != null) {
			_element = getEnum(_parsedValue);
		}
		
		return _element;
	}
	
	private T getEnum(String value) {
		try {
			return getEnumInner(camelCaseToUpperUnderScore(value));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	/**
	 * Returns the enum value parsed, or null if failed
	 * @param value The string to parse
	 * @return The enum value parsed
	 */
	protected abstract T getEnumInner(String value) throws IllegalArgumentException;
	
	@Override
	protected ParseErrorEnum getExceptionEnum() {
		if (_parsedValue == null) {
			return super.getExceptionEnum();
		} else {
			return ParseErrorEnum.BAD_ENUM_VALUE;
		}
	}
	
	private String camelCaseToUpperUnderScore(String camelCaseStr) {
		StringBuilder result = new StringBuilder();
		String[] words = camelCaseStr.replaceAll(
	      String.format("%s|%s|%s",
	    	         "(?<=[A-Z])(?=[A-Z][a-z])",
	    	         "(?<=[^A-Z])(?=[A-Z])",
	    	         "(?<=[A-Za-z])(?=[^A-Za-z])"
	    	      ),
	    	      " "
	    	   ).split(" ");
		for (int i = 0; i < words.length; i++) {
			result.append(words[i].toUpperCase());
			if (i < words.length - 1) {
				result.append("_");
			}
		}
		return result.toString();
		
	}
	
}
