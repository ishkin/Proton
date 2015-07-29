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
 dojo.provide("widgets.AddObject");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");

dojo.declare("widgets.AddObject",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","add_object.html"),

    constructor: function(item) {
    	this.newName="";
    	this.NewType=ATVars.NEWOBJECT;
    	this.fromDerivation=null;
    },
    
    newObject:function(){
		dijit.popup.close(this);
		this.newName = dojo.trim(this.ObjName.value);
		if (this.newName.length == 0) {
			alert("name cannot be made of only whitespaces!");
			return;
		}
		if (this.NewType == "Project") {
			this.newProject();
		} else if (this.NewType == "Event") {
			this.newEvent();
		} else if (this.NewType == "EPA") {
			this.newEPA();
		} else if (this.NewType == "Temporal Context") {
			this.newTemporal();
		} else if (this.NewType == "Segmentation Context") {
			this.newSegmentation();
		} else if (this.NewType == "Composite Context") {
			this.newComposite();
		} else if (this.NewType == "Consumer") {
			this.newConsumer();
		} else if (this.NewType == "Producer") {
			this.newProducer();
		} else if (this.fromDerivation) {
			dojo.publish("NewEventToDerivation", [ [ this.newName, this.fromDerivation ] ]);
		}
    },
    
    closeDialog:function(){
    	dijit.popup.close(this);
    	this.destroyRecursive(false);
    },
    
    newEvent:function(){
    	var that=this;
		//check that there is no event with this name
		var inEvents = dojo.filter(ATVars.MY_EPN.getEventList(), function(item) {
	    	return item==that.newName;
			});
	  	if (inEvents.length==0){
		// add to data structure and to array
			if (ATVars.MY_EPN.addEvent(this.newName)){
				//send to ProtonMain to create tab and tree node
				dojo.publish("PublishNewObject", [{item:this.newName, type:"Event"}]);
		}}else(alert("There is an Event with this name ("+this.newName+")"));
	},
	
	newEPA:function(){
		var that=this;
		//check that there is no EPA with this name
		var inEPAs = dojo.filter(ATVars.MY_EPN.getEpaList(), function(item) {
	    	return item==that.newName;
	  		});
	  	if (inEPAs.length==0){
		// add to data structure and to array
			if (ATVars.MY_EPN.addEPA(this.newName)){
				//send to ProtonMain to create tab and tree node
				dojo.publish("PublishNewObject", [{item:this.newName, type:"EPA"}]);
		}}else(alert("There is an EPA with this name ("+this.newName+")"));
	},
	
	newTemporal:function(){
		var that=this;
		//check that there is no EPA with this name
		var inTEMPORAL = dojo.filter(ATVars.MY_EPN.getTemporalContextList(), function(item) {
	    	return item==that.newName;
	  		});
	  	if (inTEMPORAL.length==0){
		// add to data structure and to array
			if (ATVars.MY_EPN.addTemporal(this.newName)){
				//send to ProtonMain to create tab and tree node
				dojo.publish("PublishNewObject", [{item:this.newName, type:"Temporal"}]);
		}}else(alert("There is a Temporal Context with this name ("+this.newName+")"));
	},
	
	newComposite:function(){
		var that=this;
		//check that there is no EPA with this name
		var inCOMPOSIT = dojo.filter(ATVars.MY_EPN.getCompositeContextList(), function(item) {
	    	return item==that.newName;
	  		});
	  	if (inCOMPOSIT.length==0){
		// add to data structure and to array
			if (ATVars.MY_EPN.addComposite(this.newName)){
				//send to ProtonMain to create tab and tree node
				dojo.publish("PublishNewObject", [{item:this.newName, type:"Composite"}]);
		}}else(alert("There is a Composite Context with this name ("+epaName+")"));
	},
	
	newSegmentation:function(){
		var that=this;
		//check that there is no EPA with this name
		var inSegmentation = dojo.filter(ATVars.MY_EPN.getSegmentationContextList(), function(item) {
	    	return item==that.newName;
	  		});
	  	if (inSegmentation.length==0){
		// add to data structure and to array
			if (ATVars.MY_EPN.addSegmentation(this.newName)){
				//send to ProtonMain to create tab and tree node
				dojo.publish("PublishNewObject", [{item:this.newName, type:"Segmentation"}]);
		}}else(alert("There is a Segmentation Context with this name ("+this.newName+")"));
	},
	
	newConsumer:function(){
		var that=this;
		//check that there is no Consumer with this name
		var inConsumer = dojo.filter(ATVars.MY_EPN.getConsumerList(), function(item) {
	    	return item==that.newName;
	  		});
	  	if (inConsumer.length==0){
		// add to data structure and to array
			if (ATVars.MY_EPN.addConsumer(this.newName)){
				//send to ProtonMain to create tab and tree node
				dojo.publish("PublishNewObject", [{item:this.newName, type:"Consumer"}]);
		}}else(alert("There is a Consumer with this name ("+this.newName+")"));
	},
	
	newProducer:function(){
		var that=this;
		//check that there is no Producer with this name
		var inProducer = dojo.filter(ATVars.MY_EPN.getProducerList(), function(item) {
	    	return item==that.newName;
	  		});
	  	if (inProducer.length==0){
		// add to data structure and to array
			if (ATVars.MY_EPN.addProducer(this.newName)){
				//send to ProtonMain to create tab and tree node
				dojo.publish("PublishNewObject", [{item:this.newName, type:"Producer"}]);
		}}else(alert("There is a Producer with this name ("+this.newName+")"));
	},

	newProject:function(){
		this.createNewJson();
		var that = this;
		var projectName = this.newName;
		var projectsURL = ATVars.PROJECTS_URL;
		//check that there is no project with this name
		var xhrArgs = {
                url: projectsURL,
                content: {name : projectName, content : JSON.stringify(that.obj)}, // empty project
                handleAs: "text",
                load: function(data) {
                	dojo.publish("PublishNewProject", [projectName]);
                },
                error: function(error) {
                	if (error.status == 409) {
                		alert(projectName + " already exists; please choose a different name.");
                	}
                }
            };
		dojo.xhrPost(xhrArgs);
		
	},
	
	createNewJson:function(){
		this.obj={};
		this.obj.epn={};
		this.obj.epn.events=[];
		this.obj.epn.epas=[];
		this.obj.epn.contexts={};
		this.obj.epn.contexts.temporal=[];
		this.obj.epn.contexts.segmentation=[];
		this.obj.epn.contexts.composite=[];
		this.obj.epn.consumers=[];
		this.obj.epn.producers=[];
		this.obj.epn.name=this.newName; 
		
	},
	
	 startup: function(item){
        widgets.AddObject.superclass.startup.apply(this, arguments);
        this.objectType.set("value",this.NewType +" Name:");
       	if  (item){
    		this.fromDerivation=item;
    	}
     }

});
