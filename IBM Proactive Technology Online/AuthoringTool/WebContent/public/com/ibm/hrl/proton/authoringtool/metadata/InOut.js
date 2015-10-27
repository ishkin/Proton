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
 dojo.provide("metadata.InOut");
dojo.require("metadata.ParseError");
dojo.require("metadata.BaseDefinition");
dojo.declare("metadata.InOut",metadata.BaseDefinition,{
	constructor: function(args, errors, definitionType, epn) {

		//"private" variables should be accessed by setters/getters

		//each property entry is an object with a _name, _value attributes
		this._properties=new Array(); 
		this._type=null;
		this._events=new Array(); //array of event (_name & _condition)
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
					if(args.events){
						for(i=0, l=args.events.length; i<l; i++){
							this._events[i]=new Object();
							if(args.events[i].name){this.setEventName(i,args.events[i].name);}
							if(args.events[i].condition){this.setEventCondition(i,args.events[i].condition);}
						}
					}
					//the setType adds missing attributes related to this type
					if(!args.type){
						//TODO: generate error
						console.log("Missing type in consumer/producer "+this.getName());
					}else{
						this.setType(args.type, true);
					}

				}	
			}//else, new context defined by the user, handled by base definition
		
		}catch(err){
			err.msg = "Error parsing consumer/producer " + this._name + ". " + err.msg;
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
			for(var t in ATEnum.InOutType){
				if(ATEnum.InOutType[t]===type){
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
			console.log("error in " + this.getName() + " unsupported consumer/producer type " + type);
		}
	},
	
	addMissingBuiltInAttrbutes: function(type){
		var i,l,builtInAttributes;
		
		if(this._definitionType ===ATEnum.Definitions.Consumer){
			builtInAttributes = ATEnum.ConsumerBuiltInAttributes;
		}else{
			builtInAttributes = ATEnum.ProducerBuiltInAttributes;
		}
		for(var t in builtInAttributes){
			if(t===type){
				for(i=0, l=builtInAttributes[t].length; i<l; i++){
					this.addBuiltInProperty(builtInAttributes[t][i]);
				}
				return;
			}
		}
		/*
		if(type===ATEnum.InOutType.File){
			if(this._definitionType ===ATEnum.Definitions.Consumer){
				for(i=0, l=ATEnum.ConsumerFileProperties.length; i<l; i++){
					this.addBuiltInProperty(ATEnum.ConsumerFileProperties[i]);
				}
			}else{ //Producer
				for(i=0, l=ATEnum.ProducerFileProperties.length; i<l; i++){
					this.addBuiltInProperty(ATEnum.ProducerFileProperties[i]);
				}
			}
		}else if(type===ATEnum.InOutType.Database){
			//both producer and consumers have the same properties
			for(i=0, l=ATEnum.InOutDatabaseProperties.length; i<l; i++){
				this.addBuiltInProperty(ATEnum.InOutDatabaseProperties[i]);
			}
		}else if(type===ATEnum.InOutType.JMS){
			//both producer and consumers have the same properties
			for(i=0, l=ATEnum.InOutJMSProperties.length; i<l; i++){
				this.addBuiltInProperty(ATEnum.InOutJMSProperties[i]);
			}
		}	
		*/	
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
			console.log("Internal error in trying to delete property " + i + " of consumer/producer " + 
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
			console.log("Error parsing consumer/producer " + this._name + " . Internal Error: property out of range: " + i);			
		}
	},
	
	getPropertyName: function(i){
		if(this._properties.length<=i){
			//TODO: generate error
			console.log("Internal error in consumer/producer " + this._name +". Out of bound index "+ i);
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
			console.log("Error parsing consumer/producer " + this._name + " . Internal Error: property out of range: " + i);			
		}
	},
	
	getPropertyValue: function(i){
		if(this._properties.length<=i){
			//TODO: generate error
			console.log("Internal error in consumer/producer " + this._name +". Out of bound index "+ i);
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
			console.log("Error parsing consumer/producer " + this._name + " . Internal Error: property out of range: " + i);			
		}
	},
	
	getPropertyDescription: function(i){
		if(this._properties.length<=i){
			//TODO: generate error
			console.log("Internal error in consumer/producer " + this._name +". Out of bound index "+ i);
		}else{
			return this._properties[i]._description;
		}
	},


	getNumberOfEvents: function(){
		return this._events.length;		
	},
	
	deleteEvent: function(i){
		if(i>=this._events.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete event " + i + " of consumer/producer " + 
			    this.getName() + ". event  is out of scope: ");
		}else{
			this._events=this._events.slice(0, i).concat(this._events.slice(i+1));
		}
	},

	setEventName: function(i, name){
		if(!this._epn.isEventExists(name)){
			//TODO: generate error
			console.log("Error pasing consumer/producer " + this._name + ". Event " + name + "doesn't exists");
		}
		if(this._events.length>i){
			this._events[i]._name=name;
		}else if(this._events.length===i){
			this._events[i]=new Object();
			this._events[i]._name=name;			
		}else{
			//TODO: generate error
			console.log("Error parsing consumer/producer " + this._name + " . Internal Error: events out of range: " + i);			
		}
	},
	
	getEventName: function(i){
		if(this._events.length<=i){
			//TODO: generate error
			console.log("Internal error in consumer/producer " + this._name +". Out of bound index "+ i);
		}else{
			return this._events[i]._name;
		}
	},
	
	setEventCondition: function(i, condition){
		if(this._events.length>i){
			this._events[i]._condition=condition;
		}else if(this._events.length===i){
			this._events[i]=new Object();
			this._events[i]._condition=condition;			
		}else{
			//TODO: generate error
			console.log("Error parsing consumer/producer " + this._name + " . Internal Error: events out of range: " + i);			
		}
	},
	
	getEventCondition: function(i){
		if(this._events.length<=i){
			//TODO: generate error
			console.log("Internal error in consumer/producer " + this._name +". Out of bound index "+ i);
		}else{
			return this._events[i]._condition;
		}
	}


});
