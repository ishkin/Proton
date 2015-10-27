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
package Managers;

import java.util.Collection;

public interface ISaver<T> {
	/**
	 * Checks if an element exists
	 * @param id The id to check
	 * @return True iff the element exists
	 */
	boolean exists(String id);
	/**
	 * Stores the element
	 * @param id The element's id
	 * @param element The element to store
	 */
	void store(String id, T element);
	/**
	 * Loads the element
	 * @param id The element's id
	 * @return The element loaded
	 * @throws IllegalArgumentException If the element does not exist
	 */
	T load(String id) throws IllegalArgumentException;
	/**
	 * Get all the element names
	 * @return All the element names
	 */
	Collection<String> getIDs();
	/**
	 * Deletes the element
	 * @param id The element's id
	 * @return True iff the element was deleted
	 */
	boolean delete(String id);
}
