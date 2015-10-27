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
 dojo.provide("metadata.EPA");
dojo.require("metadata.ParseError");
dojo.require("metadata.DerivedEvent");
dojo.require("metadata.BaseDefinition");
dojo.require("metadata.Event");
dojo.declare("metadata.EPA", metadata.BaseDefinition,{
	constructor: function(args, errors, definitionType, epn) {
		//Class "private" Members - should be accessed by get/set
		this._epaType=ATEnum.EPAType.FILTER;
		this._inputEvents= new Array(); 
		this._computedVariables = new Array(); //for aggregation type only
		this._derivedEvents= new Array();
		this._context=null;
		this._internalSegmentation=new Array();

		this._assertion=null;				//condition
		this._evaluation=ATEnum.EvaluationPolicy.Immediate;
		this._cardinality=ATEnum.CardinalityPolicy.Single;
		this._n=null;									//for relative N only
		this._ranking=null;								//for relative N only
		this._nTrend = null;							//for trend only
		this._trendRelation = null;						//for trend only
		this._epn = epn;
		this._errors = errors;
		this._definitionType = definitionType;
		
		var i,l,eName;
		try{
			if (dojo.isObject(args)){//EPA from reading json input
			if(!args || !args.name){
				throw new metadata.ParseError("");
			}
			if(args.assertion) {this.setAssertion(args.assertion);}
			if(args.evaluationPolicy) {this.setEvaluation(args.evaluationPolicy);}
			// this is set after evaluation policy as the type can dictate the policy
			if (args.epaType){
				this._epaType=this.getType(args.epaType);
			}
			if(args.cardinalityPolicy) {this.setCardinality(args.cardinalityPolicy);}
			if(args.N) {this.setN(args.N);}
			if(args.rankingRelation) {this.setRanking(args.rankingRelation);}
			
			if(args.trendN) {this.setTrendN(args.trendN);}
			if(args.trendRelation) {this.setTrendRelation(args.trendRelation);}
			
			if(args.context) {this.setContext(args.context);}
			
			if(args.inputEvents && args.inputEvents.length>0){
				for(i=0, l=args.inputEvents.length; i<l; i++){
					if(epn.isEventExists(args.inputEvents[i].name)){
						this._inputEvents[i]={};
						this.setInputEventName(i, args.inputEvents[i].name);
					}else{
						throw new metadata.ParseError("input event " + args.inputEvents[i].name +" does not exist");
					}
					if(args.inputEvents[i].alias){
						this.setInputEventAlias(i, args.inputEvents[i].alias);
					}
					if(args.inputEvents[i].filterExpression){
						this.setInputEventFilterExpression(i, args.inputEvents[i].filterExpression);
					}
					//DOTO: parse only for statefull EPA - each type may have different attributes
					if(args.inputEvents[i].consumptionPolicy){
						this.setInputEventConsumptionPolicy(i, args.inputEvents[i].consumptionPolicy);
					}
					if(args.inputEvents[i].instanceSelectionPolicy){
						this.setInputEventSelectionPolicy(i, args.inputEvents[i].instanceSelectionPolicy);
					}
					//DOTO: should be valid in relativeN and trend only
					if(args.inputEvents[i].expression){
						//check if thats relative N
						if (args.N) this.setInputEventRelativeNExpression(i, args.inputEvents[i].expression);
						if (args.trendN) this.setInputEventTrendExpression(i, args.inputEvents[i].expression);
					}
					
				}
			}
			//for aggregation type only
			if(args.computedVariables){
				var eventIndex,eventsNum,eventAliasName;
				for(i=0, l=args.computedVariables.length; i<l; i++){
					if(args.computedVariables[i].name){
						this.setComputedVarName(i,args.computedVariables[i].name);
						this.setComputedVarAggregationType(i,args.computedVariables[i].aggregationType);
						for(eventIndex=0, eventsNum=this._inputEvents.length; eventIndex<eventsNum; eventIndex++){
							eventAliasName= this.getAliasOrNameOfInputEvent(eventIndex);
							if(args.computedVariables[i][eventAliasName]){
								this.setComputedVarExpression(eventAliasName, i, args.computedVariables[i][eventAliasName]);
							}								
						}
					}
				}
			}

			if(args.internalSegmentation){
				for(i=0, l=args.internalSegmentation.length; i<l; i++){
					if(args.internalSegmentation[i].name){
						this.setInternalSegmentationName(i, args.internalSegmentation[i].name);
					}else{
						//TODO: generate error
						console.log("Error in EPA " + this.getName() +". Missing internal segmentation " + i + " name");	
					}
				}
			}
			
			if(!args.derivedEvents){
				console.log("missing derived event");	
			}else{
				this._derivedEvents=new Array();
				for(i=0, l=args.derivedEvents.length; i<l; i++){
					this._derivedEvents[i]=new metadata.DerivedEvent(args.derivedEvents[i], errors, definitionType, epn); 
				}
			}
		}//else, this is a new EPA that the user just now started to define and it is handled by the base definition

		}catch(err){
			err.msg = "Error parsing EPA " + this.name + ". " + err.msg;
			console.log(err.msg);			
			throw err;
		}
	},

	isStatefullEPA: function(epaType){
		if (epaType===ATEnum.EPAType.AGGREGATE ||
			epaType===ATEnum.EPAType.ALL||
			epaType===ATEnum.EPAType.SEQUENCE||
			epaType===ATEnum.EPAType.RELATIVE_N||
			epaType===ATEnum.EPAType.ABSENCE){
			
			return true;
		}
		return false;
	},
	
	getEPAType: function(){
		return(this._epaType);		
	},
	
	setEPAType: function(type){
		for(var p in ATEnum.EPAType){
			if(ATEnum.EPAType[p]===type){
				this._epaType=type;
				if (type === ATEnum.EPAType.Absence) {
					this.setEvaluation(ATEnum.EvaluationPolicy.Deferred);
				} else if (type === ATEnum.EPAType.Basic) {
					this.setEvaluation(ATEnum.EvaluationPolicy.Immediate);					
				}
				return;
			}
		}
		//TODO: generate error
		console.log("error - in EPA " + this.getName() + ". Unsupported EPA type: " + type);		
	},
	
	getType: function(epaType){
		if (!epaType) 
			return ATEnum.EPAType.Basic; //default
		if (ATEnum.EPAType[epaType]){
			return ATEnum.EPAType[epaType];
		}
		throw new metadata.ParseError("unsupported EPA type: " + epaType);
	},
	
	setContext: function(context){
		if(!context){
			this._context=null;
		}else if(this.isContextExists(context)){
			this._context=context;
		}else{
			console.log("Context " + context + " of EPA " + this.getName()+ " does not exists" );
			//TODO generate error on invalid context
		}
	},
	
	isContextExists:function(context){
		return this._epn.isContextExists(context);
	},
	
	getContext: function(){
		return this._context;
	},

	//input events 
	getNumberOfInputEvents: function(){
		return this._inputEvents.length;
	},

	deleteInputEvent: function(i){
		if(i>=this._inputEvents.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete input event " + i + " of EPA " + 
			    this.getName() + ". event is out of scope: ");
		}else{
			//delete it from the computed variables if such exists (aggregation only)
			this.deleteExpressionOfInputEvent(this.getAliasOrNameOfInputEvent(i));
			this._inputEvents=this._inputEvents.slice(0, i).concat(this._inputEvents.slice(i+1));
		}
	},
	
	isInputEvent: function(name){
		var i,l;
		for(i=0,l=this._inputEvents.length; i<l; i++){
			if(name===this._inputEvents[i]._name){
				return true;
			}
		}
		return false;
	},
	
	getInputEventName: function(i){
		if(i>=this._inputEvents.length){
			console.log("internal error, accessing input event out of scope: " + i);
			return null;
		}			
		return this._inputEvents[i]._name;		
	},

	setInputEventName: function(i, name){
		var l=this._inputEvents.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - input event is out of bound");
			return;
		}else if (i==l){
			this._inputEvents[i]={};
		}
		if(this._epn.isEventExists(name)){
			//rename the computed variables expression if needed (aggregation only)
			if(this._computedVariables.length>0 && i<l && this._inputEvents[i]._name && !this._inputEvents[i]._alias){
				this.renameExpressionOfInputEvent(this._inputEvents[i]._name, name);
			}
			this._inputEvents[i]._name = name;	
		}else{
			//TODO: generate error
			console.log("Error in EPA " + this.getName() +  " input event " + name + "doesn't exist");					
		}
	},

	
	getInputEventAlias: function(i){
		if(i>=this._inputEvents.length){
			console.log("internal error, accessing input event out of scope: " + i);
			return null;
		}			
		return this._inputEvents[i]._alias||null;		
	},

	setInputEventAlias: function(i, alias){
		var l=this._inputEvents.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - input event is out of bound");
			return;
		}else if (i==l){
			this._inputEvents[i]={};
		}
		//rename the computed variables expression if needed (aggregation only)
		if(this._computedVariables.length>0 && i<l ){
			this.renameExpressionOfInputEvent(this.getAliasOrNameOfInputEvent(i), alias);
		}

		this._inputEvents[i]._alias = alias;				
	},

	//if alias exists get it. Otherwise get the event name
	getAliasOrNameOfInputEvent:function(i){
		if(i>=this._inputEvents.length){
			console.log("internal error, accessing input event out of scope: " + i);
			return null;
		}			
		if(this._inputEvents[i]._alias && this._inputEvents[i]._alias!=""){
			return this._inputEvents[i]._alias;
		}else{
			return this._inputEvents[i]._name;
		}
	},

	getInputEventFilterExpression: function(i){
		if(i>=this._inputEvents.length){
			console.log("internal error, accessing input event out of scope: " + i);
			return null;
		}			
		return this._inputEvents[i]._filterExpression||null;		
	},

	setInputEventFilterExpression: function(i, filterExpression){
		var l=this._inputEvents.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - input event is out of bound");
			return;
		}else if (i==l){
			this._inputEvents[i]={};
		}
		//TODO: check validity of the expression
		this._inputEvents[i]._filterExpression = filterExpression;				
	},

	
	getInputEventConsumptionPolicy: function(i){
		if(i>=this._inputEvents.length){
			console.log("internal error, accessing input event out of scope: " + i);
			return null;
		}			
		if (!this._inputEvents[i]._consumptionPolicy){ 
			this._inputEvents[i]._consumptionPolicy = ATEnum.DefaultConsumptionPolicy;
		}
		return this._inputEvents[i]._consumptionPolicy;
	},

	setInputEventConsumptionPolicy: function(i, consumptionPolicy){
		var l=this._inputEvents.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - input event is out of bound");
			return;
		}else if (i==l){
			this._inputEvents[i]={};
		}
		for(var c in ATEnum.ConsumptionPolicy){
			if(ATEnum.ConsumptionPolicy[c]===consumptionPolicy){
				this._inputEvents[i]._consumptionPolicy = consumptionPolicy;	
				return;
			}
		}
		//TODO: generate error
		console.log("Error in EPA " + this.getName() +  " consumption policy " + consumptionPolicy + "doesn't supported");						
	},

	
	getInputEventInstanceSelectionPolicy: function(i){
		if(i>=this._inputEvents.length){
			console.log("internal error, accessing input event out of scope: " + i);
			return null;
		}			
		if(!this._inputEvents[i]._selectionPolicy){
			this._inputEvents[i]._selectionPolicy = ATEnum.DefaultInstanceSelectionPolicy;
		}
		return this._inputEvents[i]._selectionPolicy;
		
	},
	
	setInputEventSelectionPolicy: function(i, selectionPolicy){
		var l=this._inputEvents.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - input event is out of bound");
			return;
		}else if (i==l){
			this._inputEvents[i]={};
		}
		for(var s in ATEnum.InstanceSelectionPolicy){
			if(ATEnum.InstanceSelectionPolicy[s]===selectionPolicy){
				this._inputEvents[i]._selectionPolicy = selectionPolicy;
				return;
			}
		}
		//TODO: generate error
		console.log("Error in EPA " + this.getName() +  " selection policy " + selectionPolicy + "doesn't supported");						
	},

	//for relativeN only
	setInputEventRelativeNExpression: function(i,expression){
		var l=this._inputEvents.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - input event is out of bound");
			return;
		}else if (i==l){
			this._inputEvents[i]={};
		}
		//TODO: check it is a valid expression
		this._inputEvents[i]._expression = expression;		
	},
	
	//for relativeN only
	getInputEventRelativeNExpression: function(i){
		if(i>=this._inputEvents.length){
			console.log("internal error, accessing input event out of scope: " + i);
			return null;
		}			
		return this._inputEvents[i]._expression||null;		
	},
	
	
	//for trend only 
	setInputEventTrendExpression: function(i,expression){
		var l=this._inputEvents.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - input event is out of bound");
			return;
		}else if (i==l){
			this._inputEvents[i]={};
		}
		//TODO: check it is a valid expression
		this._inputEvents[i]._expression = expression;		
	},
	
	//for trend only
	getInputEventTrendExpression: function(i){
		if(i>=this._inputEvents.length){
			console.log("internal error, accessing input event out of scope: " + i);
			return null;
		}			
		return this._inputEvents[i]._expression||null;		
	},
	//for aggregation only
	//computed variables
	
	getNumberOfComputedVariables: function(){
		return this._computedVariables.length;
	},
	
	deleteComputedVariable: function(i){
		if(i>=this._computedVariables.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete computed variable " + i + " of EPA " + 
			    this.getName() + ". computed variable is out of scope: ");
		}else{
			this._computedVariables=this._computedVariables.slice(0, i).concat(this._computedVariables.slice(i+1));
		}
	},
	
	setComputedVarName: function(i,name){
		var l = this._computedVariables.length;
		if(i>l){
			//TODO: internal error
			console.log("internal error - computed variable is out of bound");
			return;			
		}else if(i==l){
			this._computedVariables[i]={};
		}
		this._computedVariables[i]._name=name;			
	},
	
	getComputedVarName: function(i){
		if(i>=this._computedVariables.length){
			console.log("internal error, accessing computed variable out of scope: " + i);
			return;
		}			
		return this._computedVariables[i]._name;		
	},
	
	setComputedVarAggregationType: function(i, type){
		var l = this._computedVariables.length;
		if(i>l){
			//TODO: internal error
			console.log("internal error - computed variable is out of bound");
			return;			
		}else if(i==l){
			this._computedVariables[i]={};
		}
		for (var e in ATEnum.AggregationType) {
			if(ATEnum.AggregationType[e]===type){
				this._computedVariables[i]._aggregationType=type;	
				return;
			}
		}
		//TODO: generate error;
		console.log("invalid aggregation type: " + type);
		return;				
	},
	
	getComputedVarAggregationType: function(i){
		if(i>=this._computedVariables.length){
			console.log("internal error, accessing computed variable out of scope: " + i);
			return;
		}			
		return this._computedVariables[i]._aggregationType;		
		
	},
	
	setComputedVarExpression: function(eventAliasName, i, expr){
		var l = this._computedVariables.length;
		if(i>l){
			//TODO: internal error
			console.log("internal error - computed variable is out of bound");
			return;			
		}else if(i==l){
			this._computedVariables[i]={};
		}
		this._computedVariables[i][eventAliasName]=expr;			
		
	},
	
	getComputedVarExpression: function(eventAliasName, i){
		if(i>=this._computedVariables.length){
			console.log("internal error, accessing computed variable out of scope: " + i);
			return;
		}			
		return this._computedVariables[i][eventAliasName];	
	},
	
	deleteExpressionOfInputEvent: function(eventAliasName){
		for(var i=0,l = this._computedVariables.length;i<l;i++){
			delete this._computedVariables[i][eventAliasName];
		}	
	},
	
	//called when the user changes the input event alias (or name if no alias)
	renameExpressionOfInputEvent: function(oldName, newName){
		var expr;
		for(var i=0,l = this._computedVariables.length;i<l;i++){
			if(this._computedVariables[i][oldName]){
				expr=this._computedVariables[i][oldName];
				delete this._computedVariables[i][oldName];
				this._computedVariables[i][newName]=expr;
			}
		}		
	},
	
	
	//internal segmentation
	
	getNumberOfInternalSegmentations: function(){
		return this._internalSegmentation.length;
	},

	deleteInternalSegmentation: function(i){
		if(i>=this._internalSegmentation.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete internal segmentation " + i + " of EPA " + 
			    this.getName());
		}else{
			this._internalSegmentation=this._internalSegmentation.slice(0, i).concat(this._internalSegmentation.slice(i+1));
		}
	},
	
	setInternalSegmentationName: function(i, name){
		var l=this._internalSegmentation.length;
		if (i>l){
			//TODO: internal error
			console.log("internal error - internal segmentation out of bound");
			return;
		}else if (i==l){
			this._internalSegmentation[i]={};
		}
		this._internalSegmentation[i]._name=name;			
	},
	
	getInternalSegmentationName: function(i){
		if(i>=this._internalSegmentation.length){
			console.log("internal error, accessing internal segmentation out of scope: " + i);
			return null;
		}			
		return this._internalSegmentation[i]._name;			
	},
	
	
	//Derived event methods
	
	getNumberOfDerivedEvents: function(){
		return this._derivedEvents.length;
	},

	deleteDeriveEvent: function(i){
		if(i>=this._derivedEvents.length || i<0){
			//TODO: generate internal error
			console.log("Internal error in trying to delete derive event " + i + " of EPA " + 
			    this.getName() + ". Derive event is out of scope: ");
		}else{
			this._derivedEvents=this._derivedEvents.slice(0, i).concat(this._derivedEvents.slice(i+1));
		}
	},
	isDerivedEvent: function(name){
		var i,l;
		for(i=0,l=this._derivedEvents.length; i<l; i++){
			if(name===this._derivedEvents[i]._name){
				return true;
			}
		}
		return false;
	},

	getDerivedEventName: function(i){
		if(i>=this._derivedEvents.length){
			console.log("internal error, accessing derived event out of scope: " + i);
			return null;
		}			
		return this._derivedEvents[i].getName();		
	},
	
	addDerivedEvent: function(name){
		this._derivedEvents.push(new metadata.DerivedEvent(name, this._errors, this._definitionType, this._epn));
	},
	
	setDerivedEventName: function(i, name){
		if(i>=this._derivedEvents.length){
			console.log("internal error, accessing derived event out of scope: " + i);
			return;
		}			
		this._derivedEvents[i].setName(name);		
	},
	
	getDerivedEventCondition: function(i){
		if(i>=this._derivedEvents.length){
			console.log("internal error, accessing derived event out of scope: " + i);
			return null;
		}			
		return this._derivedEvents[i].getCondition();		
	},

	setDerivedEventCondition: function(i, condition){
		if(i>=this._derivedEvents.length){
			console.log("internal error, accessing derived event out of scope: " + i);
			return;
		}
		//TODO: check validity of the condition
		this._derivedEvents[i].setCondition(condition);		
	},
			
	isReportParticipants: function(i){
		if(i>=this._derivedEvents.length){
			console.log("internal error, accessing derived event out of scope: " + i);
			return false;
		}
		return this._derivedEvents[i].isReportParticipants(); //return boolean
		
	},

	setDerivedEventReportParticipants: function(i, reportParticipants){
		if(i>=this._derivedEvents.length){
			console.log("internal error, accessing derived event out of scope: " + i);
			return;
		}			
		this._derivedEvents[i].setReportParticipants(reportParticipants);		
	},
			

	getDerivedEventExpression: function(derivedEventIndex, attributeIndex){
		if(derivedEventIndex>=this._derivedEvents.length){
			console.log("internal error, accessing derived event out of scope: " + derivedEventIndex);
			return null;
		}
		var attribute=this._derivedEvents[derivedEventIndex].getEvent().getAttributeName(attributeIndex); 
		var expr = this._derivedEvents[derivedEventIndex].getExpression(attribute);
		//taking the default value when the expression is missing
		if (!expr && attributeIndex){
			expr=this._derivedEvents[derivedEventIndex]._event.getAttributeDefaultValue(attributeIndex);
		}
		return expr;		
		
	},

	setDerivedEventExpression: function(derivedEventIndex, rowIndex, expression){
	
		if(derivedEventIndex>=this._derivedEvents.length){
			console.log("internal error, accessing derived event out of scope: " + derivedEventIndex);
			return;
		}			
		this._derivedEvents[derivedEventIndex].setExpression(rowIndex, expression);		
	},
	
	
	//conditions methods
	
	getAssertion: function(){
		return this._assertion;
	},

	setAssertion:function(assertion){
		//TODO: check it is a valid assertion
		this._assertion=assertion;
	},
	
	getEvaluation: function(){
		return this._evaluation;
	},
	
	setEvaluation:function(evaluation){
		for (var e in ATEnum.EvaluationPolicy) {
			if(ATEnum.EvaluationPolicy[e]===evaluation){
				this._evaluation=evaluation;
				return;
			}
		}
		//TODO: generate error;
		return;
	},
	
	getCardinality: function(){
		return this._cardinality;
	},

	setCardinality:function(cardinality){
		for (var card in ATEnum.CardinalityPolicy) {
			if(ATEnum.CardinalityPolicy[card]===cardinality){
				this._cardinality=cardinality;
				return;
			}
		}
		//TODO: generate error;
		return;
	},

	
	getN: function(){
		return this._n;
	},

	setN:function(n){
		//TODO: check that n is a valid integer
		this._n=n;
	},
	
	setTrendN:function(trendN){
		//TODO: check that n is a valid integer
		this._nTrend=trendN;
	},
	getTrendN: function(){
		return this._nTrend;
	},

	getRanking: function(){
		return this._ranking;
	},
	
	setRanking:function(ranking){
		for (var rank in ATEnum.RankingRelation) {
			if(ATEnum.RankingRelation[rank]===ranking){
				this._ranking=ranking;
				return;
			}
		}
		//TODO: generate error;
		return;
	},
	
	setTrendRelation:function(trendRelation){
		for (var trend in ATEnum.TrendRelation) {
			if(ATEnum.TrendRelation[trend]===trendRelation){
				this._trendRelation=trendRelation;
				return;
			}
		}
		//TODO: generate error;
		return;
	},
	
	getTrendRelation: function(){
		return this._trendRelation;
	},

	toString:function(){
		return this.getName();
	}

	});

