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
 dojo.provide("metadata.AbsoluteTimeInitiator");
dojo.require("metadata.Initiator");
dojo.require("metadata.ParseError");
dojo.declare("metadata.AbsoluteTimeInitiator",metadata.Initiator,{
	constructor: function(initiatorObject, errors, definitionType, epn) {

		this._timestamp=null;
		this._repeatingInterval=null;
		this.setType(ATEnum.InitiatorType.AbsoluteTime);
		
		try{
			if(initiatorObject){
				if(initiatorObject.timeStamp){this.setTimestamp(initiatorObject.timeStamp);}
				if(initiatorObject.repeatingInterval){this.setRepeatingInterval(initiatorObject.repeatingInterval);}
			}
		}catch(err){
			///TODO: generate error
			console.log("error in initiator parsing");
		}
	},
	

	setTimestamp: function(timestamp){
		//TODO check timestamp using regexp or EEP
		this._timestamp=timestamp;
	},
	getTimestamp: function(){
		return this._timestamp;
	},
	setRepeatingInterval: function(repeatingInterval){
		//TODO check repeatingInterval 
		this._repeatingInterval=repeatingInterval;
	},
	getRepeatingInterval: function(){
		return this._repeatingInterval;
	}
	
	
});
