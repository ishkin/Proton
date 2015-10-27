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
 dojo.provide("widgets.EPAInternalSegmentation");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("widgets.SectionPane");
dojo.declare("widgets.EPAInternalSegmentation",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","epa_InternalSegmentation.html"),
    
				_internalSegmentationTable:null,
				tblinternalSegmentationName:null,
				tblinternalSegmentationColumns:[],

								// Override this method to perform custom behavior during dijit construction.
								// Common operations for constructor:
								// 1) Initialize non-primitive types (i.e. objects and arrays)
								// 2) Add additional properties needed by succeeding lifecycle methods
								constructor : function() {
									this.tblinternalSegmentationColumns=[];
									this.myObject=ATVars.CURRENT_OBJECT;
							    	this.fieldName="Internal Segmentation";
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
									
								this.tblinternalSegmentationColumns=[];
//								
								// set segmentation options list 
								var segmentationOptions=this.myEPN.getSegmentationContextList();
								
								this.tblinternalSegmentationColumns.push(this.gridhelper.setColumn("Segmentation Context", "Selection", true, true, segmentationOptions, "setInternalSegmentationName", "getInternalSegmentationName", this.myObject, null ));
								
								this.tblinternalSegmentationName="";
							
								this._internalSegmentationTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblinternalSegmentationColumns, tblName: this.tblinternalSegmentationName, hasDel: true, 
																						metaObject: this.myObject, metaAttrSize: "getNumberOfInternalSegmentations", delAttrMethod: "deleteInternalSegmentation",
																						noShowRequiredButton:true});
								
								this.InternalSegmentation.appendChild(this._internalSegmentationTable.domNode);
								
							},
							startup: function(){
								this.inherited(arguments);
								this.internalSeg.expandCollapse();

								this._internalSegmentationTable.startup();
							}
						
		});
