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
 dojo.provide("widgets.EPATab");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require('dijit.layout.AccordionContainer');
dojo.require('dijit.layout.AccordionPane');
dojo.require("widgets.GeneralAtt");
dojo.require("widgets.Epa_eventSelection");
dojo.require("widgets.EPAInternalSegmentation");
dojo.require("widgets.EpaType");
dojo.require("widgets.EpaCondition");
dojo.require("widgets.EpaDerivationAdd");
dojo.require("widgets.EpaDerivationList");
dojo.require("widgets.SectionPane");
dojo.declare("widgets.EPATab",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","epa_tab.html"),
    
    constructor: function() {
       	this.myObject=ATVars.CURRENT_OBJECT;
    	this.derivationAdd=null;
    	this.derivationList=null;
    	this.subPaneDiv=null;
    },
    
    paneGeneralTbl:function(){
    	//using for manage view, no tables in this pane
 		this.typePane.globalContext.style.display="block";
     },
    
    paneSelectionTbl:function(){
    	try{
 			this.eventSelection.destroyRecursive();
			//dojo.destroy(this.divSelect);
			this.divSelectPane.destroyDescendants();
    	}catch(i){};
        this.divSelect = dojo.create("div", {}, this.divSelectPane);
    	this.eventSelection=new widgets.Epa_eventSelection({myObj:this.myObject},this.divSelect);
    	this.eventSelection.startup();
    	this.eventSelection._participantEventsTable._grid.update();
		if(this.myObject.getEPAType()== ATEnum.EPAType.Aggregate){
			this.eventSelection._computedVariablesTable._grid.update();
    	}
//    	this.selectPane.partEvt.expandCollapse();
//    	//this.selectPane.startup();
//    	this.selectPane._participantEventsTable._grid.update();
//    	if(this.myObject.getEPAType()== ATEnum.EPAType.Aggregate){
//			this.selectPane._computedVariablesTable._grid.update();
//    	}
    },
    

	paneConditionTbl: function() {
		// manage pane view and display the table
		var epaType = this.myObject.getEPAType();
		if (	epaType == ATEnum.EPAType.Filter ||
				epaType == ATEnum.EPAType.Absence ||
				epaType == ATEnum.EPAType.Basic ) {
			this.internalSegmentation.style.display = "none";
		} else {
			this.internalSegmentation.style.display = "block";
			this.intSegmantation._internalSegmentationTable._grid.update();
		}
		this.conditionDiv.divCondition.style.display = "block";
		this.conditionDiv.divStatefull.style.display = "block";
		this.conditionDiv.divRelativeN.style.display = "block";
		this.conditionDiv.divTrend.style.display = "block";
		this.conditionDiv.divNothing.style.display = "none";
		if (	epaType == ATEnum.EPAType.Filter ||
				epaType == ATEnum.EPAType.Absence ||
				epaType == ATEnum.EPAType.Basic	) {
			this.conditionDiv.divCondition.style.display = "none";
			this.conditionDiv.divStatefull.style.display = "none";
			this.conditionDiv.divRelativeN.style.display = "none";
			this.conditionDiv.divTrend.style.display = "none";
			this.conditionDiv.divNothing.style.display = "block";
		} else if (	epaType == ATEnum.EPAType.All ||
					epaType == ATEnum.EPAType.Sequence ||
					epaType == ATEnum.EPAType.Aggregate) {
			this.conditionDiv.divCondition.style.display = "block";
			this.conditionDiv.divStatefull.style.display = "block";
			this.conditionDiv.divRelativeN.style.display = "none";
			this.conditionDiv.divTrend.style.display = "none";
			this.conditionDiv.divNothing.style.display = "none";
		} else if (epaType == ATEnum.EPAType.RelativeN) {
			this.conditionDiv.divCondition.style.display = "none";
			this.conditionDiv.divStatefull.style.display = "block";
			this.conditionDiv.divRelativeN.style.display = "block";
			this.conditionDiv.divTrend.style.display = "none";
			this.conditionDiv.divNothing.style.display = "none";
		}else if (epaType == ATEnum.EPAType.Trend) {
			this.conditionDiv.divCondition.style.display = "none";
			this.conditionDiv.divStatefull.style.display = "block";
			this.conditionDiv.divTrend.style.display = "block";
			this.conditionDiv.divRelativeN.style.display = "none";
			this.conditionDiv.divNothing.style.display = "none";
		}
		
		//evaluation property might have changed when the type was changed 
		this.conditionDiv.cmbEvaluation.set("value",this.myObject.getEvaluation());
 		this.conditionDiv.cmbEvaluation.set("displayedValue",this.conditionDiv.cmbEvaluation.value);

	}, 
    
    sendRequiredValue: function(){
    	
    },
    
    startup: function(){
        widgets.EPATab.superclass.startup.apply(this, arguments);
        this.divWidadd = dojo.create("div", {}, this.divWidgets);
        this.divWidList = dojo.create("div", {}, this.divWidgets);
        this.derivationAdd=new widgets.EpaDerivationAdd({},this.divWidadd);
        this.derivationList= new widgets.EpaDerivationList({},this.divWidList);
        this.derivationAdd.startup();
        this.derivationList.startup();
 
}});
