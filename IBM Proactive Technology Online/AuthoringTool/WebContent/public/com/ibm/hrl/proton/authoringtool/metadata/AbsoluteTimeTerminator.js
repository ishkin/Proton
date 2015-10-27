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
 dojo.provide("metadata.AbsoluteTimeTerminator");
dojo.require("metadata.ParseError");
dojo.require("metadata.Terminator");
dojo.declare("metadata.AbsoluteTimeTerminator",metadata.Terminator,{
	constructor: function(terminatorObject, errors, definitionType, epn) {

		this._policy=null;
		this._timestamp=null;
		this.setType(ATEnum.TerminatorType.AbsoluteTime);
		
		try{
			if(terminatorObject){
				//TODO: according to the terminator type, certain fields are mandatory
				if(terminatorObject.terminatorPolicy){this.setPolicy(terminatorObject.terminatorPolicy);}
				if(terminatorObject.timeStamp){this.setTimestamp(terminatorObject.timeStamp);}
			}
		}catch(err){
			///TODO: generate error
			console.log("error in terminator parsing");
		}
	},
	
	setPolicy: function(policy){
		for(var p in ATEnum.TerminatorPolicy){
			if(ATEnum.TerminatorPolicy[p]===policy){
				this._policy=policy;
				return;
			}
		}
		//TODO: generate error
		console.log("error - non exist terminator policy " + policy);		
	},
	getPolicy:function(){
		return this._policy;
	},
	
	setTimestamp: function(timestamp){
		//TODO check timestamp using regexp or EEP
		this._timestamp=timestamp;
	},
	getTimestamp: function(){
		return this._timestamp;
	}
	
});
