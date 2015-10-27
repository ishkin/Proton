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
 dojo.provide("widgets.Epa_eventSelection");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("widgets.SectionPane");
dojo.declare("widgets.Epa_eventSelection",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
    templatePath: dojo.moduleUrl("widgets","Epa_eventSelection.html"),
    
				_participantEventsTable:null,
				tblparticipantEventsName:null,
				//tblparticipantEventsColumns:[],
				// Override this method to perform custom behavior during dijit construction.
				// Common operations for constructor:
				// 1) Initialize non-primitive types (i.e. objects and arrays)
				// 2) Add additional properties needed by succeeding lifecycle methods
				constructor : function(obj) {
					this.tblparticipantEventsColumns=[];
					this.tblcomputedVariablesColumns=[];
					this.myObject=obj.myObj;//ATVars.CURRENT_OBJECT
					//this.epaObj=obj.myObj;
			    	this.fieldName="Attributes";
			    	this.myEPN=ATVars.MY_EPN;
			    	this.gridhelper= new widgets.GridHelper();
			    	this.gridhelperComputedVariables= new widgets.GridHelper();
			    	//dojo.subscribe("TypeWasChanged",this,function(obj) {
			    		//this.manageView(obj);});
			    	
					this.instanceSelectionOptions= [];
				 	for ( var p in ATEnum.InstanceSelectionPolicy) {
				 		this.instanceSelectionOptions.push(ATEnum.InstanceSelectionPolicy[p]);
					}
				 	
				 	this.consumptionOptions= [];
				 	for ( var i in ATEnum.ConsumptionPolicy) {
				 		this.consumptionOptions.push(ATEnum.ConsumptionPolicy[i]);
					}

				 	this.aggregationTypes= [];
				 	for ( var i in ATEnum.AggregationType) {
				 		this.aggregationTypes.push(ATEnum.AggregationType[i]);
					}

				},
	
				// This method is called to change participant Events table view according to EPA type.
				// Each time when EPA type is changed we need to display different table.
				buildAll:function(){
								
				 	this.tblparticipantEventsColumns.push(this.gridhelper.setColumn("Instance Selection", "Selection", true, false, this.instanceSelectionOptions,"setInputEventSelectionPolicy", "getInputEventInstanceSelectionPolicy", this.myObject, null));
				 	this.tblparticipantEventsColumns.push(this.gridhelper.setColumn("Consumption", "Selection", true, false, this.consumptionOptions,"setInputEventConsumptionPolicy", "getInputEventConsumptionPolicy", this.myObject, null));
				},
			
				//add special columns for aggregation EPA type
				buildAggregation:function(){
					this.tblparticipantEventsColumns.push(this.gridhelper.setColumn("Consumption", "Selection", true, false, this.consumptionOptions,"setInputEventConsumptionPolicy", "getInputEventConsumptionPolicy", this.myObject, null));
				},
				
				// This method is called to change participant Events table view according to EPA type.
				// Each time when EPA type is changed we need to display different table.
				buildRelative:function(){
					this.tblparticipantEventsColumns.push(this.gridhelper.setColumn("Expression", "String", true, true, null,"setInputEventRelativeNExpression", "getInputEventRelativeNExpression", this.myObject, null));
					this.tblparticipantEventsColumns.push(this.gridhelper.setColumn("Consumption", "Selection", true, false, this.consumptionOptions,"setInputEventConsumptionPolicy", "getInputEventConsumptionPolicy", this.myObject, null));
				},
				
				// Each time when EPA type is changed we need to display different table.
				buildTrend:function(){
					this.tblparticipantEventsColumns.push(this.gridhelper.setColumn("Expression", "String", true, true, null,"setInputEventTrendExpression", "getInputEventTrendExpression", this.myObject, null));
					this.tblparticipantEventsColumns.push(this.gridhelper.setColumn("Consumption", "Selection", true, false, this.consumptionOptions,"setInputEventConsumptionPolicy", "getInputEventConsumptionPolicy", this.myObject, null));
				},
				// When this method is called, all variables inherited from superclasses are 'mixed in'.
				// Common operations for postMixInProperties
				// 1) Modify or assign values for widget property variables defined in the template HTML file
				postMixInProperties : function() {
				},

				// postCreate() is called after buildRendering().  This is useful to override when 
				// you need to access and/or manipulate DOM nodes included with your widget.
				// DOM nodes and widgets with the dojoAttachPoint attribute specified can now be directly
				// accessed as fields on "this". 
				// Common operations for postCreate
				// 1) Access and manipulate DOM nodes created in buildRendering()
				// 2) Add new DOM nodes or widgets 
				postCreate : function() {
					dojo.connect(this.myObject,"selectChild",null,this.openmytbl);
					this.tblparticipantEventsColumns=[];
//					
					// set Event Initiators table
					var eventOptions=this.myEPN.getEventList();
					
					this.tblparticipantEventsColumns.push(this.gridhelper.setColumn("Event", "Selection", true, true, eventOptions, "setInputEventName", "getInputEventName", this.myObject, null ));
					this.tblparticipantEventsColumns.push(this.gridhelper.setColumn("Alias", "String", true, false, null,"setInputEventAlias", "getInputEventAlias", this.myObject, null));
					this.tblparticipantEventsColumns.push(this.gridhelper.setColumn("Condition", "String", true, true, null,"setInputEventFilterExpression", "getInputEventFilterExpression", this.myObject, null));
					this.addTypeSpecificColumns();
					
					this.tblparticipantEventsName="Participant Events";
					this._participantEventsTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblparticipantEventsColumns, tblName: this.tblparticipantEventsName, hasDel: true, metaObject: this.myObject, metaAttrSize: "getNumberOfInputEvents", delAttrMethod: "deleteInputEvent"  });
					
					this.ParticipantEvents.appendChild(this._participantEventsTable.domNode);
					
				},
				addTypeSpecificColumns: function(){
					
					switch(this.myObject.getEPAType()){
					case ATEnum.EPAType.Aggregate : 		
						this.buildAggregation();
						break;
					case  ATEnum.EPAType.All : 
							this.buildAll();
							break;
					case   ATEnum.EPAType.Sequence : 
							this.buildAll();
							break;
					case  ATEnum.EPAType.RelativeN : 
							this.buildRelative();
							break;
					case  ATEnum.EPAType.Trend : 
							this.buildTrend();
							break;
					default: break;
					}
					
				},
				openmytbl: function(page, /*Boolean*/ animate){
					if(page==this.selectPane){
						//create table if it's not created
						this._participantEventsTable.startup();
					}
				},
				
				createComputedVariablesTbl:function(){
					this.buildComputedVariablesData();
					//insert to widget
					this.tblcomputedVariablesName="Computed Variables";
					this._computedVariablesTable = new widgets.GenGrid({ grid_inputcolumns:  this.tblcomputedVariablesColumns, tblName: this.tblcomputedVariablesName, hasDel: true, metaObject: this.myObject, metaAttrSize: "getNumberOfComputedVariables", 
						                                                 delAttrMethod: "deleteComputedVariable",refreshButton:true, refreshFunction:"updateComputedVariablesTbl",refreshObject:this,
						                                                 noShowRequiredButton:true});
					this.ComputedVariables.appendChild(this._computedVariablesTable.domNode);
				},
				
				updateComputedVariablesTbl:function(){
					this.buildComputedVariablesData();
						//update function
					this.gridhelper.updateDataStructire(this._computedVariablesTable,"",this.tblcomputedVariablesColumns, this.myObject,"getNumberOfComputedVariables");					
				},
				buildComputedVariablesData:function(){
					this.tblcomputedVariablesColumns=[];
					this.tblcomputedVariablesColumns.push(this.gridhelper.setColumn("Name", "String", true, true, null,"setComputedVarName", "getComputedVarName", this.myObject, null));
					this.tblcomputedVariablesColumns.push(this.gridhelper.setColumn("Aggregation Type", "Selection", true, true, this.aggregationTypes, "setComputedVarAggregationType", "getComputedVarAggregationType", this.myObject, null ));

					for(i=0,l=this.myObject.getNumberOfInputEvents(); i<l; i++){
						var eventAliasName= this.myObject.getAliasOrNameOfInputEvent(i);
						this.tblcomputedVariablesColumns.push(this.gridhelper.setColumn(eventAliasName+ " Expr", "String", true, true, null,"setComputedVarExpression", "getComputedVarExpression", this.myObject, eventAliasName));
					}
	
				},
				startup: function(){
					this.inherited(arguments);
					this.partEvt.expandCollapse();
			
					dojo.connect(this.myObject,"selectChild",null,this.openmytbl);
					this._participantEventsTable.startup();
					if (this.myObject.getEPAType()==ATEnum.EPAType.Aggregate){
						this.createComputedVariablesTbl();
						this._computedVariablesTable.startup();
					}
				}
							
		});
