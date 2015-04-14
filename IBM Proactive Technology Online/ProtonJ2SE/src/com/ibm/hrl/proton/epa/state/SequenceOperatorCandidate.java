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

package com.ibm.hrl.proton.epa.state;

import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

/**
 * <code>SequenceCandidate</code>.
 * 
 * 
 */
public class SequenceOperatorCandidate {
	
	protected IEventInstance event;
	public SequenceOperatorCandidate nextConsequentCandidate;
	public SequenceOperatorCandidate previousLinkedCandidate;
	
	public SequenceOperatorCandidate() {
		nextConsequentCandidate = null;
		previousLinkedCandidate = null;
		event = null;		
	}
	
	public SequenceOperatorCandidate(IEventInstance event) {
		this.event = event;
		nextConsequentCandidate = null;
		previousLinkedCandidate = null;
	}
	
	public IEventInstance getEventInstance() {
		return event;
	}

	public void setNextConsequentCandidate(SequenceOperatorCandidate next) {
		nextConsequentCandidate = next;
	}
	
	public void setPreviousCandidate(SequenceOperatorCandidate previous) {
		previousLinkedCandidate = previous;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || this.getClass() != this.getClass()) return false;		
		SequenceOperatorCandidate otherCandidate = (SequenceOperatorCandidate)other;		
		if (event.getEventId().equals(otherCandidate.event.getEventId())) {
			return true;
		}
		return false;
	}
	
}
