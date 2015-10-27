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
 dojo.provide("widgets.ProducerProperties");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("widgets.GridHelper");
dojo.require("widgets.GenGrid");
dojo.require("dijit.form.ComboBox");
dojo.require('dijit.form.Button');
dojo.require('dojo.data.ItemFileWriteStore');
dojo.require("widgets.SectionPane");
dojo.declare("widgets.ProducerProperties",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","producer_properties.html"),

    constructor: function() {
    	this.myObject=ATVars.CURRENT_OBJECT;
    	//this.objectType="objTypet";
    	this.fieldName="Properties";
    	this.tblColumns=[];
    	this.gridhelper= new widgets.GridHelper();
     },
   	saveType:function(){
   		var newType=this.comboType.value;
   		if(this.myObject.getType()!==newType){
			this.myObject.setType(newType);
	 		this._propertiesTable.onRefresh();
		}
    },

	postCreate : function() {
		
		this.tblColumns.push(this.gridhelper.setColumn("Name", "String", true, true, null, "setPropertyName", "getPropertyName", this.myObject, null ));
		this.tblColumns.push(this.gridhelper.setColumn("Value", "String", true, true, null,"setPropertyValue", "getPropertyValue", this.myObject, null));
		this.tblColumns.push(this.gridhelper.setColumn("Description", "String", true, false, null,"setPropertyDescription", "getPropertyDescription", this.myObject, null));
		this.properiesTableName="Properties";
	
		this._propertiesTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblColumns, tblName: this.properiesTableName, hasDel: true, metaObject: this.myObject, 
		                                          	  metaAttrSize: "getNumberOfProperties", delAttrMethod: "deleteProperty", refreshButton:false });
		this.propertiesGrid.appendChild(this._propertiesTable.domNode);
		
	},

    
  	 startup: function(){
        widgets.ProducerProperties.superclass.startup.apply(this, arguments);
 		
 		
	 	var optionsType = new dojo.data.ItemFileWriteStore({data: {identifier: 'name', items:[]}});
	 	for ( var p in ATEnum.InOutType) {
	 		optionsType.newItem({name: ATEnum.InOutType[p]});
		}
     	this.comboType.attr('store', optionsType);
     	
 		this.comboType.set("value",this.myObject.getType());
 		this.comboType.set("displayedValue",this.comboType.value);
 		
 		this.producerProp.expandCollapse();
 		this._propertiesTable.startup();
 		
      }
});
