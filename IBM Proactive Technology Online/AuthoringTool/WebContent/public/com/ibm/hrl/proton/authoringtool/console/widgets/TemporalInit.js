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
 dojo.provide("widgets.TemporalInit");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("widgets.SectionPane");
dojo.declare("widgets.TemporalInit",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","temporal_init.html"),
    
    _argsEventInitTable:null,
    _argsAbsoluteTimeInitTable:null,
	tblEventInitName:null,
	tblAbsoluteTimeInitName:null,
	tblEventInitColumns:[],
	tblAbsoluteTimeInitColumns:[],

    constructor: function() {
    	this.tblEventInitColumns=[];
    	this.tblAbsoluteTimeInitColumns=[];
    	this.myObject=ATVars.CURRENT_OBJECT;
    	this.myEPN=ATVars.MY_EPN;
    	this.fieldName="Initiators";
    	this.gridhelper= new widgets.GridHelper();
     },
     updateAtStartup:function(){
	 	if (this.chkAtStartup.get("value")=="on"){
	 		this.myObject.setAtStartup(true);
	 	}
	 	else {this.myObject.setAtStartup(false);
	 	}

     },

	// postCreate() is called after buildRendering().  This is useful to override when 
	// you need to access and/or manipulate DOM nodes included with your widget.
	// DOM nodes and widgets with the dojoAttachPoint attribute specified can now be directly
	// accessed as fields on "this". 
	// Common operations for postCreate
	// 1) Access and manipulate DOM nodes created in buildRendering()
	// 2) Add new DOM nodes or widgets 
	postCreate : function() {
		
		var CorrelationPolicyOptions= [];
	 	for ( var p in ATEnum.InitiatorPolicy) {
	 		CorrelationPolicyOptions.push(ATEnum.InitiatorPolicy[p]);
		}
		// set Event Initiators table
		var eventOptions=this.myEPN.getEventList();
		
//	 	for ( var i= 0; i<eventcount; i++) {
//	 		eventOptions.push(this.myObject.InitiatorPolicy[p]);
//		}
	 	
	 	
		this.tblEventInitColumns.push(this.gridhelper.setColumn("Event", "Selection", true, true, eventOptions, "setEventInitiatorName", "getEventInitiatorName", this.myObject, null ));
		this.tblEventInitColumns.push(this.gridhelper.setColumn("Condition", "String", true, false, null,"setEventInitiatorCondition", "getEventInitiatorCondition", this.myObject, null));
		this.tblEventInitColumns.push(this.gridhelper.setColumn("Correlation Policy", "Selection", true, true, CorrelationPolicyOptions,"setEventInitiatorPolicy", "getEventInitiatorPolicy", this.myObject, null));
		
		this.tblEventInitName="Event Initiators";
	
		this._argsEventInitTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblEventInitColumns, tblName: this.tblEventInitName, hasDel: true, metaObject: this.myObject, metaAttrSize: "getNumberOfEventInitiators", delAttrMethod: "deleteEventInitiator"  });
		this.EventInitiatorsGrid.appendChild(this._argsEventInitTable.domNode);
		// set Absolute Time Initiators Table

		this.tblAbsoluteTimeInitColumns.push(this.gridhelper.setColumn("Time", "String", true, true, null, "setAbsoluteTimeInitiatorTimestamp", "getAbsoluteTimeInitiatorTimestamp", this.myObject, null ));
		this.tblAbsoluteTimeInitColumns.push(this.gridhelper.setColumn("Correlation Policy", "Selection", true, true, CorrelationPolicyOptions,"setAbsoluteTimeInitiatorPolicy", "getAbsoluteTimeInitiatorPolicy", this.myObject, null));
		this.tblAbsoluteTimeInitColumns.push(this.gridhelper.setColumn("Repeating Interval", "String", true, true, null,"setAbsoluteTimeInitiatorRepeatingInterval", "getAbsoluteTimeInitiatorRepeatingInterval", this.myObject, null));
		
		this.tblAbsoluteTimeInitName="Absolute Time Initiators";
	
		this._argsAbsoluteTimeInitTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblAbsoluteTimeInitColumns, tblName: this.tblAbsoluteTimeInitName, hasDel: true, metaObject: this.myObject, metaAttrSize: "getNumberOfAbsoluteTimeInitiators", delAttrMethod: "deleteAbsoluteTimeInitiator", noShowRequiredButton:true  });
		this.EventInitiatorsGrid.appendChild(this._argsAbsoluteTimeInitTable.domNode);
	},
  	startup: function(){
        widgets.TemporalInit.superclass.startup.apply(this, arguments);
     	this.chkAtStartup.set("value",this.myObject.getAtStartup());
		this.tempoInit.expandCollapse();
		
		this._argsEventInitTable.startup();
		this._argsAbsoluteTimeInitTable.startup();
     }
});
