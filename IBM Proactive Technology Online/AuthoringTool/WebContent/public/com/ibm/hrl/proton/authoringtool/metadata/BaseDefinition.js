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
 dojo.provide("metadata.BaseDefinition");
dojo.require("metadata.ParseError");
dojo.declare("metadata.BaseDefinition",null,{
	
	constructor: function(args,errors, definitionType, epn) {
		//class "private" members - should be accessed by the view using get/set methods only
		this._name="";
		this._description="";
		this._createdDate= null;
		this._createdBy="";
		this._dependentDefinitions={};
		this._definitionType = definitionType;

		var nameError;
		try{
			if(dojo.isObject(args)){//Complete definition from the json				
				if(args && args.name){
					nameError = epn.validateDefinitionName(args.name);
					if(nameError!=null){
						if(!errors[args.name]){errors[args.name]=new Array();}
						errors[args.name].push(new metadata.ParseError(nameError, args.name));					
						
					}
					this._name = args.name;
					this._description = args.description||this._description;
					this._createdDate = args.createdDate||this._createdDate;
					this._createdBy = args.createdBy||this._createdBy;
				}else{
					if(!errors[definitionType]){errors[definitionType]=new Array();}
					errors[definitionType].push(new metadata.ParseError(ATErrorMessage.ErrorMsg.MissingDefinitionName));					
				}
			}else{ //New Definition from the user
				if(args){
					nameError = epn.validateDefinitionName(args);
					if(nameError!=null){
						if(!errors[args]){errors[args]=new Array();}
						errors[args].push(new metadata.ParseError(nameError, args));					
					}					
					this._name=args;
					this.setCreatedDate( new Date().toDateString());
					
				}
			}
		}
		catch(err){
			console.log(err.msg);
			if(!errors[this._name]){errors[this._name]=new Array();}
			errors[this._name].push(new metadata.ParseError(ATErrorMessage.ErrorMsg.ErrorParsingGeneralInfo, this._name));
		}
	},
	
	getName: function(){
		return this._name;
	},
	
	setName: function(name){
		//DOTO verify it is a legal and unique name 
		this._name = name;
		return this.getDependentDefinition();
	},

	getDescription: function(){
		return this._description;
	},
	
	setDescription: function(description){
		this._description=description;
		return null; //no affected definitions
	},
		
	getCreatedDate: function(){
		return this._createdDate;
	},
	
	setCreatedDate: function(createdDate){
		this._createdDate=createdDate;
		return null; //no affected definitions
	},
	
	getCreatedBy: function(){
		return this._createdBy;
	},
	
	setCreatedBy: function(createdBy){
		this._createdBy=createdBy;
		return null; //no affected definitions
	},

	getDependentDefinition: function(){
		return this._dependentDefinition;
	},
	
	addDependentDefinition: function(name){
		this._dependentDefinitions[name]=name;
	},
	
	removeDependentDefinition: function(name){
		delete this._dependentDefinitions[name];		
	}
	
});
