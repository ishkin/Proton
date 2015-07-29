package com.ibm.hrl.proton.utilities.facadesManager;

import com.ibm.hrl.proton.agentQueues.queuesManagement.AgentQueuesManager;
import com.ibm.hrl.proton.context.facade.IContextService;
import com.ibm.hrl.proton.epaManager.IEPAManager;
import com.ibm.hrl.proton.eventHandler.IEventHandler;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.router.IDataSender;
import com.ibm.hrl.proton.router.IEventRouter;
import com.ibm.hrl.proton.utilities.asynchronousWork.IWorkManager;
import com.ibm.hrl.proton.utilities.timerService.ITimerServices;

public interface IFacadesManager {
	public IContextService getContextServiceFacade() ;	
	public EepFacade getEepFacade() ;
	public IWorkManager getWorkManager() ;
	public ITimerServices getTimerServiceFacade() ;
	public IEventHandler getEventHandler() ;
	public IDataSender getDataSender();
	public IEventRouter getEventRouter();
	public IEPAManager getEpaManager();
	public AgentQueuesManager getAgentQueuesManager();

	
}
