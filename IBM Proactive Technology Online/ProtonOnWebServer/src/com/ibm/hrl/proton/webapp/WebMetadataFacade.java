package com.ibm.hrl.proton.webapp;

import com.ibm.hrl.proton.metadata.inout.ConsumerProducerMetadata;
import com.ibm.hrl.proton.runtime.metadata.ContextMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.EPAManagerMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.IMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.RoutingMetadataFacade;

public class WebMetadataFacade implements IMetadataFacade {

	private static WebMetadataFacade instance = null;
	private ContextMetadataFacade contextMetadataFacade;
	private EPAManagerMetadataFacade epaManagerMetadataFacade;
	private EventMetadataFacade eventMetadataFacade;
	private RoutingMetadataFacade routingMetadataFacade;
	private ConsumerProducerMetadata consumerProducerMetadata;
	
	private WebMetadataFacade(){
		
	}
	
	public synchronized static WebMetadataFacade getInstance(){	
		if (null == instance){
			instance = new WebMetadataFacade();
		}
		return instance;
	}
	
	@Override
	public ContextMetadataFacade getContextMetadataFacade() {
		return contextMetadataFacade;
	}

	@Override
	public void setContextMetadataFacade(ContextMetadataFacade contextMetadataFacade) {
		this.contextMetadataFacade = contextMetadataFacade;
	}

	@Override
	public EPAManagerMetadataFacade getEpaManagerMetadataFacade() {
		return epaManagerMetadataFacade;
	}

	@Override
	public void setEpaManagerMetadataFacade(
			EPAManagerMetadataFacade epaManagerMetadataFacade) {
		this.epaManagerMetadataFacade = epaManagerMetadataFacade;
	}

	@Override
	public EventMetadataFacade getEventMetadataFacade() {
		return eventMetadataFacade;
	}

	@Override
	public void setEventMetadataFacade(EventMetadataFacade eventMetadataFacade) {
		this.eventMetadataFacade = eventMetadataFacade;
	}

	@Override
	public RoutingMetadataFacade getRoutingMetadataFacade() {
		return routingMetadataFacade;
	}

	@Override
	public void setRoutingMetadataFacade(RoutingMetadataFacade routingMetadataFacade) {
		this.routingMetadataFacade = routingMetadataFacade;
	}
	
	@Override
	public void clear(){
		this.contextMetadataFacade.clear();
		this.routingMetadataFacade.clear();
		this.epaManagerMetadataFacade.clear();
		this.eventMetadataFacade.clear();
		this.consumerProducerMetadata.clear();
	}

	@Override
	public ConsumerProducerMetadata getConsumerProducerMetadata() {
		return consumerProducerMetadata;
	}

	@Override
	public void setConsumerProducerMetadata(
			ConsumerProducerMetadata consumerProducerMetadata) {
		this.consumerProducerMetadata = consumerProducerMetadata;
		
	}
	
}
