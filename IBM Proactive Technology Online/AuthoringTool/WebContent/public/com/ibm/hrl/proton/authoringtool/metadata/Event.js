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
 dojo.provide("metadata.Event");
dojo.require("metadata.BaseDefinition");
dojo.require("metadata.EventAttribute");
dojo.require("metadata.EventActionBase");
dojo.require("metadata.ParseError");
dojo.declare("metadata.Event",metadata.EventActionBase,{
	
	setParameterTypes: function(){
		this.parameterTypes=["attributes"];
	},

	
	addBuiltInAttributes: function(){
		return this.addAttributesBase("attributes", ATEnum.EventBuiltInAttributes);
	},
	
	getAttributesArray: function(){
		return this.getAttributesArrayBase("attributes");
	},
	
	getNumberOfAttributes: function(){
		return this.getNumberOfAttributesBase("attributes");
	},
	
	deleteAttribute: function(i){
		return this.deleteAttributeBase("attributes",i);
	},
	
	getAttributeName: function(i){
		return this.getAttributeNameBase("attributes",i);
	},

	setAttributeName: function(i,name){
		return this.setAttributeNameBase("attributes",i,name);
	},
	
	
	getAttributeType: function(i){
		return this.getAttributeTypeBase("attributes",i);
	},

	setAttributeType: function(i,type){
		return this.setAttributeTypeBase("attributes",i,type);
	},

	getAttributeDimension: function(i){
		return this.getAttributeDimensionBase("attributes",i);
	},

	setAttributeDimension: function(i,dimension){
		return this.setAttributeDimensionBase("attributes",i,dimension);

	},
	
	getAttributeDefaultValue: function(i){
		return this.getAttributeDefaultValueBase("attributes",i);
	},

	setAttributeDefaultValue: function(i,defaultValue){
		return this.setAttributeDefaultValueBase("attributes",i,defaultValue);

	},
	
	getAttributeDescription: function(i){
		return this.getAttributeDescriptionBase("attributes",i);
	},

	setAttributeDescription: function(i,description){
		return this.setAttributeDescriptionBase("attributes",i,description);

	}
});
