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
 dojo.provide("metadata.DerivedEvent");
dojo.require("metadata.ParseError");
 
dojo.declare("metadata.DerivedEvent",null,{
	/* Class Body */
	constructor: function(args, errors, definitionType, epn) {
		//Class "private" Members - accessed directly by the Event metadata object
		
		this._name;
		this._condition;
		this._reportParticipants=false;  //default value
		this._expressions=new Object; //expression for each event attribute 
		this._event;
		this._epn=epn;
		
		if(dojo.isObject(args)){//derived event from the json
			if(!args.name){
				//TODO: generate error
				console.log("error in derived event name");	
			}else{
				if(this.setName(args.name)){
					if(args.condition) {this.setCondition(args.condition);}
					if(args.reportParticipants) {this.setReportParticipants(args.reportParticipants);}
					if(args.expressions){
						for(var attr in args.expressions){
							this.setExpressionByAttributeName(attr, args.expressions[attr]);
						}
					}
				}
			}
		}else{ //new derived event by the user
			this.setName(args);
		}
	},
	setName: function(name){
		if(!this._epn.isEventExists(name)){
			//TODO: generate error
			console.log("error in derived event");
			return false;
		}else{
			this._name=name;
			this._event=this._epn.getEvent(this._name);
			return true;
		}		
	},
	getName: function(){
		return this._name;
	},
	
	setCondition: function(condition){
		//TODO: check condition validity
		this._condition=condition;
	},

	getCondition: function(){
		return this._condition;
	},

	setReportParticipants: function(reportParticipants){
		if(reportParticipants===true || reportParticipants===false){
			this._reportParticipants=reportParticipants;
		}
		else{
			//TODO: generate error
			console.log("error in report participants of derived events " + reportParticipants);
		
		}
	}, 
	isReportParticipants: function(){
		return this._reportParticipants; //returns boolean
	},
	
	setExpressionByAttributeName: function(attributeName, expression){
		if(!attributeName){
			//TODO: generate error
			console.log("error in derived event " + this._name + ", expression for non existing attribute: "+ attributeName);	
		}else{
			//TODO: validate the expression
			this._expressions[attributeName]=expression;
		}	
	},
	
	setExpression: function(attrIndex, expression){
		var attributeName=this._event.getAttributeName(attrIndex);
		this.setExpressionByAttributeName(attributeName,expression);			
	},	
	
	getExpression: function(attr){
		return this._expressions[attr];
	},
	
	getEvent: function(){
		return this._event; 
	}

	});
