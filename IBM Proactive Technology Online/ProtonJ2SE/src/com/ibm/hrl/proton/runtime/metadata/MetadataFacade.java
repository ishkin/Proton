package com.ibm.hrl.proton.runtime.metadata;

import java.io.Serializable;

public class MetadataFacade implements Serializable, IMetadataFacade{

	private ContextMetadataFacade contextMetadataFacade;
	private EPAManagerMetadataFacade epaManagerMetadataFacade;
	private EventMetadataFacade eventMetadataFacade;
	private RoutingMetadataFacade routingMetadataFacade;
	
	public MetadataFacade(){
		
	}

	public ContextMetadataFacade getContextMetadataFacade() {
		return contextMetadataFacade;
	}

	public void setContextMetadataFacade(ContextMetadataFacade contextMetadataFacade) {
		this.contextMetadataFacade = contextMetadataFacade;
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
	}
	
	
	
}
