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
 dojo.provide("metadata.ParseError");
dojo.declare("metadata.ParseError",null,{
	
	constructor: function(args
			//errorEnum, definitionInstance, definitionType, errorType, elementEnum, rowNumber, tableNumber, msg
			) {
		
		var objArgs=dojo.eval('(' + args + ')');
//		this._definitionType = definitionType||null; 	//Event/EPA/temporal context,... from ATEnum.Definitions
		this._errorType = objArgs.errorType || ATErrorMessage.errorType.Error; //Error or Warning
		this._definitionInstance = objArgs.definitionInstance;	//the name of the definition instance
		this._errorEnum = objArgs.errorEnum||null; 				//Enum that holds the error String
		this._elementEnum = objArgs.elementEnum||null;			//taken from ATEnum.Elements
		this._definitionType = objArgs.definitionType||null; 	//Event/EPA/temporal context,... from ATEnum.Definitions
		this._rowNumber = objArgs.rowNumber;				//in case of element within a list (table elements) 
		this._tableNumber = objArgs.tableNumber;			//in case of several tables of the same element
		this._msg = objArgs.msg||null;							//has value only in some cases when the message is built by externally (eep)

	},
	
	toString: function(){
		var elementStr="";
		var msg="";
		var row="";
		var table="";
		var str;
		if (this._tableNumber!=null){
			this._tableNumber++;
			table=", table number " + this._tableNumber;
		}
		if (this._rowNumber!=null){
			this._rowNumber++;
			row=", in row number " + this._rowNumber;
		}
		if(this._elementEnum!=null){
			elementStr=" of " + ATErrorMessage.ErrorElement[this._elementEnum];
		}
		if(this._errorEnum!=null){
			msg=ATErrorMessage.ErrorMsg[this._errorEnum];
		}else if(this._msg){
			msg=this._msg;
		}
		
		//if(this._rowNumber){row="["+ this._rowNumber + "]";}
		//if(this._tableNumber){table=" of table number "+this._tableNumber ;}
		
		str = ATErrorMessage.errorType[this._errorType] + " in " + this._definitionInstance + " " + this._definitionType + ": " + msg + elementStr + row + table;
		return str;
	},
	
	getDefinitionInstance: function(){
		return this._definitionInstance;
	},
	
	getDefinitionType: function(){
		return this._definitionType;
	},
	
	getErrorType: function(){
		return this._errorType;
	},

	getElementEnum: function(){
		return this._elementEnum;
	},

	getErrorEnum: function(){
		return this._errorEnum;
	},

	getRwNumber: function(){
		return this._rowNumber;
	},
	
	getTableNumber: function(){
		return this._tableNumber;
	}
	
});
