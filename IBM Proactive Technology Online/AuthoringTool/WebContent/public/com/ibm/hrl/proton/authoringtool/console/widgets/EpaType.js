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
 dojo.provide("widgets.EpaType");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("dijit.form.ComboBox");
dojo.require('dijit.form.Button');
dojo.require('dojo.data.ItemFileWriteStore');
dojo.require("widgets.SectionPane");
dojo.declare("widgets.EpaType",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","epa_type.html"),

    constructor: function() {
    	this.myObject=ATVars.CURRENT_OBJECT;
    	//this.objectType="objTypet";
    	this.fieldName="Type and Context";
     },
   	saveEpaType:function(){
		this.myObject.setEPAType(this.comboType.value);
		this.globalContext.style.display="block";
   		dojo.publish("TypeWasChanged",[{name:this.myObject._name,type:1}]);
 
//		this.manageView(this.comboType.value);
    },
    saveEpaContect:function(){
		this.myObject.setContext(this.comboContext.value);
    },

	 sendValue:function(){
	 	alert(this.comboValue.attr.value);
	 },
	 
	 manageView:function(type){
   		this.globalContext.style.display="block";
	 },
	 
 	 startup: function(){
        widgets.EpaType.superclass.startup.apply(this, arguments);
		this.epaFilt.expandCollapse();
 		
	 	var optionscontext = new dojo.data.ItemFileWriteStore({data: {identifier: 'name', items:[]}});
	 	var contextList=ATVars.MY_EPN.getContextList();
     	for (var i=0,l=contextList.length; i < l; i++) {
          optionscontext.newItem({name: contextList[i]});
     	}
     	this.comboContext.attr('store', optionscontext);
     	
	 	var optionsType = new dojo.data.ItemFileWriteStore({data: {identifier: 'name', items:[]}});
	 	for ( var p in ATEnum.EPAType) {
	 		optionsType.newItem({name: ATEnum.EPAType[p]});
		}
     	this.comboType.attr('store', optionsType);
     	
 		this.comboContext.set("value",this.myObject.getContext());
 		this.comboContext.set("displayedValue",this.comboContext.value);
 		
 		this.comboType.set("value",this.myObject.getEPAType());
 		this.comboType.set("displayedValue",this.comboType.value);
 		//this.manageView(this.comboType.value);

     }
});
