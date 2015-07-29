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
package com.ibm.hrl.proton.context.management;

import java.util.UUID;

/**
 * <code>SlidingPartitionAdditionalInformation</code>.
 * Additional information that will arrive with timer notification - sliding temporal partition
 * (where actual sliding partition resides) and its internal partition id to terminate.  
 * 
 */

public class SlidingPartitionAdditionalInformation extends AdditionalInformation {
	
	protected UUID partitionId;
	protected UUID internalPartitionId;
	
	public SlidingPartitionAdditionalInformation(String contextName,
			String agentName, UUID contextBoundId, NotificationTypeEnum notificationType,
			UUID partitionId, UUID internalPartitionId) {

		super(contextName, agentName, contextBoundId, notificationType);
		this.internalPartitionId = internalPartitionId;
		this.partitionId = partitionId;
		
	}
	
	public UUID getInternalPartitionId() {
		return internalPartitionId;
	}
	
	public UUID getPartitionId() {
		return partitionId;
	}

}
