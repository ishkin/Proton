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
 dojo.provide("metadata.SegmentationContext");
dojo.require("metadata.ParseError");
dojo.require("metadata.BaseDefinition");
dojo.declare("metadata.SegmentationContext",metadata.BaseDefinition,{
	constructor: function(args, errors, definitionType, epn) {

		//"private" variables should be accessed by setters/getters
		this._segmentations=new Array(); //each entry is an object with _event, _expression attributes
		this._epn = epn;
		this._errors = errors;
		this._definitionType = definitionType;
		var i,l;
		try{
			if (dojo.isObject(args)){//segmentation context from the json
				if(!args || !args.name){
					//TODO: generate error
					throw new metadata.ParseError("");
				}else{
					if(args.participantEvents){
						for(i=0, l=args.participantEvents.length; i<l; i++){
							this._segmentations[i]=new Object();
							if(args.participantEvents[i].name){this.setSegmentationEvent(i,args.participantEvents[i].name);}
							if(args.participantEvents[i].expression){this.setSegmentationExpression(i,args.participantEvents[i].expression);}							
						}
					}
					
				}	
			}//else, new context defined by the user, handled by base definition
		}
		catch(err){
			err.msg = "Error parsing segmentation context " + this._name + ". " + err.msg;
			console.log(err.msg);			
			throw err;
		}
	},
	
	getNumberOfSegmentationEvents: function(){
		return this._segmentations.length;		
	},
	
	deleteSegmentationEvent: function(i){
		if(i>=this._segmentations.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to segmentation event " + i + " of segmentation context " + 
			    this.getName() + ". Segmentation event is out of scope: ");
		}else{
			this._segmentations=this._segmentations.slice(0, i).concat(this._segmentations.slice(i+1));
		}
	},

	setSegmentationEvent: function(i, event){
		if(!this._epn.isEventExists(event)){
			//TODO: generate error
			console.log("error in segmentation context " + this._name +". Event doesn't exist: "+ event);
			return false;
		}else{
			if(this._segmentations.length>i){
				this._segmentations[i]._event=event;
			}else if(this._segmentations.length===i){
				this._segmentations[i]=new Object();
				this._segmentations[i]._event=event;			
			}else{
				//TODO: generate error
				console.log("Error parsing segmentation context " + this._name + " . Internal Error: segmentation out of range: " + i);			
			}
		}
	},
	
	getSegmentationEvent: function(i){
		if(this._segmentations.length<=i){
			//TODO: generate error
			console.log("Internal error in segmentation context " + this._name +". Outofbound index "+ i);
		}else{
			return this._segmentations[i]._event;
		}
	},
	
	setSegmentationExpression: function(i, expression){
		//TODO: verify expression
		if(this._segmentations.length>i){
			this._segmentations[i]._expression=expression;
		}else if(this._segmentations.length===i){
			this._segmentations[i]=new Object();
			this._segmentations[i]._expression=expression;			
		}else{
			//TODO: generate error
			console.log("Error parsing segmentation context " + this._name + " . Internal Error: segmentation out of range: " + i);			
		}
	},
	
	getSegmentationExpression: function(i){
		if(this._segmentations.length<=i){
			//TODO: generate error
			console.log("Internal error in segmentation context " + this._name +". Outofbound index "+ i);
		}else{
			return this._segmentations[i]._expression;
		}
	}



});

