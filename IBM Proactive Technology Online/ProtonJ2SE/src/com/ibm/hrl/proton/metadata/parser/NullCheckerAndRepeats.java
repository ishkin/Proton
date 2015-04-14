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

import java.util.Set;

import json.java.JSONObject;

/**
 * Checks if the argument parsed is null, and that the result element name is
 * unique
 * 
 * @param <T>
 */
class NullCheckerAndRepeats<T> extends NullChecker<T> {
	/**
	 * The set of existing names to compare against
	 */
	private final Set<T> _existingNames;
	protected boolean _isNameUnique = true;
	// if true, then if the name is repeat, the definition instance will be overriden
	// with the repeating name
	private boolean _overrideInstance = false;

	public NullCheckerAndRepeats(Set<T> existingNames, String key, JSONObject jsonObject, String name,
			DefinitionType defintionType, ErrorElement type, IParser<T> parser, int rowNumber,
			int tableNumber) {
		super(key, jsonObject, name, defintionType, type, parser, rowNumber, tableNumber);
		_existingNames = existingNames;
	}

	public NullCheckerAndRepeats(Set<T> existingNames, String key, JSONObject jsonObject, String name,
			DefinitionType defintionType, ErrorElement type, IParser<T> parser, int rowNumber) {
		super(key, jsonObject, name, defintionType, type, parser, rowNumber);
		_existingNames = existingNames;
	}

	public NullCheckerAndRepeats(Set<T> existingNames, String key, JSONObject jsonObject, String name,
			DefinitionType defintionType, ErrorElement type, IParser<T> parser) {
		super(key, jsonObject, name, defintionType, type, parser);
		_existingNames = existingNames;
	}
	
	public NullCheckerAndRepeats(Set<T> existingNames, String key, JSONObject jsonObject, String name,
			DefinitionType defintionType, ErrorElement type, IParser<T> parser, boolean overrideInstance) {
		super(key, jsonObject, name, defintionType, type, parser);
		_overrideInstance = overrideInstance;
		_existingNames = existingNames;
	}

	@Override
	public boolean checkElementParsed(T element) {
		boolean elementWasParsedSuccesfully = super.checkElementParsed(element);
		
		// if element is null, no need to check repeats
		if (elementWasParsedSuccesfully && _existingNames.contains(element)) {
			_isNameUnique = false;
			if (_overrideInstance) {
				_definitionInstance = element.toString();
			}
		}

		return elementWasParsedSuccesfully && _isNameUnique;
	}

	@Override
	protected ParseErrorEnum getExceptionEnum() {
		if (_isNameUnique == false) {
			return ParseErrorEnum.NAME_ALREADY_EXISTS;
		} else {
			return super.getExceptionEnum();
		}
	}
}
