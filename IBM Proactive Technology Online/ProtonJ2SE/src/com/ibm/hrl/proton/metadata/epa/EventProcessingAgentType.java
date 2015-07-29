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
package com.ibm.hrl.proton.metadata.epa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;
import com.ibm.hrl.proton.metadata.context.interfaces.ISegmentationContextType;
import com.ibm.hrl.proton.metadata.epa.enums.EPATypeEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IDerivationSchema;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.metadata.epa.interfaces.IFilteringSchema;
import com.ibm.hrl.proton.metadata.event.IEventType;

public class EventProcessingAgentType implements IEventProcessingAgent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected UUID id;
	protected String name;
	protected EPATypeEnum epaType;
	private Map<IEventType,List<Operand>> inputEventsMap;	
	protected List<IEventType> inputEvents;
	protected IFilteringSchema filteringSchema;
    protected IDerivationSchema derivationSchema;
	protected IContextType context;
	protected int numberOfOperands;
	protected boolean isFair;
		
	public EventProcessingAgentType(UUID id, String name, EPATypeEnum epaType,
			List<IEventType> inputEvents,			
			IFilteringSchema filteringSchema,
			IDerivationSchema derivationSchema, IContextType context,
			 boolean isFair)
	{
		super();
		this.id = id;
		this.name = name;
		this.epaType = epaType;
		this.inputEvents = inputEvents;
		
		this.filteringSchema = filteringSchema;
		this.derivationSchema = derivationSchema;
		this.context = context;
		this.numberOfOperands = inputEvents.size();		
		this.isFair = isFair;
		initializeEventMap();
	}


    private void initializeEventMap()
	{
		this.inputEventsMap = new HashMap<IEventType, List<Operand>>();
		for (int i=0; i < inputEvents.size(); i++) {
			IEventType eventType = inputEvents.get(i);
			if (!inputEventsMap.containsKey(eventType))
			{
				inputEventsMap.put(eventType, new ArrayList<Operand>());
			}
			inputEventsMap.get(eventType).add(new Operand(i,eventType));
		}
	}
	
	
	

	@Override
	public boolean isFair() {
		return isFair();
	}
	
	public String getName()
	{
		return name;
	}
	
	public EPATypeEnum getType()
	{
		return epaType;
	}
	
	public int getNumOfOperands()
	{
		return numberOfOperands;
	}
	
	
	public List<Operand> getEventInputOperands(IEventType eventType)
	{
		return inputEventsMap.get(eventType);
	}
	
	public Operand getEventInputOperand(int index)
	{
		IEventType eventType = inputEvents.get(index);
		Operand operand = new Operand(index,eventType);
		return operand;
	}
	
	
	
	
	public IFilteringSchema getFilteringSchema()
    {
        return filteringSchema;
    }

    public IDerivationSchema getDerivationSchema()
    {
        return derivationSchema;
    }


    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent#getContextTypeName()
     */
    @Override
    public String getContextTypeName()
    {
       return this.context.getName();
    }


	@Override
	public Collection<ISegmentationContextType> getLocalSegmentation() {
		return (new HashSet<ISegmentationContextType>());
	}

	@Override
	public List<IEventType> getInputEvents() {
		return inputEvents;
	}


	@Override
	public void setDerivationSchema(IDerivationSchema derivationSchema) {
		this.derivationSchema = derivationSchema;
		
	}
}
