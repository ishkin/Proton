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
 dojo.provide("metadata.Terminator");
dojo.require("metadata.ParseError");
dojo.declare("metadata.Terminator",null,{
	constructor: function(terminatorObject, errors, definitionType, epn) {

		this._type=null;
		this._terminationType=null;
		this._epn=epn;
		
		try{
			if(terminatorObject){
				//TODO: according to the terminator type, certain fields are mandatory
				if(terminatorObject.terminatorType){this.setType(terminatorObject.terminatorType);}
				if(terminatorObject.terminationType){this.setTerminationType(terminatorObject.terminationType);}
			}
		}catch(err){
			///TODO: generate error
			console.log("error in terminator parsing");
		}
	},
	
	setType: function(type){
		for(var t in ATEnum.TerminatorType){
			if(ATEnum.TerminatorType[t]===type){
				this._type=type;
				return;
			}
		}
		//TODO: generate error
		console.log("error - non exist terminator type " + type);		
	},
	getType:function(){
		return this._type;
	},

	setTerminationType: function(terminationType){
		for(var t in ATEnum.TerminationType){
			if(ATEnum.TerminationType[t]===terminationType){
				this._terminationType=terminationType;
				return;
			}
		}
		//TODO: generate error
		console.log("error - non exist terminator termination type " + terminationType);		
	},
	getTerminationType:function(){
		return this._terminationType;
	}
});
