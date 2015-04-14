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

import java.util.Map;

import com.ibm.hrl.proton.metadata.context.enums.ContextTypeEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.ISegmentationContextType;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

public class SegmentationContextType extends ContextType implements ISegmentationContextType {

	//protected Collection<EventPredicatePair> segmentationKeys;
	protected Map<IEventType,String> expressions;
	protected Map<IEventType,IExpression> parsedExpressions;
	
	public SegmentationContextType(String contextName, Map<IEventType,String> expressions,
			Map<IEventType,IExpression> parsedExpressions) {
		super(contextName,ContextTypeEnum.SEGEMENTATION);
		this.expressions = expressions;
		this.parsedExpressions = parsedExpressions;
	}
	
	@Override
	public Map<IEventType,String> getSegmentationKeys() {
		return expressions;
	}

	@Override
	public String getSegmentationExpression(IEventType eventType) {
		return expressions.get(eventType);
	}
	
	@Override
	public IExpression getParsedSegmentationExpression(IEventType eventType) {
		return parsedExpressions.get(eventType);
	}

}
