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
 dojo.provide("metadata.EventActionBase");
dojo.require("metadata.BaseDefinition");
dojo.require("metadata.EventAttribute");
dojo.require("metadata.ParseError");
dojo.declare("metadata.EventActionBase",metadata.BaseDefinition,{
	
	constructor: function(args,errors, definitionType, epn) {
		
		this._defaultAttributeType="attributes"; //for event
		
		try{
			this.setParameterTypes();
			if(dojo.isObject(args)){//new from the json
				if(!args || !args.name){
					console.log("Error parsing event - no name");	
					return;
				}
				
				var parametrType;
				for(var p=0, numOfTypes=this.parameterTypes.length; p<numOfTypes; p++){
					parametrType=this.parameterTypes[p];
					if(!this[parametrType]){
						this[parametrType]=new Array();
					}
					this.addAttributesBase(parametrType, args[parametrType]);
				}
			}else{
				//else, this is a new definition from the user, handled by the super class
				this.addBuiltInAttributes(); //only missing attributes will be added
			}
			//it is a decision to add the built-in attributes only once in the creation (since the user can't delete them)
		}
		catch(err){
			console.log("Error parsing event " + this.name + ". " + err.msg);	
			return;
		}
	},
	
	addAttributesBase:function(attributeType, attributes){
		if(!attributeType){
			attributeType=this._defaultAttributeType;
		}
		for(i=0, l=attributes.length; i<l; i++){ 
			//if(!this.isAttributeBase(attributes[i].name)){
				this.setAttributeNameBase(attributeType, i, attributes[i].name);
				this.setAttributeTypeBase(attributeType, i, attributes[i].type);
				if(attributes[i].dimension){
					this.setAttributeDimensionBase(attributeType, i, attributes[i].dimension);
				}
				if(attributes[i].defaultValue){
					this.setAttributeDefaultValueBase(attributeType, i, attributes[i].defaultValue);
				}
				if(attributes[i].description){
					this.setAttributeDescriptionBase(attributeType, i, attributes[i].description);
				}
			//}
		}
	},
	
	isAttributeBase: function(attr){
		var i,l, paramType;
		for(var p=0, numOfTypes=this.parameterTypes.length; p<numOfTypes; p++){
			paramType=this.parameterTypes[p];
			if(this[paramType]){
				for(i=0,l=this[paramType].length; i<l; i++){
					if (this.getAttributeNameBase(paramType, i)===attr) return true;
				}
			}
		}
		return false;
	},
	
	getAttributesArrayBase: function(attributeType){
		if(!attributeType){attributeType=this._defaultAttributeType;}
		if(!this[attributeType]){return null;}
		return this[attributeType];
	},
	
	getNumberOfAttributesBase: function(attributeType){
		if(!attributeType){attributeType=this._defaultAttributeType;}
		if(!this[attributeType]){return 0;}
		return this[attributeType].length;
	},
	
	deleteAttributeBase: function(attributeType, i){
		if(!this[attributeType]){return;}
		if(i>=this[attributeType].length||i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete attribute " + i + " of event/action " + 
					    this.getName() + ". Attribte is out of scope: ");
		}else{
			this[attributeType]=this[attributeType].slice(0, i).concat(this[attributeType].slice(i+1));
		}
	},
	
	getAttributeNameBase: function(attributeType, i){
		if(this[attributeType] && this[attributeType].length>i){
			return this[attributeType][i]._name;
		}
		return null;
	},

	setAttributeNameBase: function(attributeType,i,name){
		//TODO: verify it is a legal name and unique within this event attributes (note: it may be null which mean an error)  
		if(!this[attributeType]) {this[attributeType]=new Array();}
		if(this[attributeType].length>i){
			this[attributeType][i]._name=name;
		}else if(this[attributeType].length===i){
			this[attributeType][i]=new metadata.EventAttribute();
			this[attributeType][i]._name=name;
		}else{
			console.log("Error parsing event attribue " + name + " of event " + this._name + " . Internal Error: Attribute out of range: " + i);	
		}
	},
	
	
	getAttributeTypeBase: function(attributeType, i){
		if(this[attributeType] && this[attributeType].length>i){
			return this[attributeType][i]._type;			
		}
		return null;
	},

	isLegalType: function(type){
		for(var t in ATEnum.AttributeTypes){
			if(ATEnum.AttributeTypes[t]===type){
				return true;
			}
		}
		return false;		
	},
	
	setAttributeTypeBase: function(attributeType, i,type){
		
		if (!this.isLegalType(type)){
			//TODO: generate error
			console.log("error in event " + this.getName() + " - unsupported attribute type " + type);
			return;
		}
		if(!this[attributeType]) {this[attributeType]=new Array();}
		if(this[attributeType].length>i){
			this[attributeType][i]._type=type;
		}else if([attributeType].length===i){
			[attributeType][i]=new metadata.EventAttribute();
			[attributeType][i]._type=type;	
		}else{
			console.log("Error parsing event " + this._name + " attribue. Internal Error: Attribute out of range: " + i);			
		}
	},

	getAttributeDimensionBase: function(attributeType, i){
		if(this[attributeType] && this[attributeType].length>i){
			return this[attributeType][i]._dimension;			
		}
		return null;
	},

	setAttributeDimensionBase: function(attributeType, i,dimension){
		//TODO: verify it is a legal dimension   
		if(!this[attributeType]) {this[attributeType]=new Array();}
		if(this[attributeType].length>i){
			this[attributeType][i]._dimension=dimension;
		}else if(this[attributeType].length===i){
			this[attributeType][i]=new metadata.EventAttribute();
			this[attributeType][i]._dimension=dimension;			
		}else{
			console.log("Error parsing event " + this._name + " attribue. Internal Error: Attribute out of range: " + i);			
		}
	},
	
	getAttributeDefaultValueBase: function(attributeType, i){
		if(this[attributeType] && this[attributeType].length>i){
			return this[attributeType][i]._defaultValue;			
		}
		return null;
	},

	setAttributeDefaultValueBase: function(attributeType, i,defaultValue){
		//TODO: verify it is a legal dimension   
		if(!this[attributeType]) {this[attributeType]=new Array();}
		if(this[attributeType].length>i){
			this[attributeType][i]._defaultValue=defaultValue;
		}else if(this[attributeType].length===i){
			this[attributeType][i]=new metadata.EventAttribute();
			this[attributeType][i]._defaultValue=defaultValue;			
		}else{
			console.log("Error parsing event " + this._name + " attribue. Internal Error: Attribute out of range: " + i);			
		}
	},
	
	getAttributeDescriptionBase: function(attributeType, i){
		if(this[attributeType] && this[attributeType].length>i && this[attributeType][i]._description){
			return this[attributeType][i]._description;			
		}
		return null;
	},

	setAttributeDescriptionBase: function(attributeType, i,description){
		if(!this[attributeType]) {this[attributeType]=new Array();}
		if(this[attributeType].length<i){
			console.log("Error parsing event " + this._name + " attribue. Internal Error: Attribute out of range: " + i);
			return;
		}else if(this[attributeType].length===i){
			this[attributeType][i]=new metadata.EventAttribute();
		}
		this[attributeType][i]._description=description;		
	},

	
	toString: function(){
		return this.getName();
	}

	

});
