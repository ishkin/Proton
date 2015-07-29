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
 dojo.provide("metadata.EPN");
dojo.require("metadata.BaseDefinition");
dojo.require("metadata.Event");
dojo.require("metadata.ParseError");
dojo.require("metadata.EPA");
dojo.require("metadata.TemporalContext");
dojo.require("metadata.SegmentationContext");
dojo.require("metadata.CompositeContext");
dojo.require("metadata.Consumer");
dojo.require("metadata.Producer");
dojo.declare("metadata.EPN",null,{
 
	constructor: function(args){
		//"private" variables. Should be accessed with getters/setters only
		this._name=null;
		this._events=new Array();
		this._epas=new Array();
		this._temporalContexts=new Array();
		this._segmentationContexts=new Array();
		this._compositeContexts=new Array();
		this._consumers=new Array();
		this._producers=new Array();
		this._errors=new Object();
		this._eventList=new Array();
		this._epaList=new Array();
		this._temporalContextList=new Array();
		this._segmentationContextList=new Array();
		this._compositeContextList=new Array();
		this._consumerList=new Array();
		this._producerList=new Array();
		
		try{
			var i,l;
			ATVars.MY_EPN=this;
			if (dojo.isObject(args)){//EPA from reading json input
				if(!args || !args.epn || !args.epn.events){
					throw new metadata.ParseError("Error reading definition file");
				}else {				
					this._name = args.epn.name || "EPN";
					//parsing events
					if(args.epn.events){
						for(i=0, l=args.epn.events.length; i<l; i++){
							this._events[i] = new metadata.Event(args.epn.events[i], this._errors, ATEnum.Definitions.Event, this);
							this._eventList[i] = this._events[i].getName();
						}
					}
					//parsing contexts
					if(args.epn.contexts){
						if(args.epn.contexts.temporal){
							for(i=0, l=args.epn.contexts.temporal.length; i<l; i++){
								this._temporalContexts[i] = new metadata.TemporalContext(args.epn.contexts.temporal[i],
										                       this._errors, ATEnum.Definitions.TemporalContext, this);
								this._temporalContextList[i] = this._temporalContexts[i].getName();
							}					
						}
						if(args.epn.contexts.segmentation){
							for(i=0, l=args.epn.contexts.segmentation.length; i<l; i++){
								this._segmentationContexts[i] = new metadata.SegmentationContext(args.epn.contexts.segmentation[i],
					                       this._errors, ATEnum.Definitions.SegmentationContext, this);
								this._segmentationContextList[i] = this._segmentationContexts[i].getName();
							}					
						}
						if(args.epn.contexts.composite){
							for(i=0, l=args.epn.contexts.composite.length; i<l; i++){
								this._compositeContexts[i] = new metadata.CompositeContext(args.epn.contexts.composite[i],
					                       this._errors, ATEnum.Definitions.CompositeContext, this);
								this._compositeContextList[i] = this._compositeContexts[i].getName();
							}					
						}
					}
					//parsing EPAs after the context (since EPAs use context)
					if(args.epn.epas){
						for(i=0, l=args.epn.epas.length; i<l; i++){
							this._epas[i] = new metadata.EPA(args.epn.epas[i], this._errors, ATEnum.Definitions.EPA, this);
							this._epaList[i] = this._epas[i].getName();
						}
					}
					if(args.epn.consumers){
						for(i=0, l=args.epn.consumers.length; i<l; i++){
							this._consumers[i] = new metadata.Consumer(args.epn.consumers[i], this._errors, ATEnum.Definitions.Consumer, this);
							this._consumerList[i] = this._consumers[i].getName();
						}
					}
					if(args.epn.producers){
						for(i=0, l=args.epn.producers.length; i<l; i++){
							this._producers[i] = new metadata.Producer(args.epn.producers[i], this._errors, ATEnum.Definitions.Producer, this);
							this._producerList[i] = this._producers[i].getName();
						}
					}
				}
			}else{	//new EPN created by the user with name only
				this._name = args || "EPN";					
			}				
		}catch(err){
			console.log(err.msg);
		}		
	},
	getEPNName: function(){
		return this._name;
	},
	setEPNName: function(name){
		//TODO: check it is a valid name
		this._name=name;
	},
	
	addEPNObject : function(obj) {
		var type = obj._definitionType;
		if (type.match(/\bepa\b/i)) {
			type = "epa";
		} else {
			type = type.charAt(0).toLowerCase() + type.slice(1);
		}
		
		this["_" + type + "s"].push(obj);
		this["_" + type + "List"].push(obj.getName());
	}, 
	
	getEPNObject: function(objectName){
		try{
			var i,l;
			for(i=0, l=this._events.length; i<l; i++){
				if(this._events[i].getName() === objectName){
					return this._events[i];
					
				}
			}
			for(i=0, l=this._epas.length; i<l; i++){
				if(this._epas[i].getName() === objectName){
					return this._epas[i];
				}
			}
			for(i=0, l=this._temporalContexts.length; i<l; i++){
				if(this._temporalContexts[i].getName() === objectName){
					return this._temporalContexts[i];
				}
			}
			for(i=0, l=this._segmentationContexts.length; i<l; i++){
				if(this._segmentationContexts[i].getName() === objectName){
					return this._segmentationContexts[i];
				}
			}
			for(i=0, l=this._compositeContexts.length; i<l; i++){
				if(this._compositeContexts[i].getName() === objectName){
					return this._compositeContexts[i];
				}
			}
			for(i=0, l=this._consumers.length; i<l; i++){
				if(this._consumers[i].getName() === objectName){
					return this._consumers[i];
				}
			}
			for(i=0, l=this._producers.length; i<l; i++){
				if(this._producers[i].getName() === objectName){
					return this._producers[i];
				}
			}

			return false;
		}
		catch(err){
			console.log(err.msg);
		}		
	},
	
	_getDefinitions: function() {
		return dojo.map(Object.keys(ATEnum.Definitions), function(def) {
			if (def.charAt(1) === def.charAt(1).toUpperCase()) {
				// EPAs mostly
				return def.toLowerCase();
			} else {
				return def.charAt(0).toLowerCase() + def.substr(1);				
			}
		});
	},
	
	_find: function(predicate, f, verificationFunction, undo) {
		lists = this._getDefinitions();
		for (var index = 0; index < lists.length; index++) {
			list = this["_" + lists[index] + "s"];
			if (!list) {
				continue;
			}
			for(var i = 0; i < list.length; i++) {
				item = list[i];
				if (predicate(item)) {
					f(lists[index], i);
					try {
						if (verificationFunction) {
							isInErrorState = verificationFunction();							
						}
					} catch (e) {
						alert("action will put the project in an unrecoverable errornous state, aborting");
						undo();
						return false;
					}
					if (isInErrorState && confirm("The project will be in an invalid state, " +
						"continue with action?") == false) {
							undo();
							return false;
					}
					else {
						return true;							
					}
				}
			}
		}
		return false;
	},
	
	deleteEPNObject: function(objectName, verificationFunction) {
		var that =  this;
		
		var removeFromArray = function(name, i) {
			this.obj = that["_" + name + "s"][i];
			this.i = i;
			this.name = name;
			that["_" + name + "s"].splice(i, 1);
			that["_" + name + "List"].splice(i, 1);
		};
		
		var undo = function() {
			var name = this.name;
			var i = this.i;
			var obj = this.obj;
			that["_" + name + "s"].splice(i, 0, obj);
			that["_" + name + "List"].splice(i, 0, obj.getName());
		};
		
		// iterate over all the array, iterating over them and search for an item with an equal name
		// if found, return true and exit
		return this._find(function(item) {
			return item.getName() === objectName;
		}, removeFromArray, verificationFunction, undo);
	},
	
	renameEPNObject: function(objectName, newName, verificationFunction) {
		var that =  this;
		
		var rename = function(name, i) {
			that.oldName = that["_" + name + "s"][i].getName();
			that.name = name;
			that.i = i;
			that["_" + name + "s"][i].setName(newName);
			that["_" + name + "List"][i] = newName;
			
		};
		
		var undo = function() {
			oldName = that.oldName;
			name = that.name;
			i = that.i;
			
			that["_" + name + "s"][i].setName(oldName);
			that["_" + name + "List"][i] = oldName;
		};
		
		
		// iterate over all the array, iterating over them and search for an item with an equal name
		// if found, return true and exit
		lists = this._getDefinitions(); 
		return this._find(function(item) {
			return item.getName() === objectName;
		}, rename, verificationFunction, undo);		
	},
	
	isEventExists: function(eventName){
		try{
			var i,l;
			for(i=0, l=this._eventList.length; i<l; i++){
				if(this._eventList[i] === eventName){
					return true;
				}
			}
			return false;
		}
		catch(err){
			console.log(err.msg);
		}		
	},
	
	isContextExists: function(contextName){
		var i,l;
		for(i=0, l=this._temporalContexts.length; i<l; i++){
			if(this._temporalContexts[i].getName() === contextName){
				return true;
			}
		}
		for(i=0, l=this._segmentationContexts.length; i<l; i++){
			if(this._segmentationContexts[i].getName() === contextName){
				return true;
			}
		}
		for(i=0, l=this._compositeContexts.length; i<l; i++){
			if(this._compositeContexts[i].getName() === contextName){
				return true;
			}
		}
		return false;
	},
	
	isExists: function(name) {
		return !!this.getEPNObject(name);
	},
	
	
	addEvent: function(name){
		try{
			if(!name){
				throw new metadata.ParseError("Must Declare Event Name");
				return false;
			}else {
				this._events.push(new metadata.Event(name, this._errors, ATEnum.Definitions.Event, this));
				this._eventList.push(name);
				return true;
			}
		}catch(err){
			console.log(err.msg);
			return false;
		}		
	},
	
	
	addEPA: function(name){
		try{
			if(!name){
				throw new metadata.ParseError("Must Declare EPA Name");
				return false;
			}else {
				this._epas.push(new metadata.EPA(name, this._errors, ATEnum.Definitions.EPA, this));
				this._epaList.push(name);
				return true;
			}
			console.log(this._epas);
		}catch(err){
			console.log(err.msg);
			return false;
		}		
	},
	
	addTemporal: function(name){
		try{
			if(!name){
				throw new metadata.ParseError("Must Declare emporal Context Name");
				return false;
			}else {
				this._temporalContexts.push(new metadata.TemporalContext(name, this._errors, ATEnum.Definitions.TemporalContext, this));
				this._temporalContextList.push(name);
				return true;
			}
			console.log(this._temporalContexts);
		}catch(err){
			console.log(err.msg);
			return false;
		}		
	},
	
	addSegmentation: function(name){
		try{
			if(!name){
				throw new metadata.ParseError("Must Declare emporal Context Name");
				return false;
			}else {
				this._segmentationContexts.push(new metadata.SegmentationContext(name, this._errors, ATEnum.Definitions.SegmentationContext, this));
				this._segmentationContextList.push(name);
				return true;
			}
		}catch(err){
			console.log(err.msg);
			return false;
		}		
	},	
	
	addComposite: function(name){
		try{
			if(!name){
				throw new metadata.ParseError("Must Declare emporal Context Name");
				return false;
			}else {
				this._compositeContexts.push(new metadata.CompositeContext(name, this._errors, ATEnum.Definitions.CompositeContext, this));
				this._compositeContextList.push(name);
				return true;
			}
		}catch(err){
			console.log(err.msg);
			return false;
		}		
	},

	addConsumer: function(name){
		try{
			if(!name){
				throw new metadata.ParseError("Must Declare Consumer Name");
				return false;
			}else {
				this._consumers.push(new metadata.Consumer(name, this._errors, ATEnum.Definitions.Consumer, this));
				this._consumerList.push(name);
				return true;
			}
		}catch(err){
			console.log(err.msg);
			return false;
		}		
	},
	
	addProducer: function(name){
		try{
			if(!name){
				throw new metadata.ParseError("Must Declare Producer Name");
				return false;
			}else {
				this._producers.push(new metadata.Producer(name, this._errors, ATEnum.Definitions.Producer, this));
				this._producerList.push(name);
				return true;
			}
		}catch(err){
			console.log(err.msg);
			return false;
		}		
	},

	
	getEventList: function(){
		return this._eventList;
	},
	
	getEpaList: function(){
		return this._epaList;
	},
	
	getTemporalContextList: function(){
		return this._temporalContextList;
	},

	getSegmentationContextList: function(){
		return this._segmentationContextList;
	},
	
	getCompositeContextList: function(){
		return this._compositeContextList;
	},
	
	getContextList: function(){
		return this._compositeContextList.concat(this._temporalContextList);
	},
	
	getConsumerList: function(){
		return this._consumerList;
	},
	
	getProducerList: function(){
		return this._producerList;
	},
	

	getEpaObjects: function(){
		return this._epas;
	},
	
	getEvent: function(name){
		var i,l;
		for(i=0, l=this._events.length; i<l; i++){
			if(this._events[i].getName()===name){
				return this._events[i];
			}
		}		
	},
	
	
	getEPA: function(name){
		var i,l;
		for(i=0,l=this._epas.length;i<l;i++){
			if(this._epas[i].getName()===name){
				return this._epas[i];
			}
		}
	},
	
	
	getTemporalContext: function(name){
		var i,l;
		for(i=0, l=this._temporalContexts.length; i<l; i++){
			if(this._temporalContexts[i].getName()===name){
				return this._temporalContexts[i];
			}
		}		
	},

	getSegmentationContext: function(name){
		var i,l;
		for(i=0, l=this._segmentationContexts.length; i<l; i++){
			if(this._segmentationContexts[i].getName()===name){
				return this._segmentationContexts[i];
			}
		}		
	},

	getCompositeContext: function(name){
		var i,l;
		for(i=0, l=this._compositeContexts.length; i<l; i++){
			if(this._compositeContexts[i].getName()===name){
				return this._compositeContexts[i];
			}
		}		
	},

	getConsumer: function(name){
		var i,l;
		for(i=0, l=this._consumers.length; i<l; i++){
			if(this._consumers[i].getName()===name){
				return this._consumers[i];
			}
		}		
	},
	
	getProducer: function(name){
		var i,l;
		for(i=0, l=this._producers.length; i<l; i++){
			if(this._producers[i].getName()===name){
				return this._producers[i];
			}
		}		
	},

	validateDefinitionName: function(name){
		var i,l;
		if(!this.validateDefinitionNameSyntax(name)){
			return ATErrorMessage.ErrorMsg.IllegalDefinitionName;
		}
		//checking uniqueness
		for(i=0,l=this._eventList.length;i<l;i++){
			if(name===this._eventList[i]){
				return ATErrorMessage.ErrorMsg.NameUniquenessErrorEvent;
			}
		}
		for(i=0,l=this._epaList.length;i<l;i++){
			if(name===this._epaList[i]){
				return ATErrorMessage.ErrorMsg.NameUniquenessErrorEPA;
			}
		}
		for(i=0,l=this._temporalContextList.length;i<l;i++){
			if(name===this._temporalContextList[i]){
				return ATErrorMessage.ErrorMsg.NameUniquenessErrorTemporalContext;
			}
		}
		for(i=0,l=this._segmentationContextList.length;i<l;i++){
			if(name===this._segmentationContextList[i]){
				return ATErrorMessage.ErrorMsg.NameUniquenessErrorSegmentationContext;
			}
		}
		for(i=0,l=this._compositeContextList.length;i<l;i++){
			if(name===this._compositeContextList[i]){
				ATErrorMessage.ErrorMsg.NameUniquenessErrorCompositeContext;
			}
		}
		return null; //no errors
		
	},
	validateDefinitionNameSyntax: function(name){
		var validName = /[a-zA-Z_][0-9a-zA-Z_-]*/;  //starting with a letter or "_", can have numbers, letters, "_" or "-"
		return validName.test(name);
		
	}
});


