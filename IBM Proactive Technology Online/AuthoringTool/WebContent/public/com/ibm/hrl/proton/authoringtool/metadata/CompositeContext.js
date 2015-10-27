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
 dojo.provide("metadata.CompositeContext");
dojo.require("metadata.ParseError");
dojo.require("metadata.BaseDefinition");
dojo.declare("metadata.CompositeContext",metadata.BaseDefinition,{
	constructor: function(args, errors, definitionType, epn) {

		//"private" variables should be accessed by setters/getters

		//each context entry is an object with a _context attribute
		this._temporalContexts=new Array(); 
		this._segmentationContexts=new Array(); 
		this._epn = epn;
		this._errors = errors;
		this._definitionType = definitionType;
		var i,l;
		try{
			if (dojo.isObject(args)){//composite context from the json
				if(!args || !args.name){
					//TODO: generate error
					throw new metadata.ParseError("");
				}else{
					if(args.temporalContexts){
						for(i=0, l=args.temporalContexts.length; i<l; i++){
							this._temporalContexts[i]=new Object();
							if(args.temporalContexts[i].name){this.setTemporalContext(i,args.temporalContexts[i].name);}
						}
					}
					if(args.segmentationContexts){
						for(i=0, l=args.segmentationContexts.length; i<l; i++){
							this._segmentationContexts[i]=new Object();
							if(args.segmentationContexts[i].name){this.setSegmentationContext(i,args.segmentationContexts[i].name);}
						}
					}

				}	
			}//else, new context defined by the user, handled by base definition
		
		}catch(err){
			err.msg = "Error parsing composite context " + this._name + ". " + err.msg;
			console.log(err.msg);			
			throw err;
		}
	},
	
	//temporal contexts
	getNumberOfTemporalContexts: function(){
		return this._temporalContexts.length;		
	},
	
	deleteTemporalContext: function(i){
		if(i>=this._temporalContexts.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete temporal context " + i + " of composite context " + 
			    this.getName() + ". Temporal context is out of scope: ");
		}else{
			this._temporalContexts=this._temporalContexts.slice(0, i).concat(this._temporalContexts.slice(i+1));
		}
	},

	
	setTemporalContext: function(i, context){
		if(!this._epn.isContextExists(context)){
			//TODO: generate error
			console.log("error in composite context " + this._name +". Context doesn't exist: "+ context);
			return false;
		}else{
			if(this._temporalContexts.length>i){
				this._temporalContexts[i]._context=context;
			}else if(this._temporalContexts.length===i){
				this._temporalContexts[i]=new Object();
				this._temporalContexts[i]._context=context;			
			}else{
				//TODO: generate error
				console.log("Error parsing composite context " + this._name + " . Internal Error: temporal context out of range: " + i);			
			}
		}
	},
	
	getTemporalContext: function(i){
		if(this._temporalContexts.length<=i){
			//TODO: generate error
			console.log("Internal error in contexts context " + this._name +". Out of bound index "+ i);
		}else{
			return this._temporalContexts[i]._context;
		}
	},
	
	//segmentation contexts
	getNumberOfSegmentationContexts: function(){
		return this._segmentationContexts.length;		
	},
	
	deleteSegmentationContext: function(i){
		if(i>=this._segmentationContexts.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete segmentation context " + i + " of composite context " + 
			    this.getName() + ". Segmentation context is out of scope: ");
		}else{
			this._segmentationContexts=this._segmentationContexts.slice(0, i).concat(this._segmentationContexts.slice(i+1));
		}
	},

	
	setSegmentationContext: function(i, context){
		if(!this._epn.isContextExists(context)){
			//TODO: generate error
			console.log("error in composite context " + this._name +". Context doesn't exist: "+ context);
			return false;
		}else{
			if(this._segmentationContexts.length>i){
				this._segmentationContexts[i]._context=context;
			}else if(this._segmentationContexts.length===i){
				this._segmentationContexts[i]=new Object();
				this._segmentationContexts[i]._context=context;			
			}else{
				//TODO: generate error
				console.log("Error parsing composite context " + this._name + " . Internal Error: _segmentation context out of range: " + i);			
			}
		}
	},
	
	getSegmentationContext: function(i){
		if(this._segmentationContexts.length<=i){
			//TODO: generate error
			console.log("Internal error in composite context " + this._name +". Out of bound index "+ i);
		}else{
			return this._segmentationContexts[i]._context;
		}
	}



});
