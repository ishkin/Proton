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
/**
 * 
 */
package com.ibm.hrl.proton.metadata.event;

import java.util.List;

import com.ibm.hrl.proton.metadata.type.AbstractType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;

/**
 * @author zoharf
 *
 */
public class EventType extends AbstractType implements IEventType{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EventType()
	{
		super();
	}
	
	public EventType(String name){
		super(name);
	}
	
	public EventType(String name,List<TypeAttribute> payload)
	{		
	    super(name,payload);
	    
	}
	
	public EventType(List<TypeAttribute> payload)
	{
		super(payload);
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
	    return "Event name: "+getTypeName()+", attributes: "+getTypeAttributeSet();
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
	    if(this == o)
	        return true;
	    if((o == null) || (o.getClass() != this.getClass()))
	        return false;
	    //object must be EventType at this point - we assume the name is unique, no two event types with same name
	    EventType otherEventType = (EventType)o;
	    String thisEventTypeName = this.getTypeName();
	    String otherEventTypeName = otherEventType.getTypeName();
	    return (thisEventTypeName == otherEventTypeName || (thisEventTypeName != null && thisEventTypeName.equals(otherEventTypeName)));

	}
	
	@Override
	public boolean equals(IEventType other) {
		if (other == this)
			return true;
		
		if (other.getTypeName().equals(this.getTypeName()))
			return true;
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
	    //Since the name is unique the hash of EventType can be the hash of the event type name
	    String eventTypeName = getTypeName();
	    int hash = 7;
	    hash = 31 * hash + (null == eventTypeName ? 0 : eventTypeName.hashCode());
	    return hash;
	}

	@Override
	public List<TypeAttribute> getHeaderAttributes() {
		return EventHeader.getAttributes();
	}


	
	

	

	
}
