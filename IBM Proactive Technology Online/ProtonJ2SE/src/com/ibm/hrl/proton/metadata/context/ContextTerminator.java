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

import com.ibm.hrl.proton.metadata.context.enums.ContextTerminationTypeEnum;
import com.ibm.hrl.proton.metadata.context.enums.ContextTerminatorPolicyEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextTerminator;

public abstract class ContextTerminator implements IContextTerminator{

	protected UUID id;
	private static final long serialVersionUID = 1L;
	protected ContextTerminatorPolicyEnum terminatorPolicy;
	protected ContextTerminationTypeEnum terminationType;
	
	public ContextTerminator(ContextTerminatorPolicyEnum terminatorPolicy,
			ContextTerminationTypeEnum terminationType) {
		id = UUID.randomUUID();
        this.terminatorPolicy = terminatorPolicy;
        this.terminationType = terminationType;
	}
	
	@Override
	public ContextTerminationTypeEnum getTerminationType() {
		return terminationType;
	}
	
	
	public void setTerminationType(ContextTerminationTypeEnum terminationType) {
		this.terminationType = terminationType;
	}
	
	@Override
	public ContextTerminatorPolicyEnum getTerminationPolicy() {
		return terminatorPolicy;
	}
	
	public UUID getId() {
		return id;
	}
		
}
