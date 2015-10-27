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
 /**
 *  Error messages (error strings, error list) 
 */

function ATErrorMessage() {}
ATErrorMessage.ErrorMsg = { 
		NULL_ELEMENT:"missing required element",
		NAME_ALREADY_EXISTS:"name already exists",
		BAD_ENUM_VALUE:"bad enumeration value",
		EEP_ERROR:"expression error",
		INVALID_JSON_FORMAT:"invalid input definition file format",
		MISSING_REQUIRED_ATTRIBUTES:"missing required attributes",
		EMPTY_COLLECTION:"empty collection",
		INDEX_OUT_OF_RANGE:"index out of range",
		ELEMENT_NOT_DEFINED:"element is not defined",
		BAD_TYPE:"unsupported type",
		MISSING_ALIAS:"missing required alias",
		REPEATING_ALIAS:"alias is already used",
		ATTRIBUTE_ALREADY_EXISTS: "attribute already exists",
		NO_DEFAULT_VALUE: "missing default value",
		INPUT_EVENT_NOT_DEFINED_IN_CONTEXT_PARTICIPATING_EVENTS: "input event is not defined in context's participant events",
		BAD_VALUE: "bad value",
		
		
		//general
		"ParseError":"Error parsing definition. ",
		"MissingDefinitionName": "Missing definition name. ",
		"ErrorParsingGeneralInfo": "Error parsing general information. ",
		"IllegalDefinitionName":"Illegal definition name",
		"NameUniquenessErrorEvent": "There is already an event with this name. ",
		"NameUniquenessErrorEPA": "There is already an EPA with this name. ",
		"NameUniquenessErrorTemporalContext": "There is already a temporal context with this name. ",
		"NameUniquenessErrorSegmentationContext": "There is already a segmentation context with this name. ",
		"NameUniquenessErrorCompositeContext": "There is already a composite context with this name. ",
		
		//event related
		"MissingEventAttributes": "Missing event attributes. ",
		"EventError":"Error parsing event. ",
		"EventAttributeName": "Error parsing attribute name. ",
		"EventAttributeType": "Error parsing attribute type. ",
		"EventAttributeDimension": "Error parsing attribute dimension. ",
		"EventAttributeDefaultValue": "Error parsing attribute default value. ",
		"EventAttributeOutOfRange": "Error parsing event attribute. Attribute out of range. "
		
		
		//epa related
		
		
		//temporal context related
		
		
		//segmentation context related
			
};

ATErrorMessage.ErrorElement = { 
	NAME:"Name",
	ATTRIBUTES:"Attributes",
	ATTRIBUTE_NAME:"Attribute Name",
	ATTRIBUTE_TYPE:"Attribute Type",
	ATTRIBUTE_DIMENSION_SIZE:"Attribute Dimension",
	ATTRIBUTE_TYPE_DEFAULT_VALUE:"Attribute Default Value",
	TYPE:"Type",
	AT_START_UP:"At Start Up",
	NEVER_ENDING:"Never Ending",
	INITIATORS:"Initiators",
	TERMINATORS:"Terminators",
	INITIATOR_TYPE:"Initiator Type",
	INITIATOR_POLICY:"Initiator Correlation Policy",
	INITIATOR_NAME:"Initiator Name",
	INITIATOR_CONDITION:"Initiator Condition",
	INITIATION_TIME:"Initiator Time",
	REPEATING_INTERVAL:"Repeating Interval",
	TERMINATOR_TYPE:"Terminator Type",
	TERMINATION_TYPE:"Termination Type",
	TERMINATOR_POLICY:"Terminator Policy",
	TERMINATOR_NAME:"Terminator Name",
	TERMINATOR_CONDITION:"Terminator Condition",
	TERMINATOR_TIMESTAMP:"Terminator Timestamp",
	TERMINATOR_RELATIVE_TIME:"Terminator Relative Time",
	PROPERTIES:"Properties",
	SENDING_DELAY:"Sending Delay",
	PROPERTY_NAME:"Property Name",
	PROPERTY_VALUE:"Property Value",
	POLLING_INTERVAL:"Polling Interval",
	PARTICIPAING_EVENTS:"Participaing Event",
	EVENT_NAME:"Event Name",
	EVENT_EXPRESSION:"Participant Event Expression",
	CONTEXT_NAME:"Context Name",
	INPUT_EVENTS:"Input Events",
	INPUT_EVENT_NAME:"Input Event Name",
	FILTER_EXPRESSION:"Filter Expression",
	CONSUMPTION_POLICY:"Consumption Policy",
	INSTANCE_SELECTION_POLICY:"Instance Selection Policy",
	ORDER_POLICY:"Order Policy",
	N:"N",
	ASSERTION:"Assertion",
	DERIVATIONS:"Derivations",
	DERIVATION_CONDITION:"Derivation Conditions",	
	DERIVATION_EXPRESSIONS:"Derivation Expression",
	COMPOSITE_CONTEXT:"Composite Context",
	MEMBER_CONTEXT_NAME:"Context Name",
	DERIVATION_NAME:"Derivation Name",
	DERIVATION_EXPRESSION:"Derivation Expression",
	RANKING_RELATION:"Ranking Relation",
	EVALUATION_POLICY:"Evaluation Policy",
	CARDINALITY_POLICY:"Cardinality Policy",
	INTERNAL_SEGMENTATION_CONTEXT_NAME:"Internal Segmentation Context Name",
	INTERNAL_SEGMENTATION_CONTEXT:"Internal Segmentation Context",
	EXPRESSION:"Expression",
	COMPUTED_VARIABLES:"Computed Variables",
	COMPUTED_VARIABLE_NAME:"Computed Variables Name",
	AGGREGATION_TYPE:"Aggregation Type",
	OPERAND:"Operand",
	CONSUMER_EVENT_CONDITION:"Received Event Condition",
	CONSUMER_EVENT_NAME:"Received Event Name",
	CONSUMER_EVENT:"Consumer Event",
	PRODUCER_EVENT_NAME:"Producer Event Name",
	PRODUCER_EVENT_CONDITION:"Producer Event Condition",
	CONSUMER_EVENTS_ACTIONS:"Consumer Events Actions",
	SLIDING_WINDOW_DURATION:"Window duration",
	SLIDING_WINDOW_PERIOD: "Sliding period",

};

ATErrorMessage.addError = function(error, definitionName){
	if (!ATErrorMessage._errorList[definitionName]){
		ATErrorMessage._errorList[definitionName]=new Array();
	}
	ATErrorMessage._errorList[definitionName].push(error);
};

ATErrorMessage.getDefinitionErrors = function(name){
	return ATErrorMessage._errorList[name] || null; 
};

ATErrorMessage.clearAllErrors = function(){
	
	ATErrorMessage.initErrorList();	
};

//_errorList holds error array for each specific definition that has errors  
ATErrorMessage.initErrorList = function(){
	          
	ATErrorMessage._errorList = {};
};

ATErrorMessage.errorType = {
		ERROR: "Error",
		WARNING:"Warning"
}; 
								








