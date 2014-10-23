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

import java.util.ArrayList;
import java.util.UUID;

import com.ibm.hrl.proton.context.metadata.ComposedSegmentation;
import com.ibm.hrl.proton.context.metadata.ITemporalContextBound;
import com.ibm.hrl.proton.context.metadata.TemporalContextInitiator;

/**
 * Represents a single segmentation partition of a certain temporal context.
 * Partitioning is determined by a single (possibly composed) segmentation which participates
 * in the composite context: initiators must (partially) comply with this segmentation 
 * <code>ActiveTemporalContextSegment</code>.
 * 
 */
public class ActiveTemporalContextSegment implements ISegmentationPartition {
	
	protected UUID id;
	
	/** reference to segmentation context type, this segment is associated with */ 
	protected ComposedSegmentation segmentationContextType;
	/** segmentation value, where for each segment in a segmentation context type is set a value */
	protected SegmentationValue segmentationValue;
	
	/** can be a single initiator or more (in case we have "add" policy) */
	protected ArrayList<ITemporalContextBound> initiators;
	/** a single terminator, single it is immediately processed and removed */
	protected ArrayList<ITemporalContextBound> terminators;
	
	
	public ActiveTemporalContextSegment(SegmentationValue segmentationValue) {
		id = UUID.randomUUID();
		this.segmentationValue = segmentationValue;
		this.segmentationContextType = segmentationValue.segmentationType;
		
		this.initiators =	new ArrayList<ITemporalContextBound>();
		this.terminators =	new ArrayList<ITemporalContextBound>();
	}
	
	public void addInitiator(ITemporalContextBound initiator) {
		initiators.add(initiator);
	}

	public ArrayList<ITemporalContextBound> getInitiators() {
		return initiators;
	}
	
	public void addTerminator(ITemporalContextBound terminator) {
		terminators.add(terminator);
	}

	public ArrayList<ITemporalContextBound> getTerminators() {
		return terminators;
	}
		
	@Override
	public void setSegmentationValue(SegmentationValue segmentationValue) {
		this.segmentationValue = segmentationValue;
	}

	@Override
	public SegmentationValue getSegmentationValue() {
		return segmentationValue;
	}

	
	public boolean complyWith(ActiveTemporalContextSegment finest) {
		return (segmentationValue.compliesWith(finest.getSegmentationValue()));
	}
	
	public boolean hasNewlyAddedInitiator() {
		for (ITemporalContextBound initiator: initiators) {
			if (((TemporalContextInitiator)initiator).isNewlyAdded()) {
				return true;
			}
		}
		return false;
	}

	// remove the only terminator we have
	public void removeTerminator() {	
		terminators.remove(0);
		
	}

}
