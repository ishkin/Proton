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
function TemplateEnum() {
}

TemplateEnum.TemplateType = {
		AbsenceEventInitiator : "AbsenceEventInitiator",
		Filter: "Filter",
		Count: "Count",
		Trend: "Trend"
	};

TemplateEnum.TemplateBuiltInAttributes = {
		"AbsenceEventInitiator" : [
		             {"name": "$InputEvent$", "value":"","description":"The name of the input event for the absence EPA (the event which is absent). This event should already be defined by the user"},
				  	  {"name": "$OutputEvent$", "value":"","description":"The name of derived event if an absence is detected. This event should already be defined by the user"},
				  	  {"name": "$InitiatorEvent$", "value":"","description":"The name of the event initiating the context for the absence EPA.This event should already be defined by the user"},
				      {"name": "$ContextWindowSize$", "value":"","description":"The length of the temporal window in millis. During this time, starting from initiator event, an absence/presence of the input event will be monitored"},
				      {"name": "$DerivedAttributeName$", "value":"","description":"The name of the attribute in the derived event derived as a result of absence match"},
				      {"name": "$DerivedAttributeExpression$", "value":"","description":"The expression for the derived attribute. Can be a constant value,or the partition of the segmentation context."},
				      {"name": "$InputEventSegmentationAttributeExpr$", "value":"","description":"The segmentation expression of the input event for the segmentation context"},
				      {"name": "$InitiatorEventSegmentationAttributeExpr$", "value":"","description":"The segmentation expression of the initiator event for segmentation context"}, 
				     ],
		"Filter" : [
				      {"name": "$InputEvent$", "value":"","description":"The name of the input event for the filter EPA. This event should already be defined by the user"},
				      {"name": "$FilterExpression$", "value":"","description":"The condition on the input event which has to evaluate to true in order to derive output event"},
				      {"name": "$OutputEvent$", "value":"","description":"The name of derived event if the input event passes the filter. This event should already be defined by the user"},				      
				      {"name": "$DerivedAttributeName$", "value":"","description":"The name of the attribute in the derived event derived as a result of filter evaluation"},
				      {"name": "$DerivedAttributeExpression$", "value":"","description":"The expression for the derived attribute. Can be a constant value,or based on an attribute of input event"},
			 ],
		"Count" : [
			     	{"name": "$InputEvent$", "value":"","description":"The name of the input event for the count EPA. This event should already be defined by the user"},
			       	{"name": "$OutputEvent$", "value":"","description":"The name of derived event for count EPA. This event should already be defined by the user"},
			       	{"name": "$InitiatorEvent$", "value":"","description":"The name of the event initiating the context for the count EPA.This event should already be defined by the user"},
			       	{"name": "$TemporalContextDuration$", "value":"","description":"The length of the temporal window in millis. During this time, starting from initiator event, the count will be calculated"},			       
			       	{"name": "$DerivedAttributeName$", "value":"","description":"The name of the attribute in the derived event which will hold the count result"},			       				      
			       	{"name": "$InputEventSegmentationExpression$", "value":"","description":"The segmentation expression of the input event for the segmentation context"},
			       	{"name": "$InitiatorEventSegmentationExpression$", "value":"","description":"The segmentation expression of the initiator event for segmentation context"}, 
			      ],
	    "Trend" : [
					{"name": "$InputEvent$", "value":"","description":"The name of the input event for the trend EPA. This event should already be defined by the user"},
				    {"name": "$OutputEvent$", "value":"","description":"The name of derived event for trend EPA. This event should already be defined by the user"},
					{"name": "$InitiatorEvent$", "value":"","description":"The name of the event initiating the context for the trend EPA.This event should already be defined by the user"},
					{"name": "$TemporalContextDuration$", "value":"","description":"The length of the temporal window in millis. During this time, starting from initiator event, the trend will be calculated"},
					{"name": "$InputEventTrendExpression$", "value":"","description":"The expression over attributes of input event on which the trend will be monitored"},
					{"name": "$TrendThreshold$", "value":"","description":"An integer value N representing the trend threshold. If we have a trend of N events, a derived event will be emitted"},
					{"name": "$TrendRelation$", "value":"","description":"An string value N representing the trend relation. Possible values are 'Increase','Decrease' or 'Stable'"},				
					{"name": "$DerivedAttributeName$", "value":"","description":"The name of the attribute in the derived event. "},
					{"name": "$DerivedAttributeExpression$", "value":"","description":"The expression for the derived attribute. It can be a trend count (use 'trend.count' for that), a segmentation partition value or a constant value"},			
			       	{"name": "$InputEventSegmentationExpression$", "value":"","description":"The segmentation expression of the input event for the segmentation context"},
			       	{"name": "$InitiatorEventSegmentationExpression$", "value":"","description":"The segmentation expression of the initiator event for segmentation context"}, 
			      ],			      
};

TemplateEnum.Templates = {
		"AbsenceEventInitiator" : {
		    "epas": [
		             {
		               "name": "AbsenceEPA",		               
		               "epaType": "Absence",
		               "context": "CompositeContext",
		               "inputEvents": [
		                 {
		                   "name": "$InputEvent$",
		                   "consumptionPolicy": "Consume",
		                   "instanceSelectionPolicy": "First"
		                 }
		               ],
		               "computedVariables": [],
		               "evaluationPolicy": "Deferred",
		               "cardinalityPolicy": "Single",
		               "internalSegmentation": [],
		               "derivedEvents": [
		                 {
		                   "name": "$OutputEvent$",
		                   "reportParticipants": false,
		                   "expressions": {
		                     "Duration": "0",
		                     "$DerivedAttributeName$":"$DerivedAttributeExpression$"
		                   }
		                 }
		               ]
		             }
		           ],
		           "contexts": {
		             "temporal": [
		               {
		                 "name": "TemporalContext",		                 
		                 "type": "TemporalInterval",
		                 "atStartup": false,
		                 "neverEnding": false,
		                 "initiators": [
		                   {
		                     "initiatorType": "Event",
		                     "initiatorPolicy": "Ignore",
		                     "name": "$InitiatorEvent$"
		                   }
		                 ],
		                 "terminators": [
		                   {
		                     "terminatorType": "RelativeTime",
		                     "terminationType": "Terminate",
		                     "relativeTime": "$ContextWindowSize$"
		                   }
		                 ]
		               }
		             ],
		             "segmentation": [
		               {
		                 "name": "SegmentationContext",		               
		                 "participantEvents": [
		                   {
		                     "name": "$InputEvent$",
		                     "expression": "$InputEventSegmentationAttributeExpr$",              
		                   },
		                   {
		                     "name": "$InitiatorEvent$",
		                     "expression": "$InitiatorEventSegmentationAttributeExpr$",              
		                   }		                   
		                   
		                 ]
		               }
		             ],
		             "composite": [
		               {
		                 "name": "CompositeContext",		                 
		                 "temporalContexts": [
		                   {
		                     "name": "TemporalContext"
		                   }
		                 ],
		                 "segmentationContexts": [
		                   {
		                     "name": "SegmentationContext"
		                   }
		                 ]
		               }
		             ]
		           }
		},
		"Filter" : {
			"epas": [
			         {
			           "name": "FilterEPA",			          
			           "epaType": "Basic",
			           "context": "TemporalAlwaysContext",
			           "inputEvents": [
			             {
			               "name": "$InputEvent$",
			               "filterExpression": "$FilterExpression$",
			               "consumptionPolicy": "Consume",
			               "instanceSelectionPolicy": "First"
			             }
			           ],
			           "computedVariables": [],
			           "evaluationPolicy": "Immediate",
			           "cardinalityPolicy": "Unrestricted",
			           "internalSegmentation": [],
			           "derivedEvents": [
			             {
			               "name": "$OutputEvent$",
			               "reportParticipants": false,
			               "expressions": {
			                 "Duration": "0",
			                 "$DerivedAttributeName$": "$DerivedAttributeExpression$"
			               }
			             }
			           ]
			         }
			       ],
			       "contexts": {
			         "temporal": [
			           {
			             "name": "TemporalAlwaysContext",
			             "createdDate": "Tue Oct 21 2014",
			             "type": "TemporalInterval",
			             "atStartup": true,
			             "neverEnding": true,
			             "initiators": [],
			             "terminators": []
			           }
			         ]
			         
			       }
			     
		},
		"Count":
		{
			    "epas": [
				      {
				        "name": "CountEPA",
				        "epaType": "Aggregate",
				        "context": "CountCompositeContext",
				        "inputEvents": [
				          {
				            "name": "$InputEvent$",
				            "consumptionPolicy": "Consume",
				            "instanceSelectionPolicy": "First"
				          }
				        ],
				        "computedVariables": [
				          {
				            "name": "Count",
				            "aggregationType": "Count",
				            "$InputEvent$": "1"
				          }
				        ],
				        "evaluationPolicy": "Deferred",
				        "cardinalityPolicy": "Single",
				        "internalSegmentation": [],
				        "derivedEvents": [
				          {
				            "name": "$OutputEvent$",
				            "reportParticipants": false,
				            "expressions": {
				              "Duration": "0",
				              "$DerivedAttributeName$": "Count"
				            }
				          }
				        ]
				      }
				    ],
				    "contexts": {
				      "temporal": [
				        {
				          "name": "CountTemporalContext",				         
				          "type": "TemporalInterval",
				          "atStartup": false,
				          "neverEnding": false,
				          "initiators": [
				            {
				              "initiatorType": "Event",
				              "initiatorPolicy": "Ignore",
				              "name": "$InitiatorEvent$"
				            }
				          ],
				          "terminators": [
				            {
				              "terminatorType": "RelativeTime",
				              "terminationType": "Terminate",
				              "relativeTime": "$TemporalContextDuration$"
				            }
				          ]
				        }
				      ],
				      "segmentation": [
				        {
				          "name": "CountSegmentationContext",				        
				          "participantEvents": [
				            {
				              "name": "$InputEvent$",
				              "expression": "$InputEventSegmentationExpression$"
				            },
				            {
				              "name": "$InitiatorEvent$",
				              "expression": "$InitiatorEventSegmentationExpression$"
				            }
				          ]
				        }
				      ],
				      "composite": [
				        {
				          "name": "CountCompositeContext",				         
				          "temporalContexts": [
				            {
				              "name": "CountTemporalContext"
				            }
				          ],
				          "segmentationContexts": [
				            {
				              "name": "CountSegmentationContext"
				            }
				          ]
				        }
				      ]
				    },
				    
			},
			"Trend":
			{
				   "epas": [
				            {
				              "name": "TrendEPA",				            
				              "epaType": "Trend",
				              "context": "TrendCompositeContext",
				              "inputEvents": [
				                {
				                  "name": "$InputEvent$",
				                  "expression": "$InputEventTrendExpression$",
				                  "consumptionPolicy": "Consume",
				                  "instanceSelectionPolicy": "First"
				                }
				              ],
				              "computedVariables": [],
				              "evaluationPolicy": "Immediate",
				              "cardinalityPolicy": "Single",
				              "trendN": "$TrendThreshold$",
				              "trendRelation": "$TrendRelation$",
				              "internalSegmentation": [],
				              "derivedEvents": [
				                {
				                  "name": "$OutputEvent$",
				                  "reportParticipants": false,
				                  "expressions": {
				                    "Duration": "0",
				                    "$DerivedAttributeName$": "$DerivedAttributeExpression$"
				                  }
				                }
				              ]
				            }
				          ],
				          "contexts": {
				            "temporal": [
				              {
				                "name": "TrendTemporalContext",				                
				                "type": "TemporalInterval",
				                "atStartup": false,
				                "neverEnding": false,
				                "initiators": [
				                  {
				                    "initiatorType": "Event",
				                    "initiatorPolicy": "Ignore",
				                    "name": "$InitiatorEvent$"
				                  }
				                ],
				                "terminators": [
				                  {
				                    "terminatorType": "RelativeTime",
				                    "terminationType": "Terminate",
				                    "relativeTime": "$TemporalContextDuration$"
				                  }
				                ]
				              }
				            ],
				            "segmentation": [
				              {
				                "name": "TrendSegmentationContext",				              
				                "participantEvents": [
				                  {
				                    "name": "$InputEvent$",
				                    "expression": "$InputEventSegmentationExpression$"
				                  },
				                  {
				                    "name": "$InitiatorEvent$",
				                    "expression": "$InitiatorEventSegmentationExpression$"
				                  }
				                ]
				              }
				            ],
				            "composite": [
				              {
				                "name": "TrendCompositeContext",				                
				                "temporalContexts": [
				                  {
				                    "name": "TrendTemporalContext"
				                  }
				                ],
				                "segmentationContexts": [
				                  {
				                    "name": "TrendSegmentationContext"
				                  }
				                ]
				              }
				            ]
				          }
			}
		
};