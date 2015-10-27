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
// dojo.provide allows pages to use all of the types declared in this resource.
dojo.provide("widgets.Event_Attributes");

//dojo.require the necessary dijit hierarchy
dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("widgets.SectionPane");
dojo.require("widgets.GridHelper");
dojo.require("widgets.GenGrid");
dojo.require("dojo.data.ItemFileWriteStore");
dojo.require("dojo.data.ItemFileReadStore");
dojo.require("dojo.store.Memory");
dojo.require("dojo.store.Cache"); 
dojo.require("dojo.data.ObjectStore");
dojo.declare("widgets.Event_Attributes",
				[dijit._Widget, dijit._Templated],
{
			// Path to the template
			widgetsInTemplate : true,
			templatePath: dojo.moduleUrl(
					"widgets",
					"event_Attributes.html"),

			// Override this method to perform custom behavior during dijit construction.
			// Common operations for constructor:
			// 1) Initialize non-primitive types (i.e. objects and arrays)
			// 2) Add additional properties needed by succeeding lifecycle methods
			constructor : function() {
				this.tblColumns=[];
				this.myObject=ATVars.CURRENT_OBJECT;
		    	this.fieldName="Attributes";
		    	this.gridhelper= new widgets.GridHelper();
		    	//this.showRequiredOnly = false;
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
				
				var typeOptions=[];
			 	for ( var p in ATEnum.AttributeTypes) {
			 		typeOptions.push(ATEnum.AttributeTypes[p]);
				}
			 	var dimensionOptions=ATEnum.AttributeDimension;
			 	
				this.tblColumns.push(this.gridhelper.setColumn("Name", "String", true, true, null, "setAttributeName", "getAttributeName", this.myObject, null ));
				this.tblColumns.push(this.gridhelper.setColumn("Type", "Selection", true, true, typeOptions,"setAttributeType", "getAttributeType", this.myObject, null));
				this.tblColumns.push(this.gridhelper.setColumn("Dimension", "Selection", true, false, dimensionOptions,"setAttributeDimension", "getAttributeDimension", this.myObject, null));
				this.tblColumns.push(this.gridhelper.setColumn("Default Value", "String", true, false, null,"setAttributeDefaultValue", "getAttributeDefaultValue", this.myObject, null));
				this.tblparticipantEventsName="Event Attributes Table";
			
			this._argsTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblColumns, tblName: this.tblparticipantEventsName, hasDel: true, metaObject: this.myObject, metaAttrSize: "getNumberOfAttributes", delAttrMethod: "deleteAttribute" });
			this.attributeGrid.appendChild(this._argsTable.domNode);
				
			},
//			changeRequierd:function(){
//				this._argsTable.onShowRequired();
//				if(this.showRequiredOnly==false){
//					this.showReq.set('label', 'Show all columns');
//					this.showRequiredOnly=true;
//				}else{
//					this.showReq.set('label', 'Show Required Only');
//					this.showRequiredOnly=false;
//				}
//			},
			
			startup: function(){
				this.inherited(arguments);
				
				this.eventAttributesList.expandCollapse();
				this._argsTable.startup();
				
		     }
		});
