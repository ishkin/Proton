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

import com.ibm.hrl.proton.epa.interfaces.IExtendedProcessingFunction;
import com.ibm.hrl.proton.epa.interfaces.IStandardProcessingFunction;
import com.ibm.hrl.proton.epa.state.IEPAStateManager;
import com.ibm.hrl.proton.epa.state.IOperatorData;
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

/**
 * SimpleEventProcessingAgentInstance is the representation of a standard agent consisting of three
 * building blocks  - filter, processing function and derivation.
 * The standard flow is running the filter if not null, if the results are positive and true then running the processing function
 * (if not null) and at the end running derivation function.
 * If the user wishes to implement and operator running as a part of this framework he needs to implement
 * the IProcessingFunction interface and extend the AbstractOperator class and he will get all the framework services.
 * To provide a more decoupled implementation the user do not have to extend the AbstractOperator, he
 * can provide his own implementation based on the OperatorData.
 * To perform his own state management and imlpementation the user cannot use SimpleEventProcessingAgentInstance,
 * he will have to implement the IEventProcessingAgentInstance and manage the state on his own using the 
 * IStateManager and IPersistenceManager.
 * <code>SimpleEventProcessingAgentInstance</code>.
 * 
 *
 */
public class EventProcessingAgentInstance extends BaseEventProcessingAgentInstance{
	private static Logger logger = Logger.getLogger("EventProcessingAgentInstance");

	public EventProcessingAgentInstance(IEventProcessingAgent agentType,String contextPartition,IEPAStateManager stateManager,IPersistenceManager persistenceManager) throws EPAManagerLogicExecutionException
	{
	   super(agentType, contextPartition, stateManager, persistenceManager);
	
	}
	

	@Override	
	public synchronized List<ITimedObject> processEvent(IEventInstance event) {
	    
	    logger.fine("EventProcessingAgent instance of type "+ agentType+ " and partition: " +contextPartition+ " is processing event instance "+event);
		// TODO Auto-generated method stub


		// optimization: if immediate and single cardinality policy then we can terminate the instance
		// optimization: if deferred and single and first policies we can think of keeping a minimal state,
		// if an early detection attempt is possible...
	    logger.fine("processEvent: before running filtering function...");
		Set<Operand> filteredOperands = FilteringFunction.evaluate(event, agentType.getFilteringSchema());
		List<ITimedObject> derivedEvents = new ArrayList<ITimedObject>();
		if (filteredOperands.isEmpty())
		{
		    logger.fine("processEvent: no operand for this event instance passed the filter");
		    return derivedEvents;
		}
			
		if (processingFunction != null) //epa is stateful
		{
		    logger.fine("processEvent: passing event instance to the stateful operator...");
			boolean addedToState = processingFunction.addInstancetoState(event, filteredOperands);
			logger.fine("processEvent: added instance to state of the stateful operator...");
			if (addedToState && ((StatefulEventProcesingAgentType)agentType).getEvaluation().equals(EvaluationPolicyEnum.IMMEDIATE))
			{
			    logger.fine("processEvent: the evaluation policy is IMMEDIATE, proceeding to evaluation...");
				derivedEvents = matchAndDerive();
			}
			return derivedEvents;
		}
			
		
		return DerivationFunction.derive(event, agentType.getDerivationSchema());
	    
	}
	
	protected List<ITimedObject> matchAndDerive()
	{
		List<ITimedObject> derivedEvents = new ArrayList<ITimedObject>();
		MatchingSets matchingSets = processingFunction.match();
		if (matchingSets.isPatternDetected())
		{
		    logger.fine("matchAndDerive: match found, performing derivation...");
		    StatefulProcessingAgentInstance thisInstance = (StatefulProcessingAgentInstance)this;
		    derivedEvents = DerivationFunction.derive(matchingSets.getMatchingSets(), agentType.getDerivationSchema(),thisInstance.getContextSegmentationValues());
		}	
		
		return derivedEvents;
	}


		
}
