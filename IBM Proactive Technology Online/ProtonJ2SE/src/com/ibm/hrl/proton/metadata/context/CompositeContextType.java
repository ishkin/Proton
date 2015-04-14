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
package com.ibm.hrl.proton.metadata.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.ibm.hrl.proton.metadata.context.enums.ContextTypeEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;

public class CompositeContextType extends ContextType {

	protected List<IContextType> contexts;
	// TODO: should always be ordered, e.g. t-s-t differs from t-t-s
	protected boolean isOrdered;
	

	public CompositeContextType(String name,List<IContextType> contexts, boolean isOrdered) {
		super(name,ContextTypeEnum.COMPOSITE);
		this.isOrdered = isOrdered;
		this.contexts = contexts;		
	}
	
	public List<IContextType> getMemberContexts() {
		return contexts;
	}
	
	public int getTemporalContextWithFinestSegment() {
		
		List<TemporalContextType> temporalContexts = getTemporalContexts();
		
		int finestTemporal = 0;
		int finestTemporalIndex = 0;
		for (int i=0; i<temporalContexts.size(); i++) {
			if (temporalContexts.get(i).hasEventInititators()) {
				ContextInitiator initiator = temporalContexts.get(i).getEventInitiators().get(0);
				int activeSegments = calculateActiveSegments(initiator);
				if (activeSegments > finestTemporal) { 
					finestTemporal = activeSegments;
					finestTemporalIndex = i;
					
				}
			}
		}		
		return finestTemporalIndex;
	}

	private int calculateActiveSegments(ContextInitiator initiator) {

		int totalActiveSegments = 0;
		List<SegmentationContextType> segmentationContexts = getSegmentationContexts();
		for (SegmentationContextType segment: segmentationContexts) {
			if (segment.getSegmentationKeys().keySet().contains(initiator.getInitiatorType())) {
				totalActiveSegments++;
			}
		}		
		return totalActiveSegments;
	}

	private List<SegmentationContextType> getSegmentationContexts() {
		List<SegmentationContextType> sContexts = new ArrayList<SegmentationContextType>();
		for (IContextType member: contexts) {
			if (member instanceof SegmentationContextType) {
				sContexts.add((SegmentationContextType)member);
			}
		}		
		return sContexts;
	}

	private List<TemporalContextType> getTemporalContexts() {
		List<TemporalContextType> tContexts = new ArrayList<TemporalContextType>();
		for (IContextType member: contexts) {
			if (member instanceof TemporalContextType) {
				tContexts.add((TemporalContextType)member);
			}
		}		
		return tContexts;
	}

	public boolean hasSystemStartupInitiator() {		
		for (IContextType context: contexts) {
			if (context instanceof TemporalContextType) {
				if (((TemporalContextType)context).startsAtSystemStartup()) {
					return true;
 				}
			}
		}		
		return false;
	}

	public boolean hasAbsoluteTimeInitiator() {
		for (IContextType context: contexts) {
			if (context instanceof TemporalContextType) {
				if (((TemporalContextType)context).hasAbsoluteTimeInititators()) {
					return true;
				}
			}
		}
		return false;
	}

	public Collection<ContextAbsoluteTimeInitiator> getAbsoluteTimeInitiators() {

		Collection<ContextAbsoluteTimeInitiator> initiators =
			new HashSet<ContextAbsoluteTimeInitiator>();
		
		for (IContextType context: contexts) {
			if (context instanceof TemporalContextType) {
				if (((TemporalContextType)context).hasAbsoluteTimeInititators()) {
					initiators.addAll(((TemporalContextType)context).getAbsoluteTimeInitiators());
				}
			}
		}				
		return initiators;
	}

	public TemporalContextType getTemporalMemberContext(int member) {
		return ((TemporalContextType)(getTemporalContexts().get(member)));
	}
	
}
