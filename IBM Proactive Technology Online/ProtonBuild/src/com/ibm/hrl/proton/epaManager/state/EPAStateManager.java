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

package com.ibm.hrl.proton.epaManager.state;

import com.ibm.hrl.proton.epa.state.IEPAStateManager;
import com.ibm.hrl.proton.epa.state.IOperatorData;
import com.ibm.hrl.proton.epa.state.OperatorData;
import com.ibm.hrl.proton.epa.state.SequenceOperatorData;
import com.ibm.hrl.proton.epa.state.TrendOperatorData;
import com.ibm.hrl.proton.metadata.epa.enums.EPATypeEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.runtime.metadata.EPAManagerMetadataFacade;
import com.ibm.hrl.proton.utilities.persistence.IPersistenceManager;

/**
 * <code>EPAStateManager</code>.
 * EPA module specific implementation for state manager.
 * This class will contain methods for EPA state manipulation. The class will construct appropriate queries and 
 * delegate their execution to environment specific persistence manager.
 * 
 * 
 */
public class EPAStateManager implements IEPAStateManager
{
    private IPersistenceManager persistenceManager;
    
    

    public EPAStateManager(IPersistenceManager persistenceManager)
    {       
        this.persistenceManager = persistenceManager;
    }



    /* (non-Javadoc)
     * @see com.ibm.hrl.proton.epa.state.IStateManager#getState(java.lang.String, java.lang.String)
     */
    @Override
    public IOperatorData getState(String agentTypeName, String contextPartition)
    {
       IEventProcessingAgent agentType = EPAManagerMetadataFacade.getInstance().getAgentDefinition(agentTypeName);
        if (agentType.getType().equals(EPATypeEnum.SEQUENCE))
        {            
            return new SequenceOperatorData(agentType.getNumOfOperands());
        }
        else if (agentType.getType().equals(EPATypeEnum.TREND))
        {
        	return new TrendOperatorData(agentType.getNumOfOperands());
        }
        else
        {
            return new OperatorData(agentType.getNumOfOperands());
        }
    }

}
