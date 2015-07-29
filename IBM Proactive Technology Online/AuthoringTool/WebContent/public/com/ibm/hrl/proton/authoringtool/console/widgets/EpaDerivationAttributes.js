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
 dojo.provide("widgets.EpaDerivationAttributes");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("widgets.SectionPane");
dojo.declare("widgets.EpaDerivationAttributes",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","epa_DerivationAttributes.html"),
    
    _argsDerivedEventTable:null,
	tblDerivedEventName:null,
	tblDerivedEventColumns:[],
	eventObject:null,
			// Override this method to perform custom behavior during dijit construction.
			// Common operations for constructor:
			// 1) Initialize non-primitive types (i.e. objects and arrays)
			// 2) Add additional properties needed by succeeding lifecycle methods
			constructor : function(obj) {
				this.tblDerivedEventColumns=[];
		    	this.epaObject=obj.myObject;
		    	this.myEPN=ATVars.MY_EPN;
		    	this.fieldName="Event Attributes";
		    	this.gridhelper= new widgets.GridHelper();
		    	this.numInList=obj.numInList;
		    	dojo.subscribe("SectionWasExpanded",this,function() {
		    		this.refreshGrid();});
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
				if(this.numInList>=0){
					var eventName=this.epaObject.getDerivedEventName(this.numInList);
					this.eventObject=this.myEPN.getEvent(eventName);
					
					var epressParmsArray=[];
					epressParmsArray[0]=this.numInList;
									
					this.tblDerivedEventColumns.push(this.gridhelper.setColumn("Attribute", "String", false, true, null, "setAttributeName", "getAttributeName", this.eventObject,null ));
					this.tblDerivedEventColumns.push(this.gridhelper.setColumn("Type", "String", false, true, null,"setAttributeType", "getAttributeType", this.eventObject, null));
					this.tblDerivedEventColumns.push(this.gridhelper.setColumn("Expression", "String", true, true, null,"setDerivedEventExpression", "getDerivedEventExpression", this.epaObject, epressParmsArray));
					
					this.tblDerivedEventName="Event Attributes";
				
					this._argsDerivedEventTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblDerivedEventColumns, tblName: this.tblDerivedEventName, hasDel: false, metaObject: this.eventObject, 
																		metaAttrSize: "getNumberOfAttributes", delAttrMethod: "deleteAttribute", addButton: false, noShowRequiredButton:true  });
					this.derivationAttributes.appendChild(this._argsDerivedEventTable.domNode);
				}
			
		},
		
		refreshGrid:function(){
			this._argsDerivedEventTable._grid.update();
		},

	  	startup: function(){
		        widgets.EpaDerivationAttributes.superclass.startup.apply(this, arguments);
		        //this.epaDerivationTbl.expandCollapse();
		    	if(this.numInList>=0){
		    		this._argsDerivedEventTable.startup();
		    	}
	     }
	
});
