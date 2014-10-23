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
package com.ibm.hrl.proton.metadata.epa.schemas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.hrl.proton.metadata.TypePredicatePair;
import com.ibm.hrl.proton.metadata.epa.enums.MultiDerivationPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IDerivationSchema;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.interfaces.IBasicType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

public class DerivationSchema implements IDerivationSchema {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected Map<IBasicType, Map<TypeAttribute, String>> derivationExpressions;   
    protected Map<IBasicType, Map<TypeAttribute, IExpression>> parsedExpressions;
    protected List<TypePredicatePair> derivedTypes;
    protected Map<IEventType, Boolean> reportParticipants;    
	protected MultiDerivationPolicyEnum multiDerivationPolicy = MultiDerivationPolicyEnum.MULTIPLE_EVENTS;    
	protected IEventType composedEventType;
	protected boolean reportingParticipants = false;
    
    
    

	public DerivationSchema()
    {
    	this.derivationExpressions = new HashMap<IBasicType, Map<TypeAttribute, String>>();
    	this.parsedExpressions = new HashMap<IBasicType, Map<TypeAttribute, IExpression>>();
    	this.derivedTypes = new ArrayList<TypePredicatePair>();
    	this.reportParticipants =new HashMap<IEventType, Boolean>();
    }
    
    public DerivationSchema(
            Map<IBasicType, Map<TypeAttribute, String>> derivationExpressions,
            Map<IBasicType, Map<TypeAttribute, IExpression>> parsedExpressions,
            List<TypePredicatePair> derivedEvents,
            MultiDerivationPolicyEnum multiDerivationPolicy,
            IEventType composedEventType)
    {
        super();
        this.derivationExpressions = derivationExpressions;
        this.parsedExpressions = parsedExpressions;
        this.derivedTypes = derivedEvents;
        this.multiDerivationPolicy = multiDerivationPolicy;
        this.composedEventType = composedEventType;
    }
    
    public boolean isReportingParticipants() {
		return reportingParticipants;
	}

	public void setReportingParticipants(boolean reportingParticipants) {
		this.reportingParticipants = reportingParticipants;
	}
	
    public Map<IEventType, Boolean> getReportParticipants() {
		return reportParticipants;
	}
    
    public void addDerivationStringExpression(IBasicType type, Map<TypeAttribute,String> stringExpressions)
    {
    	this.derivationExpressions.put(type, stringExpressions);
    }
    
    public void addDerivationExpression(IBasicType type, Map<TypeAttribute,IExpression> expressions)
    {
    	this.parsedExpressions.put(type, expressions);
    }
    
    public void addDerivationCondition(TypePredicatePair derivationCondition)
    {
    	derivedTypes.add(derivationCondition);
    }
    
    public void addReportParticipant(IEventType event, Boolean report)
    {
    	this.reportParticipants.put(event, report);
    }
    
    public Map<IBasicType, Map<TypeAttribute, IExpression>> getParsedExpressions() {
        return parsedExpressions;
    }
    public void setParsedExpressions(
            Map<IBasicType, Map<TypeAttribute, IExpression>> parsedExpressions) {
        this.parsedExpressions = parsedExpressions;
    }
    public List<TypePredicatePair> getDerivedTypes() {
        return derivedTypes;
    }
    public void setDerivedTypes(List<TypePredicatePair> derivedTypes) {
        this.derivedTypes = derivedTypes;
    }

    @Override
    public List<TypePredicatePair> getDerivationConditions() {       
        return derivedTypes;
    }
    @Override
    public MultiDerivationPolicyEnum getMultiDerivationPolicy() {
        return multiDerivationPolicy;
    }
    public void setMultiDerivationPolicy(
            MultiDerivationPolicyEnum multiDerivationPolicy) {
        this.multiDerivationPolicy = multiDerivationPolicy;
    }
    @Override
    public IEventType getComposedEventType() {
        return composedEventType;
    }
    
    public void setComposedEventType(IEventType composedEventType) {
		this.composedEventType = composedEventType;
	}

    
    
}
