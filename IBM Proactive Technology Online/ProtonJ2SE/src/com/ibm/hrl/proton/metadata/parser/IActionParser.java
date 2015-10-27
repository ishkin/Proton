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

/**
 * Tries to perform a parsing operation, and checks the validity of the element parsed
 * If failed can also return the proper exception
 * @param <T> The type of the element parsed
 */
interface IActionParser<T> {
	/**
	 * Tries to perform a parsing operation
	 * @return The element parsed, or null if failed
	 */
	T tryParse();
	/**
	 * Check if the element parsed satisfies the required condition
	 * @param element The element parse to check
	 * @return True iff the element satisfies the condition
	 */
	boolean checkElementParsed(T element);
	/**
	 * The exception to create if the element does not satisfy the condition
	 * @return
	 */
	ProtonParseException getException();
}
