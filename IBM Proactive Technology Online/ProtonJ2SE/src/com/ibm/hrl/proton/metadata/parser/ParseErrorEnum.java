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

enum ParseErrorEnum {
	NULL_ELEMENT,
	NAME_ALREADY_EXISTS,
	BAD_ENUM_VALUE,
	EEP_ERROR,
	INVALID_JSON_FORMAT,
	MISSING_REQUIRED_ATTRIBUTES,
	EMPTY_COLLECTION,
	INDEX_OUT_OF_RANGE,
	ELEMENT_NOT_DEFINED,
	BAD_TYPE,
	MISSING_ALIAS,
	REPEATING_ALIAS,
	ATTRIBUTE_ALREADY_EXISTS,
	NO_DEFAULT_VALUE,
	BAD_VALUE,
	INPUT_EVENT_NOT_DEFINED_IN_CONTEXT_PARTICIPATING_EVENTS;
	
	public String toString(String elementString) {
		switch (this) {
			case NULL_ELEMENT: return "Missing required element of " + elementString;
			case NAME_ALREADY_EXISTS: return "Name of " + elementString + " already exists";
			case BAD_ENUM_VALUE: return "Bad " + elementString + "'s Enum value";
			case EEP_ERROR: return "EEP error in " + elementString;
			case INVALID_JSON_FORMAT: return "Invalid JSON format of " + elementString;
			case MISSING_REQUIRED_ATTRIBUTES: return "Missing required attributes of  " + elementString;
			case EMPTY_COLLECTION: return "Empty Collection of " + elementString;
			case INDEX_OUT_OF_RANGE: return "Index of " + elementString + " out of range";
			case ELEMENT_NOT_DEFINED: return "Element " + elementString + " is not defined";
			case BAD_TYPE: return "Bad type of " + elementString;
			case MISSING_ALIAS: return "Missing alias of " + elementString;
			case REPEATING_ALIAS: return "Repeating alias of " + elementString;
			case ATTRIBUTE_ALREADY_EXISTS: return "Attribute of " + elementString + " already exists";
			case NO_DEFAULT_VALUE: return "No default value for " + elementString;
			case BAD_VALUE: return "Bad value of " + elementString;
			case INPUT_EVENT_NOT_DEFINED_IN_CONTEXT_PARTICIPATING_EVENTS: return "Input Event not defined in Context's Participating Events";
			default:  throw new IllegalArgumentException();
		}
	}
	
}
