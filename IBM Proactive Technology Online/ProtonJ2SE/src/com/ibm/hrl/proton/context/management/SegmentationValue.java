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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.ibm.hrl.proton.context.metadata.ComposedSegmentation;

/**
 * Represents (possibly composed) segmentation value for both global and local segmentation.
 * Maps distinct segments ids to value, enables segmentation values comparison. Two segmentation
 * values are equal if they have map same segments to identical values (must have same number
 * of segments). Two segmentation values comply with each other if one is a proper subset of
 * another, mapping same ids to identical values in that subset.
 * <code>SegmentationValue</code>.
 * 
 */
public class SegmentationValue {
	
	protected ComposedSegmentation segmentationType;
	protected Map<UUID,String> segmentsValuesMap;
	
	public SegmentationValue(ComposedSegmentation segmentationType) {
		this.segmentationType = segmentationType;
		segmentsValuesMap = new HashMap<UUID,String>();
	}
	
	public SegmentationValue() {
		segmentsValuesMap = new HashMap<UUID,String>();
		segmentationType = null;
	}
	
	public void addValue(UUID key, String value) {
		segmentsValuesMap.put(key,value);

	}	

	public Map<UUID,String> getValues() {
		return segmentsValuesMap;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || this.getClass() != this.getClass()) return false;
		
		SegmentationValue otherMap = (SegmentationValue)other;

		if (segmentsValuesMap.keySet().size() == 0 &&
				otherMap.getValues().keySet().size() == 0) {
			return true;
		}		
		if (segmentsValuesMap.keySet().size() != otherMap.getValues().keySet().size()) {
			return false;
		}
		
		for (UUID key: segmentsValuesMap.keySet()) {
			if (!otherMap.getValues().containsKey(key)) {
				return false;
			}
			if (!segmentsValuesMap.get(key).equals(otherMap.getValues().get(key))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int code = 0;
		for (String value: segmentsValuesMap.values()) {
			code += value.hashCode();
		}
		return code;
	}

	// TODO the entire maintenance of composite segments, both data structures and
	// algorithms should be optimized (partial keys trees etc.)
			
	// this function verifies if two composite segments comply with each other
	// two composed segments comply if key set of one is a subset of the another and values
	// for identical keys are equal
	public boolean compliesWith(SegmentationValue other) {
		
		Map<UUID,String> otherMap = other.getValues();
		int size = segmentsValuesMap.keySet().size();
		int otherSize = otherMap.keySet().size();
		
		// null segment is automatically a subset of another one
		if (size == 0 || otherSize == 0) {
			return true;
		}
		
		Map<UUID,String> superset =	(size >= otherSize) ? segmentsValuesMap : otherMap;
		Map<UUID,String> subset = 	(size >= otherSize) ? otherMap : segmentsValuesMap;
						
		for (UUID key: subset.keySet()) {
			if (!superset.containsKey(key) || !superset.get(key).equals(subset.get(key))) {
				return false;
			}
		}
		
		return true;
	}

	public ComposedSegmentation getType() {
		return segmentationType;
	}

	public boolean isEmpty() {
		return segmentsValuesMap.isEmpty();
	}
	
	@Override
	public String toString() {
		StringBuffer segmentsValue = new StringBuffer();
		for (UUID key: segmentsValuesMap.keySet()) {
			segmentsValue.append("key:"+key+", value: "+segmentsValuesMap.get(key));			
		}
		
		return segmentsValue.toString();
	}

}
