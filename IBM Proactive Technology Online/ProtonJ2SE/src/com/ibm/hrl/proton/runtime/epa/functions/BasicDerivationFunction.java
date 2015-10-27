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
package com.ibm.hrl.proton.runtime.epa.functions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.ibm.hrl.proton.metadata.TypePredicatePair;
import com.ibm.hrl.proton.metadata.epa.basic.DataObject;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.epa.enums.MultiDerivationPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IDerivationSchema;
import com.ibm.hrl.proton.metadata.event.EventHeader;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.runtime.computedVariable.interfaces.IComputedVariableInstance;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.EventInstance;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;

public abstract class BasicDerivationFunction implements Serializable{
	 private static Logger logger = Logger.getLogger("BasicDerivationFunction");
	    
	    public static List<ITimedObject> derive(IDataObject inputData, IDerivationSchema derivationSchema) 
	    {
	        List<IDataObject> list = new ArrayList<IDataObject>();
	        list.add(inputData);
	        return derive(list, derivationSchema,null);
	    }
	    
	    public static List<ITimedObject> derive(List<? extends IDataObject> inputData, IDerivationSchema derivationSchema,IComputedVariableInstance agentContextValues) 
	    {
	        HashSet<List<? extends IDataObject>> set = new HashSet<List<? extends IDataObject>>();
	        set.add(inputData);
	        return derive(set, derivationSchema,agentContextValues);
	    }
	    
	    public static List<ITimedObject> derive(Set<List<? extends IDataObject>> inputData, IDerivationSchema derivationSchema,IComputedVariableInstance agentContextValues) {
	        
	    	//TODO : change - should support both events and actions
	        logger.entering("DerivationFunction", "derive");
	        ArrayList<ITimedObject> derivedEves = new ArrayList<ITimedObject>();
	        ArrayList<IEventInstance> typeDerivedEves = null;
	        HashMap<String, Object> composedEventsList = null;
	        if (derivationSchema.getMultiDerivationPolicy().equals(MultiDerivationPolicyEnum.COMPOSITION))            
	        {
	            logger.fine("derive: the composition policy is COMPOSE, will create a composed event...");
	            composedEventsList = new HashMap<String, Object>();
	        }

	        long currentTime = Calendar.getInstance().getTimeInMillis();
	        
	        EventInstance e;
	        for (TypePredicatePair eventPair : derivationSchema.getDerivedTypes())
	        {
	        	IEventType eventType = (IEventType)eventPair.getType();
	            logger.fine("derive: creating a derived event of type: "+eventType);
	            IExpression derivationCondition = eventPair.getParsedCondition();
	            if (derivationSchema.getMultiDerivationPolicy().equals(MultiDerivationPolicyEnum.COMPOSITION))
	            {
	                typeDerivedEves = new ArrayList<IEventInstance>();
	                composedEventsList.put(eventType.getName(), typeDerivedEves);
	            }
	            for (List<? extends IDataObject> input : inputData)
	            {
	                logger.fine("derive: performing derivation for matching set: "+input+" and agent contexts: "+agentContextValues);
	                List<IDataObject> listWithContextValues = new ArrayList<IDataObject>();
	                //to take care of absence - the derivation expressions signatures are built based on input data and context segmentation values, the input data
	                //for absence in runtime is obviously empty - simulate runtime instances so it fits signatures
	                if (input.isEmpty()) listWithContextValues.add(new DataObject());
	                listWithContextValues.addAll(input);
	                if (agentContextValues != null) listWithContextValues.add(agentContextValues);
	                
	                if (derivationCondition != null && !(Boolean)derivationCondition.evaluate(listWithContextValues))
	                    continue;
	                HashMap<String, Object> atts = new HashMap<String, Object>();
	                for(TypeAttribute att : eventType.getTypeAttributes())
	                {                    
	                    IExpression exp = derivationSchema.getParsedExpressions().get(eventType).get(att);                   
	                    if (exp == null) continue; //for attributes which should not be set
	                    logger.fine("derive: creating attribute: "+att+" with expression: "+exp);
	                    Object o = exp.evaluate(listWithContextValues);
	                    logger.fine("derive: created attribute: "+att+" with value: "+o);
	                    atts.put(att.getName(), o);
	                }
	               
	                if (atts.get(EventHeader.OCCURENCE_TIME_ATTRIBUTE) == null)
	                	e = new EventInstance(eventType,currentTime ,currentTime,(Map<String,Object>)atts);
	                else
	                	e = new EventInstance(eventType,currentTime ,(Map<String,Object>)atts);
	                // need to check what are the missing attributes
	                if (derivationSchema.getMultiDerivationPolicy().equals(MultiDerivationPolicyEnum.COMPOSITION))
	                    typeDerivedEves.add(e);
	                else
	                    derivedEves.add(e);
	            }
	        }
	        // TODO: need to revisit after figuring out the true intention of mutiple derivation options
	        if (derivationSchema.getMultiDerivationPolicy().equals(MultiDerivationPolicyEnum.COMPOSITION))
	            derivedEves.add(new EventInstance(derivationSchema.getComposedEventType(), currentTime ,currentTime,composedEventsList));
	        return (List<ITimedObject>)derivedEves;
	    }

	  
	    
	    
}
