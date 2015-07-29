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
 dojo.provide("metadata.Initiator");
dojo.require("metadata.ParseError");
dojo.declare("metadata.Initiator",null,{
	constructor: function(initiatorObject, errors, definitionType, epn) {

		this._type=null;
		this._policy=null;
		this._epn=epn;
		
		try{
			if(initiatorObject){
				if(initiatorObject.initiatorType){this.setType(initiatorObject.initiatorType);}
				if(initiatorObject.initiatorPolicy){this.setPolicy(initiatorObject.initiatorPolicy);}
			}
		}catch(err){
			///TODO: generate error
			console.log("error in initiator parsing");
		}
	},
	
	setType: function(type){
		for(var t in ATEnum.InitiatorType){
			if(ATEnum.InitiatorType[t]===type){
				this._type=type;
				return;
			}
		}
		//TODO: generate error
		console.log("error - non exist initiator type " + type);		
	},
	getType:function(){
		return this._type;
	},

	setPolicy: function(policy){
		for(var p in ATEnum.InitiatorPolicy){
			if(ATEnum.InitiatorPolicy[p]===policy){
				this._policy=policy;
				return;
			}
		}
		//TODO: generate error
		console.log("error - non exist initiator policy " + policy);		
	},
	getPolicy:function(){
		return this._policy;
	}
	
});
