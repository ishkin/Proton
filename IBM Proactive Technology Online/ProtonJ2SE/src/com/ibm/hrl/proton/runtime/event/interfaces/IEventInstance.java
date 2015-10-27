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
package com.ibm.hrl.proton.runtime.event.interfaces;

import java.util.Map;
import java.util.UUID;

import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.inout.IObjectMessage;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;

public interface IEventInstance extends IDataObject,ITimedObject,IObjectMessage{

	public IEventType getEventType();
	public Map<String, Object> getAttributes();
	public Object getEventAttribute(String attName);
	public UUID getEventId();
	public long getOccurenceTime();
	public long getDetectionTime();
	public String getObjectName();	
	public Double getEventDuration();
	public void setDetectionTime(long detectionTime);
	public void setEventType(IEventType eventType);
}
