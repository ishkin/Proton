package com.ibm.hrl.proton.agentQueues.async;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.ibm.hrl.proton.agentQueues.queues.QueueElement;
import com.ibm.hrl.proton.agentQueues.queuesManagement.AgentQueuesManager;
import com.ibm.hrl.proton.eventHandler.IEventHandler;
import com.ibm.hrl.proton.runtime.metadata.RoutingMetadataFacade;
import com.ibm.hrl.proton.runtime.timedObjects.ITimedObject;
import com.ibm.hrl.proton.utilities.asynchronousWork.IWorkItem;

public class ConsumerWorkItem implements IWorkItem {

	 	private String agentName;
	    private String contextName;
	    private LinkedBlockingQueue<QueueElement> queue;
	    private AgentQueuesManager manager;
	    private RoutingMetadataFacade metadataFacade;
	    private IEventHandler eventHandler;
	    protected static Logger logger = Logger.getLogger(ConsumerWorkItem.class.getName());
	    
	    public ConsumerWorkItem(String agentName, String contextName, LinkedBlockingQueue<QueueElement> registeredQueue
	            ,AgentQueuesManager manager,RoutingMetadataFacade metadataFacade)
	    {
	        super();
	        this.agentName = agentName;
	        this.contextName = contextName;
	        this.queue = registeredQueue;
	        this.manager = manager;
	        this.metadataFacade = metadataFacade;
	        eventHandler = manager.getEventHandler();
	    }


	    
	@Override
	public void run() {
		try 
		{	
			while (true)
			{
				QueueElement queueElement = queue.take();
			    logger.fine( "removing item from queue: for agent: "+agentName+"and context: "+contextName+", item: "+queueElement);
			    ITimedObject timedObject = queueElement.getTimedObject();
			    eventHandler.handleEventInstance(timedObject, agentName, contextName);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in queue consumer: for queue for agent" +agentName+"and context "+contextName+", reason: "+e.getMessage());
		} 

	}

}
