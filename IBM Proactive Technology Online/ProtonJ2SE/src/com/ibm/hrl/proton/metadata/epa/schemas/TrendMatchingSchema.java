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

import com.ibm.hrl.proton.metadata.computedVariable.ComputedVariableType;
import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.enums.ConsumptionPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.TrendRelationEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IMatchingSchema;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

public class TrendMatchingSchema extends StandardMatchingSchema implements IMatchingSchema {
	
	private static final long serialVersionUID = 1L;
	public static final String TREND_COMPUTED_VARIABLE_NAME = "trend";
	public static final String TREND_COMPUTED_VARIABLE_ATTR_NAME = "count";
	public static final String TREND_COMPUTED_VARIABLE_PARTICIPANTS_NAME = "participants";
	
	protected Map<Operand,String> expressions; // textual representation of the trend expression
	protected Map<Operand,IExpression> parsedExpressions; // run-time expression evaluate

	
	protected TrendRelationEnum relation;
	protected Integer trendTreshold;
	protected Double trendRatio;
	private ComputedVariableType computedVariableType;
	
	

	public TrendMatchingSchema(){
	    this(new HashMap<Operand,String>(), new HashMap<Operand,IExpression>(), null,
	    		new HashMap<Operand,ConsumptionPolicyEnum>(), null, null);
	}
	
	public TrendMatchingSchema(Map<Operand,String> expressions,
			Map<Operand,IExpression> parsedExpressions, TrendRelationEnum relation,
			Map<Operand,ConsumptionPolicyEnum> consumption,Integer trendTreshold)
    {
		this(expressions, parsedExpressions, relation, consumption, trendTreshold, null);
    }
	
	public TrendMatchingSchema(Map<Operand,String> expressions,
			Map<Operand,IExpression> parsedExpressions, TrendRelationEnum relation,
			Map<Operand,ConsumptionPolicyEnum> consumption,Integer trendTreshold,
			Double trendRatio)
    {
		super(consumption);
        this.expressions = expressions;
        this.parsedExpressions = parsedExpressions;       
        this.relation = relation;
        this.trendTreshold = trendTreshold;
        this.trendRatio = trendRatio;
        
        List<TypeAttribute> attributes = new ArrayList<TypeAttribute>();
	    attributes.add(new TypeAttribute(TREND_COMPUTED_VARIABLE_ATTR_NAME,AttributeTypesEnum.INTEGER));
	    attributes.add(new TypeAttribute(TREND_COMPUTED_VARIABLE_PARTICIPANTS_NAME,AttributeTypesEnum.OBJECT));
	    this.computedVariableType = new ComputedVariableType(TREND_COMPUTED_VARIABLE_NAME, attributes);
    }

	public ComputedVariableType getComputedVariableType() {
		return computedVariableType;
	}
	
	public Double getTrendRatio() {
		return trendRatio;
	}

	public void setTrendRatio(Double trendRatio) {
		this.trendRatio = trendRatio;
	}

	public Integer getTrendTreshold() {
		return this.trendTreshold;
	}

	public void setTrendTreshold(Integer trendTreshold) {
		this.trendTreshold = trendTreshold;
	}

	public void setRelation(TrendRelationEnum relation) {
		this.relation = relation;
	}
    	
	public String getExpression(Operand operand) {
		return expressions.get(operand);
	}
	
	public IExpression getParsedExpression(Operand operand) {
		return parsedExpressions.get(operand);
	}
		
	public TrendRelationEnum getTrendRelation() {
		return relation;
	}
	
	public void addExpression(Operand operand, String expression)
	{
		expressions.put(operand, expression);
	}
	
	public void addParsedExpression(Operand operand, IExpression parsedExpression)
	{
		parsedExpressions.put(operand, parsedExpression);
	}

}
