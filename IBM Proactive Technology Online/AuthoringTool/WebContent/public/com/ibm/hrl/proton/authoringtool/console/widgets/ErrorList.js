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
 dojo.provide("widgets.ErrorList");

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
dojo.declare("widgets.ErrorList",
				[dijit._Widget, dijit._Templated, widgets.GridHelper],
{
	// Path to the template
	widgetsInTemplate : true,
	templatePath: dojo.moduleUrl(
			"widgets","error_list.html"),

	_participantEventsTable:null,
	tblparticipantEventsName:null,
	tblparticipantEventsColumns:[],
	
	constructor : function() {
		this.tblColumns=[];
		//this.myObject=ATVars.CURRENT_OBJECT;
	},
	
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
	 	
		this.tblColumns.push(this.setColumn("Description", "String"));
		this.tblColumns.push(this.setColumn("Definition", "String"));
		this.tblColumns.push(this.setColumn("Element", "String"));
//		this.tblColumns.push(this.setColumn("defaultValue", "String", true, false, null,"setAttributeDefaultValue", "getAttributeDefaultValue", this.myObject));
//		this.tblColumns.push(this.setColumn("opt", "String", true, null, null));
//		var selectionList=["0", "1"];
//		this.tblColumns.push(this.setColumn("style", "Selection", true, null, selectionList));
//		this.tblColumns.push(this.setColumn("check", "CheckBox", true, null));
		this.tblName="Event Attributes Table";
	
	this._argsTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblColumns, tblName: this.tblName, hasDel: false });
	this.errorsGrid.appendChild(this._argsTable.domNode);
		
	},
	
	startup: function(){
		this.inherited(arguments);
		this._argsTable.startup();
		
     }
});
