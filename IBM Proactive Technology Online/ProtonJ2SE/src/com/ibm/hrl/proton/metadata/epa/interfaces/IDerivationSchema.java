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
package com.ibm.hrl.proton.metadata.epa.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.ibm.hrl.proton.metadata.TypePredicatePair;
import com.ibm.hrl.proton.metadata.epa.enums.MultiDerivationPolicyEnum;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.interfaces.IBasicType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

public interface IDerivationSchema  extends Serializable{
	public List<TypePredicatePair> getDerivedTypes();
	// The Integer represents the operand number
	//get derived  attribute expressions
	public Map<IBasicType , Map<TypeAttribute, IExpression>> getParsedExpressions();
	public List<TypePredicatePair> getDerivationConditions();
	MultiDerivationPolicyEnum getMultiDerivationPolicy();
    IEventType getComposedEventType();
    public Map<IEventType, Boolean> getReportParticipants();
    public boolean isReportingParticipants();
    public void setReportingParticipants(boolean report);
    public void setDerivedTypes(List<TypePredicatePair> derivedTypes);
    public void setParsedExpressions(
            Map<IBasicType, Map<TypeAttribute, IExpression>> parsedExpressions);

}
