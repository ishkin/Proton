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
 dojo.provide("widgets.Segmentation_events");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("widgets.SectionPane");
dojo.declare("widgets.Segmentation_events",
             [dijit._Widget, dijit._Templated],

			{
				// Set this to true if your widget contains other widgets
				widgetsInTemplate : true,

				// Path to the template
				templatePath: dojo.moduleUrl(
						"widgets",
						"Segmentation_events.html"),

				_participantEventsTable:null,
				tblParticipantEventsName:null,
				tblParticipantEventsColumns:[],

				// Override this method to perform custom behavior during dijit construction.
				// Common operations for constructor:
				// 1) Initialize non-primitive types (i.e. objects and arrays)
				// 2) Add additional properties needed by succeeding lifecycle methods
				constructor : function() {
					this.tblParticipantEventsColumns=[];
					this.myObject=ATVars.CURRENT_OBJECT;
			    	this.fieldName="Events";
			    	this.myEPN=ATVars.MY_EPN;
			    	this.gridhelper= new widgets.GridHelper();
				},

				// When this method is called, all variables inherited from superclasses are 'mixed in'.
				// Common operations for postMixInProperties
				// 1) Modify or assign values for widget property variables defined in the template HTML file
				postMixInProperties : function() {
				},

				// postCreate() is called after buildRendering().  This is useful to override when 
				// you need to access and/or manipulate DOM nodes included with your widget.
				// DOM nodes and widgets with the dojoAttachPoint attribute specified can now be directly
				// accessed as fields on "this". 
				// Common operations for postCreate
				// 1) Access and manipulate DOM nodes created in buildRendering()
				// 2) Add new DOM nodes or widgets 
				postCreate : function() {
					
				// set Event Participants table
				var eventParticipantsList=this.myEPN.getEventList();
			 	
				this.tblParticipantEventsColumns.push(this.gridhelper.setColumn("Event", "Selection", true, true, eventParticipantsList, "setSegmentationEvent", "getSegmentationEvent", this.myObject, null ));
				this.tblParticipantEventsColumns.push(this.gridhelper.setColumn("Expression", "String", true, true, null,"setSegmentationExpression", "getSegmentationExpression", this.myObject, null));
				
				this.tblParticipantEventsName="Participant Events";
			
				this._participantEventsTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblParticipantEventsColumns, tblName: this.tblParticipantEventsName, hasDel: true, metaObject: this.myObject, 
																	 metaAttrSize: "getNumberOfSegmentationEvents", delAttrMethod: "deleteSegmentationEvent", noShowRequiredButton:true  });
				this.segmentationEvents.appendChild(this._participantEventsTable.domNode);
				
			},
			
			startup: function(){
				this.inherited(arguments);
//				widgets.Segmentation_events.superclass.startup.apply(this, arguments);
				this.segmentationEventList.expandCollapse();
				this._participantEventsTable.startup();
				
		     }
							
		});
