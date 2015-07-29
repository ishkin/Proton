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
 dojo.provide("widgets.GeneralAtt");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("widgets.SectionPane");
dojo.require("widgets.ProtonMain");
dojo.declare("widgets.GeneralAtt", [ dijit._Widget, dijit._Templated ], {
	widgetsInTemplate : true,
	templatePath : dojo.moduleUrl(
			"widgets",
			"general_att.html"),

	constructor : function() {
		this.myObject = ATVars.CURRENT_OBJECT;
		this.fieldName = "General";
	},
	saveObjectName : function() {
		var that = this;
		var undo = function() {
			that.objectName.set("value", that.myObject.getName());			
		};
		
		if (!(this.myObject.getName() == this.objectName.value)) {
			if (ATVars.MY_EPN.isExists(this.objectName.value)) {
				alert("There is an object with this name");
				undo();
			} else {
				var protonMain = ATVars.PROTON_MAIN;
				var result = ATVars.MY_EPN.renameEPNObject(this.myObject.getName(), this.objectName.value,
					dojo.hitch(protonMain, protonMain.verifyToJSON));
				if (result) {
					this.myObject.setName(this.objectName.value);
					ATVars.CURRENT_TAB.set("title", this.objectName.value);
					ATVars.CURRENT_TREE_NODE.set("label", this.objectName.value);
				} else {
					undo();
				}
			}
		}
	},
	
	saveCreatedBy : function() {
		this.myObject.setCreatedBy(this.createdBy.value);
	}, 
	
	saveObjectDesc : function() {
		this.myObject.setDescription(this.objectDesc.value);
	},
	startup : function() {
		widgets.GeneralAtt.superclass.startup.apply(this, arguments);
		this.objectName.set("value", this.myObject.getName());
		this.objectDesc.set("value", this.myObject.getDescription());
		this.createdBy.set("value", this.myObject.getCreatedBy());
		this.createdOn.set("value", this.myObject.getCreatedDate());
		this.gAtt.expandCollapse();
	}
});
