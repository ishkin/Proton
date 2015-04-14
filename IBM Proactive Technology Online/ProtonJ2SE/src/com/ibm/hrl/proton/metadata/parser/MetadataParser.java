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
package com.ibm.hrl.proton.metadata.parser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import json.java.JSON;
import json.java.JSONArray;
import json.java.JSONObject;

import com.ibm.eep.exceptions.ParseException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.TypePredicatePair;
import com.ibm.hrl.proton.metadata.computedVariable.ComputedVariableType;
import com.ibm.hrl.proton.metadata.computedVariable.IComputedVariableType;
import com.ibm.hrl.proton.metadata.context.CompositeContextType;
import com.ibm.hrl.proton.metadata.context.enums.ContextTypeEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;
import com.ibm.hrl.proton.metadata.context.interfaces.ISegmentationContextType;
import com.ibm.hrl.proton.metadata.epa.EventProcessingAgentType;
import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.StatefulEventProcesingAgentType;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObjectMeta;
import com.ibm.hrl.proton.metadata.epa.enums.CardinalityPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.EPATypeEnum;
import com.ibm.hrl.proton.metadata.epa.enums.EvaluationPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.MultiDerivationPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IDerivationSchema;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.metadata.epa.interfaces.IFilteringSchema;
import com.ibm.hrl.proton.metadata.epa.interfaces.IMatchingSchema;
import com.ibm.hrl.proton.metadata.epa.schemas.AggregationSchema;
import com.ibm.hrl.proton.metadata.epa.schemas.DerivationSchema;
import com.ibm.hrl.proton.metadata.epa.schemas.FilteringSchema;
import com.ibm.hrl.proton.metadata.epa.schemas.StandardMatchingSchema;
import com.ibm.hrl.proton.metadata.epa.schemas.TrendMatchingSchema;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.inout.BaseConsumerMetadata.ConsumerType;
import com.ibm.hrl.proton.metadata.inout.ConsumerMetadata;
import com.ibm.hrl.proton.metadata.inout.ConsumerProducerMetadata;
import com.ibm.hrl.proton.metadata.inout.ProducerMetadata;
import com.ibm.hrl.proton.metadata.inout.ProducerMetadata.ProducerType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.metadata.type.interfaces.IBasicType;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.metadata.ContextMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.EPAManagerMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.RoutingMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.epa.AgentQueueMetadata;
import com.ibm.hrl.proton.runtime.metadata.epa.AgentQueueMetadata.OrderingPolicy;
import com.ibm.hrl.proton.runtime.metadata.epa.AgentQueueMetadata.SortingPolicy;
import com.ibm.hrl.proton.utilities.containers.Pair;

public class MetadataParser extends BaseMetadataParser{

	public MetadataParser(EepFacade eep) {
		super(eep);
	}

	

	@Override
	public Collection<ProtonParseException> parseEPN(String jsonTxt)
	throws ParsingException {
		try {
			JSONObject json = (JSONObject) JSON.parse(jsonTxt);
			// get the root object
			JSONObject root = (JSONObject) json.get("epn");

			if (root == null) {
				throw new ParsingException("Incorrect JSON structure");
			}

			JSONArray eventsArray = (JSONArray) root.get("events");
			JSONArray actionsArray = (JSONArray) root.get("actions");
			JSONArray epasArray = (JSONArray) root.get("epas");
			JSONObject contexts = (JSONObject) root.get("contexts");
			JSONArray consumers = (JSONArray) root.get("consumers");
			JSONArray producers = (JSONArray) root.get("producers");


			
			parseEvents(eventsArray);			
			parseConsumerProducer(consumers, producers);
			parseContexts(contexts);
			parseEPAs(epasArray);

			

			ContextMetadataFacade.getInstance().setContextAgents(contextAgents);

			return exceptions;
		} catch (IOException e) {
			throw new ParsingException(e.getMessage());
		}catch(ParseException e){
			throw new ParsingException(e.getMessage());
		}

	}
	
	
	
	

	protected void parseEPAs(JSONArray epasArray) throws ParseException {

		//TODO: validation : the context name is not absolute must - we can do without the context for pra. 
		//the derived events are also not a must - we have to have either derived events (for regular epas) or actions (for pras)
		
		
		Iterator<JSONObject> iter = epasArray.iterator();
		Map<String, IEventProcessingAgent> agentDefs = new HashMap<String, IEventProcessingAgent>();
		Map<String, AgentQueueMetadata> agentChannelInfo = new HashMap<String, AgentQueueMetadata>();
		Map<String, Set<Pair<String, String>>> eventsRoutingInfo = new HashMap<String, Set<Pair<String, String>>>();
		while (iter.hasNext()) {
			try {
				JSONObject epa = iter.next();
				// must exist and be unique
				String epaName = tryException(new NullCheckerAndRepeats<String>(agentDefs.keySet(), "name", epa,
						"epa name", DefinitionType.EPA, ErrorElement.NAME, stringParser, true));
				// must belong to enum
				EPATypeEnum epaType = tryException(new EPATypeEnumCheck("epaType", epa, epaName,
						DefinitionType.EPA, ErrorElement.TYPE));
				// must be defined
				String contextName = tryException(new NullChecker<String>("context", epa, epaName,
							DefinitionType.EPA, ErrorElement.CONTEXT_NAME, stringParser));
				
				
				// get the context
				IContextType contextType = ContextMetadataFacade.getInstance().getContext(contextName);
				// must be defined
				checkElementDefined(contextType, epaName, DefinitionType.EPA, ErrorElement.CONTEXT_NAME,
						ProtonParseException.DEFAULT_INDEX, contextName);
				//if the context has segmentation contexts, need to add them to agent context map
				Set<TypeAttribute> segmentationContextsSet = new HashSet<TypeAttribute>();
				ContextTypeEnum contextTypeEnum = contextType.getType();				
				if (contextTypeEnum.equals(ContextTypeEnum.SEGEMENTATION))
				{
					segmentationContextsSet.add(new TypeAttribute(contextType.getName(), AttributeTypesEnum.STRING));
				}
				if (contextTypeEnum.equals(ContextTypeEnum.COMPOSITE))
				{
					List<IContextType> memberContexts = ((CompositeContextType)contextType).getMemberContexts();
					for (IContextType memberContext : memberContexts) {
						if (memberContext.getType().equals(ContextTypeEnum.SEGEMENTATION))
						{
							segmentationContextsSet.add(new TypeAttribute(memberContext.getName(), AttributeTypesEnum.STRING));
						}
					}
				}
				
				// warning on null / empty
				JSONArray inputEvents = tryException(new WarningNullChecker<JSONArray>("inputEvents", epa,
						epaName, DefinitionType.EPA, ErrorElement.INPUT_EVENTS, jsonArrayParser));
				generateWarningOnEmpty(inputEvents, epaName, DefinitionType.EPA, ErrorElement.INPUT_EVENTS);
				
				AgentQueueMetadata agentMeta = new AgentQueueMetadata(epaName,
						OrderingPolicy.DETECTION_TIME, DEFAULT_BUFFERING_TIME, SortingPolicy.NON_SORTED);
				agentChannelInfo.put(epaName, agentMeta);

				// parse input events list and filtering schema defined within
				// it
				ArrayList<IEventType> inputEventsList = new ArrayList<IEventType>();
				List<String> aliases = new ArrayList<String>();
				HashMap<Operand, String> filterStringExpressions = new HashMap<Operand, String>();
				HashMap<Operand, IExpression> filterExpressions = new HashMap<Operand, IExpression>();
				IMatchingSchema matchingSchema = determineMatchingSchema(epaType);

				if (inputEvents == null) {
					// to avoid throwing null exception
					inputEvents = new JSONArray();
				}
				int i = 0;
				// set (in addition to list) for quicker lookup
				Set<String> inputEventAliases = new HashSet<String>();
				Map<String,Operand> operands = new HashMap<String,Operand>();
				for (int rowNumber = 0; rowNumber < inputEvents.size(); rowNumber++) {
					try {
						JSONObject inputEvent = (JSONObject)inputEvents.get(rowNumber);
						// must be defined
						String inputEventName = tryException(new NullChecker<String>("name", inputEvent, epaName,
								DefinitionType.EPA, ErrorElement.INPUT_EVENT_NAME, stringParser, rowNumber));
						IEventType inputEventType = EventMetadataFacade.getInstance().getEventType(
								inputEventName);
						// must be defined
						checkElementDefined(inputEventType, epaName, DefinitionType.EPA,
								ErrorElement.INPUT_EVENT_NAME, rowNumber, inputEventName);
						inputEventsList.add(inputEventType);
						String aliasName = (String)inputEvent.get("alias");
						if (aliasName == null)
							aliasName = inputEventName;
						aliases.add(aliasName);
						
						// check alias repeat / correctness
						if (inputEventAliases.contains(aliasName)) {
							ParseErrorEnum errorEnum;
							if (aliasName.equals(inputEventName)) {
								errorEnum = ParseErrorEnum.MISSING_ALIAS;
							} else {
								errorEnum = ParseErrorEnum.REPEATING_ALIAS;
							}
							exceptions.add(new ProtonParseException(errorEnum, epaName,
									DefinitionType.EPA, ErrorType.ERROR, ErrorElement.INPUT_EVENT_NAME,
									rowNumber));
							throw new MetadataParseException("Bad alias");
						}
						// add the event information to routing info
						Set<Pair<String, String>> eventRouting = null;
						eventRouting = eventsRoutingInfo.get(inputEventName);
						if (eventRouting == null) {
							eventRouting = new HashSet<Pair<String, String>>();
							eventsRoutingInfo.put(inputEventName, eventRouting);
						}
						eventRouting.add(new Pair<String, String>(epaName, contextName));

						String filterExpression = (String)inputEvent.get("filterExpression");
						Operand operand = new Operand(i, inputEventType);
						operands.put(aliasName, operand);
						i++;

						// create the eep expression and add to filtering schema
						try {
							filterStringExpressions.put(operand, filterExpression);
							IExpression filterParsedExpression = null;
							if (filterExpression != null) {
								ArrayList<IDataObjectMeta> signature = new ArrayList<IDataObjectMeta>();
								signature.add(inputEventType);
								ArrayList<String> signatureAliases = new ArrayList<String>();
								signatureAliases.add(aliasName);
								// must be parsed by eep
								filterParsedExpression = eep.createExpression(filterExpression, signature,
										signatureAliases);
							}

							filterExpressions.put(operand, filterParsedExpression);
							addOperandInformationToMatchingSchema(matchingSchema, epaType, inputEvent, operand, aliasName,epaName,
									rowNumber);
							inputEventAliases.add(aliasName);
						} catch (MetadataParseException e) {
							continue;
						} catch (Exception e) {
							handleEEPException(epaName, DefinitionType.EPA, ErrorElement.FILTER_EXPRESSION,
									rowNumber, ProtonParseException.DEFAULT_INDEX, e);
							continue;
						}

						// according to the epa type need to parse additional
						// properties
					} catch (MetadataParseException e) {
						continue;
					}
				}
																
				IFilteringSchema filteringSchema = new FilteringSchema(filterStringExpressions, filterExpressions);

				try {
					finalizeMatchingSchema(epa,epaName,epaType,matchingSchema,operands);
				} catch (ParseException e) {
					//TODO add to the list of exceptions
					e.printStackTrace();
				}

				// parse the derived events/actions and derivation schema
				IDerivationSchema derivationSchema = new DerivationSchema();
				IComputedVariableType computedVariableType = null;
				if (epaType.equals(EPATypeEnum.AGGREGATE))
				{
					//get the computedVariable type from the aggregation schema
					computedVariableType = ((AggregationSchema)matchingSchema).getComputedVariableType();
				}else if (epaType.equals(EPATypeEnum.TREND)){
					computedVariableType = ((TrendMatchingSchema)matchingSchema).getComputedVariableType();
				}
				//Moving parsing derivations after parsing additional EPA information - for stateful agents
				//need access to local segmentation information before parsing derivations
				/*List<IBasicType> derivations  = parseDerivations(epa, epaType,inputEventsList, computedVariableType,aliases,
						derivationSchema,operands);*/				
				
				// TODO Need to check with Tali / Inna
				// parse the fairness information
				Boolean fair = false;
				Boolean isFair = (Boolean)epa.get("isFair");
				if (isFair != null)
					fair = isFair;

				// create the agent according to the type
				IEventProcessingAgent agent;
				if (epaType.equals(EPATypeEnum.BASIC)) {
					agent = new EventProcessingAgentType(UUID.randomUUID(), epaName, epaType,
							inputEventsList, filteringSchema, derivationSchema,
							contextType, fair);
				}				
				else {
					agent = new StatefulEventProcesingAgentType(UUID.randomUUID(), epaName, epaType,
							inputEventsList,  matchingSchema, filteringSchema,
							derivationSchema, contextType, fair);
					parseAdditionalEPAInformation(epa, epaType, agent, inputEventsList, aliases,segmentationContextsSet);					
				}

				List<IBasicType> derivations   = parseDerivations(agent,epa, epaType,inputEventsList, computedVariableType,aliases,
						derivationSchema,operands);
				
				// write down this agent under the specified context
				Collection<IEventProcessingAgent> agentsList = contextAgents.get(contextName);
				if (agentsList == null) {
					// a list for such a context hasn't been initialized yet
					agentsList = new ArrayList<IEventProcessingAgent>();
					contextAgents.put(contextName, agentsList);
				}
				agentsList.add(agent);
				// for this context - iterate over the list of
				// terminators/initiators, if there are events in those
				// lists add those events to the routing info of the agent
				addInitiatorsTerminatorsRoutingInfo(contextName, agent.getName(), eventsRoutingInfo);

				agentDefs.put(agent.getName(), agent);
			} catch (MetadataParseException e) {
				continue;
			}
		}

		// TODO need to add to eventsRoutingInfo also the context
		// terminators and context initiator events

		EPAManagerMetadataFacade.initializeEPAMetadata(agentDefs);
		RoutingMetadataFacade.initializeChannels(eventsRoutingInfo, agentChannelInfo, consumerEvents,
				contextAgents);
	}

	

	
	
	protected IMatchingSchema determineMatchingSchema(EPATypeEnum epaType) {
		IMatchingSchema matchingSchema = null;
		switch (epaType) {
		case ALL:
		case ABSENCE:
		case SEQUENCE:
			matchingSchema = new StandardMatchingSchema();
			break;
		case AGGREGATE:
			matchingSchema = new AggregationSchema();
			break;		
		case TREND:
			matchingSchema = new TrendMatchingSchema();
			break;
		default:
			break;
		}

		return matchingSchema;
	}
	
	protected void parseAdditionalEPAInformation(JSONObject epa, EPATypeEnum epaType, IEventProcessingAgent agent,
			ArrayList<IEventType> inputEventsList, List<String> inputEventsAliases, Set<TypeAttribute> segmentationContextsSet) throws MetadataParseException {
		String epaName = (String)epa.get("name");
		assert epaName != null;
		switch (epaType) {
		case BASIC:	
			return;
		case RELATIVE_N:
		case ABSENCE:
		case SEQUENCE:
		case AGGREGATE:
		case TREND:
		case ALL:
			//  must belong to enum
			EvaluationPolicyEnum evaluationPolicy = tryException(new EvaluationPolicyEnumCheck("evaluationPolicy",
					epa, epaName, DefinitionType.EPA, ErrorElement.EVALUATION_POLICY));
			//  must belong to enum
			CardinalityPolicyEnum cardinalityPolicy = tryException(new CardinalityPolicyEnumCheck("cardinalityPolicy",
					epa, epaName, DefinitionType.EPA, ErrorElement.CARDINALITY_POLICY));
			//  can be null
			String assertion = (String)epa.get("assertion");
			IExpression parsedExpression = null;
			if (assertion != null) {
				try {
					if (epaType.equals(EPATypeEnum.AGGREGATE))
					{
						List<IDataObjectMeta> inputList = new ArrayList<IDataObjectMeta>();
						AggregationSchema aggrMatchingSchema = (AggregationSchema)((StatefulEventProcesingAgentType)agent).getMatchingSchema();
						IComputedVariableType computedVariableType = aggrMatchingSchema.getComputedVariableType();
						inputList.add(computedVariableType);
						assertion = findAndReplace(assertion,computedVariableType);
						parsedExpression = eep.createExpression(assertion, inputList);
					}else
					{
						parsedExpression = eep.createExpression(assertion,
								(List<IDataObjectMeta>)(List<?>)inputEventsList, inputEventsAliases);
					}
					
				} catch (Exception e) {
					handleEEPException(epaName, DefinitionType.EPA, ErrorElement.ASSERTION,
							ProtonParseException.DEFAULT_INDEX, ProtonParseException.DEFAULT_INDEX, e);
				}
			}

			// parse the internal segmentation contexts and create a variable for accessing them
			JSONArray internalSegementationObject = (JSONArray)epa.get("internalSegmentation");			
			Collection<ISegmentationContextType> epaSegmentation = new ArrayList<ISegmentationContextType>();
			List<TypeAttribute> attributes = new ArrayList<TypeAttribute>();
			
			for (int rowNumber = 0; rowNumber < internalSegementationObject.size(); rowNumber++) {				
				JSONObject internalSegmentationContext = (JSONObject)internalSegementationObject.get(rowNumber);
				
				String internalSegmentationContextName = tryException(new NullChecker<String>("name", internalSegmentationContext,
						epaName, DefinitionType.EPA, ErrorElement.INTERNAL_SEGMENTATION_CONTEXT_NAME, stringParser));
				IContextType segmentationContext = ContextMetadataFacade.getInstance().getContext(
						internalSegmentationContextName);
				checkElementDefined(segmentationContext, epaName, DefinitionType.EPA,
						ErrorElement.INTERNAL_SEGMENTATION_CONTEXT_NAME, rowNumber,
						internalSegmentationContextName);
				// All EPA's input events must also exist as part of the internalSegmentationContext
				// participantEvents; warning if not
				assert ((segmentationContext != null) && (segmentationContext instanceof ISegmentationContextType));
				ISegmentationContextType segContext = (ISegmentationContextType)segmentationContext;
				for (IEventType inputEvent: inputEventsList) {
					if (segContext.getSegmentationKeys().containsKey(inputEvent) == false) {
						exceptions.add(new ProtonParseException(
								ParseErrorEnum.INPUT_EVENT_NOT_DEFINED_IN_CONTEXT_PARTICIPATING_EVENTS, epaName,
								DefinitionType.EPA, ErrorType.WARNING, ErrorElement.INTERNAL_SEGMENTATION_CONTEXT,
								rowNumber, inputEvent.getName()));
					}
				}

				epaSegmentation.add((ISegmentationContextType)segmentationContext);
				//add the name of the context to the attributes list of the type representing the segmentations contexts
				TypeAttribute attribute = new TypeAttribute(((ISegmentationContextType)segmentationContext).getName(), AttributeTypesEnum.STRING);
				attributes.add(attribute);

			}
			attributes.add(new TypeAttribute(TIME_WINDOW, AttributeTypesEnum.DOUBLE));
			attributes.addAll(segmentationContextsSet);
			IComputedVariableType agentContextInformation =  new ComputedVariableType("context",attributes);
			((StatefulEventProcesingAgentType)agent).setAssertion(assertion);
			((StatefulEventProcesingAgentType)agent).setCardinality(cardinalityPolicy);
			((StatefulEventProcesingAgentType)agent).setEvaluation(evaluationPolicy);
			((StatefulEventProcesingAgentType)agent).setParsedAssertion(parsedExpression);
			((StatefulEventProcesingAgentType)agent).setEpaSegmentation(epaSegmentation);
			((StatefulEventProcesingAgentType)agent).setEpaSegmentation(epaSegmentation);
			((StatefulEventProcesingAgentType)agent).setAgentContextInformation(agentContextInformation);
			break;
		
		default:
			break;
		}
	}
	



	/**
	 * EPA parsing - derived events and derivation schema
	 * 
	 * @param epa
	 * @param inputEventsList
	 * @return
	 * @throws ParseException
	 * @throws MetadataParseException
	 */
	protected List<IBasicType> parseDerivations(IEventProcessingAgent agent,JSONObject epa, EPATypeEnum epaType, ArrayList<IEventType> inputEventsList,IComputedVariableType computedVariableType,
			List<String> aliases, IDerivationSchema derivationSchema,Map<String,Operand> operands) throws MetadataParseException {
		
		List<IBasicType> derivationsList = new ArrayList<IBasicType>();
		String epaName = (String)epa.get("name");
		assert epaName != null;

		JSONArray derivations = tryException(new WarningNullChecker<JSONArray>("derivedEvents", epa, epaName,
					DefinitionType.EPA, ErrorElement.DERIVATIONS, jsonArrayParser));
			generateWarningOnEmpty(derivations, epaName, DefinitionType.EPA, ErrorElement.DERIVATIONS);
		

		if (derivations == null || derivations.isEmpty()) {
			return derivationsList;
		}

		// parse the derivation schema
		// Obsolete
		JSONObject compositedDerivation = (JSONObject)epa.get("compositionDefinitions");
		MultiDerivationPolicyEnum compositionPolicy = MultiDerivationPolicyEnum.MULTIPLE_EVENTS;
		IEventType composedEventType = null;
		if (compositedDerivation != null) // in case we need composite
											// derivation
		{
			Boolean composedDerivationPolicy = (Boolean)compositedDerivation.get("compositionPolicy");

			if (composedDerivationPolicy != null && composedDerivationPolicy.equals(true)) {
				compositionPolicy = MultiDerivationPolicyEnum.COMPOSITION;
			}
			composedEventType = EventMetadataFacade.getInstance().getEventType(
					(String)compositedDerivation.get("composedEvent"));
		}

		for (int tableNumber = 0; tableNumber < derivations.size(); tableNumber++) {
			try {
				JSONObject derivation = (JSONObject)derivations.get(tableNumber);
				String derivationName = tryException(new NullChecker<String>("name", derivation, epaName,
						DefinitionType.EPA, ErrorElement.DERIVATION_NAME, stringParser,
						ProtonParseException.DEFAULT_INDEX, tableNumber));
				// get the definition for the action/event type
				IBasicType derivationDef = EventMetadataFacade.getInstance().getEventType(derivationName);
				

				checkElementDefined(derivationDef, epaName, DefinitionType.EPA, ErrorElement.DERIVATION_NAME,
						ProtonParseException.DEFAULT_INDEX, tableNumber, derivationName);
				derivationsList.add(derivationDef);

				
				IComputedVariableType agentContextSegmentation = null;
				if (agent instanceof StatefulEventProcesingAgentType)
				{
					agentContextSegmentation = ((StatefulEventProcesingAgentType)agent).getAgentContextInformation();
				}
				
				// parse derivation condition for this derivation
				String derivationCondition = (String)derivation.get("condition");
				IExpression parsedCondition = null;
				if (derivationCondition != null) {
					try {
						
						// in case of AGGREGATION the derivation condition
						// should be defined on computed variable and not input
						// events
						if (epaType.equals(EPATypeEnum.AGGREGATE)) {
							List<IDataObjectMeta> inputList = new ArrayList<IDataObjectMeta>();
							inputList.add(computedVariableType);
							//right now assume the derivation condition cannot be based on input event's ,only the derivation expressions can
							String aggregationDerivationCondition = findAndReplace(derivationCondition,
									computedVariableType);
							inputList.add(agentContextSegmentation);
							parsedCondition = eep.createExpression(aggregationDerivationCondition, inputList);
						} else if (epaType.equals(EPATypeEnum.TREND)){
							List<IDataObjectMeta> inputList = new ArrayList<IDataObjectMeta>();
							inputList.add(computedVariableType);
							inputList.add(agentContextSegmentation);
							parsedCondition = eep.createExpression(derivationCondition, inputList);
						}
						else
						{
							List<IDataObjectMeta> inputList = new ArrayList<IDataObjectMeta>();							
							inputList.addAll((List<IDataObjectMeta>)(List<?>)inputEventsList);
							List<String> segmentationNamesSig = new ArrayList<String>();
							segmentationNamesSig.addAll(aliases);
							if (agentContextSegmentation != null)
							{
								inputList.add(agentContextSegmentation);
								segmentationNamesSig.add(agentContextSegmentation.getName());
							}
							// must be parsed successfuly
							parsedCondition = eep.createExpression(derivationCondition,
									inputList, segmentationNamesSig);
						}

					} catch (Exception e) {
						handleEEPException(epaName, DefinitionType.EPA, ErrorElement.DERIVATION_CONDITION,
								ProtonParseException.DEFAULT_INDEX, tableNumber, e);
						continue;
					}
				}
				TypePredicatePair derivationPredicatePair = new TypePredicatePair(derivationDef, parsedCondition,
						derivationCondition);
				((DerivationSchema)derivationSchema).addDerivationCondition(derivationPredicatePair);

				// parse derivation expressions
				JSONObject derivationExpressions = tryException(new NullChecker<JSONObject>("expressions",
						derivation, epaName, DefinitionType.EPA, ErrorElement.DERIVATION_EXPRESSIONS,
						jsonObjectParser, ProtonParseException.DEFAULT_INDEX, tableNumber));
				Map<TypeAttribute, String> specificStringExpressions = new HashMap<TypeAttribute, String>();
				Map<TypeAttribute, IExpression> specificExpressions = new HashMap<TypeAttribute, IExpression>();
				((DerivationSchema)derivationSchema).addDerivationStringExpression(derivationDef,
						specificStringExpressions);
				((DerivationSchema)derivationSchema).addDerivationExpression(derivationDef, specificExpressions);
				Collection<TypeAttribute> derivationAttributes = derivationDef.getTypeAttributes();
				
				for (TypeAttribute typeAttribute : derivationAttributes) {
					String attributeExpression = (String)derivationExpressions.get(typeAttribute.getName());
					if ((attributeExpression != null && epaType.equals(EPATypeEnum.AGGREGATE))) 
					{
						Integer rowNumber = attributeRowNumbers.get(derivationName + "." + typeAttribute.getName());
						// convert the expression to correct syntax using computed variables
						attributeExpression = findAndReplace(attributeExpression, computedVariableType);
						//check if the expression is also based on the input events ,in that case need to convert the expression to another syntax and add appropriate attributes to the
						//computed variable type to gather participant events
						//also need to update the aggregation matchign schema to mark that need to gather participants
						String newDerivationExpression;
						try {
							newDerivationExpression = findParticipantsWithinExpression(attributeExpression,inputEventsList,aliases,computedVariableType,operands);
						} catch (ParseException e) {
							handleEEPException(epaName, DefinitionType.EPA, ErrorElement.DERIVATION_EXPRESSIONS,
									rowNumber, tableNumber, e);
							continue;
						}
						if (!newDerivationExpression.equals(attributeExpression)) {
							//at least one of the expression depends on the input events, need to gather them in computed variable
							derivationSchema.setReportingParticipants(true);
							attributeExpression = newDerivationExpression;
						}
							
						
					}

					specificStringExpressions.put(typeAttribute, attributeExpression);
					IExpression parsedAttributeExpression = null;
					if (attributeExpression != null) {
						Integer rowNumber = attributeRowNumbers.get(derivationName + "." + typeAttribute.getName());
						if (rowNumber == null) {
							rowNumber = ProtonParseException.DEFAULT_INDEX;
						}
						try {
							if (epaType.equals(EPATypeEnum.AGGREGATE) || epaType.equals(EPATypeEnum.TREND)) {
								// parse with computed variable
								List<IDataObjectMeta> inputList = new ArrayList<IDataObjectMeta>();
								inputList.add(computedVariableType);
								inputList.add(agentContextSegmentation);
								parsedAttributeExpression = eep.createExpression(attributeExpression, inputList);
							} else {
								List<IDataObjectMeta> inputList = new ArrayList<IDataObjectMeta>();							
								inputList.addAll((List<IDataObjectMeta>)(List<?>)inputEventsList);
								List<String> segmentationNamesSig = new ArrayList<String>();
								segmentationNamesSig.addAll(aliases);
								if (agentContextSegmentation != null)
								{
									inputList.add(agentContextSegmentation);
									segmentationNamesSig.add(agentContextSegmentation.getName());
								}
								parsedAttributeExpression = eep.createExpression(attributeExpression,
										inputList, segmentationNamesSig);
							}

						} catch (Exception e) {
							handleEEPException(epaName, DefinitionType.EPA, ErrorElement.DERIVATION_EXPRESSIONS,
									rowNumber, tableNumber, e);
							continue;
						}
					} 
					specificExpressions.put(typeAttribute, parsedAttributeExpression);
				}
				
			} catch (MetadataParseException e) {
				continue;
			}
		}

		((DerivationSchema)derivationSchema).setMultiDerivationPolicy(compositionPolicy);
		((DerivationSchema)derivationSchema).setComposedEventType(composedEventType);

		return derivationsList;

	}

	
	/**
	 * Parsing consumer events (outgoing to sinks)
	 * 
	 * @param consumers
	 * @throws ParseException 
	 */
	protected void parseConsumerProducer(JSONArray consumers, JSONArray producers) throws ParseException {
		EventMetadataFacade instance = EventMetadataFacade.getInstance();
		ConsumerProducerMetadata consumerProducerMetadata = ConsumerProducerMetadata.initializeInstance();

		// parsing consumer definitions
		Set<String> consumerNames = new HashSet<String>();
		Set<String> producerNames = new HashSet<String>();
		Iterator arrayIter = consumers.iterator();
		while (arrayIter.hasNext()) {
			try{
				JSONObject consumerObject = (JSONObject) arrayIter.next();
				// parse consumer name, check uniqueness			
				String consumerName = tryException(new NullCheckerAndRepeats<String>(consumerNames, "name",
						consumerObject, "consumer name", DefinitionType.CONSUMER, ErrorElement.NAME, stringParser,
						true));
				// must belong to enum
				ConsumerType consumerType = tryException(new ConsumerTypeEnumChecker("type", consumerObject,
						consumerName, DefinitionType.CONSUMER, ErrorElement.TYPE));						
				
				JSONArray properties = (JSONArray)consumerObject.get("properties");
				Iterator propertiesIter = properties.iterator();
				Map<String, Object> propertiesMap = new HashMap<String, Object>();
				for (int rowNumber = 0; propertiesIter.hasNext(); rowNumber++) {
					try {
						JSONObject property = (JSONObject) propertiesIter.next();
						// Parse property name, check uniqueness
						String propertyName = tryException(new NullCheckerAndRepeats<String>(
								propertiesMap.keySet(), "name", property, consumerName, DefinitionType.CONSUMER,
								ErrorElement.PROPERTY_NAME, stringParser, rowNumber));
						// parse property, warning on null
						String propertyValue = tryException(new WarningNullChecker<String>("value", property,
								consumerName, DefinitionType.CONSUMER, ErrorElement.PROPERTY_VALUE, stringParser, rowNumber));
						if (propertyName.equals(DATE_FORMAT)) //if there is a date format property check that the format is legal one
						{
							try{
								SimpleDateFormat dateFormatter = new SimpleDateFormat(propertyValue);
							}catch(IllegalArgumentException e){
								//add to exceptions list
								ProtonParseException exception = new ProtonParseException(ParseErrorEnum.BAD_VALUE, consumerName, DefinitionType.CONSUMER, ErrorType.ERROR,ErrorElement.PROPERTY_VALUE, rowNumber);
								exceptions.add(exception);
							}
							
						}
						propertiesMap.put(propertyName, propertyValue);
					} catch (MetadataParseException e) {
						// allows catching exceptions for separate attributes
						continue;
					}
				}

				//TODO: reinitiate once all the properties are supported in runtime
				
	//			checkRequiredProperties(consumerType.toString(), propertiesMap.keySet(), consumerName, false);
				// TODO (NOTE TO GAL) do not touch yet
				//parse consumer events
				JSONArray consumerEventsArray = (JSONArray)consumerObject.get("events");
				if (consumerEventsArray == null) //this consumer consumes only actions
				{
					consumerEventsArray = new JSONArray();
				}
				Map<IEventType,IExpression> events = new HashMap<IEventType,IExpression>();
				Set<String> eventNames = new HashSet<String>();
				for (int rowNumber = 0; rowNumber < consumerEventsArray.size(); rowNumber++) {
					try {
						JSONObject event = (JSONObject)consumerEventsArray.get(rowNumber);
						String eventName = tryException(new NullCheckerAndWarningRepeats<String>(eventNames, "name",
								event, consumerName, DefinitionType.CONSUMER, ErrorElement.CONSUMER_EVENT_NAME, stringParser, rowNumber));
						IEventType eventType = instance.getEventType(eventName);
						checkElementDefined(eventType, consumerName, DefinitionType.CONSUMER,
								ErrorElement.CONSUMER_EVENT_NAME, rowNumber, eventName);
						String condition = (String)event.get("condition");
						IExpression expression = null;
						if (condition != null)
						{
							try {
								ArrayList<IDataObjectMeta> signature = new ArrayList<IDataObjectMeta>();
								signature.add(eventType);						
								expression = eep.createExpression(condition, signature);
							} catch (Exception e) {
								handleEEPException(consumerName, DefinitionType.CONSUMER, ErrorElement.CONSUMER_EVENT_CONDITION,
										rowNumber, ProtonParseException.DEFAULT_INDEX, e);
								continue;
							}
						}
						this.consumerEvents.add(eventName);
						events.put(eventType,expression);
						eventNames.add(eventName);
					} catch (MetadataParseException e) {
						continue;
					}
				}
				
				//parse actions and initialize appropriate structures
				JSONArray consumerActionArray = (JSONArray) consumerObject.get("actions");
				if (consumerActionArray == null) {// this consumer consumes only actions
					consumerActionArray = new JSONArray();
				}

				ConsumerMetadata consumerMetadata = new ConsumerMetadata(consumerName, consumerType,
						propertiesMap, events);
				consumerProducerMetadata.addConsumer(consumerName, consumerMetadata);
				
				if (events.isEmpty()) {
					exceptions.add(new ProtonParseException(ParseErrorEnum.EMPTY_COLLECTION, consumerName, DefinitionType.CONSUMER,
							ErrorType.WARNING, ErrorElement.CONSUMER_EVENTS_ACTIONS));
				}
				consumerNames.add(consumerName);
			} catch (MetadataParseException e) {
				continue;
			}
		}

		// parsing producer definitions
		Iterator producerIter = producers.iterator();
		while (producerIter.hasNext()) {
			try {
				JSONObject producerObject = (JSONObject) producerIter.next();
				String producerName = tryException(new NullCheckerAndRepeats<String>(producerNames, "name",
						producerObject, "producer name", DefinitionType.PRODUCER, ErrorElement.NAME, stringParser,
						true));
				
				
				
				// must belong to enum
				ProducerType producerType = tryException(new ProducerTypeEnumChecker("type", producerObject,
						producerName, DefinitionType.PRODUCER, ErrorElement.TYPE));
				

				JSONArray properties = (JSONArray) producerObject.get("properties");
				Map<String, Object> propertiesMap = new HashMap<String, Object>();
				for (int rowNumber = 0; rowNumber < properties.size(); rowNumber++) {
					try {
						JSONObject property = (JSONObject)properties.get(rowNumber);
						// Parse property name, check uniqueness
						String propertyName = tryException(new NullCheckerAndRepeats<String>(
								propertiesMap.keySet(), "name", property, producerName, DefinitionType.PRODUCER,
								ErrorElement.PROPERTY_NAME, stringParser, rowNumber));
						// parse property, warning on null
						String propertyValue = tryException(new WarningNullChecker<String>("value", property,
								producerName, DefinitionType.PRODUCER, ErrorElement.PROPERTY_VALUE, stringParser, rowNumber));
						
						if (propertyName.equals(DATE_FORMAT)) //if there is a date format property check that the format is legal one
						{
							try{
								SimpleDateFormat dateFormatter = new SimpleDateFormat(propertyValue);
							}catch(IllegalArgumentException e){
								//add to exceptions list
								ProtonParseException exception = new ProtonParseException(ParseErrorEnum.BAD_VALUE, producerName, DefinitionType.PRODUCER, ErrorType.ERROR,ErrorElement.PROPERTY_VALUE, rowNumber);
								exceptions.add(exception);
							}
							
						}
						
						propertiesMap.put(propertyName, propertyValue);
					} catch (MetadataParseException e) {
						continue;
					}
				}
				
				
		//		checkRequiredProperties(producerType.toString(), propertiesMap.keySet(), producerName, true);
				
				//parse filtered events
				JSONArray producerEventsArray = (JSONArray)producerObject.get("filterOutEvents");
				Map<IEventType,IExpression> eventsMap = null;
				Set<String> eventNames = new HashSet<String>();
				//this object can be non-existent - in which case we do not filter out the events
				if (producerEventsArray != null)
				{
					eventsMap = new HashMap<IEventType,IExpression>();
					for (int rowNumber = 0; rowNumber < producerEventsArray.size(); rowNumber++) {
						JSONObject event = (JSONObject)producerEventsArray.get(rowNumber);
						String eventName = tryException(new NullCheckerAndWarningRepeats<String>(eventNames, "name",
								event, producerName, DefinitionType.PRODUCER, ErrorElement.PRODUCER_EVENT_NAME,
								stringParser, rowNumber));
						IEventType eventType = EventMetadataFacade.getInstance().getEventType(eventName);
						checkElementDefined(eventType, producerName, DefinitionType.PRODUCER,
								ErrorElement.PRODUCER_EVENT_NAME, rowNumber, eventName);
						String condition = (String)event.get("condition");
						IExpression expression  = null;
						if (condition != null)
						{
							try {
								ArrayList<IDataObjectMeta> signature = new ArrayList<IDataObjectMeta>();
								signature.add(eventType);						
								expression = eep.createExpression(condition, signature);
							} catch (Exception e) {
								handleEEPException(producerName, DefinitionType.PRODUCER, ErrorElement.PRODUCER_EVENT_CONDITION,
										rowNumber, ProtonParseException.DEFAULT_INDEX, e);
								continue;
							}
						}
						eventsMap.put(eventType, expression);
						eventNames.add(eventName);
					}
				}
				
				ProducerMetadata producerMetadata = new ProducerMetadata(producerName, producerType,
						propertiesMap,eventsMap);
				consumerProducerMetadata.addProducer(producerName, producerMetadata);
				producerNames.add(producerName);
			} catch (MetadataParseException e) 
			{
				continue;
			}
		}
	}
			
			
	
	public void clear() {
		EventMetadataFacade.clear();
		EPAManagerMetadataFacade.clear();
		ContextMetadataFacade.clear();		
		RoutingMetadataFacade.clear();
		
	}



	@Override
	protected Set<EPATypeEnum> getEpasWithNoInstanceSelectionPolicy() {
		return new HashSet<EPATypeEnum>(Arrays.asList(
				EPATypeEnum.BASIC, EPATypeEnum.RELATIVE_N, EPATypeEnum.ABSENCE));
	}
}
