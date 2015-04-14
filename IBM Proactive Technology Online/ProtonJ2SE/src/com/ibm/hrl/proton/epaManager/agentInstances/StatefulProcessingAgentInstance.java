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
package com.ibm.hrl.proton.epaManager.agentInstances;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.ibm.hrl.proton.epa.interfaces.IExtendedProcessingFunction;
import com.ibm.hrl.proton.epa.interfaces.IStandardProcessingFunction;
import com.ibm.hrl.proton.epa.state.IEPAStateManager;
import com.ibm.hrl.proton.epa.state.IOperatorData;
import com.ibm.hrl.proton.epaManager.exceptions.EPAManagerLogicExecutionException;
import com.ibm.hrl.proton.metadata.epa.StatefulEventProcesingAgentType;
import com.ibm.hrl.proton.metadata.epa.enums.EPATypeEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.runtime.computedVariable.ComputedVariableInstance;
import com.ibm.hrl.proton.runtime.computedVariable.interfaces.IComputedVariableInstance;
import com.ibm.hrl.proton.runtime.epa.interfaces.IProcessingFunction;
import com.ibm.hrl.proton.utilities.persistence.IPersistenceManager;

public class StatefulProcessingAgentInstance extends
		EventProcessingAgentInstance {

	private final static String PACKAGE_NAME = "com.ibm.hrl.proton.epa.simple.operators.";
	private final static String OPERATOR = "Operator";	
	private static Logger logger = Logger.getLogger("StatefulProcessingAgentInstance");
	private IComputedVariableInstance contextSegmentationValues;
	
	public StatefulProcessingAgentInstance(IEventProcessingAgent agentType,
			String contextPartition, IEPAStateManager stateManager,
			IPersistenceManager persistenceManager)
			throws EPAManagerLogicExecutionException {
		super(agentType, contextPartition, stateManager, persistenceManager);	
		//if the agent is statefull:
		//IProcessingFuncion is not null
		//IProcessingFunction is of two types  - SimpleProcessingFunction which is based on framework
		//for fetching the state for the operator
		//ExtendedProcessingFunction is one managing the state on its own
		logger.fine("EventProcessingAgent: the agent is a stateful agent , trying to fetch the class for processing function...");
		StatefulEventProcesingAgentType statefullAgent = (StatefulEventProcesingAgentType)agentType;

		//TODO: optimization - this code can be invoked when actually has a need to fetch state and invoke the operator for the first time
		//since perhaps the filter will never be passed
		Class agentOperator = fetchAgentOperatorClass(statefullAgent);
		try
		{
			//derive the type of the processing function as defined by the EPA and initialize it
			IProcessingFunction processingFunction = (IProcessingFunction)agentOperator.newInstance();
			logger.fine("EventProcessingAgent: fetched the operator class, fetching the data...");
			if (processingFunction instanceof IStandardProcessingFunction)
			{
				//fetch the state of the EPA from persistent store
				logger.fine("EventProcessingAgent: the operator is a standard operator ,fetching the data..");
				IOperatorData operatorData = stateManager.getState(statefullAgent.getName(),contextPartition);
				IStandardProcessingFunction simpleProcFunction = (IStandardProcessingFunction)processingFunction;
				simpleProcFunction.setAgentType(statefullAgent);
				simpleProcFunction.setOperatorData(operatorData);
				this.processingFunction = simpleProcFunction;
			}else
			{
				//processing function is instance of IExtendedProcessingFunction
				logger.fine("EventProcessingAgent: the operator is extended operator ,fetching the data..");
				IExtendedProcessingFunction extendedProcFunction = (IExtendedProcessingFunction)processingFunction;
				extendedProcFunction.setAgentType(statefullAgent);
				extendedProcFunction.setPersistenceManager(persistenceManager);
			}



			//fetch the relevant operator constructor, and build the constructor
			//Constructor c = agentOperator.getConstructor(new Class[]{OperatorData.class,StatefullEventProcesingAgentType.class });
			//this.processingFunction = (IProcessingFunction)c.newInstance(new Object[]{operatorData, statefullAgent});
		}
		catch (Exception e)
		{
			throw new EPAManagerLogicExecutionException(e.getMessage());
		}
		
		contextSegmentationValues = new ComputedVariableInstance(((StatefulEventProcesingAgentType)agentType).getAgentContextInformation(), new HashMap<String,Object>());
		
	}
	
	public IComputedVariableInstance getContextSegmentationValues() {
		return contextSegmentationValues;
	}

	public void setContextSegmentationValues(
			Map<String,Object> values) {
		this.contextSegmentationValues.setAttributes(values);
	}

	/**
	 * @param statefullAgent
	 * @return
	 * @throws EPAManagerLogicExecutionException 
	 */
	static Class fetchAgentOperatorClass(
			StatefulEventProcesingAgentType statefullAgent) throws EPAManagerLogicExecutionException
	{
		EPATypeEnum epaType = statefullAgent.getType();
		String operatorName = epaType.toString().toLowerCase();
		String firstCharacter = operatorName.substring(0,1).toUpperCase(); //first character should be upper case
		String otherChars = operatorName.substring(1);
		
		logger.fine("fetchAgentOperatorClass: fetching class implementing operator "+epaType);
		String operatorClassName = firstCharacter+otherChars+OPERATOR;
		
		try
        {
		    String className = PACKAGE_NAME+operatorClassName;
		    logger.fine("fetchAgentOperatorClass: class name for the operator is "+className);
            Class operatorClass  = Class.forName(className);
            return operatorClass;
        }
        catch (ClassNotFoundException e)
        {
            throw new EPAManagerLogicExecutionException("Could not find operator class with name "+PACKAGE_NAME+operatorClassName);
        }
        
	}

}
