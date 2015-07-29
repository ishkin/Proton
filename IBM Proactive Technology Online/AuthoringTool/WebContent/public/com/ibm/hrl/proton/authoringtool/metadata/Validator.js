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
 dojo.provide("metadata.Validator");
dojo.require("metadata.BaseDefinition");
dojo.require("metadata.Event");
dojo.require("metadata.ParseError");

dojo.require("metadata.RelativeTimeTerminator");
dojo.declare("metadata.Validator",null,{
	
	

						// Constructor function. Called when instance of this class is created
						constructor : function() {
							
							this._validationMessages=[];
							this.buildMessages();
						},
						buildMessages : function(){
							var curMessage;
							curMessage={icon: "error", description: "Context is Missing", resource: "epa1"};
							this._validationMessages.push(curMessage);
							curMessage={icon: "error", description: "No Derrived Event", resource: "e1"};
							this._validationMessages.push(curMessage);
							curMessage={icon: "warning", description: "participant event is not part of segmentation context", resource: "segConx1"};
							this._validationMessages.push(curMessage);
							curMessage={icon: "warning", description: "EPA type is missing", resource: "epa2"};
							this._validationMessages.push(curMessage);
						},
					
						getMessageIcon: function(i){
							if (i<this._validationMessages.length){
								return this._validationMessages[i].icon;
							}			
						},
						getMessageDescription: function(i){
							if (i<this._validationMessages.length){
								return this._validationMessages[i].description;
							}			
						},
						getMessageResource: function(i){
							if (i<this._validationMessages.length){
								return this._validationMessages[i].resource;
							}			
						},
						setMessageIcon: function(i, icon){
							//set here icon for specific message				
						},
						setMessageDescription: function(i, description){
							//set here description for specific message				
						},
						setMessageResource: function(i, type){
							//set here resource for specific message					
						},
						getNumberOfValidationMessages: function(){
							return this._validationMessages.length;		
						},

	
				
		});
