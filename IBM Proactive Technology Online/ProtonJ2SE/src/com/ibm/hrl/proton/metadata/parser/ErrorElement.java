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

enum ErrorElement {
	NAME,
	ATTRIBUTES,
	ATTRIBUTE_NAME,
	ATTRIBUTE_TYPE,
	ATTRIBUTE_DIMENSION_SIZE,
	ATTRIBUTE_TYPE_DEFAULT_VALUE,
	TYPE,
	AT_START_UP,
	NEVER_ENDING,
	INITIATORS,
	TERMINATORS,
	INITIATOR_TYPE,
	INITIATOR_POLICY,
	INITIATOR_NAME,
	INITIATOR_CONDITION,
	INITIATION_TIME,
	REPEATING_INTERVAL,
	TERMINATOR_TYPE,
	TERMINATION_TYPE,
	TERMINATOR_POLICY,
	TERMINATOR_NAME,
	TERMINATOR_CONDITION,
	TERMINATOR_TIMESTAMP,
	TERMINATOR_RELATIVE_TIME,
	PROPERTIES,
	SENDING_DELAY,
	PROPERTY_NAME,
	PROPERTY_VALUE,
	POLLING_INTERVAL,
	PARTICIPAING_EVENTS,
	EVENT_NAME,
	EVENT_EXPRESSION,
	CONTEXT_NAME,
	INPUT_EVENTS,
	INPUT_EVENT_NAME,
	FILTER_EXPRESSION,
	CONSUMPTION_POLICY,
	INSTANCE_SELECTION_POLICY,
	ORDER_POLICY,
	N,
	ASSERTION,
	DERIVATIONS,
	DERIVATION_CONDITION,
	DERIVATION_EXPRESSIONS,
	COMPOSITE_CONTEXT,
	MEMBER_CONTEXT_NAME,
	DERIVATION_NAME,
	DERIVATION_EXPRESSION,
	RANKING_RELATION,
	EVALUATION_POLICY,
	CARDINALITY_POLICY,
	INTERNAL_SEGMENTATION_CONTEXT_NAME,
	INTERNAL_SEGMENTATION_CONTEXT,
	EXPRESSION,
	COMPUTED_VARIABLES,
	COMPUTED_VARIABLE_NAME,
	AGGREGATION_TYPE,
	TREND_RELATION_TYPE,
	TREND_EXPRESSIONS,
	TREND_TRESHOLD,
	OPERAND,
	CONSUMER_EVENT_CONDITION,
	CONSUMER_EVENT_NAME,
	CONSUMER_ACTION_NAME,
	CONSUMER_ACTION,
	CONSUMER_EVENT,
	CONSUMER_ACTION_CONDITION,
	PRODUCER_EVENT_NAME,
	PRODUCER_EVENT_CONDITION,
	CONSUMER_EVENTS_ACTIONS,
	SLIDING_WINDOW_DURATION,
	SLIDING_WINDOW_PERIOD;
	
	public String toString() {
		return capitalizeFirsts(super.toString().replace('_', ' '));
	}
	
	private String capitalizeFirsts(String str) {
		int index = str.indexOf(' ');
		
		if (str.length() <= 1) {
			return str.toUpperCase();
		}
		
		String tmpStr = new String(str.split(" ")[0]);
		tmpStr = Character.toUpperCase(tmpStr.charAt(0)) + tmpStr.substring(1).toLowerCase();
		if (index == -1) {
			return tmpStr;
		}
		return tmpStr + " " + capitalizeFirsts(str.substring(index+1));
	}
}
