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
 dojo.provide("widgets.validate");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.declare("widgets.validate",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","validate.html"),
    
	constructor : function() {
		this.tblValidateColumns=[];
		//this.myObject=new metadata.Validator();
    	this.myObject=ATVars.MY_ERRORS;
    	this.gridhelper= new widgets.GridHelper();
	},


	postMixInProperties : function() {
	},

	postCreate : function() {
		this.tblValidateColumns.push(this.gridhelper.setColumn("Icon", "Icon", false, true, null, "setMessageIcon", "getErrorType", this.myObject,null, null ));
		this.tblValidateColumns.push(this.gridhelper.setColumn("Description", "LongLink", false, true, null,"setMessageDescription", "getErrorEnum", this.myObject, null, "openDefinition"));
		this.tblValidateColumns.push(this.gridhelper.setColumn("Definition", "Link", false, true, null,"setMessageResource", "getDefinitionInstance", this.myObject, null, "openDefinition"));
		this.tblValidateColumns.push(this.gridhelper.setColumn("Element", "String", false, true, null,"setMessageDescription", "getElementEnum", this.myObject, null, null));
		
//				this.tblValidateName = "Errors and warnings";
	
		this._argsVlidateTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblValidateColumns, tblName: this.tblValidateName, hasDel: false, metaObject: this.myObject, metaAttrSize: "getNumOfError", 
													   delAttrMethod: "", addButton: false, refreshButton: false, noShowRequiredButton:true  });
		this.validatetbl.appendChild(this._argsVlidateTable.domNode);
	},
	
	startup: function(){
        widgets.validate.superclass.startup.apply(this, arguments);
        this._argsVlidateTable.startup();
	},
	
	resize: function(changeSize, resultSize) {
		this._argsVlidateTable.resize(changeSize, resultSize);
	},
	
	setColumns: function(columns) {
		this._argsVlidateTable.setColumns(columns);
	},
	
	getStructure: function() {
		return this._argsVlidateTable.getStructure();
	}
});
