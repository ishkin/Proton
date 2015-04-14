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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.ibm.hrl.proton.epa.state.IEPAStateManager;
import com.ibm.hrl.proton.epaManager.exceptions.EPAManagerLogicExecutionException;
import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.StatefulEventProcesingAgentType;
import com.ibm.hrl.proton.metadata.epa.enums.EPATypeEnum;
import com.ibm.hrl.proton.metadata.epa.enums.EvaluationPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.runtime.epa.MatchingSets;
import com.ibm.hrl.proton.runtime.epa.functions.DerivationFunction;
import com.ibm.hrl.proton.runtime.epa.functions.FilteringFunction;
import com.ibm.hrl.proton.runtime.epa.interfaces.IEventProcessingAgentInstance;
import com.ibm.hrl.proton.runtime.epa.interfaces.IProcessingFunction;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.persistence.IPersistenceManager;

public abstract class BaseEventProcessingAgentInstance implements IEventProcessingAgentInstance{
	protected IProcessingFunction processingFunction = null;
	protected IEventProcessingAgent agentType;
	

	protected String contextPartition;
	protected IEPAStateManager stateManager;
	
	private static Logger logger = Logger.getLogger("EventProcessingAgentInstance");

	public BaseEventProcessingAgentInstance(IEventProcessingAgent agentType,String contextPartition,IEPAStateManager stateManager,IPersistenceManager persistenceManager) throws EPAManagerLogicExecutionException
	{
	    logger.fine("EventProcessingAgent: Building EventProcessingAgent instance of type "+ agentType+ " for partition: " +contextPartition);
		this.agentType = agentType;
		this.contextPartition = contextPartition;
		this.stateManager = stateManager;
		
		
		
		
	}

	public IEventProcessingAgent getAgentType() {
		return agentType;
	}
	

	@Override	
	public abstract  List<ITimedObject> processEvent(IEventInstance event);
	
	protected abstract List<ITimedObject> matchAndDerive();

	@Override	
	public synchronized List<ITimedObject> terminate() {
		// TODO Auto-generated method stub
		// if deferred evaluation policy then perform detection attempt, otherwise nothing
		// if single cardinality policy then detect once, otherwise unrestricted
		// terminate the epa instance
		List<ITimedObject> derivedEvents = new ArrayList<ITimedObject>();
		if (processingFunction != null && ((StatefulEventProcesingAgentType)agentType).getEvaluation().equals(EvaluationPolicyEnum.DEFERRED))
			derivedEvents = matchAndDerive();
		
		if (processingFunction != null)  //stateful agent
		{
			//clean the state of the internal state-
			processingFunction.getInternalState().clean();
		}
		return derivedEvents;
	}

	public synchronized void clearState(){
		if (processingFunction != null){
			processingFunction.getInternalState().clean();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
	    return "EventProcessingAgent instance for agent:"+agentType+" and partition: "+contextPartition;
	}

	/**
	 * Check if an agent instance needs to be instantiated upon termination request if no instance
	 * have existed at the moment - for deffered agents, such as absence
	 * @param agent
	 * @return
	 * @throws EPAManagerLogicExecutionException
	 */
	public static boolean checkInstantiationNeed(IEventProcessingAgent agent) throws EPAManagerLogicExecutionException {
		boolean returnValue = false;
		
		EPATypeEnum agentType = agent.getType();
		if (!agentType.equals(EPATypeEnum.BASIC))
		{
			StatefulEventProcesingAgentType statefullAgent = (StatefulEventProcesingAgentType)agent;
			if (statefullAgent.getEvaluation().equals(EvaluationPolicyEnum.DEFERRED))
			{
				//in case of a stateful agent with DEFFERED evaluation policy and no existing instance- need to 
				//determine if this agent requires instantiation at termination time, like ABSENCE
				//operator for example				
				try {
					Class agentOperator = StatefulProcessingAgentInstance.fetchAgentOperatorClass(statefullAgent);					
					IProcessingFunction processingFunction = (IProcessingFunction)agentOperator.newInstance();
					returnValue = processingFunction.determineTerminationInstantiation();
				} catch (Exception e) {
					throw new EPAManagerLogicExecutionException(e.getMessage());
				} 
				
			}

		}
			
	    return returnValue;
	}
}
