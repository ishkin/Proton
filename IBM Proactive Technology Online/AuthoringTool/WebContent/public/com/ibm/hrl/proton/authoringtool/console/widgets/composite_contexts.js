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
 dojo.provide("widgets.composite_contexts");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("widgets.SectionPane");
dojo.declare("widgets.composite_contexts",
             [dijit._Widget, dijit._Templated],
{
			// Path to the template
			templatePath: dojo.moduleUrl("widgets","composite_contexts.html"),
			
			// Set this to true if your widget contains other widgets
			widgetsInTemplate : true,
			
			_temporalContextTable:null,
		    _segmentationContextTable:null,
			tbltemporalContextName:null,
			tblsegmentationContextName:null,
			tbltemporalContextColumns:[],
			tblsegmentationContextColumns:[],

			// Override this method to perform custom behavior during dijit construction.
			// Common operations for constructor:
			// 1) Initialize non-primitive types (i.e. objects and arrays)
			// 2) Add additional properties needed by succeeding lifecycle methods
			constructor : function() {
				this.tbltemporalContextColumns=[];
		    	this.tblsegmentationContextColumns=[];
		    	this.myObject=ATVars.CURRENT_OBJECT;
		    	this.myEPN=ATVars.MY_EPN;
		    	this.fieldName="Contexts";
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
				
				// set Temporal Context table
				var temporalContextOptions=this.myEPN.getTemporalContextList();
				
//			 	for ( var i= 0; i<eventcount; i++) {
//			 		eventOptions.push(this.myObject.InitiatorPolicy[p]);
//				}
			 	
			 	
				this.tbltemporalContextColumns.push(this.gridhelper.setColumn("Temporal Context", "Selection", true, true, temporalContextOptions, "setTemporalContext", "getTemporalContext", this.myObject,null ));
				
				this.tbltemporalContextName="Temporal Contexts";
			
				this._temporalContextTable = new widgets.GenGrid({ grid_inputcolumns:  this.tbltemporalContextColumns, tblName: this.tbltemporalContextName, hasDel: true, metaObject: this.myObject, 
																   metaAttrSize: "getNumberOfTemporalContexts", delAttrMethod: "deleteTemporalContext", noShowRequiredButton:true  });
				this.TemporalContexts.appendChild(this._temporalContextTable.domNode);
				// set Segmentation Context Table
				var segmentationContextOptions=this.myEPN.getSegmentationContextList();
				
				this.tblsegmentationContextColumns.push(this.gridhelper.setColumn("Segmentation Context", "Selection", true, true, segmentationContextOptions, "setSegmentationContext", "getSegmentationContext", this.myObject, null ));
				
				this.tblsegmentationContextName ="Segmentation Contexts";
			
				this._segmentationContextTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblsegmentationContextColumns, tblName: this.tblsegmentationContextName, hasDel: true, metaObject: this.myObject, 
																	   metaAttrSize: "getNumberOfSegmentationContexts", delAttrMethod: "deleteSegmentationContext", noShowRequiredButton:true  });
				this.SegmentationContexts.appendChild(this._segmentationContextTable.domNode);
			},
		  	startup: function(){
		  		this.inherited(arguments);
				this.compositeContexts.expandCollapse();
				
				this._temporalContextTable.startup();
				this._segmentationContextTable.startup();
		     }
		});
