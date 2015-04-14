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
package com.ibm.hrl.proton.context.exceptions;

/**
 * Exception thrown upon context unsuccessful creation
 * <code>ContextCreationException</code>.
 * 
 */
public class ContextCreationException extends ContextServiceException {
	
	private static final long serialVersionUID = 1L;

	public ContextCreationException(String message, Throwable cause) {
		super(message,cause);
		
	}

}
