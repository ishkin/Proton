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
package com.ibm.hrl.proton.runtime.epa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;

public class MatchingSets implements Iterable<List<? extends IDataObject>>{

	private boolean patternDetected;
	private Set<List<? extends IDataObject>> detectedMatchingSets;
	// matching set contains the event instances, and there is an importance to the order of the instances
	
	public MatchingSets() 
	{
		patternDetected = false;
		detectedMatchingSets = new HashSet<List<? extends IDataObject>>();
	}
	
	
	public MatchingSets(MatchingSets otherMatchingSets)
	{				
		Set<List<? extends IDataObject>> newSet = new HashSet<List<? extends IDataObject>>(otherMatchingSets.getMatchingSets());
		patternDetected = otherMatchingSets.isPatternDetected();		
		detectedMatchingSets = newSet;
	}
	
	public void addMatchingSet(List<? extends IDataObject> matchedEvents)
	{
		detectedMatchingSets.add(new ArrayList<IDataObject>(matchedEvents));
		patternDetected = true;
	}
	
	public boolean isPatternDetected(){
		return patternDetected;
	}
	
	public Set<List<? extends IDataObject>> getMatchingSets(){
		return detectedMatchingSets;
	}

	/* 
	 * Should be used in case of the Absence pattern, where pattern is detected with an empty
	 * matching set, so we need to set patternDetected "manually".
	 */
	public void setPatternDetected(boolean patternDetected) {
		this.patternDetected = patternDetected;
	}

	public void clear() {
		patternDetected = false;
		detectedMatchingSets.clear();
		
	}


    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<List<? extends IDataObject>> iterator()
    {
        return detectedMatchingSets.iterator();
    }

}
