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
 dojo.provide("widgets.EpaDerivationAdd");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("dijit.form.ComboBox");
dojo.require('dijit.form.TextBox');
dojo.require('dijit.form.Button');
dojo.require('dojo.data.ItemFileWriteStore');
dojo.require("widgets.SectionPane");
dojo.declare("widgets.EpaDerivationAdd",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","epa_derivationAdd.html"),

    constructor: function() {
    	this.myObject=ATVars.CURRENT_OBJECT;
    	this.fieldName="Add Derivation";
    	this.derivedEventsList=null;
    	dojo.subscribe("NewEventToDerivation",this,function(args) {
    		this.AddDerivation(args);});
    },
    
    saveDerivedEventsList:function(derivedEventsList){
    	this.derivedEventsList=derivedEventsList;
    },
    
    AddDerivation:function(args){
    	if (ATVars.MY_EPN.isEventExists(args[0])){
    		if (args[1]==this.myObject.getName()){
	    		this.myObject.addDerivedEvent(args[0]);
	    		dojo.publish("DerivationWasAdded");
    		}
    	}
    	else if (this.cmbEvent.getValue().length>0){
    		this.myObject.addDerivedEvent(this.cmbEvent.getValue());
    		dojo.publish("DerivationWasAdded");
    	}
    },
    
    NewEvent:function(){
    	epaName=this.myObject.getName();
    	dojo.publish("PopUpNewEvent",[epaName]);
    },
    
    getEventsList:function(){
	 	var options = new dojo.data.ItemFileWriteStore({data: {identifier: 'name', items:[]}});
	 	var eventList=ATVars.MY_EPN.getEventList();
     	for (var i=0,l=eventList.length; i < l; i++) {
          options.newItem({name: eventList[i]});
     	}
     	this.cmbEvent.attr('store', options);
    },
    
	 sendValue:function(){
	 	alert(this.cmbEvent.attr.value);
	 	this.epaDeriv.expandCollapse();
	 },
 	 startup: function(){
        widgets.EpaDerivationAdd.superclass.startup.apply(this, arguments);
 		 this.epaDeriv.expandCollapse();
 	
   }
   });
