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
package com.ibm.hrl.proton.runtime.epa.functions;

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

/**
 * @author zoharf
 *
 */
public class DerivationFunction extends BasicDerivationFunction {

    private static Logger logger = Logger.getLogger("DerivationFunction");
    
    public static List<ITimedObject> derive(IDataObject inputData, IDerivationSchema derivationSchema) 
    {       
        return BasicDerivationFunction.derive(inputData, derivationSchema);
    }
    
    public static List<ITimedObject> derive(List<? extends IDataObject> inputData, IDerivationSchema derivationSchema,IComputedVariableInstance agentContextValues) 
    {
      return BasicDerivationFunction.derive(inputData, derivationSchema, agentContextValues);
        
    }
    
    public static List<ITimedObject> derive(Set<List<? extends IDataObject>> inputData, IDerivationSchema derivationSchema,IComputedVariableInstance agentContextValues) {
        
    	return BasicDerivationFunction.derive(inputData, derivationSchema, agentContextValues);
    }

    
    
   

}
