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
 dojo.provide("widgets.ConsumerProperties");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("widgets.GridHelper");
dojo.require("widgets.GenGrid");
dojo.require("dijit.form.ComboBox");
dojo.require('dijit.form.Button');
dojo.require('dojo.data.ItemFileWriteStore');
dojo.require("widgets.SectionPane");
dojo.declare("widgets.ConsumerProperties",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","consumer_properties.html"),
    
    constructor: function() {
    	this.myObject=ATVars.CURRENT_OBJECT;
    	this.myEPN=ATVars.MY_EPN;
    	this.fieldName="Properties";
    	
    	this.propertiesTblColumns=[];
       	this.propertiesGridhelper= new widgets.GridHelper();
       	
       	this.eventsTblColumns=[];
       	this.eventsGridhelper= new widgets.GridHelper();
       	
     },
   	saveType:function(){
   		var newType=this.comboType.value;
   		if(this.myObject.getType()!==newType){
			this.myObject.setType(newType);
	 		this._propertiesTable.onRefresh();
		}
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
		
		//properties table
		this.propertiesTblColumns.push(this.propertiesGridhelper.setColumn("Name", "String", true, true, null, "setPropertyName", "getPropertyName", this.myObject, null ));
		this.propertiesTblColumns.push(this.propertiesGridhelper.setColumn("Value", "String", true, true, null,"setPropertyValue", "getPropertyValue", this.myObject, null));
		this.propertiesTblColumns.push(this.propertiesGridhelper.setColumn("Description", "String", true, false, null,"setPropertyDescription", "getPropertyDescription", this.myObject, null));
		this.properiesTableName="Properties";
	
		this._propertiesTable = new widgets.GenGrid({ grid_inputcolumns:  this.propertiesTblColumns, tblName: this.properiesTableName, hasDel: true, metaObject: this.myObject, 
		                                          	  metaAttrSize: "getNumberOfProperties", delAttrMethod: "deleteProperty", refreshButton:false });
		this.consumerPropertiesGrid.appendChild(this._propertiesTable.domNode);
		
		//events table
		var eventOptions=this.myEPN.getEventList();
		this.eventsTblColumns.push(this.propertiesGridhelper.setColumn("Name", "Selection", true, true, eventOptions, "setEventName", "getEventName", this.myObject, null ));
		this.eventsTblColumns.push(this.propertiesGridhelper.setColumn("Condition", "String", true, false, null,"setEventCondition", "getEventCondition", this.myObject, null));
		this.eventsTableName="Received Events";
	
		this._eventsTable = new widgets.GenGrid({ grid_inputcolumns:  this.eventsTblColumns, tblName: this.eventsTableName, hasDel: true, metaObject: this.myObject, 
		                                          	  metaAttrSize: "getNumberOfEvents", delAttrMethod: "deleteEvent", refreshButton:false });
		this.consumerEventsGrid.appendChild(this._eventsTable.domNode);
		
	},

 	 startup: function(){
        widgets.ConsumerProperties.superclass.startup.apply(this, arguments);
 		
	 	var optionsType = new dojo.data.ItemFileWriteStore({data: {identifier: 'name', items:[]}});
	 	for ( var p in ATEnum.InOutType) {
	 		optionsType.newItem({name: ATEnum.InOutType[p]});
		}
     	this.comboType.attr('store', optionsType);
     	
 		this.comboType.set("value",this.myObject.getType());
 		this.comboType.set("displayedValue",this.comboType.value);
 		this.consumerProp.expandCollapse();
 		this._propertiesTable.startup();
 		this._eventsTable.startup();
 		
     }
});
