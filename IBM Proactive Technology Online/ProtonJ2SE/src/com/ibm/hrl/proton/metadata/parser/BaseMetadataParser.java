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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import json.java.JSONArray;
import json.java.JSONObject;

import com.ibm.eep.exceptions.ParseException;
import com.ibm.hrl.proton.expression.eep.EepExpression;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.computedVariable.ComputedVariableType;
import com.ibm.hrl.proton.metadata.computedVariable.IComputedVariableType;
import com.ibm.hrl.proton.metadata.context.CompositeContextType;
import com.ibm.hrl.proton.metadata.context.ContextAbsoluteTimeInitiator;
import com.ibm.hrl.proton.metadata.context.ContextAbsoluteTimeTerminator;
import com.ibm.hrl.proton.metadata.context.ContextEventInitiator;
import com.ibm.hrl.proton.metadata.context.ContextEventTerminator;
import com.ibm.hrl.proton.metadata.context.ContextInitiator;
import com.ibm.hrl.proton.metadata.context.ContextRelativeTimeTerminator;
import com.ibm.hrl.proton.metadata.context.ContextTerminator;
import com.ibm.hrl.proton.metadata.context.SegmentationContextType;
import com.ibm.hrl.proton.metadata.context.SlidingTimeWindow;
import com.ibm.hrl.proton.metadata.context.TemporalContextType;
import com.ibm.hrl.proton.metadata.context.enums.ContextInitiatorPolicyEnum;
import com.ibm.hrl.proton.metadata.context.enums.ContextInitiatorType;
import com.ibm.hrl.proton.metadata.context.enums.ContextIntervalPolicyEnum;
import com.ibm.hrl.proton.metadata.context.enums.ContextTerminationTypeEnum;
import com.ibm.hrl.proton.metadata.context.enums.ContextTerminatorPolicyEnum;
import com.ibm.hrl.proton.metadata.context.enums.ContextTypeEnum;
import com.ibm.hrl.proton.metadata.context.interfaces.IContextType;
import com.ibm.hrl.proton.metadata.context.interfaces.ITemporalContextType;
import com.ibm.hrl.proton.metadata.epa.Operand;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObjectMeta;
import com.ibm.hrl.proton.metadata.epa.enums.ConsumptionPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.EPATypeEnum;
import com.ibm.hrl.proton.metadata.epa.enums.InstanceSelectionPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.OrderPolicyEnum;
import com.ibm.hrl.proton.metadata.epa.enums.TrendRelationEnum;
import com.ibm.hrl.proton.metadata.epa.interfaces.IEventProcessingAgent;
import com.ibm.hrl.proton.metadata.epa.interfaces.IMatchingSchema;
import com.ibm.hrl.proton.metadata.epa.schemas.AggregationSchema;
import com.ibm.hrl.proton.metadata.epa.schemas.AggregationSchema.AggregationTypeEnum;
import com.ibm.hrl.proton.metadata.epa.schemas.StandardMatchingSchema;
import com.ibm.hrl.proton.metadata.epa.schemas.TrendMatchingSchema;
import com.ibm.hrl.proton.metadata.event.EventType;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.TypeAttribute;
import com.ibm.hrl.proton.metadata.type.TypeAttributeSet;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.metadata.ContextMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;
import com.ibm.hrl.proton.runtime.metadata.IMetadataFacade;
import com.ibm.hrl.proton.utilities.constants.ProtonConstants;
import com.ibm.hrl.proton.utilities.containers.Pair;

public abstract class BaseMetadataParser {
	public static final String FORMATTER = "formatter";
	public static final String FILENAME = "filename";
	public static final String DELAY = "sendingDelay";
	public static final String POLLING_DELAY = "pollingInterval";
	public static final String POLLING_MODE = "pollingMode";
	public static final String HOSTNAME = "hostname";
	public static final String PORT_NUM = "port";
	public static final String CONNECTION_FACTORY_JNDI = "connectionFactory";
	public static final String DESTINATION_JNDI = "destinationName";
	public static final String TIMEOUT = "timeout";
	public static final String URL = "URL";
	public static final String AUTH_TOKEN = "AuthToken";
	public static final String CONTENT_TYPE = "contentType";
	public static final String TIME_WINDOW = "windowSize";
	public static final String DATE_FORMAT = "dateFormat";
	
	protected final IParser<String>		stringParser	= new StringParser();
	protected final IParser<JSONArray>	jsonArrayParser	= new JSONArrayParser();
	protected final IParser<Boolean>		booleanParser	= new BooleanParser();
	protected final IParser<Long>			longParser		= new LongParser();
	protected final IParser<Integer>		intParser		= new IntParser();
	protected final IParser<JSONObject>	jsonObjectParser = new JSONObjectParser();
	protected final IParser<Date> dateParser;
	public static final long DEFAULT_BUFFERING_TIME = 100;
	public static final String DEFAULT_CONTEXT_NAME = "Always";
	
	
	protected Map<String, Collection<IEventProcessingAgent>> contextAgents = new HashMap<String, Collection<IEventProcessingAgent>>();
	protected Set<String> consumerEvents = new HashSet<String>();	

	
	protected SimpleDateFormat df;
	protected Collection<ProtonParseException> exceptions = new LinkedList<ProtonParseException>();
	protected Map<String, Integer> attributeRowNumbers= new HashMap<String, Integer>();
	
	protected IMetadataFacade metadataFacade;
	protected EepFacade eep;

	public BaseMetadataParser(EepFacade eep,IMetadataFacade metadataFacade2) {
		
		df = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
		dateParser = new DateParser(df);
		this.metadataFacade = metadataFacade2;
		this.eep = eep;
	}

	/*public static void main(String[] args) throws Exception {
		String line;
		StringBuilder sb = new StringBuilder();
		BufferedReader in = null;
		String jsonFileName = args[0];
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFileName), "UTF-8"));
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			in.close();
		}

		String jsonTxt = sb.toString();

		EepFacade eep = EepFacade.getInstance();
		MetadataParser metadataParser = new MetadataParser(eep);
		metadataParser.parseEPN(jsonTxt);

	}*/

	public abstract Collection<ProtonParseException> parseEPN(String jsonTxt)
			throws ParsingException ;	
	/*
	 * 
	 * @throws ParsingException
	 */
	protected void parseEvents(JSONArray eventsArray) throws ParsingException {
		Map<String, IEventType> eventsMap = new HashMap<String, IEventType>();
		Iterator<JSONObject> iter = eventsArray.iterator();
		while (iter.hasNext()) {
			try {
				final JSONObject event = iter.next();

				// parse event name and check for repeats
				String eventName = tryException(new NullCheckerAndRepeats<String>(eventsMap.keySet(), "name",
						event, "event name", DefinitionType.EVENT, ErrorElement.NAME, stringParser, true));

				// parse attributes if exist, if not create an empty array list
				JSONArray eventAttributes = tryException(new WarningNullChecker<JSONArray>("attributes", event,
						eventName, DefinitionType.EVENT, ErrorElement.ATTRIBUTES, jsonArrayParser));
				generateWarningOnEmpty(eventAttributes, eventName, DefinitionType.EVENT, ErrorElement.ATTRIBUTES);
				ArrayList<TypeAttribute> parsedAttributes;
				EventType eventType = new EventType(eventName);
				if (eventAttributes != null) 
				{
					parsedAttributes = parseAttributes(eventType,eventName, eventAttributes);
				} else {
					parsedAttributes = new ArrayList<TypeAttribute>();
				}
				eventType.setAttributes(parsedAttributes);
				eventsMap.put(eventName, eventType);
			} catch (MetadataParseException e) {
				continue;
			}
		}

		EventMetadataFacade eventMetadataFacade = new EventMetadataFacade(eventsMap);
		metadataFacade.setEventMetadataFacade(eventMetadataFacade);

		// parse consumer events and add them to the relevant structure

	}
	
	
	protected ArrayList<TypeAttribute> parseAttributes(IDataObjectMeta object, String objectName, JSONArray typeAttributes)
	throws ParsingException {

		//built in attributes are displayed to the user in "unmodifiable" mode  - the only thing which can be modified is
		//the default value for those attribute
		//therefore a built in attribute might be specified and it will be with exactly the same name and type as defined in runtime

		//TODO: Gal - should validate that if built in attribute specified, it is
		//of exactly same type as the one defined in the system
		
		//create the signature used in EEP default value expression creation, and an instance used in 
		//EEP default value expression evaluation
		/*ArrayList<IDataObjectMeta> signature = new ArrayList<IDataObjectMeta>();
		signature.add(object);
		IDataObject instance;
		if (object instanceof IEventType)
		{
			instance = new EventInstance((IEventType)object, new HashMap<String,Object>());
		}
		else
		{
			instance = new ActionInstance((IActionType)object, new HashMap<String,Object>());
		}*/
		
		ArrayList<TypeAttribute> attributes = new ArrayList<TypeAttribute>();
		Iterator<JSONObject> iter = typeAttributes.iterator();
		Set<String> attributeNames = new HashSet<String>();
		try {
			for (int rowNumber = 0; iter.hasNext(); rowNumber++) {
				try {
					JSONObject attribute = iter.next();
					String attrName = tryException(new NullCheckerAndRepeats<String>(attributeNames,
							"name", attribute, objectName, DefinitionType.EVENT,
							ErrorElement.ATTRIBUTE_NAME, stringParser, rowNumber));

					//can't be null
//					String attrTypeString = tryException(new NullChecker<String>("type", attribute,
//							objectName, DefinitionType.EVENT,
//							ErrorElement.ATTRIBUTE_TYPE, stringParser, rowNumber));

					
//					AttributeTypesEnum type;
//					type = AttributeTypesEnum.valueOf(attrTypeString.toUpperCase());
					AttributeTypesEnum type = tryException(new AttributeTypesEnumCheck("type", attribute, objectName,
							DefinitionType.EVENT, ErrorElement.ATTRIBUTE_TYPE, rowNumber));
					String defaultValue = (String) attribute.get("defaultValue");
					Object value = null;
					//can be null
					if (defaultValue != null)
						//use EEP to determine the value of defaultValue field,we assume here default value is 
						//always defined on constants/functions and do not use any actual attributes of the instance
					{
						//IExpression defaultValueExpression = eep.createExpression(defaultValue, signature);						
						//value = defaultValueExpression.evaluate(instance);
						value = TypeAttribute.parseConstantValue(defaultValue,attrName, object,null,eep);						
						
						
					}
					/*try {
						// TODO add handler for bad parsing
						if (attrTypeString.equals("Integer")) {
							type = AttributeTypesEnum.LONG;
							if (defaultValue != null)
								value = Long.valueOf(defaultValue);
						} else if (attrTypeString.equals("Double")) {
							type = AttributeTypesEnum.DOUBLE;
							if (defaultValue != null)
								value = TypeAttribute.parseDouble(defaultValue);
						} else if (attrTypeString.equals("DateTime")) {
							type = AttributeTypesEnum.DATETIME;
							if (defaultValue != null)
								value = df.parse(defaultValue);
						} else if (attrTypeString.equals("String")) {
							type = AttributeTypesEnum.STRING;
							if (defaultValue != null)
								value = defaultValue;
						} else if (attrTypeString.equals("Boolean")) {
							type = AttributeTypesEnum.BOOLEAN;
							if (defaultValue != null) {

								if (defaultValue.equals("True")) {
									value = true;
								} else if (defaultValue.equals("False")) {
									value = false;
								} else {
									throw new java.text.ParseException(defaultValue, -1);
								}
							}
						} else if (attrTypeString.equals("Object")) {
							type = AttributeTypesEnum.OBJECT;
							if (defaultValue != null)
								value = defaultValue;
						} else {
							exceptions.add(new ProtonParseException(ParseErrorEnum.BAD_TYPE, objectName,
									DefinitionType.EVENT, ErrorType.ERROR,
									ErrorElement.ATTRIBUTE_TYPE, rowNumber));
							continue;
						}
					} catch (java.text.ParseException e) {
						handleParseException(objectName, rowNumber);
						continue;
					} catch (NumberFormatException e) {
						handleParseException(objectName, rowNumber);
						continue;
					}*/

					TypeAttribute eventTypeAttr;

					Integer dimensionNum = tryException(new LegalNullChecker<Integer>("dimension", attribute, objectName,
							DefinitionType.EVENT, ErrorElement.ATTRIBUTE_DIMENSION_SIZE,
							intParser, rowNumber));
					if (dimensionNum != null) {
						if (dimensionNum > TypeAttribute.HIGHEST_DIMENSION || dimensionNum < 0) {
							exceptions.add(new ProtonParseException(ParseErrorEnum.INDEX_OUT_OF_RANGE, objectName,
									DefinitionType.EVENT, ErrorType.ERROR,
									ErrorElement.ATTRIBUTE_DIMENSION_SIZE, rowNumber));
							continue;
						}
					} else {
						dimensionNum = TypeAttribute.DEFAULT_DIMENSION;
					}

					if (value != null) {
						eventTypeAttr = new TypeAttribute(attrName, type, dimensionNum, value);
					} else {
						eventTypeAttr = new TypeAttribute(attrName, type,(int)dimensionNum);
						
					}

					attributeNames.add(attrName);
					attributes.add(eventTypeAttr);
					
					// add rowNumber to attribute
					attributeRowNumbers.put(objectName + "." + attrName, rowNumber);
				} catch (MetadataParseException e) {
					continue;
				} catch (Exception e) {
					handleEEPException(objectName, DefinitionType.EVENT,
							ErrorElement.ATTRIBUTE_TYPE_DEFAULT_VALUE, rowNumber, ProtonParseException.DEFAULT_INDEX, e);
					continue;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParsingException(e.getMessage());
		}

		return attributes;
	}

	private Object checkValue(Object value, AttributeTypesEnum type) throws Exception{
		switch (type) {
		case INTEGER:
			if (!(value instanceof Integer)) throw new Exception();
			return value;
		case LONG:
			if (!(value instanceof Long)) throw new Exception();
			return value;
		case FLOAT:				
		case DOUBLE:
			if (!(value instanceof Double)&& (value instanceof Float)) throw new Exception();
			return value;
		case BOOLEAN:
			if (!(value instanceof Boolean)) throw new Exception();
			return value;
		case DATE:
		case TIME:
			if (value instanceof String){
				
			}
			if (!(value instanceof Date)) throw new Exception();
			return value;
		case STRING:
			return value.toString();			
		default:
			throw new Exception();
		}
		
	}

	protected abstract void parseEPAs(JSONArray epaArray) throws ParseException;
	
	

	/**
	 * Add additional not input-events related information to the matching schema
	 * @param epa
	 * @param matchingSchema
	 * @throws ParseException 
	 * @throws MetadataParseException 
	 */
	protected void finalizeMatchingSchema(JSONObject epa,String epaName,EPATypeEnum epaType,
			IMatchingSchema matchingSchema,Map<String,Operand> operands) throws ParseException, MetadataParseException {
		switch (epaType) {	
		
		case AGGREGATE:
			AggregationSchema aggregationSchema = (AggregationSchema)matchingSchema;			
			List<TypeAttribute> attributes = new ArrayList<TypeAttribute>();
			
			//add the calculated variables information
			Set<String> variableNames = new HashSet<String>();
			JSONArray calculatedVariables = tryException(new NullChecker<JSONArray>("computedVariables",
					epa, epaName, DefinitionType.EPA, ErrorElement.COMPUTED_VARIABLES, jsonArrayParser));
			for (int rowNumber = 0; rowNumber < calculatedVariables.size(); rowNumber++) {
				JSONObject computedVariable = (JSONObject)calculatedVariables.get(rowNumber);
				String computedVariableName = tryException(new NullCheckerAndRepeats<String>(variableNames,
						"name", computedVariable, epaName, DefinitionType.EPA,
						ErrorElement.COMPUTED_VARIABLE_NAME, stringParser, rowNumber));
				
				//add this attribute to the computed variable type
				TypeAttribute attribute = new TypeAttribute(computedVariableName, AttributeTypesEnum.DOUBLE);
				attributes.add(attribute);
				
				AggregationTypeEnum aggregationType = tryException(new AggregationTypeEnumCheck("aggregationType", computedVariable,
						epaName, DefinitionType.EPA, ErrorElement.AGGREGATION_TYPE, rowNumber));
				
				aggregationSchema.addCalculatedVariableAggregationType(computedVariableName, aggregationType);
				//iterate over the operands and determine the expressions
				for (String operandAliasName : operands.keySet())
				{
					Operand operand = operands.get(operandAliasName);
					String expression = (String)computedVariable.get(operandAliasName);
					//build the expression based on the string expression and on alias
					ArrayList<IDataObjectMeta> signature = new ArrayList<IDataObjectMeta>();
					signature.add(operand.getEventType());
					ArrayList<String> signatureAliases = new ArrayList<String>();
					signatureAliases.add(operandAliasName);
					// must be parsed by eep
					try {
						IExpression parsedExpression = eep.createExpression(expression, signature,
								signatureAliases);
						aggregationSchema.addCalculatedVariableOperandExpression(computedVariableName, operand, parsedExpression);
					} catch (Exception e) {
						handleEEPException(epaName, DefinitionType.EPA, ErrorElement.OPERAND, rowNumber,
								ProtonParseException.DEFAULT_INDEX, e);
						continue;
					}
				}
				variableNames.add(computedVariableName);
			}
			
			//create the computed variable type and add it to aggregation schema
			//the name of the computed variable is as the aggreagation epa name, 
			//the fields names are as the names of calculations defined by the user ,their type is LONG
			IComputedVariableType computedVariableType = new ComputedVariableType(epaName,attributes);
			aggregationSchema.setComputedVariableType(computedVariableType);
			break;	
		case TREND:
			TrendMatchingSchema trendSchema = (TrendMatchingSchema)matchingSchema;
			TrendRelationEnum trendRelation = tryException(new TrendTypeEnumCheck("trendRelation", epa,
					epaName, DefinitionType.EPA, ErrorElement.TREND_RELATION_TYPE));
			trendSchema.setRelation(trendRelation);
			
			String trendTreshold = tryException(new NullChecker<String>("trendN", epa,
					epaName, DefinitionType.EPA, ErrorElement.TREND_TRESHOLD,stringParser));
			try{
				trendSchema.setTrendTreshold(Integer.valueOf(trendTreshold));
			}catch(NumberFormatException e){
				exceptions.add(new ProtonParseException(ParseErrorEnum.BAD_VALUE, epaName, DefinitionType.EPA, ErrorType.ERROR,
						ErrorElement.TREND_TRESHOLD, ProtonParseException.DEFAULT_INDEX, ProtonParseException.DEFAULT_INDEX, e.getMessage()));
			}
			
								
			break;
		default:
			break;
		}
		
	}

	protected void addInitiatorsTerminatorsRoutingInfo(String contextName, String name,
			Map<String, Set<Pair<String, String>>> eventsRoutingInfo) {
		IContextType contextType = metadataFacade.getContextMetadataFacade().getContext(contextName);
		
			
		if (contextType instanceof TemporalContextType) {
			addInfo(contextName, name, eventsRoutingInfo, contextType);
		}
		//Look for the temporal context member of a composite context.
		//add its initiators and terminators to the routing info
		else if (contextType instanceof CompositeContextType){
			for (IContextType contextTypeMember: ((CompositeContextType)contextType).getMemberContexts())            {
            	
            	// it is only relevant for temporal context (not segmentation)
	            if (contextTypeMember instanceof ITemporalContextType)                
	            {
	            	addInfo(contextName,name, eventsRoutingInfo, contextTypeMember);
	            }
	        }

		}

	}

	private void addInfo(String contextName, String name,
			Map<String, Set<Pair<String, String>>> eventsRoutingInfo,
			IContextType contextType) {
		// iterate over initiators and terminators
		List<ContextEventInitiator> eventInitiators = ((TemporalContextType)contextType).getEventInitiators();
		List<ContextEventTerminator> eventTerminators = ((TemporalContextType)contextType)
				.getEventTerminators();

		for (ContextEventInitiator contextEventInitiator : eventInitiators) {
			String eventName = contextEventInitiator.getEventTypeName();
			Set<Pair<String, String>> internalSet = null;
			if ((internalSet = eventsRoutingInfo.get(eventName)) == null) {
				internalSet = new HashSet<Pair<String, String>>();
				eventsRoutingInfo.put(eventName, internalSet);
			}

			internalSet.add(new Pair<String, String>(name, contextName));
		}

		for (ContextEventTerminator contextEventTerminator : eventTerminators) {
			String eventName = contextEventTerminator.getEventTypeName();
			Set<Pair<String, String>> internalSet = null;
			if ((internalSet = eventsRoutingInfo.get(eventName)) == null) {
				internalSet = new HashSet<Pair<String, String>>();
				eventsRoutingInfo.put(eventName, internalSet);
			}

			internalSet.add(new Pair<String, String>(name, contextName));
		}
	}

	protected abstract Set<EPATypeEnum> getEpasWithNoInstanceSelectionPolicy();
	/**
	 * Add information defined on per-participant basis to the matching schema,
	 * if such exists (for the right EPA type)
	 * 
	 * @param matchingSchema
	 * @param epaType
	 * @param inputEvent
	 * @param operand
	 * @throws ParseException
	 * @throws ParsingException
	 */
	protected void addOperandInformationToMatchingSchema(IMatchingSchema matchingSchema, EPATypeEnum epaType,
			JSONObject inputEvent, Operand operand, String aliasName,String epaName, int rowNumber) throws ParseException,
			ParsingException, MetadataParseException {
		List<IDataObjectMeta> signature = new ArrayList<IDataObjectMeta>();
		ConsumptionPolicyEnum consumptionPolicy;
		String expression;
		switch (epaType) {
		case BASIC:
			return;
		case ABSENCE:
		case ALL:
		case SEQUENCE:
			// must belong to enum
			consumptionPolicy = tryException(new ConsumptionPolicyEnumCheck(
					"consumptionPolicy", inputEvent, epaName, DefinitionType.EPA, ErrorElement.CONSUMPTION_POLICY,
					rowNumber));
			// must belong to enum
			InstanceSelectionPolicyEnum instanceSelectionPolicy;
			Set<EPATypeEnum> epasWithNoInstanceSelectionPolicy = getEpasWithNoInstanceSelectionPolicy();
			if (epasWithNoInstanceSelectionPolicy.contains(epaType) == false) {
				instanceSelectionPolicy = tryException(new InstanceSelectionPolicyEnumCheck(
					"instanceSelectionPolicy", inputEvent, epaName, DefinitionType.EPA,
					ErrorElement.INSTANCE_SELECTION_POLICY, rowNumber));
			} else {
				instanceSelectionPolicy = InstanceSelectionPolicyEnum.FIRST;
			}

			// if not null must belong to enum
			String orderPolicyString = (String)inputEvent.get("orderPolicy");
			OrderPolicyEnum orderPolicy = OrderPolicyEnum.DETECTION;
			if (orderPolicyString != null) {
				orderPolicy = tryException(new OrderPolicyEnumCheck("instanceSelectionPolicy", inputEvent,
						epaName, DefinitionType.EPA, ErrorElement.ORDER_POLICY, rowNumber));
			}

			((StandardMatchingSchema)matchingSchema).setConsumptionPolicy(operand, consumptionPolicy);
			((StandardMatchingSchema)matchingSchema).setInstanceSelectionPolicy(operand, instanceSelectionPolicy);
			((StandardMatchingSchema)matchingSchema).setOrderPolicy(operand, orderPolicy);
			break;
		case AGGREGATE:
			//consumption policy is the only policy relevant for the input event in the matching schema			
			// must belong to enum
			consumptionPolicy = tryException(new ConsumptionPolicyEnumCheck(
					"consumptionPolicy", inputEvent, epaName, DefinitionType.EPA, ErrorElement.CONSUMPTION_POLICY,
					rowNumber));
			((AggregationSchema)matchingSchema).setConsumptionPolicy(operand, consumptionPolicy);
			break;
		case TREND:
			consumptionPolicy = tryException(new ConsumptionPolicyEnumCheck(
					"consumptionPolicy", inputEvent, epaName, DefinitionType.EPA, ErrorElement.CONSUMPTION_POLICY,
					rowNumber));
			((TrendMatchingSchema)matchingSchema).setConsumptionPolicy(operand, consumptionPolicy);
			
			expression = (String)inputEvent.get("expression");
			if (expression == null) throw new ParseException("Trend expression for an operand: "+operand+" is null");
						
			//todo add the expression to expressions set and parsed expression sets and to the schema
			((TrendMatchingSchema)matchingSchema).addExpression(operand, expression);
			
			ArrayList<IDataObjectMeta> trendSignature = new ArrayList<IDataObjectMeta>();
			trendSignature.add(operand.getEventType());
			ArrayList<String> signatureAliases = new ArrayList<String>();
			signatureAliases.add(aliasName);
			// must be parsed by eep
			try {
				IExpression parsedExpression =  eep.createExpression(expression, trendSignature,
						signatureAliases);
				((TrendMatchingSchema)matchingSchema).addParsedExpression(operand, parsedExpression);
			}catch (Exception e) {
				handleEEPException(epaName, DefinitionType.EPA, ErrorElement.TREND_EXPRESSIONS, ProtonParseException.DEFAULT_INDEX,
						ProtonParseException.DEFAULT_INDEX, e);
				
			}
			break;
		default:
			// epa type was parsed before
			assert false;
		}

	}

	/**
	 * Determine matching schema type according to EPA type
	 * 
	 * @param epaType
	 * @return
	 */
	protected abstract IMatchingSchema determineMatchingSchema(EPATypeEnum epaType) ;

	/**
	 * Parse additional information for stateful agents, some of the information
	 * are common to all, some is relevant only for the concrete type
	 * 
	 * @param epa
	 * @param epaType
	 * @param agent
	 * @param inputEventsList
	 * @param segmentationContextsSet 
	 * @throws MetadataParseException
	 * @throws ParseException
	 */
	protected abstract void parseAdditionalEPAInformation(JSONObject epa, EPATypeEnum epaType, IEventProcessingAgent agent,
			ArrayList<IEventType> inputEventsList, List<String> inputEventsAliases, Set<TypeAttribute> segmentationContextsSet) throws MetadataParseException ;

	protected void handleEEPException(String defInstance, DefinitionType defType, ErrorElement element,
			int rowNumber, int tableNumber, Exception e) {
		exceptions.add(new ProtonParseException(ParseErrorEnum.EEP_ERROR, defInstance, defType, ErrorType.ERROR,
				element, rowNumber, tableNumber, e.getMessage()));
	}

		

	/**
	 * For aggregation: check if expression is based on input event (should be array expression)
	 * if so add this as an attribute of computed variable
	 * When doing so transform the expression
	 * For example: for expression OutputEvent.A1 = InputEvent.A1[0]*InputEventAlias.A2[1] will be transformed to
	 * ComputedVariableName.0_A1[0]*1_ComputedVariableName.1_A2[1] (We are transforming the attributes to pseudo-attributes within computed variable while renaming them
	 * to <operandIndex>_<attributeName> 
	 * @param expression
	 * @param inputEventsList
	 * @param aliases
	 * @param computedVariableType
	 * @throws ParseException 
	 */
	protected String findParticipantsWithinExpression(String expression,
			ArrayList<IEventType> inputEventsList, List<String> aliases,
			IComputedVariableType computedVariableType,Map<String,Operand> operands) throws ParseException {
		String newExpression = expression;
		
		
		
		String replacingPrefix = computedVariableType.getName()+".";
		HashMap<String,TypeAttribute> attributes = new HashMap<String,TypeAttribute>();
		
		if (aliases != null && !aliases.isEmpty())
		{
			//check if there is the name of the alias within expression
			for (int i=0; i < aliases.size(); i++) 
			{
				String alias = aliases.get(i);
				if (expression.indexOf(alias+".") != -1) //the expression depends on the alias name
				{
					List<String> attributeVariables = getAttributeName(alias,expression,inputEventsList,aliases);
					//all the attributes of this alias in the expression
					for (Iterator iterator = attributeVariables.iterator(); iterator
							.hasNext();) {
						String attributeName = (String) iterator.next();
						//extract the alias name, the attribute name and find the original event name
						//extract the attribute type
						IEventType eventType = inputEventsList.get(i);					
						TypeAttribute attrType= (TypeAttribute)eventType.getFieldMetaData(attributeName);
						//calculate new attribute name based on the operand
						int operandIndex = operands.get(alias).getOperandIndex();
						String newAttrName = "operand"+operandIndex+"_"+attributeName;					
						attributes.put(newAttrName,attrType);					
						
						//in the expression - change the original value to modified one
						newExpression = newExpression.replaceAll(alias+"."+attributeName, replacingPrefix+newAttrName);
					}
					
					
				}
			
			}
		}
			
		//iterate over collected attributes, add them to the computed variable
	    TypeAttributeSet computedVariableAttributesSet= computedVariableType.getTypeAttributeSet();
		for (Map.Entry<String, TypeAttribute> attribute : attributes.entrySet()) 
		{
			TypeAttribute arrayAttribute = new TypeAttribute(attribute.getKey(), attribute.getValue().getTypeEnum(), 1); //create an array attribute of the specified type
			computedVariableAttributesSet.addAttribute(arrayAttribute);
		}
		
		//return the new expression
		return newExpression;	
		
	}
	
	/**
	 * Calculate and return the attribute name from the given expression, and check whether this attribute is an array or single value within the array
	 * @param name
	 * @param expression
	 * @param aliases 
	 * @param inputEventsList 
	 * @return Pair<String,Boolean> return attribute name, a boolean value indicating whether this attribute is an array or single value within the array
	 * @throws ParseException 
	 */
	private List<String> getAttributeName(String name, String expression, ArrayList<IEventType> inputEventsList, List<String> aliases) throws ParseException
	{
		List<String> aliasAttributes = new ArrayList<String>();
		String attributeName = null;
		EepExpression parsedExpression;
		
		parsedExpression = (EepExpression)  eep.createExpression(expression, (List<IDataObjectMeta>)(List<?>)inputEventsList, aliases);
			
		for (Iterator iterator = parsedExpression.getExpressionVariables(); iterator.hasNext();) {
			String variableName = (String) iterator.next();
			if (variableName.indexOf(name) != -1)
			{
				int separator = variableName.indexOf(ProtonConstants.SEPARATOR);
				if (separator == -1) 
					throw new ParseException("Could not parse the expression "+variableName+", illegal separator");
				;
				aliasAttributes.add(variableName.substring(separator + 1));
			}
		}
		
		return aliasAttributes;
	}

	/**
	 * For aggregation - take the expressions defined for calculated variables and convert them
	 * to appropriate EEP format. For example each expression like "sumX" have to be converted to 
	 * "<dataObjectName>.sumX" in order to be acceptable EEP expression.
	 * @param attributeExpression
	 * @param computedVariableType
	 * @return
	 */
	protected String findAndReplace(String attributeExpression,
			IComputedVariableType computedVariableType) {		
		String replacingPrefix = computedVariableType.getName()+".";
		Collection<TypeAttribute> attributesList = computedVariableType.getTypeAttributes();
		for (Iterator iterator = attributesList.iterator(); iterator.hasNext();) {
			TypeAttribute typeAttribute = (TypeAttribute) iterator.next();
			String attributeName = typeAttribute.getName();
			attributeExpression = attributeExpression.replaceAll(attributeName, replacingPrefix+attributeName);
		}
		return attributeExpression;
	}

	protected void checkElementDefined(Object element, String definitionInstance, DefinitionType type,
			ErrorElement errorElement, int rowNumber, String elementName) throws MetadataParseException {
		checkElementDefined(element, definitionInstance, type, errorElement, rowNumber, ProtonParseException.DEFAULT_INDEX,
				elementName);
	}
	
	protected void checkElementDefined(Object element, String definitionInstance, DefinitionType type,
			ErrorElement errorElement, int rowNumber, int tableNumber, String elementName) throws MetadataParseException {
		if (element == null) {
			exceptions.add(new ProtonParseException(ParseErrorEnum.ELEMENT_NOT_DEFINED, definitionInstance, type,
					ErrorType.ERROR, errorElement, rowNumber, tableNumber, elementName));
			throw new MetadataParseException("Element not defined");
		}
	}

	protected void generateWarningOnEmpty(JSONArray array, String definitionInstance, DefinitionType type,
			ErrorElement element) {
		// assume null warning is handled elsewhere
		if (array != null && array.isEmpty()) {
			exceptions.add(new ProtonParseException(ParseErrorEnum.EMPTY_COLLECTION, definitionInstance, type,
					ErrorType.WARNING, element));
		}
	}
	
	// TODO: find out if the external and internal contexts are combined at the
	// definition level
	// or if the local segmentation is defined per agent (
	// (meaning that the same temporal C1 on which A1 and A2 are defined are
	// basically two different contexts)
	protected void parseContexts(JSONObject contexts) throws ParsingException {
		try {
			
			//always context
			IContextType defaultContext = new TemporalContextType(DEFAULT_CONTEXT_NAME, null, null, ContextIntervalPolicyEnum.CLOSED,ContextIntervalPolicyEnum.OPEN, true, true);
			
			JSONArray temporalContexts = (JSONArray) contexts.get("temporal");		
			JSONArray segmentationContexts = (JSONArray) contexts.get("segmentation");
			JSONArray compositeContexts = (JSONArray) contexts.get("composite");
			Map<String, IContextType> finalCompositeContexts = new HashMap<String, IContextType>();

			Map<String, IContextType> temporalContextsMap = parseTemporalContexts(temporalContexts);
			temporalContextsMap.put(DEFAULT_CONTEXT_NAME, defaultContext);
			finalCompositeContexts.putAll(temporalContextsMap);
			Map<String, IContextType> segmentationContextsMap = parseSegmentationContexts(segmentationContexts);
			finalCompositeContexts.putAll(segmentationContextsMap);
			Map<String, IContextType> compositeContextsMap = parseCompositeContexts(compositeContexts,
					finalCompositeContexts);
			finalCompositeContexts.putAll(compositeContextsMap);

			ContextMetadataFacade contextMetadataFacade= new ContextMetadataFacade(finalCompositeContexts, contextAgents);
			metadataFacade.setContextMetadataFacade(contextMetadataFacade);
		} catch (Exception e) {
			throw new ParsingException("Could not parse contexts , reason: " + e.getMessage());
		}
	}

	private Map<String, IContextType> parseSegmentationContexts(JSONArray segmentationContexts)
			throws ParseException {

		Map<String, IContextType> contexts = new HashMap<String, IContextType>();
		Iterator iter = segmentationContexts.iterator();
		while (iter.hasNext()) {
			try {
				Map<IEventType, String> expressions = new HashMap<IEventType, String>();
				Map<IEventType, IExpression> parsedExpressions = new HashMap<IEventType, IExpression>();
				
				JSONObject segmentationContextObject = (JSONObject)iter.next();
				// name must be unique
				String contextName = tryException(new NullCheckerAndRepeats<String>(contexts.keySet(), "name",
						segmentationContextObject, "context name", DefinitionType.SEGMENTATION_CONTEXT,
						ErrorElement.NAME, stringParser));

				JSONArray events = tryException(new NullChecker<JSONArray>("participantEvents",
						segmentationContextObject, contextName, DefinitionType.SEGMENTATION_CONTEXT,
						ErrorElement.PARTICIPAING_EVENTS, jsonArrayParser));

				Set<String> eventNames = new HashSet<String>();
				for (int rowNumber = 0; rowNumber < events.size(); rowNumber++) {
					JSONObject event = (JSONObject)events.get(rowNumber);
					// must be defined and unique
					String eventName = tryException(new NullCheckerAndRepeats<String>(eventNames, "name", event,
							contextName, DefinitionType.SEGMENTATION_CONTEXT, ErrorElement.EVENT_NAME,
							stringParser, rowNumber));

					IEventType eventType = metadataFacade.getEventMetadataFacade().getEventType(eventName);
					checkElementDefined(eventType, contextName, DefinitionType.SEGMENTATION_CONTEXT,
							ErrorElement.EVENT_NAME, rowNumber, eventName);
					
					// must exist
					// must exist
					String expression = tryException(new NullChecker<String>("expression", event, contextName,
							DefinitionType.SEGMENTATION_CONTEXT, ErrorElement.EVENT_EXPRESSION, stringParser,
							rowNumber));
					List<IDataObjectMeta> signature = new ArrayList<IDataObjectMeta>();
					signature.add(eventType);
					
					// must be parsed validly
					try {
						IExpression parsedCondition = eep.createExpression(expression, signature);
						expressions.put(eventType, expression);
						parsedExpressions.put(eventType, parsedCondition);
					} catch (Exception e) {
						// this is caught here in order to add to exception
						handleEEPException(contextName, DefinitionType.SEGMENTATION_CONTEXT,
								ErrorElement.EVENT_EXPRESSION, rowNumber, ProtonParseException.DEFAULT_INDEX, e);
					}
				}
				
				if (expressions.isEmpty()) {
					assert parsedExpressions.isEmpty();
					assert eventNames.isEmpty();
					assert events.isEmpty();
					exceptions.add(new ProtonParseException(ParseErrorEnum.EMPTY_COLLECTION, contextName,
							DefinitionType.SEGMENTATION_CONTEXT, ErrorType.WARNING,
							ErrorElement.PARTICIPAING_EVENTS));
				}
				
				IContextType segmentationContext = new SegmentationContextType(contextName, expressions,
						parsedExpressions);
				contexts.put(contextName, segmentationContext);
	
			} catch (MetadataParseException e) {
				continue;
			}
		}
		return contexts;
	}

	private Map<String, IContextType> parseCompositeContexts(JSONArray compositeContexts,
			Map<String, IContextType> finalCompositeContexts) {
		Map<String, IContextType> contexts = new HashMap<String, IContextType>();

		Iterator<JSONObject> iter = compositeContexts.iterator();
		while (iter.hasNext()) {
			try {
				List<IContextType> contextsMembersMap = new ArrayList<IContextType>();
				JSONObject compositeContextObject = (JSONObject) iter.next();
				// name must be unique
				String contextName = tryException(new NullCheckerAndRepeats<String>(contexts.keySet(), "name",
						compositeContextObject, "context name", DefinitionType.COMPOSITE_CONTEXT,
						ErrorElement.NAME, stringParser));
				
				JSONArray temporalMemberContexts = (JSONArray)compositeContextObject.get("temporalContexts");
				JSONArray segmentationMemberContexts = (JSONArray)compositeContextObject.get("segmentationContexts");
				
				// as they can be null, this is to avoid null exceptions
				if (temporalMemberContexts == null) {
					temporalMemberContexts = new JSONArray();
				}
				if (segmentationMemberContexts == null) {
					segmentationMemberContexts = new JSONArray();
				}
				
				JSONArray memberContexts = new JSONArray();
				memberContexts.addAll(temporalMemberContexts);
				memberContexts.addAll(segmentationMemberContexts);
				generateWarningOnEmpty(memberContexts, contextName, DefinitionType.COMPOSITE_CONTEXT,
						ErrorElement.COMPOSITE_CONTEXT);
				Set<String> contextNames = new HashSet<String>();
				for (int rowNumber = 0; rowNumber < memberContexts.size(); rowNumber++) {
					JSONObject memberContext = (JSONObject) memberContexts.get(rowNumber);
					String memberContextName = tryException(new NullCheckerAndRepeats<String>(contextNames,
							"name", memberContext, contextName, DefinitionType.COMPOSITE_CONTEXT,
							ErrorElement.MEMBER_CONTEXT_NAME, stringParser, rowNumber));
					IContextType memberContextType = finalCompositeContexts.get(memberContextName);
					checkElementDefined(memberContextType, contextName, DefinitionType.COMPOSITE_CONTEXT,
							ErrorElement.MEMBER_CONTEXT_NAME, rowNumber, memberContextName);
					contextsMembersMap.add(memberContextType);
					contextNames.add(memberContextName);
				}
				CompositeContextType compositeContext = new CompositeContextType(contextName,
						contextsMembersMap, false);
				contexts.put(contextName, compositeContext);
			} catch (MetadataParseException e) {
				continue;
			}
		}

		return contexts;
	}

	private Map<String, IContextType> parseTemporalContexts(JSONArray temporalContexts) {
		Map<String, IContextType> contexts = new HashMap<String, IContextType>();
		Iterator iter = temporalContexts.iterator();
		while (iter.hasNext()) {
			try {
				JSONObject temporalContext = (JSONObject) iter.next();
				String name = tryException(new NullCheckerAndRepeats<String>(contexts.keySet(), "name",
						temporalContext, "context name", DefinitionType.TEMPORAL_CONTEXT, ErrorElement.NAME,
						stringParser));
				// check context type
				ContextTypeEnum contextType = tryException(new ContextTypeEnumsChecker("type", temporalContext,
						name,DefinitionType.TEMPORAL_CONTEXT, ErrorElement.TYPE));
				// must be boolean
				final boolean atStartUp = tryException(new NullChecker<Boolean>("atStartup", temporalContext,
						name, DefinitionType.TEMPORAL_CONTEXT, ErrorElement.AT_START_UP, booleanParser));
				final boolean neverEnding = tryException(new NullChecker<Boolean>("neverEnding", temporalContext,
						name, DefinitionType.TEMPORAL_CONTEXT, ErrorElement.NEVER_ENDING, booleanParser));

				// can only be null if at startup is true
				JSONArray initiatorsArray = tryException(new NullCheckerUnderCondition<JSONArray>("initiators",
						temporalContext, name, DefinitionType.TEMPORAL_CONTEXT, ErrorElement.INITIATORS,
						jsonArrayParser, atStartUp == true));
				if (atStartUp == false) {
					generateWarningOnEmpty(initiatorsArray, name, DefinitionType.TEMPORAL_CONTEXT,
							ErrorElement.INITIATORS);
				}
				if (initiatorsArray == null) {
					// to avoid null exception
					initiatorsArray = new JSONArray();
				}
				// can only be null if never ending is true
				JSONArray terminatorsArray = tryException(new NullCheckerUnderCondition<JSONArray>("terminators",
						temporalContext, name, DefinitionType.TEMPORAL_CONTEXT, ErrorElement.TERMINATORS,
						jsonArrayParser, neverEnding == true));
				if (neverEnding == false) {
					generateWarningOnEmpty(terminatorsArray, name, DefinitionType.TEMPORAL_CONTEXT,
							ErrorElement.TERMINATORS);
				}
				if (terminatorsArray == null) {
					// to avoid null exception
					terminatorsArray = new JSONArray();
				}
				List<ContextInitiator> contextInitiators = new ArrayList<ContextInitiator>();
				List<ContextTerminator> contextTerminators = new ArrayList<ContextTerminator>();

				// parse initiators
				Set<String> eventInitiatorNames = new HashSet<String>();
				for (int rowNumber = 0; rowNumber < initiatorsArray.size(); rowNumber++) {
					try {
						JSONObject initiatorObject = (JSONObject)initiatorsArray.get(rowNumber);
						// must be in enum
						ContextInitiatorType initiatorType = tryException(new ContextInitiatorTypeCheck(
								"initiatorType", initiatorObject, name, DefinitionType.TEMPORAL_CONTEXT,
								ErrorElement.INITIATOR_TYPE, rowNumber));
						// must be in enum
						ContextInitiatorPolicyEnum initiatorPolicy = tryException(new ContextInitiatorPolicyEnumChecker(
								"initiatorPolicy", initiatorObject, name, DefinitionType.TEMPORAL_CONTEXT,
								ErrorElement.INITIATOR_POLICY, rowNumber));

						ContextInitiator initiator = null;

						switch (initiatorType) {
						case EVENT:
							// must exist, warning on repeats
							String eventName = tryException(new NullCheckerAndWarningRepeats<String>(
									eventInitiatorNames, "name", initiatorObject, name,
									DefinitionType.TEMPORAL_CONTEXT, ErrorElement.INITIATOR_NAME, stringParser,
									rowNumber));
							
							IEventType initiatorEventType = metadataFacade.getEventMetadataFacade().getEventType(
									eventName);
							checkElementDefined(initiatorEventType, name, DefinitionType.TEMPORAL_CONTEXT,
									ErrorElement.INITIATOR_NAME, rowNumber, eventName);

							String condition = tryException(new LegalNullChecker<String>("condition", initiatorObject,
									name, DefinitionType.TEMPORAL_CONTEXT, ErrorElement.INITIATOR_CONDITION,
									stringParser, rowNumber));
							
							// error on non-existant
							IExpression parsedCondition = null;
							if (condition != null) {
								List<IDataObjectMeta> signature = new ArrayList<IDataObjectMeta>();
								signature.add(initiatorEventType);
								try {
									parsedCondition =  eep.createExpression(condition, signature);
								} catch (Exception e) {
									// this is caught here in order to add to exception
									handleEEPException(name, DefinitionType.TEMPORAL_CONTEXT, ErrorElement.INITIATOR_CONDITION,
											rowNumber, ProtonParseException.DEFAULT_INDEX, e);
								}
							}
							eventInitiatorNames.add(eventName);
							initiator = new ContextEventInitiator(initiatorEventType, parsedCondition,
									condition, initiatorPolicy);
							break;
						case ABSOLUTE_TIME:
							// must be in time format
							Date initiationTime = tryException(new NullChecker<Date>("timeStamp", initiatorObject,
									name, DefinitionType.TEMPORAL_CONTEXT, ErrorElement.INITIATION_TIME,
									dateParser, rowNumber));

							// if exists, must be in proper format
							Long repeatingInterval = tryException(new LegalNullChecker<Long>("repeatingInterval",
									initiatorObject, name, DefinitionType.TEMPORAL_CONTEXT,
									ErrorElement.REPEATING_INTERVAL, longParser, rowNumber));
							
							final int DEFAULT_VALUE = -1;
							initiator = new ContextAbsoluteTimeInitiator(initiationTime,
									repeatingInterval != null,
									repeatingInterval != null ? repeatingInterval : DEFAULT_VALUE, initiatorPolicy);
							break;
						}

						contextInitiators.add(initiator);
					} catch (MetadataParseException e) {
						continue;
					}
				}

				// parse terminators
				Set<String> eventTerminatorNames = new HashSet<String>();
				for (int rowNumber = 0; rowNumber < terminatorsArray.size(); rowNumber++) {
					try {
						JSONObject terminatorObject = (JSONObject) terminatorsArray.get(rowNumber);						
						ContextTerminatorTypeEnum terminatorType = tryException(new ContextTerminatorTypeCheck(
								"terminatorType", terminatorObject, name, DefinitionType.TEMPORAL_CONTEXT,
								ErrorElement.TERMINATOR_TYPE, rowNumber));

						ContextTerminationTypeEnum terminationType = tryException(new ContextTerminationTypeCheck(
								"terminationType", terminatorObject, name, DefinitionType.TEMPORAL_CONTEXT,
								ErrorElement.TERMINATION_TYPE, rowNumber));
						ContextTerminatorPolicyEnum terminatorPolicy;
						// relative time has no terminator policy, select first
						// as default
						if (terminatorType != ContextTerminatorTypeEnum.RELATIVE_TIME) {
							terminatorPolicy = tryException(new ContextTerminatorPolicyChecker(
									"terminatorPolicy", terminatorObject, name, DefinitionType.TEMPORAL_CONTEXT,
									ErrorElement.TERMINATOR_POLICY, rowNumber));
						} else {
							terminatorPolicy = ContextTerminatorPolicyEnum.FIRST;
						}

						ContextTerminator terminator;
						switch (terminatorType) {
						case EVENT:
							// must exist, warning on repeats
							String eventName = tryException(new NullCheckerAndWarningRepeats<String>(
									eventTerminatorNames, "name", terminatorObject, name,
									DefinitionType.TEMPORAL_CONTEXT, ErrorElement.TERMINATOR_NAME, stringParser,
									rowNumber));

							IEventType terminatorEventType = metadataFacade.getEventMetadataFacade().getEventType(
									eventName);

							// error on non-existent
							checkElementDefined(terminatorEventType, name, DefinitionType.TEMPORAL_CONTEXT,
									ErrorElement.TERMINATOR_NAME, rowNumber, eventName);
							String condition = tryException(new LegalNullChecker<String>("condition", terminatorObject,
									name, DefinitionType.TEMPORAL_CONTEXT, ErrorElement.TERMINATOR_CONDITION,
									stringParser, rowNumber));
							
							IExpression parsedCondition = null;
							if (condition != null) {
								//can be a simple condition based only on the terminator attributes or a complex condition based also on initiator attributes
																
								List<IDataObjectMeta> signature = new ArrayList<IDataObjectMeta>();
								signature.add(terminatorEventType);
								
								//if we have only one initiator, of type event and add policy of IGNORE, and it is different than the terminator event (not the same event acting 
								//as both initiator and terminator)- will build signature based both on initiator and terminator, cause it might be
								//a complex condition
								//otherwise it must be a simple condition
								if (contextInitiators.size() == 1 && 
										contextInitiators.get(0) instanceof ContextEventInitiator && 
										((ContextEventInitiator)contextInitiators.get(0)).getInitiationPolicy().equals(ContextInitiatorPolicyEnum.IGNORE)&&
										((ContextEventInitiator)contextInitiators.get(0)).getInitiatorType().getName() != terminatorEventType.getName())
								{
									signature.add(((ContextEventInitiator)contextInitiators.get(0)).getInitiatorType());
								}
								try {
									parsedCondition =  eep.createExpression(condition, signature);
								} catch (Exception e) {
									// this is caught here in order to add to exception
									handleEEPException(name, DefinitionType.TEMPORAL_CONTEXT,
											ErrorElement.TERMINATOR_CONDITION, rowNumber,
											ProtonParseException.DEFAULT_INDEX, e);
								}
							}
							eventTerminatorNames.add(eventName);
							terminator = new ContextEventTerminator(terminatorEventType, parsedCondition,
									condition, terminatorPolicy, terminationType);
							break;
						case ABSOLUTE_TIME:
							// must be in time format
							Date terminationTime = tryException(new NullChecker<Date>("timeStamp",
									terminatorObject, name, DefinitionType.TEMPORAL_CONTEXT,
									ErrorElement.TERMINATOR_TIMESTAMP, dateParser, rowNumber));
							terminator = new ContextAbsoluteTimeTerminator(terminationTime,
									terminatorPolicy, terminationType);
							break;
						case RELATIVE_TIME:
							Long relativeMiliSecs = tryException(new NullChecker<Long>("relativeTime",
									terminatorObject, name, DefinitionType.TEMPORAL_CONTEXT,
									ErrorElement.TERMINATOR_RELATIVE_TIME, longParser, rowNumber));
							terminator = new ContextRelativeTimeTerminator(relativeMiliSecs,
									terminatorPolicy, terminationType);
							break;
						default: terminator = null; // only for assignment correctness
						}

						contextTerminators.add(terminator);
					} catch (MetadataParseException e) {
						continue;
					}
				}
				TemporalContextType temporalContextType = null;
				// temporal context can (currently) be one of two types:
				// temporal interval and temporal sliding window; for the former we create TemporalContextType,
				// and for the latter we create SlidingTimeWindow (inherits TemporalContextType)
				switch (contextType) {
				case TEMPORAL_INTERVAL:
					temporalContextType = new TemporalContextType(name,
							contextInitiators, contextTerminators, ContextIntervalPolicyEnum.CLOSED,
							ContextIntervalPolicyEnum.OPEN, atStartUp, neverEnding);	
					break;
				case SLIDING_TIME_WINDOW:
					// get duration and sliding period and initiate new sliding temporal context
					Long duration = tryException(new NullChecker<Long>("duration",
							temporalContext,name,DefinitionType.TEMPORAL_CONTEXT,
							ErrorElement.SLIDING_WINDOW_DURATION,
							longParser));

					Long period = tryException(new NullChecker<Long>("slidingPeriod",
							temporalContext,name,DefinitionType.TEMPORAL_CONTEXT,
							ErrorElement.SLIDING_WINDOW_PERIOD,
							longParser));
					
					temporalContextType = new SlidingTimeWindow(name,
							contextInitiators, contextTerminators, ContextIntervalPolicyEnum.CLOSED,
							ContextIntervalPolicyEnum.OPEN, atStartUp, neverEnding,
							duration,period);					
					
					break;					
				}
				contexts.put(name, temporalContextType);
			}
			catch (MetadataParseException e) {
				continue;
			}
		}

		return contexts;

	}



	/**
	 * Attempts to perform a parsing action, and check for any parsing errors
	 * 
	 * @param action
	 *            The action to perform
	 * @return The element parsed, or null if the element was not parsed
	 *         successfully, but it is possible to continue
	 * @throws MetadataParseException
	 *             if the element was not parsed successfully, and the exception
	 *             stops the current iteration
	 */
	protected <T> T tryException(IActionParser<T> action) throws MetadataParseException {
		T element = action.tryParse();
		if (action.checkElementParsed(element) == false) {
			ProtonParseException exception = action.getException();
			exceptions.add(exception);
			if (exception.stopIteration()) {
				throw new MetadataParseException();
			} else {
				return element;
			}
		}

		return element;
	}

	
	
	protected abstract void parseConsumerProducer(JSONArray consumers, JSONArray producers) throws ParseException ;
	
	

	
			
			
	//TODO : for now removed the call since not all properties are supported yet
	//TODO: the name of the properties in JSON and here vary, that's why this method will always return exceptions - correct!
	private void checkRequiredProperties(String type, Set<String> propertiesSet, String instanceName, boolean isProducer)
			throws MetadataParseException {
		Set<String> requiredProperties = new HashSet<String>();
		// check required properties
		if (type == "DB") {
			// must have "host-name", "host-port", "database-name",
			// "table-name", "query"
			requiredProperties.add("Host-name");
			requiredProperties.add("Host-port");
			requiredProperties.add("Database-name");
			requiredProperties.add("Table-name");
			requiredProperties.add("Query");
		}
		if (type == "FILE") {
			// must have "filename", "format"
			requiredProperties.add("Filename");
			requiredProperties.add("Format");
			if (isProducer) {
				requiredProperties.add("Delay");
			}
		}
		if (type == "JMS") {
			// must have "host-name", "host-port", "factory-name", "queue-name",
			// "object-class-name"
			requiredProperties.add("Host-name");
			requiredProperties.add("Host-port");
			requiredProperties.add("Factory-name");
			requiredProperties.add("Queue-name");
			requiredProperties.add("Object-class-name");
		}

		// check required is subset
		for (String requiredName : requiredProperties) {
			if (propertiesSet.contains(requiredName) == false) {
				exceptions.add(new ProtonParseException(ParseErrorEnum.MISSING_REQUIRED_ATTRIBUTES, instanceName,
						isProducer ? DefinitionType.PRODUCER : DefinitionType.CONSUMER, ErrorType.ERROR,
						ErrorElement.PROPERTIES, ProtonParseException.DEFAULT_INDEX,
						ProtonParseException.DEFAULT_INDEX, true, requiredName));
				throw new MetadataParseException();
			}
		}
	}

	

	private void handleParseException(String eventName, int i) {
		exceptions.add(new ProtonParseException(ParseErrorEnum.INVALID_JSON_FORMAT, eventName,
				DefinitionType.EVENT, ErrorType.ERROR, ErrorElement.ATTRIBUTE_TYPE_DEFAULT_VALUE, i));
	}

	protected abstract void clear();
}
