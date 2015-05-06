package com.ibm.hrl.proton.webapp;

import com.ibm.hrl.proton.agentQueues.queuesManagement.AgentQueuesManager;
import com.ibm.hrl.proton.context.facade.IContextService;
import com.ibm.hrl.proton.epaManager.IEPAManager;
import com.ibm.hrl.proton.eventHandler.IEventHandler;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.router.IDataSender;
import com.ibm.hrl.proton.router.IEventRouter;
import com.ibm.hrl.proton.utilities.asynchronousWork.IWorkManager;
import com.ibm.hrl.proton.utilities.facadesManager.IFacadesManager;
import com.ibm.hrl.proton.utilities.timerService.ITimerServices;

public class WebFacadesManager implements IFacadesManager {
	private static WebFacadesManager instance;
	
	private WebFacadesManager(){
		
	}

	public static synchronized WebFacadesManager getInstance(){
		if (null == instance){
			instance = new WebFacadesManager();
		}
		return instance;
	}
	IContextService contextServiceFacade;	
	ITimerServices timerServiceFacade ;
    IEventHandler eventHandler;
    IDataSender dataSender;
    IEventRouter eventRouter;           
    IEPAManager epaManager;
    AgentQueuesManager agentQueuesManager; 
    IWorkManager workManager;
    EepFacade eepFacade;
	
	
	
	public IContextService getContextServiceFacade() {
		return contextServiceFacade;
	}
	
	public EepFacade getEepFacade() {
		return eepFacade;
	}

	public void setEepFacade(EepFacade eepFacade) {
		this.eepFacade = eepFacade;
	}


	public void setContextServiceFacade(IContextService contextServiceFacade) {
		this.contextServiceFacade = contextServiceFacade;
	}
	
	public IWorkManager getWorkManager() {
		return workManager;
	}

	public void setWorkManager(IWorkManager workManager) {
		this.workManager = workManager;
	}

	

	public ITimerServices getTimerServiceFacade() {
		return timerServiceFacade;
	}

	public void setTimerServiceFacade(ITimerServices timerServiceFacade) {
		this.timerServiceFacade = timerServiceFacade;
	}

	public IEventHandler getEventHandler() {
		return eventHandler;
	}

	public void setEventHandler(IEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	public IDataSender getDataSender() {
		return dataSender;
	}

	public void setDataSender(IDataSender dataSender) {
		this.dataSender = dataSender;
	}

	public IEventRouter getEventRouter() {
		return eventRouter;
	}

	public void setEventRouter(IEventRouter eventRouter) {
		this.eventRouter = eventRouter;
	}

	public IEPAManager getEpaManager() {
		return epaManager;
	}

	public void setEpaManager(IEPAManager epaManager) {
		this.epaManager = epaManager;
	}

	public AgentQueuesManager getAgentQueuesManager() {
		return agentQueuesManager;
	}

	public void setAgentQueuesManager(AgentQueuesManager agentQueuesManager) {
		this.agentQueuesManager = agentQueuesManager;
	}


}
