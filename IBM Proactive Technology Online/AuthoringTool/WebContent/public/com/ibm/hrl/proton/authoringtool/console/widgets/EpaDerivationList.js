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
 dojo.provide("widgets.EpaDerivationList");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("dijit.form.ComboBox");
dojo.require('dijit.form.TextBox');
dojo.require('dijit.form.Button');
dojo.require('dojo.data.ItemFileWriteStore');
dojo.require("widgets.EpaDerivationObj");
dojo.require("widgets.EpaDerivationAttributes");
dojo.require("widgets.SectionPane");
dojo.declare("widgets.EpaDerivationList", [ dijit._Widget, dijit._Templated], {
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","epa_derivationList.html"),

	constructor: function() {
		this.myObject = ATVars.CURRENT_OBJECT;
		this.fieldName = "Derivations List";
		this.numOfObj = 0;
		this.AtributesWidgetArray = [];
		this.derivedEvents = [];
		// var that = this;
		dojo.subscribe("DerivationWasAdded", this, function() {
			this.afterAddDerivation();
		});
		dojo.subscribe("DerivationWasDeleted", this, function(numInList) {
			this.afterDeleteDerivation(numInList);
		});
	},

	saveEvent: function() {
	},
	sendValue: function() {
	},
	newEvent: function() {
	},

	sendValue: function() {
		alert(this.cmbEvent.attr.value);
		this.epaDeriv.expandCollapse();

	},

	afterAddDerivation: function() {
		var i = this.myObject.getNumberOfDerivedEvents();
		var node = dojo.create("div", {}, this.epaDerivObjects, "last");
		var derivedEventObj = new widgets.EpaDerivationObj({
			numInList: i - 1,
			myObject: this.myObject
		}, node);
		var attrnode = dojo.create("div", {}, this.epaDerivObjects, "last");
		var derivedEventAtributes = new widgets.EpaDerivationAttributes({
			numInList: i - 1,
			myObject: this.myObject
		}, attrnode);
		this.AtributesWidgetArray.push(derivedEventAtributes);
		this.derivedEvents.push(derivedEventObj);

		derivedEventObj.startup();
		derivedEventAtributes.startup();
	},

	afterDeleteDerivation: function(numInList) {
		this.AtributesWidgetArray[numInList].destroy();
		this.derivedEvents[numInList].destroy();
		this.AtributesWidgetArray.splice(numInList, 1);
		this.derivedEvents.splice(numInList, 1);
	},

	startup: function() {
		var i, l;
		widgets.EpaDerivationList.superclass.startup.apply(this, arguments);
		this.epaDerivList.expandCollapse();
		var numOfDerivedEvents = this.myObject.getNumberOfDerivedEvents();
		for (i = 0; i < numOfDerivedEvents; i++) {
			this.numOfObj = i;
			var node = dojo.create("div", {}, this.epaDerivObjects, "last");
			var derivedEventObj = new widgets.EpaDerivationObj({
				numInList: i,
				myObject: this.myObject
			}, node);
			var attrnode = dojo.create("div", {}, this.epaDerivObjects, "last");
			var derivedEventAtributes = new widgets.EpaDerivationAttributes({
				numInList: i,
				myObject: this.myObject
			}, attrnode);
			this.AtributesWidgetArray.push(derivedEventAtributes);
			this.derivedEvents.push(derivedEventObj);
			derivedEventObj.startup();
			derivedEventAtributes.startup();
		}
	}
});
