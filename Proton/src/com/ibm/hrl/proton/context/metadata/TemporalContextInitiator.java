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

package com.ibm.hrl.proton.context.metadata;

import java.util.UUID;

/**
 * Represents temporal context initiator (either event or absolute time).
 * <code>TemporalContextInitiator</code>.
 * 
 */
public abstract class TemporalContextInitiator implements ITemporalContextBound {

	protected boolean newlyAdded;
	
	public TemporalContextInitiator() {
		newlyAdded = true;
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.hrl.proton.context.metadata.ITemporalContextBound#getId()
	 */
	@Override
	public UUID getId() {
		// should be overridden in children 
		return null;
	}
		
	public boolean isNewlyAdded() {
		return newlyAdded;
	}

	public void notNewlyAdded() {
		newlyAdded = false;
	}

}
