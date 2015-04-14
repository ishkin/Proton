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

import java.util.UUID;

import com.ibm.hrl.proton.metadata.context.enums.ContextTypeEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;

public abstract class ContextType implements IContextType {

	protected UUID id;
	protected String name;
    // redundant if its has one-to-one correlation with classes
	protected ContextTypeEnum type;

    public ContextType()
    {
        
    }
    
    public ContextType(String name, ContextTypeEnum type)
    {     
        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
    }
    
	
	
	@Override
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public ContextTypeEnum getType() {
		return type;
	}
	public void setType(ContextTypeEnum type) {
		this.type = type;
	}
		
}
