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
 dojo.provide("widgets.TemporalTerminator");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("widgets.SectionPane");
dojo.declare("widgets.TemporalTerminator",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","temporal_terminator.html"),

    constructor: function() {
    	this.tblEventTermColumns=[];
    	this.tblAbsoluteTimeTermColumns=[];
    	this.myObject=ATVars.CURRENT_OBJECT;
    	this.fieldName="Terminators";
    	this.myEPN=ATVars.MY_EPN;
    	this.gridhelper= new widgets.GridHelper();
     },
     
	updateNeverEnds:function(){
	 	if (this.chkNeverEnds.get("value")=="on"){
	 		this.myObject.setNeverEnding(true);
	 		this.withEnd.style.display="none";
	 	}
	 	else {this.myObject.setNeverEnding(false);
	 		this.withEnd.style.display="block";
	 	}
	 },
     
	saveTimeTerminator:function(){
		//First check if there is a value or a change of value
		var newRelativeValue = this.txtTimeTerminator.get("value");
		if(newRelativeValue!==""){
			this.myObject.setRelativeTerminatorRelativeTime(newRelativeValue);
		}else{
			//delete the relative time terminator and empty the its type
			this.myObject.deleteRelativeTerminator();
			this.cmbType.reset();
		}
		
		//
	},
	
	saveType:function(){
		this.myObject.setRelativeTerminatorType(this.cmbType.get("value"));
	},
	
	
	// postCreate() is called after buildRendering().  This is useful to override when 
	// you need to access and/or manipulate DOM nodes included with your widget.
	// DOM nodes and widgets with the dojoAttachPoint attribute specified can now be directly
	// accessed as fields on "this". 
	// Common operations for postCreate
	// 1) Access and manipulate DOM nodes created in buildRendering()
	// 2) Add new DOM nodes or widgets 
	postCreate : function() {
		
		//Set Quantifier option list
		var QuantifierOptions= [];
	 	for ( var p in ATEnum.TerminatorPolicy) {
	 		QuantifierOptions.push(ATEnum.TerminatorPolicy[p]);
		}
	 	//Set Type option list
		var typeOptions= [];
	 	for ( var p in ATEnum.TerminationType) {
	 		typeOptions.push(ATEnum.TerminationType[p]);
		}
		// set Event Terminators table
		var eventOptions=this.myEPN.getEventList();
		
//	 	for ( var i= 0; i<eventcount; i++) {
//	 		eventOptions.push(this.myObject.InitiatorPolicy[p]);
//		}
	 	
	 	
		this.tblEventTermColumns.push(this.gridhelper.setColumn("Event", "Selection", true, true, eventOptions, "setEventTerminatorName", "getEventTerminatorName", this.myObject, null ));
		this.tblEventTermColumns.push(this.gridhelper.setColumn("Condition", "String", true, false, null,"setEventTerminatorCondition", "getEventTerminatorCondition", this.myObject, null));
		this.tblEventTermColumns.push(this.gridhelper.setColumn("Quantifier", "Selection", true, true, QuantifierOptions,"setEventTerminatorQuantifierPolicy", "getEventTerminatorQuantifierPolicy", this.myObject, null));
		this.tblEventTermColumns.push(this.gridhelper.setColumn("Termination Type", "Selection", true, true, typeOptions,"setEventTerminatorType", "getEventTerminatorType", this.myObject, null));
		
		this.tblEventTermName="Event Terminators";
	
		this._argsEventTermTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblEventTermColumns, tblName: this.tblEventTermName, hasDel: true, metaObject: this.myObject, metaAttrSize: "getNumberOfEventTerminators", delAttrMethod: "deleteEventTerminator"  });
		this.EventTerminatorGrid.appendChild(this._argsEventTermTable.domNode);
		// set Absolute Time Terminators Table

		this.tblAbsoluteTimeTermColumns.push(this.gridhelper.setColumn("Time", "String", true, true, null, "setAbsoluteTerminatorTimestamp", "getAbsoluteTerminatorTimestamp", this.myObject, null ));
		this.tblAbsoluteTimeTermColumns.push(this.gridhelper.setColumn("Quantifier", "Selection", true, true, QuantifierOptions,"setAbsoluteTerminatorQuantifierPolicy", "getAbsoluteTerminatorQuantifierPolicy", this.myObject, null));
		this.tblAbsoluteTimeTermColumns.push(this.gridhelper.setColumn("Termination Type", "Selection", true, true, typeOptions,"setAbsoluteTerminatorType", "getAbsoluteTerminatorType", this.myObject, null));
		
		this.tblAbsoluteTimeTermName="Absolute Time Terminators";
	
		this._argsAbsoluteTimeTermTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblAbsoluteTimeTermColumns, tblName: this.tblAbsoluteTimeTermName, hasDel: true, metaObject: this.myObject, metaAttrSize: "getNumberOfAbsoluteTerminators", delAttrMethod: "deleteAbsoluteTerminator", noShowRequiredButton:true  });
		this.AbsoluteTimeTerminatorGrid.appendChild(this._argsAbsoluteTimeTermTable.domNode);
	},
  	startup: function(){
        widgets.TemporalTerminator.superclass.startup.apply(this, arguments);
         
	 	var optionsType = new dojo.data.ItemFileWriteStore({data: {identifier: 'name', items:[]}});
	 	for ( var p in ATEnum.TerminationType) {
	 		optionsType.newItem({name: ATEnum.TerminationType[p]});
		}
     	this.cmbType.attr('store', optionsType);
     	var i =this.myObject.getNeverEnding();
     	this.chkNeverEnds.set("value",i);
     	if (i==true){
     		this.withEnd.style.display="none";
     	}
     	this.txtTimeTerminator.set("value",this.myObject.getRelativeTerminatorRelativeTime());
        this.cmbType.set("value",this.myObject.getRelativeTerminatorType());
        
		this.tempoTermin.expandCollapse();
		this._argsEventTermTable.startup();
		this._argsAbsoluteTimeTermTable.startup();
     }
});
