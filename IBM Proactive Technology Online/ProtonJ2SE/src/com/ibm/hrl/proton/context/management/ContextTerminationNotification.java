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

import com.ibm.hrl.proton.runtime.context.notifications.IContextTerminationNotification;

/**
 * <code>ContextTerminationNotification</code>.
 * 
 */
public class ContextTerminationNotification extends ContextNotification 
	implements IContextTerminationNotification {
	private SegmentationValue segmentValue;
	
	public ContextTerminationNotification(String name, long occurrence,
			long detection, String boundId,String agentName,SegmentationValue segmentValue) {		
		super(name,occurrence,detection,boundId,agentName);
		this.segmentValue = segmentValue;
	}

	public SegmentationValue getSegmentValue() {
		return segmentValue;
	}

}
