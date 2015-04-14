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
 * Interface for temporal context bound (initiator or terminator).
 * <code>ITemporalContextBound</code>.
 * 
 */
public interface ITemporalContextBound {
	
	public UUID getId();
	//public IEventType getEventType();
	//public IEventInstance getEventInstance();
	//public SegmentationValue getSegmentationValue();
	public boolean equals(Object other);
}
