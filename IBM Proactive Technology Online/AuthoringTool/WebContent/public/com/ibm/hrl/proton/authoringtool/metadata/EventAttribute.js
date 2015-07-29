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
 dojo.provide("metadata.EventAttribute");
dojo.require("metadata.ParseError");
dojo.declare("metadata.EventAttribute",null,{
	constructor: function(atributeObject, name, type, defaultValue, dimension) {

		this._name=null;
		this._type=ATEnum.AttributeTypes.String;
		this._defaultValue=null;
		this._dimension=0;

		//get/set is done thought the event object
	}
});
