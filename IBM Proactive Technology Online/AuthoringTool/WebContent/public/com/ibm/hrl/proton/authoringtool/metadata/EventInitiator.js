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
 dojo.provide("metadata.EventInitiator");
dojo.require("metadata.Initiator");
dojo.require("metadata.ParseError");
dojo.declare("metadata.EventInitiator",metadata.Initiator,{
	
	constructor: function(initiatorObject, errors, definitionType, epn) {

		this._name="";
		this._condition=null;
		this.setType(ATEnum.InitiatorType.Event);
		
		try{
			if(initiatorObject){
				if(initiatorObject.name){this.setName(initiatorObject.name);}
				if(initiatorObject.condition){this.setCondition(initiatorObject.condition);}
			}
		}catch(err){
			///TODO: generate error
			console.log("error in initiator parsing");
		}
	},
	
	setName: function(name){
		if(this._epn.isEventExists(name)){
			this._name=name;
		}else{
			//TODO: generate error
			console.log("error - non exist initiator event");
		}		
	},
	getName: function(){
		return this._name;
	},
	
	setCondition:function(condition){
		//TODO check condition
		this._condition=condition;
	},
	getCondition: function(){
		return this._condition;
	}
	
});
