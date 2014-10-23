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
	INPUT_EVENT_NOT_DEFINED_IN_CONTEXT_PARTICIPATING_EVENTS
}
