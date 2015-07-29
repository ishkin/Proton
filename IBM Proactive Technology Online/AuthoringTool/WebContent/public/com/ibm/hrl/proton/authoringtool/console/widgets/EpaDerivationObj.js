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
 dojo.provide("widgets.EpaDerivationObj");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("dijit.form.ComboBox");
dojo.require('dijit.form.TextBox');
dojo.require('dijit.form.Button');
dojo.require('dijit.form.CheckBox');
dojo.require("widgets.SectionPane");
dojo.declare("widgets.EpaDerivationObj",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","epa_derivationObj.html"),

    constructor: function(obj) {
    	this.myObject=obj.myObject;
    	//ATVars.CURRENT_OBJECT;
    	this.fieldName="Add Derivation";
    	this.numInList=obj.numInList||0;
    	
    	//this.expandeName="Derivation List";
    },
    // manageView:function(){
    	// this.divFilter.style.display = "block";
    	// if (this.myObject.epaType=="Filter"){
    		 // this.divFilter.style.display = "none";
    	// }
    // },
    updateCondition:function(){
    	this.myObject.setDerivedEventCondition(this.numInList,this.condition.get("value"));
    },
    
    updateReport:function(){
//    	if (this.report.value=="true"){
//    		this.report.attr("value","false");
//    	}else {this.report.attr("value","true");}
    },
	saveEvent:function(){},
	newEvent:function(){},
	sendDelete:function(){
		this.myObject._derivedEvents.splice(this.numInList,1);
		dojo.publish("DerivationWasDeleted", [this.numInList]);
		this.destroyRecursive(true);
	} ,  
	sendValue:function(){
	 	alert(this.cmbEvent.attr.value);
	 	this.epaDeriv.expandCollapse();
	},
	 startup: function(){
	    widgets.EpaDerivationObj.superclass.startup.apply(this, arguments);
	    var eventName=this.myObject.getDerivedEventName(this.numInList);
	    var condition=this.myObject.getDerivedEventCondition(this.numInList);
	    var report=this.myObject.isReportParticipants(this.numInList);
	    this.eventName.set("value",eventName);
	    this.condition.set("value",condition);
	    this.report.setValue(report);
	 }
	
});
