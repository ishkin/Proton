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
 dojo.provide("metadata.Template");
dojo.require("metadata.ParseError");
dojo.require("metadata.BaseDefinition");
dojo.declare("metadata.Template",metadata.BaseDefinition,{
	constructor: function(args, errors, definitionType, epn) {

		//"private" variables should be accessed by setters/getters

		//each property entry is an object with a _name, _value attributes
		this._properties=new Array(); 
		this._type=null;		
		this._epn = epn;
		this._errors = errors;
		this._definitionType = definitionType;
		var i,l;
		try{
			if (dojo.isObject(args)){//consumer/producer context from the json
				if(!args || !args.name){
					//TODO: generate error
					throw new metadata.ParseError("");
				}else{
					if(args.properties){
						for(i=0, l=args.properties.length; i<l; i++){
							this._properties[i]=new Object();
							if(args.properties[i].name){this.setPropertyName(i,args.properties[i].name);}
							if(args.properties[i].value){this.setPropertyValue(i,args.properties[i].value);}
						}
					}
					
					//the setType adds missing attributes related to this type
					if(!args.type){
						//TODO: generate error
						console.log("Missing type in template "+this.getName());
					}else{
						this.setType(args.type, true);
					}

				}	
			}//else, new context defined by the user, handled by base definition
		
		}catch(err){
			err.msg = "Error parsing template " + this._name + ". " + err.msg;
			console.log(err.msg);			
			throw err;
		}
	},
	
	getType: function(){
		return this._type;
	},
	
	//readingJson is true if the definition is read from the JSON - in this case do not add builtin attributes
	setType: function(type, readingJson){
		if(this._type!==type){
			for(var t in TemplateEnum.TemplateType){
				if(TemplateEnum.TemplateType[t]===type){
					if(this._type){
						//if we changing type (and not building a new definition)
						//remove old attributes (assuming that changing the type changes the whole meaning)
						this._properties=new Array();
					}
					if(!readingJson){
						//add missing built-in attributes of this type
						this.addMissingBuiltInAttrbutes(type);
					}
					this._type=type;
					return;
				}
			}
			//TODO: generate error
			console.log("error in " + this.getName() + " unsupported template type " + type);
		}
	},
	
	addMissingBuiltInAttrbutes: function(type){
		var i,l,builtInAttributes;
		
		
		builtInAttributes = TemplateEnum.TemplateBuiltInAttributes;
		
		for(var t in builtInAttributes){
			if(t===type){
				for(i=0, l=builtInAttributes[t].length; i<l; i++){
					this.addBuiltInProperty(builtInAttributes[t][i]);
				}
				return;
			}
		}
		
	},
	
	addBuiltInProperty:function(attr){
		var name = attr.name;
		var attrIndex = this._properties.length;
		if(!this.hasProperty(name)){
			this.setPropertyName(attrIndex, name);
			if(attr.value){this.setPropertyValue(attrIndex, attr.value);}
			if(attr.description){this.setPropertyDescription(attrIndex, attr.description);}
		}
	},	
	
	getNumberOfProperties: function(){
		return this._properties.length;		
	},
	
	deleteProperty: function(i){
		if(i>=this._properties.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete property " + i + " of template " + 
			    this.getName() + ". property  is out of scope: ");
		}else{
			this._properties=this._properties.slice(0, i).concat(this._properties.slice(i+1));
		}
	},

	hasProperty: function(name){
		var i,l;
		for(i=0,l=this._properties.length; i<l; i++){
			if(name&&this._properties[i]._name.toLowerCase()===name.toLowerCase()){
				return true;
			}
		}
		return false;
	},
	
	setPropertyName: function(i, name){
		if(this._properties.length>i){
			this._properties[i]._name=name;
		}else if(this._properties.length===i){
			this._properties[i]=new Object();
			this._properties[i]._name=name;			
		}else{
			//TODO: generate error
			console.log("Error parsing template " + this._name + " . Internal Error: property out of range: " + i);			
		}
	},
	
	getPropertyName: function(i){
		if(this._properties.length<=i){
			//TODO: generate error
			console.log("Internal error in template " + this._name +". Out of bound index "+ i);
		}else{
			return this._properties[i]._name;
		}
	},
	
	setPropertyValue: function(i, value){
		if(this._properties.length>i){
			this._properties[i]._value=value;
		}else if(this._properties.length===i){
			this._properties[i]=new Object();
			this._properties[i]._value=value;			
		}else{
			//TODO: generate error
			console.log("Error parsing template " + this._name + " . Internal Error: property out of range: " + i);			
		}
	},
	
	getPropertyValue: function(i){
		if(this._properties.length<=i){
			//TODO: generate error
			console.log("Internal error in template " + this._name +". Out of bound index "+ i);
		}else{
			return this._properties[i]._value;
		}
	},
	setPropertyDescription: function(i, desc){
		if(this._properties.length>i){
			this._properties[i]._description=desc;
		}else if(this._properties.length===i){
			this._properties[i]=new Object();
			this._properties[i]._description=desc;			
		}else{
			//TODO: generate error
			console.log("Error parsing template " + this._name + " . Internal Error: property out of range: " + i);			
		}
	},
	
	getPropertyDescription: function(i){
		if(this._properties.length<=i){
			//TODO: generate error
			console.log("Internal error in template " + this._name +". Out of bound index "+ i);
		}else{
			return this._properties[i]._description;
		}
	},


	
	
	

	
	
	
	
	
	
	


});
