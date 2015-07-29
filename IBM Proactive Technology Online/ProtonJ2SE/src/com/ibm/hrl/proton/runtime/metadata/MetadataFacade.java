package com.ibm.hrl.proton.runtime.metadata;

import java.io.Serializable;

import com.ibm.hrl.proton.metadata.inout.ConsumerProducerMetadata;

public class MetadataFacade implements Serializable, IMetadataFacade{

	private ContextMetadataFacade contextMetadataFacade;
	private EPAManagerMetadataFacade epaManagerMetadataFacade;
	private EventMetadataFacade eventMetadataFacade;
	private RoutingMetadataFacade routingMetadataFacade;
	private ConsumerProducerMetadata consumerProducerMetadata;
		

	public MetadataFacade(){
		
	}

	public ContextMetadataFacade getContextMetadataFacade() {
		return contextMetadataFacade;
	}

	public void setContextMetadataFacade(ContextMetadataFacade contextMetadataFacade) {
		this.contextMetadataFacade = contextMetadataFacade;
	}
	
	public ConsumerProducerMetadata getConsumerProducerMetadata() {
		return consumerProducerMetadata;
	}

	public void setConsumerProducerMetadata(
			ConsumerProducerMetadata consumerProducerMetadata) {
		this.consumerProducerMetadata = consumerProducerMetadata;
	}

	public EPAManagerMetadataFacade getEpaManagerMetadataFacade() {
		return epaManagerMetadataFacade;
	}

	public void setEpaManagerMetadataFacade(
			EPAManagerMetadataFacade epaManagerMetadataFacade) {
		this.epaManagerMetadataFacade = epaManagerMetadataFacade;
	}

	public EventMetadataFacade getEventMetadataFacade() {
		return eventMetadataFacade;
	}

	public void setEventMetadataFacade(EventMetadataFacade eventMetadataFacade) {
		this.eventMetadataFacade = eventMetadataFacade;
	}

	public RoutingMetadataFacade getRoutingMetadataFacade() {
		return routingMetadataFacade;
	}

	public void setRoutingMetadataFacade(RoutingMetadataFacade routingMetadataFacade) {
		this.routingMetadataFacade = routingMetadataFacade;
	}
	
	public void clear(){
		if (this.contextMetadataFacade != null) this.contextMetadataFacade.clear();
		if (this.contextMetadataFacade != null) this.routingMetadataFacade.clear();
		if (this.contextMetadataFacade != null) this.epaManagerMetadataFacade.clear();
		if (this.contextMetadataFacade != null) this.eventMetadataFacade.clear();
		if (this.consumerProducerMetadata != null) this.consumerProducerMetadata.clear();
	}
	
	
	
}
