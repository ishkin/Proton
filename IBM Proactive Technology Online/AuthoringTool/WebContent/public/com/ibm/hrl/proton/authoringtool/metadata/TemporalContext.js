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
 dojo.provide("metadata.TemporalContext");
dojo.require("metadata.BaseDefinition");
dojo.require("metadata.Event");
dojo.require("metadata.ParseError");
dojo.require("metadata.Initiator");
dojo.require("metadata.EventInitiator");
dojo.require("metadata.AbsoluteTimeInitiator");
dojo.require("metadata.Terminator");
dojo.require("metadata.EventTerminator");
dojo.require("metadata.AbsoluteTimeTerminator");
dojo.require("metadata.RelativeTimeTerminator");
dojo.declare("metadata.TemporalContext",metadata.BaseDefinition,{
	
	constructor: function(args, errors, definitionType, epn) {

		//class "private" variables - should be accessed by getters/setters
		this._type=ATEnum.DefaultTemporalContexts;
		this._atStartup=false;
		this._neverEnding=false;
		this._eventInitiators=new Array();
		this._absoluteTimeInitiators=new Array();
		this._eventTerminators=new Array();
		this._absoluteTerminators=new Array();
		this._relativeTerminator=null;
		this._errors = errors;
		this._epn = epn;
		this._definitionType = definitionType;
		
		var i,l, terminatorType, initiatorType;
		try{
			if (dojo.isObject(args)){//temporal context from the json
				if(!args || !args.name){
					throw new metadata.ParseError("");
				}	
				if(args.type){this.setType(args.type);}
				if(args.duration){this.setDuration(args.duration);}
				if(args.slidingPeriod){this.setSlidingPeriod(args.slidingPeriod);}
				if(args.atStartup){this.setAtStartup(args.atStartup);}
				if(args.neverEnding){this.setNeverEnding(args.neverEnding);}
				if(args.initiators){
					for (i = 0, l = args.initiators.length; i < l; i++) {
						initiatorType = args.initiators[i].initiatorType;
						if (!initiatorType) {
							console.log("Missing initiator type: " + this.getName());
						} else {
							if (initiatorType === ATEnum.InitiatorType.Event) {
								this._eventInitiators.push(new metadata.EventInitiator(args.initiators[i],
										this._errors, this._definitionType, this._epn));
							} else if (initiatorType === ATEnum.InitiatorType.AbsoluteTime) {
								this._absoluteTimeInitiators.push(new metadata.AbsoluteTimeInitiator(
										args.initiators[i], this._errors, this._definitionType, this._epn));
							}
						}
					}
				}
						if (args.terminators) {
							for (i = 0, l = args.terminators.length; i < l; i++) {
								terminatorType = args.terminators[i].terminatorType;
								if (!terminatorType) {
									// TODO: generate error
									console.log("Missing terminator type: " + this.getName());
								} else {
									if (terminatorType === ATEnum.TerminatorType.Event) {
										this._eventTerminators.push(new metadata.EventTerminator(args.terminators[i],
												this._errors, this._definitionType, this._epn));
									} else if (terminatorType === ATEnum.TerminatorType.AbsoluteTime) {
										this._absoluteTerminators.push(new metadata.AbsoluteTimeTerminator(
												args.terminators[i], this._errors, this._definitionType, this._epn));
									} else if (terminatorType === ATEnum.TerminatorType.RelativeTime) {
										this._relativeTerminator = new metadata.RelativeTimeTerminator(
												args.terminators[i], this._errors, this._definitionType, this._epn);
									}
								}
							}
						}
			}//else, new context defined by the user, handled by the base definition 
		}
		catch(err){
			err.msg = "Error parsing temporal context " + this._name + ". " + err.msg;
			console.log(err.msg);			
			throw err;
		}
	},
	
	setType: function(type){
		for (var t in ATEnum.TemporalContexts){
			if (ATEnum.TemporalContexts[t]===type){
				this._type=type;
				return;
			}
		}
		//DOTO: generate error
		console.log("unsupported temporal context type: "+ type + " in temporal context " + this._name);
	},
	getType: function(){
		return this._type;
	},
	
	_isInt: function(n) {
		return (parseFloat(n) == parseInt(n)) && !isNaN(n); 
	},
	
	setDuration: function(value) {
		if (value == null || this._isInt(value)) {
			this._duration = value;			
		} else {
			console.log("unsupported duration value: " + value + " in temporal context " + this._name);
		}
	},
	
	getDuration: function() {
		return this._duration;
	},
	
	setSlidingPeriod: function(value) {
		if (value == null || this._isInt(value)) {
			this._slidingPeriod = value;			
		} else {
			console.log("unsupported duration value: " + value + " in temporal context " + this._name);
		}
	},
	
	getSlidingPeriod: function() {
		return this._slidingPeriod;
	},
	
	setAtStartup: function(atStartup){
		if(atStartup===false || atStartup===true){
			this._atStartup=atStartup;
		}else{
			//DOTO: generate error
			console.log("unsupported at startup value: "+ atStartup + " in temporal context " + this._name);
		}
	},
	getAtStartup: function(){
		return this._atStartup;
	},

	//Event Initiator Methods
	
	getNumberOfEventInitiators: function(){
		return this._eventInitiators.length;		
	},
	
	deleteEventInitiator: function(i){
		if(i>=this._eventInitiators.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete event initiator " + i + " of event " + 
			    this.getName() + ". Initiator is out of scope: ");
		}else{
			this._eventInitiators=this._eventInitiators.slice(0, i).concat(this._eventInitiators.slice(i+1));
		}
	},

	getEventInitiatorName: function(i){
		if (i<this._eventInitiators.length){
			return this._eventInitiators[i].getName();
		}			
	},
	setEventInitiatorName: function(i, name){
		var l=this._eventInitiators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - event initiator out of bound");
			return;
		}else if (i==l){
			this._eventInitiators[i]=new metadata.EventInitiator(null, this._errors, this._definitionType, this._epn);
		}
		this._eventInitiators[i].setName(name);					
	},
	
	getEventInitiatorType: function(i){
		if (i<this._eventInitiators.length){
			return this._eventInitiators[i].getType();
		}			
	},
	setEventInitiatorType: function(i, type){
		var l=this._eventInitiators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - event initiator out of bound");
			return;
		}else if (i==l){
			this._eventInitiators[i]=new metadata.EventInitiator(null, this._errors, this._definitionType, this._epn);
		}
		this._eventInitiators[i].setType(type);					
	},
	
	getEventInitiatorPolicy: function(i){
		if (i<this._eventInitiators.length){
			return this._eventInitiators[i].getPolicy();
		}			
	},
	setEventInitiatorPolicy: function(i, policy){
		var l=this._eventInitiators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - event initiator out of bound");
			return;
		}else if (i==l){
			this._eventInitiators[i]=new metadata.EventInitiator(null, this._errors, this._definitionType, this._epn);
		}
		this._eventInitiators[i].setPolicy(policy);					
	},

	getEventInitiatorCondition: function(i){
		if (i<this._eventInitiators.length){
			return this._eventInitiators[i].getCondition();
		}			
	},
	setEventInitiatorCondition: function(i, condition){
		var l=this._eventInitiators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - event initiator out of bound");
			return;
		}else if (i==l){
			this._eventInitiators[i]=new metadata.EventInitiator(null, this._errors, this._definitionType, this._epn);
		}
		this._eventInitiators[i].setCondition(condition);					
	},

	//Absolute Time Initiator methods

	getNumberOfAbsoluteTimeInitiators: function(){
		return this._absoluteTimeInitiators.length;		
	},
	
	deleteAbsoluteTimeInitiator: function(i){
		if(i>=this._absoluteTimeInitiators.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete absolute time initiator " + i + " of event " + 
			    this.getName() + ". Initiator is out of scope: ");
		}else{
			this._absoluteTimeInitiators=this._absoluteTimeInitiators.slice(0, i).concat(this._absoluteTimeInitiators.slice(i+1));
		}
	},


	getAbsoluteTimeInitiatorType: function(i){
		if (i<this._absoluteTimeInitiators.length){
			return this._absoluteTimeInitiators[i].getType();
		}			
	},
	setAbsoluteTimeInitiatorType: function(i, type){
		var l=this._absoluteTimeInitiators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - absolute time initiator out of bound");
			return;
		}else if (i==l){
			this._absoluteTimeInitiators[i]=new metadata.AbsoluteTimeInitiator(null, this._errors, this._definitionType, this._epn);
		}
		this._absoluteTimeInitiators[i].setType(type);					
	},
	
	getAbsoluteTimeInitiatorPolicy: function(i){
		if (i<this._absoluteTimeInitiators.length){
			return this._absoluteTimeInitiators[i].getPolicy();
		}			
	},
	setAbsoluteTimeInitiatorPolicy: function(i, policy){
		var l=this._absoluteTimeInitiators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - absolute initiator out of bound");
			return;
		}else if (i==l){
			this._absoluteTimeInitiators[i]=new metadata.AbsoluteTimeInitiator(null, this._errors, this._definitionType, this._epn);
		}
		this._absoluteTimeInitiators[i].setPolicy(policy);					
	},
	
	getAbsoluteTimeInitiatorTimestamp: function(i){
		if (i<this._absoluteTimeInitiators.length){
			return this._absoluteTimeInitiators[i].getTimestamp();
		}			
	},
	
	setAbsoluteTimeInitiatorTimestamp: function(i, timestamp){
		var l=this._absoluteTimeInitiators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - absolute time initiator out of bound");
			return;
		}else if (i==l){
			this._absoluteTimeInitiators[i]=new metadata.AbsoluteTimeInitiator(null, this._errors, this._definitionType, this._epn);
		}
		this._absoluteTimeInitiators[i].setTimestamp(timestamp);					
	},
	
	getAbsoluteTimeInitiatorRepeatingInterval: function(i){
		if (i<this._absoluteTimeInitiators.length){
			return this._absoluteTimeInitiators[i].getRepeatingInterval();
		}			
	},
	
	setAbsoluteTimeInitiatorRepeatingInterval: function(i, repeatingInterval){
		var l=this._absoluteTimeInitiators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - absolute time initiator out of bound");
			return;
		}else if (i==l){
			this._absoluteTimeInitiators[i]=new metadata.AbsoluteTimeInitiator(null, this._errors, this._definitionType, this._epn);
		}
		this._absoluteTimeInitiators[i].setRepeatingInterval(repeatingInterval);					
	},

	//Terminator methods
	
	setNeverEnding: function(neverEnding){
		if(neverEnding===false || neverEnding===true){
			this._neverEnding=neverEnding;
		}else{
			//TODO: generate error
			console.log("unsupported never ending value: "+ neverEnding + " in temporal context " + this._name);
		}
	},
	getNeverEnding: function(){
		return this._neverEnding;
	},
	
	//Event Terminator methods
	
	getNumberOfEventTerminators: function(){
		return this._eventTerminators.length;		
	},
	
	deleteEventTerminator: function(i){
		if(i>=this._eventTerminators.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete terminator " + i + " of event " + 
			    this.getName() + ". Terminator is out of scope: ");
		}else{
			this._eventTerminators=this._eventTerminators.slice(0, i).concat(this._eventTerminators.slice(i+1));
		}
	},
	
	getEventTerminatorName: function(i){
		if (i<this._eventTerminators.length){
			return this._eventTerminators[i].getName();
		}			
	},
	setEventTerminatorName: function(i, name){
		var l=this._eventTerminators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - event terminator out of bound");
			return;
		}else if (i==l){
			this._eventTerminators[i]=new metadata.EventTerminator(null, this._errors, this._definitionType, this._epn);
		}
		this._eventTerminators[i].setName(name);					
	},
	
	getEventTerminatorCondition: function(i){
		if (i<this._eventTerminators.length){
			return this._eventTerminators[i].getCondition();
		}			
	},
	setEventTerminatorCondition: function(i, condition){
		var l=this._eventTerminators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - event terminator out of bound");
			return;
		}else if (i==l){
			this._eventTerminators[i]=new metadata.EventTerminator(null, this._errors, this._definitionType, this._epn);
		}
		this._eventTerminators[i].setCondition(condition);					
	},
	
	getEventTerminatorQuantifierPolicy: function(i){
		if (i<this._eventTerminators.length){
			return this._eventTerminators[i].getPolicy();
		}			
	},
	setEventTerminatorQuantifierPolicy: function(i, quantifierPolicy){
		var l=this._eventTerminators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - event terminator out of bound");
			return;
		}else if (i==l){
			this._eventTerminators[i]=new metadata.EventTerminator(null, this._errors, this._definitionType, this._epn);
		}
		this._eventTerminators[i].setPolicy(quantifierPolicy);					
	},
	
	getEventTerminatorType: function(i){
		if (i<this._eventTerminators.length){
			return this._eventTerminators[i].getTerminationType();
		}			
	},
	setEventTerminatorType: function(i, type){
		var l=this._eventTerminators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - event terminator out of bound");
			return;
		}else if (i==l){
			this._eventTerminators[i]=new metadata.EventTerminator(null, this._errors, this._definitionType, this._epn);
		}
		this._eventTerminators[i].setTerminationType(type);					
	},
	
	//Absolute Time Terminator methods

	getNumberOfAbsoluteTerminators: function(){
		return this._absoluteTerminators.length;		
	},
	
	deleteAbsoluteTerminator: function(i){
		if(i>=this._absoluteTerminators.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete absolute time terminator " + i + " of event " + 
			    this.getName() + ". Terminator is out of scope: ");
		}else{
			this._absoluteTerminators=this._absoluteTerminators.slice(0, i).concat(this._absoluteTerminators.slice(i+1));
		}
	},

	
	getAbsoluteTerminatorTimestamp: function(i){
		if (i<this._absoluteTerminators.length){
			return this._absoluteTerminators[i].getTimestamp();
		}			
	},
	setAbsoluteTerminatorTimestamp: function(i, time){
		var l=this._absoluteTerminators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - absolute terminator out of bound");
			return;
		}else if (i==l){
			this._absoluteTerminators[i]=new metadata.AbsoluteTimeTerminator(null, this._errors, this._definitionType, this._epn);
		}
		this._absoluteTerminators[i].setTimestamp(time);					
	},

	getAbsoluteTerminatorQuantifierPolicy: function(i){
		if (i<this._absoluteTerminators.length){
			return this._absoluteTerminators[i].getPolicy();
		}			
	},
	setAbsoluteTerminatorQuantifierPolicy: function(i, policy){
		var l=this._absoluteTerminators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - absolute terminator out of bound");
			return;
		}else if (i==l){
			this._absoluteTerminators[i]=new metadata.AbsoluteTimeTerminator(null, this._errors, this._definitionType, this._epn);
		}
		this._absoluteTerminators[i].setPolicy(policy);					
	},

	getAbsoluteTerminatorType: function(i){
		if (i<this._absoluteTerminators.length){
			return this._absoluteTerminators[i].getTerminationType();
		}			
	},
	setAbsoluteTerminatorType: function(i, type){
		var l=this._absoluteTerminators.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - absolute terminator out of bound");
			return;
		}else if (i==l){
			this._absoluteTerminators[i]=new metadata.AbsoluteTimeTerminator(null, this._errors, this._definitionType, this._epn);
		}
		this._absoluteTerminators[i].setTerminationType(type);					
	},

	//Relative Time Terminator methods
	
	isRelativeTerminator: function(){
		return this._relativeTerminator?true:false;
	},
	
	deleteRelativeTerminator: function(){
		this._relativeTerminator=null;
	},
	
	getRelativeTerminatorRelativeTime: function(){
		if (this._relativeTerminator){
			return this._relativeTerminator.getRelativeTime();
		}			
	},
	setRelativeTerminatorRelativeTime: function(time){
		if (!this._relativeTerminator){
			this._relativeTerminator=new metadata.RelativeTimeTerminator(null, this._errors, this._definitionType, this._epn);
		}
		this._relativeTerminator.setRelativeTime(time);					
	},

	getRelativeTerminatorType: function(){
		if (this._relativeTerminator){
			return this._relativeTerminator.getTerminationType();
		}			
	},
	setRelativeTerminatorType: function(type){
		if(type && type!=""){
			if (!this._relativeTerminator){
				this._relativeTerminator=new metadata.RelativeTimeTerminator(null, this._errors, this._definitionType, this._epn);
			}
			this._relativeTerminator.setTerminationType(type);
		}
	}

});
