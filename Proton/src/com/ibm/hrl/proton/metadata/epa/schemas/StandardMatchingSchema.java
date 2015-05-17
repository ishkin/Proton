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

import java.util.HashMap;
import java.util.Map;

import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.enums.ConsumptionPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.InstanceSelectionPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.OrderPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IMatchingSchema;

public class StandardMatchingSchema implements IMatchingSchema{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// for All, Sequence, Any
	Map<Operand, ConsumptionPolicyEnum> consumption;
	Map<Operand, InstanceSelectionPolicyEnum> repeatedType;
    Map<Operand, OrderPolicyEnum> order;
	
	public StandardMatchingSchema(){
	    consumption = new HashMap<Operand,ConsumptionPolicyEnum>();
	    repeatedType = new HashMap<Operand,InstanceSelectionPolicyEnum>();
	    order = new HashMap<Operand,OrderPolicyEnum>();
	}
	
	public StandardMatchingSchema(
            Map<Operand, ConsumptionPolicyEnum> consumption,
            Map<Operand, InstanceSelectionPolicyEnum> repeatedType,
            Map<Operand, OrderPolicyEnum> order)
    {
        super();
        this.consumption = consumption;
        this.repeatedType = repeatedType;
        this.order = order;
    }
	
	public StandardMatchingSchema(
            Map<Operand, ConsumptionPolicyEnum> consumption)
    {
        this();
        this.consumption = consumption;       
    }
    	
	public void setConsumption(Map<Operand, ConsumptionPolicyEnum> consumption) {
		this.consumption = consumption;
	}

	public void setRepeatedType(
			Map<Operand, InstanceSelectionPolicyEnum> repeatedType) {
		this.repeatedType = repeatedType;
	}

	public void setOrder(Map<Operand, OrderPolicyEnum> order) {
		this.order = order;
	}

	public ConsumptionPolicyEnum getConsumption(Operand operand) {
		return consumption.get(operand);
	}
	public InstanceSelectionPolicyEnum getRepeatedType(Operand operand) {
		return repeatedType.get(operand);
	}
	public OrderPolicyEnum getOrder(Operand operand) {
		return order.get(operand);
	}
	
	public void setConsumptionPolicy(Operand operand, ConsumptionPolicyEnum consumptionPolicy)
	{
		this.consumption.put(operand, consumptionPolicy);
	}
	
	public void setInstanceSelectionPolicy(Operand operand, InstanceSelectionPolicyEnum selectionPolicy)
	{
		this.repeatedType.put(operand, selectionPolicy);
	}
	
	public void setOrderPolicy(Operand operand, OrderPolicyEnum orderPolicy)
	{
		this.order.put(operand, orderPolicy);
	}
}
