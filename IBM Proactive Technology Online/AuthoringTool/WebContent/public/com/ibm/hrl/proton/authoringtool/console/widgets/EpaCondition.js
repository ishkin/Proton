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
 dojo.provide("widgets.EpaCondition");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("dijit.form.ComboBox");
dojo.require('dijit.form.TextBox');
dojo.require('dijit.form.Button');
dojo.require("widgets.SectionPane");
dojo.declare("widgets.EpaCondition",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","epa_condition.html"),

    constructor: function() {
    	this.myObject=ATVars.CURRENT_OBJECT;
    	//this.objectType="objTypet";
    	this.fieldName="Condition";
//    	dojo.subscribe("TypeWasChanged",this,function() {
//    		this.manageView();});
    },
    manageView:function(){
    },
   	saveAssertion:function(){//condition
		this.myObject.setAssertion(this.txtAssertion.value);
    },
   	saveEvaluation:function(){
		this.myObject.setEvaluation(this.cmbEvaluation.value);
    },
   	saveCardinality:function(){
		this.myObject.setCardinality(this.cmbCardinality.value);
    },
   	saveN:function(){
		this.myObject.setN(this.txtN.value);
    },
    saveTrendN:function(){
		this.myObject.setTrendN(this.txtTrendN.value);
    },
    
   	saveRanking:function(){
		this.myObject.setRanking(this.cmbRanking.value);
    },
    
    saveTrendRelation:function(){
		this.myObject.setTrendRelation(this.cmbTrend.value);
    },

	 sendValue:function(){
	 	alert(this.comboValue.attr.value);
	 },
	 
 	 startup: function(){
        widgets.EpaCondition.superclass.startup.apply(this, arguments);
        
	 	var optionsEveluation = new dojo.data.ItemFileWriteStore({data: {identifier: 'name', items:[]}});
	 	for ( var p in ATEnum.EvaluationPolicy) {
	 		optionsEveluation.newItem({name: ATEnum.EvaluationPolicy[p]});
		}
     	this.cmbEvaluation.attr('store', optionsEveluation);

	 	var optionsCardinality = new dojo.data.ItemFileWriteStore({data: {identifier: 'name', items:[]}});
	 	for ( var p in ATEnum.CardinalityPolicy) {
	 		optionsCardinality.newItem({name: ATEnum.CardinalityPolicy[p]});
		}
     	this.cmbCardinality.attr('store', optionsCardinality);
     
	 	var optionsRankingRelation= new dojo.data.ItemFileWriteStore({data: {identifier: 'name', items:[]}});
	 	for ( var p in ATEnum.RankingRelation) {
	 		optionsRankingRelation.newItem({name: ATEnum.RankingRelation[p]});
		}
     	this.cmbRanking.attr('store', optionsRankingRelation);
     	
     	var optionsTrendRelation= new dojo.data.ItemFileWriteStore({data: {identifier: 'name', items:[]}});
	 	for ( var p in ATEnum.TrendRelation) {
	 		optionsTrendRelation.newItem({name: ATEnum.TrendRelation[p]});
		}
     	this.cmbTrend.attr('store', optionsTrendRelation);

 		 this.cmbEvaluation.set("value",this.myObject.getEvaluation());
 		 this.cmbEvaluation.set("displayedValue",this.cmbEvaluation.value);
 		 this.cmbCardinality.set("value",this.myObject.getCardinality());
 		 this.cmbCardinality.set("displayedValue",this.cmbCardinality.value);
 		 this.cmbRanking.set("value",this.myObject.getRanking());
 		 this.cmbRanking.set("displayedValue",this.cmbRanking.value);
 		 this.cmbTrend.set("value",this.myObject.getTrendRelation());
 		 this.cmbTrend.set("displayedValue",this.cmbTrend.value);
 		 this.txtAssertion.set("value",this.myObject.getAssertion());
 		 this.txtN.set("value",this.myObject.getN());
 		 this.txtTrendN.set("value",this.myObject.getTrendN());
 		 
 //		 this.manageView();
 		 this.epaCond.expandCollapse();
   }
   });
