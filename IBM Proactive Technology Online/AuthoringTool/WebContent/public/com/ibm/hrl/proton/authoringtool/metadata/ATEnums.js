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
 * global enums
 */

function ATEnum() {
}
ATEnum.EPNType = { // types of objects
	Event : "Event",
	EPA : "EPA",
	Temporal : "Temporal Context",
	Segmentation : "Segmentation Context",
	Composite : "Composite Context"
};

ATEnum.EPAType = {
	Basic : "Basic", // covered by filter
	Aggregate : "Aggregate",
	All : "All",
	Sequence : "Sequence",
	// RelativeN:"RelativeN", //not yet supported
	Trend : "Trend",
	Absence : "Absence"
};

ATEnum.ContextType = {
	Temporal : "Temporal",
	Segmentation : "Segmentation",
	Composite : "Composite"
};

ATEnum.TemporalContexts = {
	TemporalInterval : "TemporalInterval",
	SlidingTimeWindow : "SlidingTimeWindow"
// ,SlidingEventWindow: "SlidingEventWindow"//not supported yet
};
ATEnum.DefaultTemporalContexts = ATEnum.TemporalContexts.TemporalInterval;

ATEnum.SegmentationContexts = {
	Segmentation : "Segmentation" // default
	// ,FixedPartitions: "FixedPartitions" //not supported yet
};

ATEnum.EvaluationPolicy = {
	Immediate : "Immediate", // default
	Deferred : "Deferred"
};

ATEnum.CardinalityPolicy = {
	Single : "Single",
	Unrestricted : "Unrestricted" // default
};

ATEnum.ConsumptionPolicy = {
	Consume : "Consume",
	Reuse : "Reuse"
};

ATEnum.DefaultConsumptionPolicy = ATEnum.ConsumptionPolicy.Consume;

ATEnum.InstanceSelectionPolicy = {
	First : "First",
	Last : "Last"
	// ,Every: "Every",
	,
	Override : "Override"
};

ATEnum.DefaultInstanceSelectionPolicy = ATEnum.InstanceSelectionPolicy.First;

ATEnum.RankingRelation = {
	Lowest : "Lowest",
	Highest : "Highest",
	First : "First",
	Last : "Last"
};

ATEnum.DefaultRankingRelation = ATEnum.RankingRelation.Highest;

ATEnum.TrendRelation = {
		Increase : "Increase",
		Decrease : "Decrease",
		Stable : "Stable"
	};

ATEnum.DefaultTrendRelation = ATEnum.TrendRelation.Increase;

ATEnum.AggregationType = {
	Count : "Count",
	Sum : "Sum",
	Min : "Min",
	Max : "Max",
	Average : "Average"
};

ATEnum.InitiatorPolicy = {
	Add : "Add",
	Ignore : "Ignore"
// , Extends: "Extends"
// , Refresh: "Refresh"
};

ATEnum.TerminatorPolicy = {
	First : "First",
	Last : "Last",
	Each : "Each"
};

ATEnum.TerminationType = {
	Terminate : "Terminate",
	Discard : "Discard"
};

ATEnum.InitiatorType = {
	Event : "Event",
	AbsoluteTime : "AbsoluteTime"
};

ATEnum.TerminatorType = {
	Event : "Event",
	AbsoluteTime : "AbsoluteTime",
	RelativeTime : "RelativeTime"
};

ATEnum.Elements = {
	EventAttribute : 1,
	EventAttributeName : 2,
	EventAttributeType : 3,
	EventAttributeDimension : 4,
	EventAttributeDefaultValue : 5,

	DefinitionName : 6,
	DefinitionDescription : 7,
	DefinitionCreatedBy : 8,
	DefinitionCreatedDate : 9
};

ATEnum.Definitions = {
	Event : "Event",
	EPA : "EPA",
	TemporalContext : "TemporalContext",
	SegmentationContext : "SegmentationContext",
	CompositeContext : "CompositeContext",
	Consumer : "Consumer",
	Producer : "Producer",
	Template: "Template"
};

ATEnum.AttributeTypes = {
	Integer : "Integer",
	Long : "Long",
	Double : "Double",
	DateTime : "Date",
	String : "String",
	Boolean : "Boolean",
	Object : "Object"
};
ATEnum.AttributeDimension = [ 0, 1, 2 ];

ATEnum.InOutType = {
	File : "File",
	Timed: "Timed", 
	// Database:"DB", //currently not implemented
	// JMS : "JMS",   //does not supported in the open source version
	Rest : "Rest",
	Custom : "Custom"
};

ATEnum.ConsumerBuiltInAttributes = {
	"File" : [
			{
				"name" : "filename",
				"value" : "",
				"description" : ""
			},
			{
				"name" : "formatter",
				"value" : "tag",
				"description" : ""
			},
			{
				"name" : "delimiter",
				"value" : ";",
				"description" : ""
			},
			{
				"name" : "tagDataSeparator",
				"value" : "=",
				"description" : ""
			},
			{
				"name" : "SendingDelay",
				"value" : "1000",
				"description" : "Requested delay between events sent to the consumer"
			},
			{
				"name" : "dateFormat",
				"value" : "dd/MM/yyyy-HH:mm:ss",
				"description" : "Optional formatter for date type attributes. The default formatter is dd/MM/yyyy-HH:mm:ss"
			} ],
	"Timed" : [
			{
				"name" : "filename",
				"value" : "",
				"description" : ""
			},
			{
				"name" : "formatter",
				"value" : "tag",
				"description" : ""
			},
			{
				"name" : "delimiter",
				"value" : ";",
				"description" : ""
			},
			{
				"name" : "tagDataSeparator",
				"value" : "=",
				"description" : ""
			},
			{
				"name" : "SendingDelay",
				"value" : "1000",
				"description" : "Requested delay between events sent to the consumer"
			},
			{
				"name" : "dateFormat",
				"value" : "dd/MM/yyyy-HH:mm:ss",
				"description" : "Optional formatter for date type attributes. The default formatter is dd/MM/yyyy-HH:mm:ss"
			} ],
	"JMS" : [
			{
				"name" : "hostname"
			},
			{
				"name" : "port"
			},
			{
				"name" : "connectionFactory"
			},
			{
				"name" : "destinationName"
			},
			{
				"name" : "timeout"
			},
			{
				"name" : "formatter",
				"value" : "tag"
			},
			{
				"name" : "delimiter",
				"value" : ";"
			},
			{
				"name" : "tagDataSeparator",
				"value" : "="
			},
			{
				"name" : "dateFormat",
				"value" : "dd/MM/yyyy-HH:mm:ss",
				"description" : "Optional formatter for date type attributes. The default formatter is dd/MM/yyyy-HH:mm:ss"
			} ],

	"Rest" : [
			{
				"name" : "URL"
			},
			{
				"name" : "contentType",
				"value" : "text/plain"
			},
			{
				"name" : "formatter",
				"value" : "tag"
			},
			{
				"name" : "delimiter",
				"value" : ";"
			},
			{
				"name" : "tagDataSeparator",
				"value" : "="
			},
			{
				"name" : "dateFormat",
				"value" : "dd/MM/yyyy-HH:mm:ss",
				"description" : "Optional formatter for date type attributes. The default formatter is dd/MM/yyyy-HH:mm:ss"
			},
			{
				"name" : "AuthToken",
				"value" : "",
				"description" : "Optional X-Auth-Token that will be added to the header"
			}],
	"DB" : [
			{
				"name" : "host-name"
			},
			{
				"name" : "host-port"
			},
			{
				"name" : "database-name"
			},
			{
				"name" : "table-name"
			},
			{
				"name" : "query"
			},
			{
				"name" : "dateFormat",
				"value" : "dd/MM/yyyy-HH:mm:ss",
				"description" : "Optional formatter for date type attributes. The default formatter is dd/MM/yyyy-HH:mm:ss"
			} ],
	"Custom" : [ {
		"name" : "classname"
	} ]
};

ATEnum.ProducerBuiltInAttributes = {
	"File" : [
			{
				"name" : "filename",
				"description" : ""
			},
			{
				"name" : "pollingInterval",
				"description" : ""
			},
			{
				"name" : "sendingDelay",
				"description" : ""
			},
			{
				"name" : "formatter",
				"value" : "tag",
				"description" : ""
			},
			{
				"name" : "delimiter",
				"value" : ";",
				"description" : ""
			},
			{
				"name" : "tagDataSeparator",
				"value" : "=",
				"description" : ""
			},
			{
				"name" : "dateFormat",
				"value" : "dd/MM/yyyy-HH:mm:ss",
				"description" : "Optional formatter for date type attributes. The default formatter is dd/MM/yyyy-HH:mm:ss"
			} ],
	"Timed" : [
			{
				"name" : "filename",
				"description" : ""
			},
			{
				"name" : "formatter",
				"value" : "tag",
				"description" : ""
			},
			{
				"name" : "delimiter",
				"value" : ";",
				"description" : ""
			},
			{
				"name" : "tagDataSeparator",
				"value" : "=",
				"description" : ""
			},
			{
				"name" : "dateFormat",
				"value" : "dd/MM/yyyy-HH:mm:ss",
				"description" : "Optional formatter for date type attributes. The default formatter is dd/MM/yyyy-HH:mm:ss"
			} ],
	"JMS" : [
			{
				"name" : "hostname"
			},
			{
				"name" : "port"
			},
			{
				"name" : "connectionFactory"
			},
			{
				"name" : "destinationName"
			},
			{
				"name" : "timeout"
			},
			{
				"name" : "pollingInterval",
				"description" : ""
			},
			{
				"name" : "sendingDelay",
				"description" : ""
			},
			{
				"name" : "formatter",
				"value" : "tag"
			},
			{
				"name" : "delimiter",
				"value" : ";"
			},
			{
				"name" : "tagDataSeparator",
				"value" : "="
			},
			{
				"name" : "dateFormat",
				"value" : "dd/MM/yyyy-HH:mm:ss",
				"description" : "Optional formatter for date type attributes. The default formatter is dd/MM/yyyy-HH:mm:ss"
			} ],
	"Rest" : [
			{
				"name" : "URL"
			},
			{
				"name" : "contentType",
				"value" : "text/plain"
			},
			{
				"name" : "sendingDelay"
			},
			{
				"name" : "pollingInterval"
			},
			{
				"name" : "pollingMode",
				"value" : "BATCH",
				"description" : "BATCH/SINGLE"
			},
			{
				"name" : "formatter",
				"value" : "tag"
			},
			{
				"name" : "delimiter",
				"value" : ";"
			},
			{
				"name" : "tagDataSeparator",
				"value" : "="
			},
			{
				"name" : "dateFormat",
				"value" : "dd/MM/yyyy-HH:mm:ss",
				"description" : "Optional formatter for date type attributes. The default formatter is dd/MM/yyyy-HH:mm:ss"
			} ],

	"DB" : [
			{
				"name" : "host-name"
			},
			{
				"name" : "host-port"
			},
			{
				"name" : "database-name"
			},
			{
				"name" : "table-name"
			},
			{
				"name" : "query"
			},
			{
				"name" : "dateFormat",
				"value" : "dd/MM/yyyy-HH:mm:ss",
				"description" : "Optional formatter for date type attributes. The default formatter is dd/MM/yyyy-HH:mm:ss"
			} ],
	"Custom" : [ {
		"name" : "classname"
	} ]
};

ATEnum.EventBuiltInAttributes = [
		{
			"name" : "Certainty",
			"type" : "Double",
			"defaultValue" : "1",
			"description" : "The certainty that this event happen (value between 0 to 1)"
		},
		{
			"name" : "OccurrenceTime",
			"type" : "Date",
			"description" : "No value means it equals the event detection time, other option is to use one of the defined distribution functions with parameters"
		},
		{
			"name" : "ExpirationTime",
			"type" : "Date"
		},
		{
			"name" : "Cost",
			"type" : "Double",
			"description" : "The cost of this event occurrence. Negative if this is an opportunity"
		},
		// {"name":"EventSource", "type":"String", "description":"The source of
		// this event"},//seems to be assigned by the engine/adapter
		{
			"name" : "Duration",
			"type" : "Double",
			"defaultValue" : "0",
			"description" : "Used in case the this event occur within an interval"
		} ];

