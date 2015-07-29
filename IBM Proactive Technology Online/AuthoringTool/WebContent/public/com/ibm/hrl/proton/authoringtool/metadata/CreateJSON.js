/******************************************************************************
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
 dojo.provide("metadata.CreateJSON");
dojo.require("metadata.EPN");
dojo.require("metadata.BaseDefinition");
dojo.require("metadata.Event");
dojo.require("metadata.ParseError");
dojo.require("metadata.EPA");
dojo.require("metadata.TemporalContext");
dojo.require("metadata.SegmentationContext");
dojo.require("metadata.CompositeContext");
dojo
		.declare(
				"metadata.CreateJSON",
				null,
				{

					constructor : function() {
						this.epn = {};
						this.epn.events = [];
						this.epn.epas = [];
						this.epn.contexts = {};
						this.epn.contexts.temporal = [];
						this.epn.contexts.segmentation = [];
						this.epn.contexts.composite = [];
						this.epn.consumers = [];
						this.epn.producers = [];
						this.epn.name = ATVars.MY_EPN.getEPNName();
					},

					setEPNName : function(name) {
						this.epn.name = name;
					},

					StartCreate : function() {						
						this.epn.name = ATVars.MY_EPN.getEPNName();

						var eventList = ATVars.MY_EPN.getEventList();
						if (eventList.length > 0) {
							for (i = 0, l = eventList.length; i < l; i++) {
								if (this.hasValue(eventList[i])) {
									this.createJsonEvent(eventList[i]);
								}
							}
						}
						var epaList = ATVars.MY_EPN.getEpaList();
						if (epaList.length > 0) {
							for (i = 0, l = epaList.length; i < l; i++) {
								if (this.hasValue(epaList[i])) {
									this.createJsonEPA(epaList[i]);
								}
							}
						}
						var temporalList = ATVars.MY_EPN
								.getTemporalContextList();
						if (temporalList.length > 0) {
							for (i = 0, l = temporalList.length; i < l; i++) {
								if (this.hasValue(temporalList[i])) {
									this.createJsonTemporal(temporalList[i]);
								}
							}
						}
						var segmentationList = ATVars.MY_EPN
								.getSegmentationContextList();
						if (segmentationList.length > 0) {
							for (i = 0, l = segmentationList.length; i < l; i++) {
								if (this.hasValue(segmentationList[i])) {
									this
											.createJsonSegmentation(segmentationList[i]);
								}
							}
						}
						var compositeList = ATVars.MY_EPN
								.getCompositeContextList();
						if (compositeList.length > 0) {
							for (i = 0, l = compositeList.length; i < l; i++) {
								if (this.hasValue(compositeList[i])) {
									this.createJsonComposite(compositeList[i]);
								}
							}
						}
						var consumerList = ATVars.MY_EPN.getConsumerList();
						if (consumerList.length > 0) {
							for (i = 0, l = consumerList.length; i < l; i++) {
								if (this.hasValue(consumerList[i])) {
									this.createJsonConsumer(consumerList[i]);
								}
							}
						}
						var producerList = ATVars.MY_EPN.getProducerList();
						if (producerList.length > 0) {
							for (i = 0, l = producerList.length; i < l; i++) {
								if (this.hasValue(producerList[i])) {
									this.createJsonProducer(producerList[i]);
								}
							}
						}
						// alert("Finish To Save");
						var templateList = ATVars.MY_EPN.getTemplateList();
						if (templateList.length > 0) {
							for (i = 0, l = templateList.length; i < l; i++) {
								if (this.hasValue(templateList[i])) {
									this.pushTemplateDefinitions(templateList[i]);
								}
							}
						}
					},
					
					pushTemplateDefinitions : function(templateName) {
						//the template UI object
						var tTemplate = ATVars.MY_EPN.getTemplate(templateName);
						//the matching template description
						var templateTypeEnum = tTemplate.getType();						
						var templateType = TemplateEnum.Templates[templateTypeEnum];
						var templateTypeString = JSON.stringify(templateType);
						//TODO: future - get the template name and add the template name to all constructs names					
						var i, l;
						for (i = 0, l = tTemplate.getNumberOfProperties(); i < l; i++) {							
							var propertyName = tTemplate.getPropertyName(i);
							var propertyValue = tTemplate.getPropertyValue(i);
							templateTypeString = templateTypeString.replace(new RegExp(this.escapeRegExp(propertyName), 'g'),propertyValue);
							
						}
						
						//go over all the constructs in the JSON, and change their names
						//to be constructed from object name+template name
						var parsedJSON = JSON.parse(templateTypeString);
						var epas = parsedJSON.epas;
						for(var i = 0; i < epas.length; i++) {
						    var obj = epas[i];						    						 
						    var oldName = obj.name;
						    templateTypeString = templateTypeString.replace(new RegExp(this.escapeRegExp(oldName), 'g'),oldName+templateName);
						}
						var parsedJSON = JSON.parse(templateTypeString);
						var contexts = parsedJSON.contexts;
						if (typeof contexts !== 'undefined'){
							var temporalContexts = contexts.temporal;
							var segmentationContexts = contexts.segmentation;
							var compositeContexts = contexts.composite;
							
							if(typeof temporalContexts !== 'undefined')
							{
								for(var i = 0; i < temporalContexts.length; i++) 
								{
								    var obj = temporalContexts[i];
								    var oldName = obj.name;
								    templateTypeString = templateTypeString.replace(new RegExp(this.escapeRegExp(oldName), 'g'),oldName+templateName);
								}
							}
							
							if(typeof segmentationContexts !== 'undefined')
							{
								for(var i = 0; i < segmentationContexts.length; i++) 
								{
								    var obj = segmentationContexts[i];
								    var oldName = obj.name;
								    templateTypeString = templateTypeString.replace(new RegExp(this.escapeRegExp(oldName), 'g'),oldName+templateName);
								}
							}
							
							if(typeof compositeContexts !== 'undefined')
							{
								for(var i = 0; i < compositeContexts.length; i++) 
								{
								    var obj = compositeContexts[i];
								    var oldName = obj.name;
								    templateTypeString = templateTypeString.replace(new RegExp(this.escapeRegExp(oldName), 'g'),oldName+templateName);
								}
							}
							
						}
						
						//parse the JSON, push the subjsons into appropriate JSON objects
						var newParsedJSON = JSON.parse(templateTypeString);
						var epas = newParsedJSON.epas;
						for(var i = 0; i < epas.length; i++) {
						    var obj = epas[i];						    
						    this.epn.epas.push(obj);						    
						}
						var contexts = newParsedJSON.contexts;
											
						if (typeof contexts !== 'undefined'){
							var temporalContexts = contexts.temporal;
							var segmentationContexts = contexts.segmentation;
							var compositeContexts = contexts.composite;
							
							if(typeof temporalContexts !== 'undefined')
							{
								for(var i = 0; i < temporalContexts.length; i++) {
								    var obj = temporalContexts[i];
								    this.epn.contexts.temporal.push(obj);						    
								}
							}
							
							if(typeof segmentationContexts !== 'undefined')
							{
								for(var i = 0; i < segmentationContexts.length; i++) {
								    var obj = segmentationContexts[i];
								    this.epn.contexts.segmentation.push(obj);						    
								}
							}
							
							if(typeof compositeContexts !== 'undefined')
							{
								for(var i = 0; i < compositeContexts.length; i++) {
								    var obj = compositeContexts[i];
								    this.epn.contexts.composite.push(obj);						    
								}
							}
						}
						
						
					},
					
					escapeRegExp : function (string) {
					    return string.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
					},

					createJsonEvent : function(eventName) {
						var tEvent = ATVars.MY_EPN.getEvent(eventName);
						var jEvent = {};
						this.getGeneralAtt(tEvent, jEvent);
						jEvent.attributes = [];
						var i, l;
						for (i = 0, l = tEvent.getNumberOfAttributes(); i < l; i++) {
							jEvent.attributes[i] = {};
							value = tEvent.getAttributeName(i);
							if (this.hasValue(value)) {
								jEvent.attributes[i].name = value;
							}
							;
							value = tEvent.getAttributeType(i);
							if (this.hasValue(value)) {
								jEvent.attributes[i].type = value;
							}
							;
							value = tEvent.getAttributeDefaultValue(i);
							if (this.hasValue(value)) {
								jEvent.attributes[i].defaultValue = value;
							}
							;
							value = tEvent.getAttributeDimension(i);
							if (this.hasValue(value)) {
								jEvent.attributes[i].dimension = value;
							}
							;
							value = tEvent.getAttributeDescription(i);
							if (this.hasValue(value)) {
								jEvent.attributes[i].description = value;
							}
							;
						}
						this.epn.events.push(jEvent);
					},

					createJsonEPA : function(epaName) {
						var tEpa = ATVars.MY_EPN.getEPA(epaName);
						var jEpa = {};
						this.getGeneralAtt(tEpa, jEpa);
						value = tEpa.getEPAType();
						if (this.hasValue(value)) {
							jEpa.epaType = value;
						}
						;
						value = tEpa.getContext();
						if (this.hasValue(value)) {
							jEpa.context = value;
						}
						;
						jEpa.inputEvents = [];
						var i, l;
						for (i = 0, l = tEpa.getNumberOfInputEvents(); i < l; i++) {
							jEpa.inputEvents[i] = {};
							value = tEpa.getInputEventName(i);
							if (this.hasValue(value)) {
								jEpa.inputEvents[i].name = value;
							}
							;
							value = tEpa.getInputEventAlias(i);
							if (this.hasValue(value)) {
								jEpa.inputEvents[i].alias = value;
							}
							;
							value = tEpa.getInputEventFilterExpression(i);
							if (this.hasValue(value)) {
								jEpa.inputEvents[i].filterExpression = value;
							}
							;
							value = tEpa.getInputEventRelativeNExpression(i);
							if (this.hasValue(value)) {
								jEpa.inputEvents[i].expression = value;
							}
							
							value = tEpa.getInputEventTrendExpression(i);
							if (this.hasValue(value)) {
								jEpa.inputEvents[i].expression = value;
							}
							
							;
							value = tEpa.getInputEventConsumptionPolicy(i);
							if (this.hasValue(value)) {
								jEpa.inputEvents[i].consumptionPolicy = value;
							}
							;
							value = tEpa
									.getInputEventInstanceSelectionPolicy(i);
							if (this.hasValue(value)) {
								jEpa.inputEvents[i].instanceSelectionPolicy = value;
							}
							;
						}
						jEpa.computedVariables = [];
						var cvi, cvl;
						for (cvi = 0, cvl = tEpa.getNumberOfComputedVariables(); cvi < cvl; cvi++) {
							jEpa.computedVariables[cvi] = {};
							value = tEpa.getComputedVarName(cvi);
							if (this.hasValue(value)) {
								jEpa.computedVariables[cvi].name = value;
							}
							;
							value = tEpa.getComputedVarAggregationType(cvi);
							if (this.hasValue(value)) {
								jEpa.computedVariables[cvi].aggregationType = value;
							}
							;
							for (i = 0; i < l; i++) {
								var eventAliasName = tEpa
										.getAliasOrNameOfInputEvent(i);
								value = tEpa.getComputedVarExpression(
										eventAliasName, cvi);
								if (this.hasValue(value)) {
									jEpa.computedVariables[cvi][eventAliasName] = value;
								}
								;
							}
							;
						}
						value = tEpa.getAssertion();
						if (this.hasValue(value)) {
							jEpa.assertion = value;
						}
						;
						value = tEpa.getEvaluation();
						if (this.hasValue(value)) {
							jEpa.evaluationPolicy = value;
						}
						;
						value = tEpa.getCardinality();
						if (this.hasValue(value)) {
							jEpa.cardinalityPolicy = value;
						}
						;
						value = tEpa.getN();
						if (this.hasValue(value)) {
							jEpa.N = value;
						}
						
						value = tEpa.getTrendN();
						if (this.hasValue(value)) {
							jEpa.trendN = value;
						}
						;
						value = tEpa.getRanking();
						if (this.hasValue(value)) {
							jEpa.rankingRelation = value;
						}
						;
						value = tEpa.getTrendRelation();
						if (this.hasValue(value)) {
							jEpa.trendRelation = value;
						}
						;
						jEpa.internalSegmentation = [];
						var x, y;
						for (x = 0, y = tEpa.getNumberOfInternalSegmentations(); x < y; x++) {
							jEpa.internalSegmentation[x] = {};
							value = tEpa.getInternalSegmentationName(x);
							if (this.hasValue(value)) {
								jEpa.internalSegmentation[x].name = tEpa
										.getInternalSegmentationName(x);
							}
							;
						}
						jEpa.derivedEvents = [];
						var a, b;
						for (a = 0, b = tEpa.getNumberOfDerivedEvents(); a < b; a++) {
							jEpa.derivedEvents[a] = {};
							value = tEpa.getDerivedEventName(a);
							if (this.hasValue(value)) {
								jEpa.derivedEvents[a].name = value;
							}
							;
							value = tEpa.getDerivedEventCondition(a);
							if (this.hasValue(value)) {
								jEpa.derivedEvents[a].condition = value;
							}
							;
							value = tEpa.isReportParticipants(a);
							if (this.hasValue(value)) {
								jEpa.derivedEvents[a].reportParticipants = value;
							}
							;
							jEpa.derivedEvents[a].expressions = {};
							var e = ATVars.MY_EPN
									.getEvent(jEpa.derivedEvents[a].name);
							var ei, el;
							for (ei = 0, el = e.getNumberOfAttributes(); ei < el; ei++) {
								var ea = e.getAttributeName(ei);
								if (tEpa.getDerivedEventExpression(a, ei)) {
									value = tEpa.getDerivedEventExpression(a,
											ei);
									if (this.hasValue(value)) {
										jEpa.derivedEvents[a].expressions[ea] = value;
									}
									;
								}
							}
						}
						
						this.epn.epas.push(jEpa);
					},

					createJsonTemporal : function(temporalName) {
						var tTemporal = ATVars.MY_EPN
								.getTemporalContext(temporalName);
						var jTemporal = {};
						var value;
						this.getGeneralAtt(tTemporal, jTemporal);
						value = tTemporal.getType();
						if (this.hasValue(value)) {
							jTemporal.type = value;
						}
						;
						value = tTemporal.getAtStartup();
						if (this.hasValue(value)) {
							jTemporal.atStartup = value;
						}
						value = tTemporal.getNeverEnding();
						if (this.hasValue(value)) {
							jTemporal.neverEnding = value;
						}
						value = tTemporal.getDuration();
						if (this.hasValue(value)) {
							jTemporal.duration = value;
						}
						value = tTemporal.getSlidingPeriod();
						if (this.hasValue(value)) {
							jTemporal.slidingPeriod = value;
						}

						jTemporal.initiators = [];
						var i, l;
						for (i = 0, l = tTemporal.getNumberOfEventInitiators(); i < l; i++) {
							jTemporal.initiators[i] = {};
							value = tTemporal.getEventInitiatorType(i);
							if (this.hasValue(value)) {
								jTemporal.initiators[i].initiatorType = value;
							}
							;
							value = tTemporal.getEventInitiatorPolicy(i);
							if (this.hasValue(value)) {
								jTemporal.initiators[i].initiatorPolicy = value;
							}
							;
							value = tTemporal.getEventInitiatorName(i);
							if (this.hasValue(value)) {
								jTemporal.initiators[i].name = value;
							}
							;
							value = tTemporal.getEventInitiatorCondition(i);
							if (this.hasValue(value)) {
								jTemporal.initiators[i].condition = value;
							}
							;
						}
						var x, y;
						for (x = 0, y = tTemporal
								.getNumberOfAbsoluteTimeInitiators(); x < y; x++) {
							jTemporal.initiators[x + i] = {};
							value = tTemporal.getAbsoluteTimeInitiatorType(x);
							if (this.hasValue(value)) {
								jTemporal.initiators[x + i].initiatorType = value;
							}
							;
							value = tTemporal.getAbsoluteTimeInitiatorPolicy(x);
							if (this.hasValue(value)) {
								jTemporal.initiators[x + i].initiatorPolicy = value;
							}
							;
							value = tTemporal
									.getAbsoluteTimeInitiatorTimestamp(x);
							if (this.hasValue(value)) {
								jTemporal.initiators[x + i].timeStamp = value;
							}
							;
							value = tTemporal
									.getAbsoluteTimeInitiatorRepeatingInterval(x);
							if (this.hasValue(value)) {
								jTemporal.initiators[x + i].repeatingInterval = value;
							}
							;
						}
						jTemporal.terminators = [];
						if (jTemporal.neverEnding == false) {
							var i, l;
							for (i = 0, l = tTemporal
									.getNumberOfEventTerminators(); i < l; i++) {
								jTemporal.terminators[i] = {};
								jTemporal.terminators[i].terminatorType = ATEnum.TerminatorType.Event;
								value = tTemporal
										.getEventTerminatorQuantifierPolicy(i);
								if (this.hasValue(value)) {
									jTemporal.terminators[i].terminatorPolicy = value;
								}
								;
								value = tTemporal.getEventTerminatorType(i);
								if (this.hasValue(value)) {
									jTemporal.terminators[i].terminationType = value;
								}
								;
								value = tTemporal.getEventTerminatorName(i);
								if (this.hasValue(value)) {
									jTemporal.terminators[i].name = value;
								}
								;
								value = tTemporal
										.getEventTerminatorCondition(i);
								if (this.hasValue(value)) {
									jTemporal.terminators[i].condition = value;
								}
								;
							}
							var x, y;
							for (x = 0, y = tTemporal
									.getNumberOfAbsoluteTerminators(); x < y; x++) {
								jTemporal.terminators[x + i] = {};
								jTemporal.terminators[x + i].terminatorType = ATEnum.TerminatorType.AbsoluteTime;
								value = tTemporal
										.getAbsoluteTerminatorQuantifierPolicy(x);
								if (this.hasValue(value)) {
									jTemporal.terminators[x + i].terminatorPolicy = value;
								}
								;
								value = tTemporal.getAbsoluteTerminatorType(x);
								if (this.hasValue(value)) {
									jTemporal.terminators[x + i].terminationType = value;
								}
								;
								value = tTemporal
										.getAbsoluteTerminatorTimestamp(x);
								if (this.hasValue(value)) {
									jTemporal.terminators[x + i].timeStamp = value;
								}
								;
							}
							if (tTemporal.isRelativeTerminator()) {
								jTemporal.terminators[x + i] = {};
								jTemporal.terminators[x + i].terminatorType = ATEnum.TerminatorType.RelativeTime;
								value = tTemporal.getRelativeTerminatorType();
								if (this.hasValue(value)) {
									jTemporal.terminators[x + i].terminationType = value;
								}
								;
								value = tTemporal
										.getRelativeTerminatorRelativeTime();
								if (this.hasValue(value)) {
									jTemporal.terminators[x + i].relativeTime = value;
								}
								;
							}
						}
						this.epn.contexts.temporal.push(jTemporal);

					},
					createJsonSegmentation : function(segmentationName) {
						var tSegmentation = ATVars.MY_EPN
								.getSegmentationContext(segmentationName);
						var jSegmentation = {};
						this.getGeneralAtt(tSegmentation, jSegmentation);
						// jSegmentation.type=tSegmentation.getType();
						jSegmentation.participantEvents = [];
						var i, l;
						for (i = 0, l = tSegmentation
								.getNumberOfSegmentationEvents(); i < l; i++) {
							jSegmentation.participantEvents[i] = {};
							value = tSegmentation.getSegmentationEvent(i);
							if (this.hasValue(value)) {
								jSegmentation.participantEvents[i].name = value;
							}
							;
							value = tSegmentation.getSegmentationExpression(i);
							if (this.hasValue(value)) {
								jSegmentation.participantEvents[i].expression = value;
							}
							;
						}
						this.epn.contexts.segmentation.push(jSegmentation);

					},
					createJsonComposite : function(compositeName) {
						var tComposite = ATVars.MY_EPN
								.getCompositeContext(compositeName);
						var jComposite = {};
						this.getGeneralAtt(tComposite, jComposite);
						jComposite.temporalContexts = [];
						var i, l;
						for (i = 0, l = tComposite
								.getNumberOfTemporalContexts(); i < l; i++) {
							jComposite.temporalContexts[i] = {};
							value = tComposite.getTemporalContext(i);
							if (this.hasValue(value)) {
								jComposite.temporalContexts[i].name = value;
							}
							;
						}
						jComposite.segmentationContexts = [];
						var i, l;
						for (i = 0, l = tComposite
								.getNumberOfSegmentationContexts(); i < l; i++) {
							jComposite.segmentationContexts[i] = {};
							value = tComposite.getSegmentationContext(i);
							if (this.hasValue(value)) {
								jComposite.segmentationContexts[i].name = value;
							}
							;
						}
						this.epn.contexts.composite.push(jComposite);
					},

					createJsonConsumer : function(consumerName) {
						var tConsumer = ATVars.MY_EPN.getConsumer(consumerName);
						var jConsumer = {};
						this.getGeneralAtt(tConsumer, jConsumer);
						value = tConsumer.getType();
						if (this.hasValue(value)) {
							jConsumer.type = value;
						}
						;
						value = tConsumer.getDescription();
						if (this.hasValue(value)) {
							jConsumer.description = value;
						}
						;
						jConsumer.properties = [];
						var i, l;
						for (i = 0, l = tConsumer.getNumberOfProperties(); i < l; i++) {
							jConsumer.properties[i] = {};
							value = tConsumer.getPropertyName(i);
							if (this.hasValue(value)) {
								jConsumer.properties[i].name = value;
							}
							;
							value = tConsumer.getPropertyValue(i);
							if (this.hasValue(value)) {
								jConsumer.properties[i].value = value;
							}
							;
						}
						jConsumer.events = [];
						var i, l;
						for (i = 0, l = tConsumer.getNumberOfEvents(); i < l; i++) {
							jConsumer.events[i] = {};
							value = tConsumer.getEventName(i);
							if (this.hasValue(value)) {
								jConsumer.events[i].name = value;
							}
							;
							value = tConsumer.getEventCondition(i);
							if (this.hasValue(value)) {
								jConsumer.events[i].condition = value;
							}
							;
						}

						this.epn.consumers.push(jConsumer);
					},

					createJsonProducer : function(produserName) {
						var tProducer = ATVars.MY_EPN.getProducer(produserName);
						var jProducer = {};
						this.getGeneralAtt(tProducer, jProducer);
						value = tProducer.getType();
						if (this.hasValue(value)) {
							jProducer.type = value;
						}
						;
						value = tProducer.getDescription();
						if (this.hasValue(value)) {
							tProducer.description = value;
						}
						;
						jProducer.properties = [];
						var i, l;
						for (i = 0, l = tProducer.getNumberOfProperties(); i < l; i++) {
							jProducer.properties[i] = {};
							value = tProducer.getPropertyName(i);
							if (this.hasValue(value)) {
								jProducer.properties[i].name = value;
							}
							;
							value = tProducer.getPropertyValue(i);
							if (this.hasValue(value)) {
								jProducer.properties[i].value = value;
							}
							;
						}
						jProducer.events = [];
						var i, l;
						for (i = 0, l = tProducer.getNumberOfEvents(); i < l; i++) {
							jProducer.events[i] = {};
							value = tProducer.getEventName(i);
							if (this.hasValue(value)) {
								jProducer.events[i].name = value;
							}
							;
							value = tProducer.getEventCondition(i);
							if (this.hasValue(value)) {
								jProducer.events[i].condition = value;
							}
							;
						}

						this.epn.producers.push(jProducer);
					},


					getGeneralAtt : function(epnObj, jObj) {
						value = epnObj.getName();
						if (this.hasValue(value)) {
							jObj.name = value;
						}
						;
						value = epnObj.getDescription();
						if (this.hasValue(value)) {
							jObj.description = value;
						}
						;
						value = epnObj.getCreatedDate();
						if (this.hasValue(value)) {
							jObj.createdDate = value;
						}
						;
						value = epnObj.getCreatedBy();
						if (this.hasValue(value)) {
							jObj.createdBy = value;
						}
						;
					},

					hasValue : function(value) {
						if (value === null || value === undefined
								|| value === "") {
							return false;
						}
						return true;
					},

					assignValue : function(value, target) {
						if (this.hasValue(value)) {
							target = assignValue;
						}
					}

				});
