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
 dojo.provide("metadata.ParseErrors");
dojo.require("metadata.ParseError");
dojo.declare("metadata.ParseErrors",null,{
	
	constructor: function(args) {
		this._parseError=new Array();
		
		for ( var i = 0; i < args.length; i++) {
			//alert(args[i]);
			this._parseError[i] = new metadata.ParseError(args[i]);
		}
	},

	getDefinitionInstance: function(i){
		return this._parseError[i].getDefinitionInstance();
	},
	
	getDefinitionType: function(i){
		return this._parseError[i].getDefinitionType();
	},
	
	getErrorType: function(i){
		return this._parseError[i].getErrorType();
	},

	getElementEnum: function(i){
		return ATErrorMessage.ErrorElement[this._parseError[i].getElementEnum()];
	},
	
	getErrorEnum: function(i){
		return this._parseError[i].toString();
	},

	getRwNumber: function(i){
		return this._parseError[i].getRwNumber();
	},
	
	getTableNumber: function(i){
		return this._parseError[i].getTableNumber();
	},
	getNumOfError:function(){
		return this._parseError.length;
	},
	openDefinition: function(definitionID){
		var defIns=this.getDefinitionInstance(definitionID);
		var defType=this.getDefinitionType(definitionID);
		dojo.publish("OpenObjectFromErrTbl",[{name: defIns, type: defType}]);
	}
});
