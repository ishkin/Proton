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
 

dojo.provide("widgets.ProtonMain");
dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("dijit.Dialog");
dojo.require('dijit.layout.BorderContainer');
dojo.require('dijit.layout.ContentPane');
dojo.require('dijit.MenuBar');
dojo.require('dojo.io.iframe');
dojo.require('dijit.PopupMenuBarItem');
dojo.require('dijit.MenuItem');
dojo.require('dijit.Menu');
dojo.require('dojo.data.ItemFileReadStore');
dojo.require('dijit.Tree');
dojo.require('dijit.layout.TabContainer');
dojo.require('dijit.form.TextBox');
dojo.require('dijit.form.Button');
dojo.require('dijit.layout.AccordionContainer');
dojo.require('dijit.layout.AccordionPane');
dojo.require('dojo.store.Memory');
dojo.require('dojo.hash');
dojo.require('dijit.form.DropDownButton');
dojo.require('dijit.Menu');
dojo.require('dijit.MenuItem');
dojo.require("dijit.TooltipDialog");
dojo.require("dijit.Tooltip");
dojo.require("dijit.InlineEditBox");
dojo.require("dijit.form.Textarea");
dojo.require("widgets.GeneralAtt");
dojo.require("widgets.EpaType");
//dojo.require("dojox.uuid.generateRandomUuid");
dojo.require("metadata.EPN");
dojo.require("metadata.ParseErrors");
dojo.require("widgets.AddObject");
dojo.require("widgets.EpaCondition");
dojo.require("widgets.EpaDerivationAdd");
//dojo.require("widgets.EpaDerivationObj");
dojo.require("widgets.EpaDerivationList");
dojo.require("widgets.EventTab");
dojo.require("widgets.EPATab");
dojo.require("widgets.TemporalTab");
dojo.require("widgets.SegmentationTab");
dojo.require("widgets.ProducerTab");
dojo.require("widgets.ConsumerTab");
dojo.require("widgets.TemplateTab");
dojo.require("widgets.CompositeTab");
dojo.require("widgets.TemporalTerminator");
dojo.require("widgets.validate");
dojo.require("metadata.Validator");
dojo.require("dojox.json.schema");
dojo.require("metadata.CreateJSON");
dojo.require("widgets.ErrorList");
dojo.require("widgets.GridHelper");
dojo.require("dijit.Menu");
dojo.require("dijit.MenuItem");
dojo.require("dijit.MenuSeparator");
dojo.require("dijit.PopupMenuItem");
dojo.declare("widgets.ProtonMain",
             [dijit._Widget, dijit._Templated],
{
    widgetsInTemplate: true,
     //templateString: '<div>Go</div>', 
    templatePath: dojo.moduleUrl("widgets","proton_main.html"),
    
    constructor: function() {
		this.myEPN=null;							//hold the data structure
		// this.eventsList = new Array();			//list of Events
		// this.epasList = new Array();			//list of EPA
		// this.temporalContextsList = new Array();	//list of temporal contexts
		// this.segmentationContextsList = new Array();//list of segmentation contexts
		// this.compositeContextsList = new Array();	//list of composite contexts		
		this.currentProject="";					//the selected project
		//rawdata.children.Object have id, label Attributes.
		this.rawdata= new Array(); 				//the sdata structure for the tree store from eventsList & epasList
		this.copyObject=""; 					//the object on tree that was copy
		this.selectedTreeObject="";
		this.jsonData="";
		this.projectJsonFilePath="public/com/ibm/hrl/proton/authoringtool/jsonFiles/projectsListExample.json";
		this.EPNJsonFilePath="public/com/ibm/hrl/proton/authoringtool/jsonFiles/";
		this.configFileName = "Config.json";
		this.projectsURL="";
		this.verifierURL = "";
		this.tooltipDialog="";
		//this.newObjName="";
		this.newObjType="";
		this.dialog="";
		this.treeModel="";
		//this.myErrList=null;
		var that = this;
		dojo.subscribe("PublishNewObject",function(item) {
		  	that.publishNewObject(item);
		});
		dojo.subscribe("PublishNewProject",this,function(item) {
		  	this.publishNewProject(item);
		});
		dojo.subscribe("PopUpNewEvent",this,function(item, callBack) {
		  	this.showDialog("Event", item, callBack);
		});
		dojo.subscribe("OpenObjectFromErrTbl",this,function(node) {
		  	this.addTab(node.name, node.type);
		});
		ATVars.PROTON_MAIN = this;
	},
	showProject: function() {this.showDialog("Project");},
	showEvent: function() {this.showDialog("Event");},
	showEPA: function() {this.showDialog("EPA");},
	showTemporal: function() {this.showDialog("Temporal Context");},
	showSegmentation: function() {this.showDialog("Segmentation Context");},
	showComposite: function() {this.showDialog("Composite Context");},
	showConsumer: function() {this.showDialog("Consumer");},
	showTemplate: function() {this.showDialog("Template");},
	showProducer: function() {this.showDialog("Producer");},
		
	showDialog: function(name, item, callBack) {
		ATVars.NEWOBJECT=name;
		var node = dojo.byId('ddbNew');
		tooltipDialog = new widgets.AddObject();
		tooltipDialog.startup(item);
        dijit.popup.open({
        	popup:tooltipDialog,
        	around:node,
        	onClose: function() {
        		if (tooltipDialog.ObjName.value && callBack) {
        			callBack(tooltipDialog.ObjName.value);
        		}
        	}
        });
	},
   
   startup: function(){
        widgets.ProtonMain.superclass.startup.apply(this, arguments);
        //tooltipDialog = new widgets.AddObject();
        this.loadJSONSync(this.EPNJsonFilePath + this.configFileName);
        this.projectsURL = ATVars.PROJECTS_URL = this.jsonData.projectsURL;
        this.verifierURL = ATVars.VERIFIER_URL = this.jsonData.verifierURL;
        this.fileURL = ATVars.FILE_URL = this.jsonData.fileURL;
        this.serverURL = ATVars.SERVER_URL = this.jsonData.serverURL;
        this.loadJSONSync(this.projectsURL);
        this.addToDropDownButton(this.jsonData);

	 	tMenu = new dijit.Menu({
	 		targetNodeIds: ["TreeContainer"]
        });
	 	
	 	var that = this;
        tMenu.addChild(new dijit.MenuItem({
            label: "Delete",
            iconClass: "dijitEditorIcon dijitEditorIconDelete",
            onClick: function() {
            	var getObject = function(o) {
            		if (!o) {
            			return undefined;
            		}
            		var name = dojo.trim(o.innerText);
            		if (that.myEPN.isExists(name)) {
            			return name;
            		} else {
            			return getObject(o.parentNode);
            		}
            	};
            	var name = getObject(that.selectedTreeObject);
            	if (!name) {
            		return;
            	}
                var deleted = that.myEPN.deleteEPNObject(name, function() {
                	return that.verifyToJSON();
                });
                if (deleted) {
                	// close tab if open
                	var tc = dijit.byId('tabContainer');
                	for (var child in tc.getChildren()) {
                		var item = tc.getChildren()[child];
                		if (item.title === name) {
                			tc.removeChild(item);
                			break;
                		}
                	}
                	var node = that.selectedTreeObject;
                	// find row in tree node
                	while (dojo.hasClass(node, "dijitTreeNode") == false) {
                		node = node.parentNode;
                	}
                	dojo.destroy(node);                	
                }
            }
        }));
        
        tMenu.addChild(new dijit.MenuItem({
        	label: "Replicate",
        	iconClass: "dijitEditorIcon dijitEditorIconCopy",
        	onClick: function() {
        		var getObject = function(o) {
        			if (!o) {
        				return undefined;
        			}
        			var name = dojo.trim(o.innerText);
        			if (that.myEPN.isExists(name)) {
        				return name;
        			} else {
        				return getObject(o.parentNode);
        			}
        		};
        		var name = getObject(that.selectedTreeObject);
        		if (!name) {
        			return;
        		}
        		var epnObject = that.myEPN.getEPNObject(name);
//        		var newObject = epnObject;
//        		delete newObject._epn; // this avoid a stackoverflow error
//				newObject = dojo.fromJson(dojo.toJson(epnObject));
        		var newObject = {};
        		// copy methods
        		var shallowCopy = function(o) {
    				switch(Object.prototype.toString.call(o)) {
        			case '[object Array]':
        				return o.slice(0);
        			case '[object Object]': 
        				temp = {};
        				for (var prop in o) {
        					temp[prop] = o[prop];
        				}
        				return temp;
        			}
        		};
        		var level2Copy = function(o) {
        			switch(Object.prototype.toString.call(o)) {
        			case '[object Array]':
        				var arr = o.slice(0);
        				for (var i in arr) {
        					arr[i] = shallowCopy(o[i]);
        				}
        				return arr;
        			case '[object Object]': 
        				temp = {};
        				for (var prop in o) {
        					temp[prop] = shallowCopy(o[prop]);
        				}
        				return temp;
        			}
        		};
        		for (var prop in epnObject) {
        			var otherProp = epnObject[prop];
        			// arrays and objects need a 2-level deep copy
        			if (Object.prototype.toString.call(otherProp) === '[object Array]') {
        				otherProp = level2Copy(otherProp);
        			} else if (Object.prototype.toString.call(otherProp) === '[object Object]') {
        				otherProp = level2Copy(otherProp);
        			} 
        			newObject[prop] = otherProp;
        		}
        		
        		newObject._epn = epnObject._epn; // this one a shallow copy
        		var defaultName = "copy of " + newObject.getName();
        		var name = defaultName;
        		if (that.myEPN.isExists(defaultName)) {
        			var i = 2;
        			do  {
        				var name = defaultName + "(" + i++ + ")";
        			} while (that.myEPN.isExists(name));
        		}
        		newObject.setName(name);
        		newObject.item = newObject.getName();
        		newObject.type = epnObject._definitionType;
        		that.myEPN.addEPNObject(newObject);
        		that.publishNewObject(newObject);
        	}
        }));
        
        tMenu.addChild(new dijit.MenuSeparator());

        tMenu.startup();
        this.errorPane.oldResize = this.errorPane.resize;
		this.errorPane.resize = function (changeSize, resultSize) {
			that.errorPane.oldResize(changeSize, resultSize);
			dojo.forEach(dojo.query("#tabContainer .widgetTab"), function(tab) {
				var widget = dijit.getEnclosingWidget(tab);
				if (widget.resize) {
					widget.resize();
				}
			});
			if(that.validateTable) {
				that.validateTable.resize(changeSize, resultSize);
			}
		};
    },
	loadJSONSync:function(JsonFilePath){
		var that = this;
		jsonFile=JsonFilePath;
		dojo.xhrGet({
		    url:jsonFile,
		    handleAs:"json",
		    sync:true,
		    load: function(jsonData){
		    	that.jsonData=jsonData;
		    }
		});
	},
	
	getSchema: function(schemaFileName){
		var that = this;
		
 		dojo.xhrGet({
		    url:schemaFileName,
		    handleAs:"json",
		    sync:true,
		    load: function(schema){
		    	that.schemaData=schema;
		    }
 		});
	},
	
	validateInputBySchema: function(schemaFileName, jsonInput){
		
		this.getSchema(schemaFileName); //put the schema data in this.schemaData
		
		var result = dojox.json.schema.validate(jsonInput,this.schemaData);
		if(!result.valid){
			//TODO: generate error messages
		    console.log("Errors in project according to schema:");
		    for(var i=0, l=result.errors.length; i<l;i++){
		    	console.log(i+". Error in property " + result.errors[i].property+ " " + result.errors[i].message);
		    }
		}
	},		

	addToDropDownButton:function(jsonData){
			var that = this;
		  	var menu = new dijit.Menu({
	            style: "display: none;",
	            id: "projectsList"
	        });
	        for(i=0, l=jsonData.WorkSpace.Projects.length; i<l; i++){
		        var menuItem = new dijit.MenuItem({
		            label:jsonData.WorkSpace.Projects[i].name,
		            onClick: function(e) {
		            	//closeAllTabs();//Close all opend Tabs
		            	dojo.byId("prog_name").innerText=e.target.innerHTML;
		            	that.loadProject(e.target.innerHTML);
	 	           }
	 	       });
	        	menu.addChild(menuItem);
	        }
	      var button = new dijit.form.DropDownButton({
	        label: "Open Project",
	        name: "programmaticDDB",
	        dropDown: menu,
	        id: "progButton"
	    });
	   dojo.byId("prog_menu").appendChild(button.domNode);
	},
	
	loadProject:function(fileName, loadAnyway){
		if (fileName != this.currentProject || loadAnyway) {
			fileName = dojo.trim(fileName);
			dojo.byId("prog_name").innerText=fileName;
			this.loadJSONSync(this.projectsURL + fileName);
			this.validateInputBySchema(this.EPNJsonFilePath + ATVars.SCHEMA_NAME, this.jsonData);
			this.currentProject = fileName;
			this.myEPN = new metadata.EPN(this.jsonData);
			this.myEPN.setEPNName(this.currentProject);
			this.closeAllTabs();
			var num = dojo.byId("errNum");
			num.innerText = "";
			this.prepareTreeData();
		}
	},
	
   prepareTreeData:function() {
   	
   		var that=this;
		   	var xEvent = dijit.byId("eventMenu");
		   	xEvent.setDisabled(false);
		   	var xEPA = dijit.byId("epaMenu");
		   	xEPA.setDisabled(false);
		   	var xTC = dijit.byId("tcMenu");
		   	xTC.setDisabled(false);
		   	var xSC = dijit.byId("scMenu");
		   	xSC.setDisabled(false);
		   	var xCC = dijit.byId("ccMenu");
		   	xCC.setDisabled(false);
		   	var consumerEntry = dijit.byId("consumerMenu");
		   	consumerEntry.setDisabled(false);
		   	var producerEntry = dijit.byId("producerMenu");
		   	producerEntry.setDisabled(false);
		   	var templateEntry = dijit.byId("templateMenu");
		   	templateEntry.setDisabled(false);
			this.saveJson.set('disabled',false);
			this.verifyJson.set('disabled',false);
			this.deleteProject.set('disabled',false);
			this.exportJson.set('disabled',false);
		   	this.fillData();
	        var store = new dojo.data.ItemFileReadStore({
	            data: {
	                identifier: 'id',
	                label: 'label',
	                items: that.rawdata
	            }
	        });
	        treeModel = new dijit.tree.ForestStoreModel({
	            store: store
	        });
	        var tempTC= dijit.byId("dojoTree");
	    	if (tempTC){
	    		//old tree is presented. delete it and later connect the new tree to the dom
				tempTC.destroyRecursive(/* preserveDom */ false);
				//this.closeAllTabs();
				dojo.destroy(this.divTree);
	 		}
			this.divTree = dojo.create("div", {}, dojo.byId("TreeContainer"));
			this.treePane.setAttribute("style","overflow:auto");
	        treeControl = new dijit.Tree({
	            model: treeModel,
	            showRoot: false,
	          	rootLabel: "Title",
	          	//allowAutoScroll: "true", 
	            id: 'dojoTree',
	            rootId: 'id',
	            overflow : 'none',
	            rootLabel: 'name',
			    onDblClick: (function(i, n, e) {
			    	that.addTab(n.label, i.tag[0]);
//			    	ATVars.CURRENT_TREE_NODE = n;
			    }),
	        	onMouseDown: (function(e){// Create RightClick menu on the Tree
	        		that.selectedTreeObject=e.toElement;
	        		//if (e.button==2){that.DisplayTreeMenu(e.toElement.innerText);};
	        	})
	        },
	        this.divTree);
	       
//        }
    },
    
    DisplayTreeMenu: function(itemName){
		var tMenu= dijit.byId('tree_menu');
		tMenu.bindDomNode(treeControl.domNode);
	},
	 fillData:function(){
	  	this.rawdata = [{
	    label: 'Events',
	    id: '1',
	    children: []},
		{
	    label: 'EPAs',
	    id: '2',
	    children: []},
	    {
	    label: 'Contexts',
	    id: '3',
	    children: [{
		    label: 'Temporal Contexts',
		    id: '4',
		    children: []},{
		    label: 'Segmentation Contexts',
		    id: '5',
		    children: []},{
		    label: 'Composite Contexts',
		    id: '6',
		    children: []}]},
	    {
	    label: 'Consumers',
	    id: '7',
	    children: []},
	    {
	    label: 'Producers',
	    id: '8',
	    children: []},
	    {
	    label: 'Templates',
	    id: '9',
	    children: []}
	    ];
	  	
	  	this.rawdata.id="1";
	  	this.rawdata.name="project";
	  	var eventList=this.myEPN.getEventList();
	  	for(i=0, l=eventList.length; i<l; i++){
	  		this.rawdata[0]['children'][i]=new Object;
	    	this.rawdata[0]['children'][i].id = 1000+i;
	    	this.rawdata[0]['children'][i].label = eventList[i];
	    	this.rawdata[0]['children'][i].tag ="Event";
		}
	  	var epaList=this.myEPN.getEpaList();
	   	for(i=0, l=epaList.length; i<l; i++){
	  		this.rawdata[1]['children'][i]=new Object;
	    	this.rawdata[1]['children'][i].id = 2000+i;
	    	this.rawdata[1]['children'][i].label = epaList[i];
	    	this.rawdata[1]['children'][i].tag="EPA";
		}
	   	var contextT = this.rawdata[2]['children'][0];
	   	var contextS = this.rawdata[2]['children'][1];
	   	var contextC = this.rawdata[2]['children'][2];
	   	
	   	var temporalContextList=this.myEPN.getTemporalContextList();
	   	for(i=0, l=temporalContextList.length; i<l; i++){
	   		contextT['children'][i]=new Object;
	   		contextT['children'][i].id = 3000+i;
	   		contextT['children'][i].label = temporalContextList[i];
	   		contextT['children'][i].tag="Temporal";
		}
	   	var segmentationContextList=this.myEPN.getSegmentationContextList();
	   	for(i=0, l=segmentationContextList.length; i<l; i++){
	   		contextS['children'][i]=new Object;
	   		contextS['children'][i].id = 4000+i;
	   		contextS['children'][i].label = segmentationContextList[i];
	   		contextS['children'][i].tag="Segmentation";
		}
	   	var compositeContextList=this.myEPN.getCompositeContextList();
	   	for(i=0, l=compositeContextList.length; i<l; i++){
	   		contextC['children'][i]=new Object;
	   		contextC['children'][i].id = 5000+i;
	   		contextC['children'][i].label = compositeContextList[i];
	   		contextC['children'][i].tag="Composite";
		}
	   	var consumerList=this.myEPN.getConsumerList();
	   	for(i=0, l=consumerList.length; i<l; i++){
	   		this.rawdata[3]['children'][i]=new Object;
	   		this.rawdata[3]['children'][i].id = 6000+i;
	   		this.rawdata[3]['children'][i].label = consumerList[i];
	   		this.rawdata[3]['children'][i].tag="Consumer";
		}
	   	var producerList=this.myEPN.getProducerList();
	   	for(i=0, l=producerList.length; i<l; i++){
	   		this.rawdata[4]['children'][i]=new Object;
	   		this.rawdata[4]['children'][i].id = 7000+i;
	   		this.rawdata[4]['children'][i].label = producerList[i];
	   		this.rawdata[4]['children'][i].tag="Producer";
		}
	   	var templateList=this.myEPN.getTemplateList();
	   	for(i=0, l=templateList.length; i<l; i++){
	   		this.rawdata[5]['children'][i]=new Object;
	   		this.rawdata[5]['children'][i].id = 8000+i;
	   		this.rawdata[5]['children'][i].label = templateList[i];
	   		this.rawdata[5]['children'][i].tag="Template";
		}
	   	
	 },
	addTab:function(name,type) {
		 	var tc = dijit.byId('tabContainer');
		var updateContextComboBox = function(name) {
			var comboboxes = dojo.query(".epaContext");
			dojo.forEach(comboboxes, function(item) {
				item = dijit.getEnclosingWidget(item);
				item.store.newItem({
					"name": name
				});
			});
		};
		if (name) {
			var tcCh = tc.getChildren();
			for (i = 0, l = tcCh.length; i < l; i++) {
				if (tcCh[i].title == name) {
					tc.selectChild(tcCh[i]);
					return;
				}
			}
		}
		ATVars.CURRENT_OBJECT=ATVars.MY_EPN.getEPNObject(name);
		var that = this;
		dojo.forEach(dojo.query(".dijitTreeLabel"), function(e) {
			if (dijit.getEnclosingWidget(e).label == name) {
				ATVars.CURRENT_TREE_NODE = dijit.getEnclosingWidget(e);
			} 
		});
	 	if (type=="Event"||type=="EVENT"){
	         var eventTab= new widgets.EventTab({
	         	title: name||"New Event",
	         	closable:true,
	         	iconClass:ATVars.event_icon  //"dijitEditorIcon dijitLeaf"
	         });
	         tc.addChild(eventTab);
	         tc.selectChild(eventTab);
	         ATVars.CURRENT_TAB=eventTab;
	 	}
 	 	else if (type == "EPA") {
			var epaTab = new widgets.EPATab({
				title: name || "New EPA",
				closable: true
			});
			tc.addChild(epaTab);
			tc.selectChild(epaTab);
			epaTab.epaObj.resize();
			ATVars.CURRENT_TAB = epaTab;
		} else if (type == "Temporal" || type == "TEMPORAL_CONTEXT"
				|| type == "TemporalContext") {
			var temporalTab = new widgets.TemporalTab({
				title: name || "New Temporal",
				closable: true
			});
			tc.addChild(temporalTab);
			tc.selectChild(temporalTab);
			ATVars.CURRENT_TAB = temporalTab;
			updateContextComboBox(temporalTab.title);
		} else if (type == "Segmentation" || type == "SEGMENTATION_CONTEXT"
				|| type == "SegmentationContext") {
			var segmentationTab = new widgets.SegmentationTab({
				title: name || "New Segmentation",
				closable: true
			});
			tc.addChild(segmentationTab);
			tc.selectChild(segmentationTab);
			ATVars.CURRENT_TAB = segmentationTab;
			updateContextComboBox(segmentationTab.title);
		} else if (type == "Composite" || type == "COMPOSITE_CONTEXT"
				|| type == "CompositeContext") {
			var compositeTab = new widgets.CompositeTab({
				title: name || "New Composite",
				closable: true
			});
			tc.addChild(compositeTab);
			tc.selectChild(compositeTab);
			ATVars.CURRENT_TAB = compositeTab;
			updateContextComboBox(compositeTab.title);
		} else if (type == "Consumer" || type == "CONSUMER") {
			var consumerTab = new widgets.ConsumerTab({
				title: name || "New Comsumer",
				closable: true
			});
			tc.addChild(consumerTab);
			tc.selectChild(consumerTab);
			ATVars.CURRENT_TAB = consumerTab;
		} else if (type == "Producer" || type == "PRODUCER") {
			var producerTab = new widgets.ProducerTab({
				title: name || "New Producer",
				closable: true
			});
			tc.addChild(producerTab);
			tc.selectChild(producerTab);
			ATVars.CURRENT_TAB = producerTab;
		} else if (type == "Template" || type == "TEMPLATE") {
			var templateTab = new widgets.TemplateTab({
				title: name || "New Template",
				closable: true
			});
			tc.addChild(templateTab);
			tc.selectChild(templateTab);
			ATVars.CURRENT_TAB = templateTab;
		}
	},
	closeAllTabs:function(){
		// Close All Tabs
	 	var tc= dijit.byId('tabContainer');
	 	tc.destroyDescendants(true);
	 	// Close Error Table
	 	try{
	 		dojo.query(".validate").forEach(dojo.destroy);
// var ec= dijit.byId("validatetbl");
// if (ec){
// ec.destroyRecursive(/* preserveDom */ true);}
		}
	 	catch(err){}
	 	
	 	// ATVars.CURRENT_OBJECT="";
	 		// for(i=0, l=tc.getChildren.length; i<l; i++){
	 				// tc.removeChild(tc.children[i]);
				// }
	 },
	publishNewObject : function (item) {
		//var myTree = dojo.widget.manager.getWidgetById("dojoTree");
		
		this.prepareTreeData();
	  	this.addTab(item.item, item.type);
//	  	ATVars.CURRENT_TREE_NODE=item;
	},
	publishNewProject : function (project) {
		var that = this;
		var menuItem = new dijit.MenuItem({
    		label: project,
    		onClick: function(e) {
    			dojo.byId("prog_name").innerText = e.target.innerHTML;
    			that.loadProject(e.target.innerHTML);
    		}
    	});
		var projects = dijit.byId("projectsList");
		dijit.byId("projectsList").addChild(menuItem);
		menuItem.onClick({target : {innerHTML : project}});
	},
	
	// verifies the content using a parser
	// returns true iff there are errors
	verifyJSON:function(JSONContent) {
		var that = this;
		var errors = [];
		dojo.xhrPost({
			url: that.verifierURL,
			content: {content :  JSONContent},
			handleAs: "text",
			sync: true,
	        load: function(data) {
	        	errors = dojo.fromJson(data);
	        },
	        error: function(error) {
               alert(error.responseText);
               errors = true;
            }
		});
		return errors;
	},
	
	saveJason:function(x){
		//save on server if verification was successful
		var that = this;
		var projectName = that.currentProject;
		dojo.xhrPut({
			url: that.projectsURL,
			putData: dojo.toJson({"name" : projectName, "content" : dojo.toJson(x)}),
			handleAs: "json",
			sync:true,
			headers: { "Content-Type": "application/json"}
		});
		
	},
	
	saveToJSON:function() {
		//a stub verification
		//this.verifyJSON('{"epn": { "name":"epn1", "events":[{"description":"descE1","createdDate": "01/01/2011","createdBy": "Tomer"}], "epas":[], "contexts": { "temporal":[], "segmentation":[], "composite":[] }, "consumers":[], "producers":[]}}');
		var x = new metadata.CreateJSON();
		x.StartCreate();
		x = {epn: x.epn};
		var result = this.verifyJSON(dojo.toJson(x));
		if (result==false){
			this.saveJason(x);
			return true;
		}else{
			var answer = confirm ("File is incorrect, continue to save?");
			if (answer) {
				this.saveJason(x);
				return true;
			}
			//send errors to the table
			else {
				alert("Save action was canceled");
				return false;
			}
			//send errors to the table
		}
	},
	
	verifyToJSON:function(){
		var x = new metadata.CreateJSON();
		x.StartCreate();
		//x.epn=x.EPN;
		var errors = this.verifyJSON(JSON.stringify(x));
		if (errors === true) {
			// some error occured that prevented the verifier from running
			return;
		}
		ATVars.MY_ERRORS = new metadata.ParseErrors(errors);
		var errNum =ATVars.MY_ERRORS._parseError.length;
		var num = dojo.byId("errNum");
		num.innerText= "Number of errors and warnings = " + errNum;
		
		//remove the old table if exists
		var ec = dijit.byId("Errorlist");
		if (this.validateTable) {
			this.columns = this.validateTable.getStructure();
		}
		if (ec){
			ec.destroyRecursive(/* preserveDom */ false);
			dojo.destroy(this.divErrTbl);
		}
		if (errNum > 0){
			this.divErrTbl = dojo.create("div", {}, dojo.byId("errList"));
			this.validateTable = new widgets.validate({
				id:"Errorlist"
			}, this.divErrTbl);
			this.validateTable.startup();
			// FIXME this is currently a bug in dojo
//			if (this.columns) {
//				this.validateTable.setColumns(this.columns);
//			}
		}
		return errNum > 0;
	},
	
	exportToJSON: function() {
		if (this.saveToJSON() == false) {
			return;
		}
		var that = this;
		// desroy dialog with delay to avoid exception (bug in dojo)
		var destroyDialog = function(what) {
			setTimeout(function() {what.destroyRecursive();}, 50);
		};
		var destinationDiag = new dijit.Dialog({
			title: "Select Destination",
			content: "<button id='download'/>" +
			"<button id='export'/>",
			style: "width: 300px",
			onHide: function(){ destroyDialog(this); }
		});
		new dijit.form.Button({
			label: "Download file",
			onClick: function(){
				window.location.href = that.fileURL + "/" + that.currentProject;
				destroyDialog(destinationDiag);
            }}, "download");
		new dijit.form.Button({
			label: "Export to external repository",
			onClick: function(){
				var exportDiag = new dijit.Dialog({
					title: "Select Destination",
					content: "Name: <input id='name'/><br/>" +
							"Url: <input id='url'/><br/>" +
							"<button id='ok'/><button id='back'/>",
					style: "width: 300px",
					onHide: function(){ destroyDialog(this); }
				});
				new dijit.form.TextBox({
					value: that.myEPN.getEPNName()
				}, "name");
				new dijit.form.TextBox({
					label:'URL',
					value: /(.*\:?\d+?)/g.exec(window.location)[1] + "/ProtonOnWebServerAdmin/resources/definitions"
				},"url");
				new dijit.form.Button({
					label: "OK",
					onClick: function() {
						var epnName=dojo.byId("name").value;
						var x = new metadata.CreateJSON();
						x.StartCreate();
						x.setEPNName(epnName);
						var URL = dojo.byId("url").value;
						var existing = {};
						dojo.xhrGet({
							url: that.serverURL + "resources/proxy?url=" + URL,
							handleAs: 'json',
							sync: true,
							load: function(data) {
								existing = data;
								var postObj = {
										url: that.serverURL + "resources/proxy?url=" + URL,
										postData: dojo.toJson(x),
										content: dojo.toJson(x),
										headers: { "Content-Type": "application/json"},
										sync: true,
										load: function() {
											alert("successfully added " + epnName);
											destinationDiag.hide();
											exportDiag.hide();								
										},
										error: function(error) {
											alert("error: " + error.message);
										}
									};
									var overwriteRequested = false; 
									for (var i in existing) {
										var item = existing[i];
										var itemNameMatch = /(.*[\\\/])([^\.]+)\..*/g.exec(item.name);
										var itemName = itemNameMatch[itemNameMatch.length - 1];
										if (itemName === epnName) {
											overwriteRequested = true;
											if (window.confirm("Would you like to overwrite existing file")) {
												postObj.url += "/" + itemName;
												dojo.xhrPut(postObj);
											} else {
												return;
											}
											break;
										}
									}
									if (overwriteRequested == false) {
										dojo.xhrPost(postObj);
									}

							},
							error: function(error) {
								alert("error: " + error.message);
								destinationDiag.hide();
								exportDiag.hide();								
							}
						});
					}
				}, "ok");
				new dijit.form.Button({
					label: "Back",
					onClick: function() {exportDiag.hide();}
				}, "back");
				exportDiag.show();
            }},"export");
		destinationDiag.show();
	},
	
		
	deleteProjectFromServer: function() {
		var that = this;
		var projectName = this.currentProject;
		var userConfirmed = window.confirm("Are you sure you want to delete " + projectName + "?");
		if (userConfirmed == false) {
			return;
		}

		dojo.xhrDelete({
		    url:this.projectsURL + projectName,
		    handleAs:"json",
		    sync:true,
		    load: function(jsonData){
		    	dojo.byId("prog_name").innerText="";
//            	that.loadProject();
		    	that.closeAllTabs();
    			var num = dojo.byId("errNum");
    			num.innerText = "";
    			dijit.byId("dojoTree").destroyRecursive();
    			pList = dijit.byId("projectsList");
    			var children = pList.getChildren(); 
    			var child = undefined;
    			for (var i in children) {
    				var tempChild = children[i];
    				if (tempChild.label === projectName) {
    					child = tempChild;
    					break;
    				}
    			}
    			pList.removeChild(child);
		    }
		});	
	},
	
	importProjectToServer: function() {
		if (this.currentProject) {
			if (confirm("All unsaved changed will be discarded, continue with import?") == false) {
				return;
			}
		}
		fileUpload.click();
	},
	
	onFileSelected: function() {
		var fileUpload = dojo.query("#fileUpload")[0];
		var file = fileUpload.files[0];
		if (file == null) {
			return;
		}
        var r = new FileReader();
        var that = this;
        r.onload = (function (file) {
            return function (e) {
            	try {
	                var contents = e.target.result;
	                var result = that.verifyJSON(contents);
	                var importAnyway=false;
	               
	                if(result === true){
	                	// error in the verify process. reported to the user already
	                	retrun;
	                }else if (result.length>0){
	                	//some errors in the definition file were found
	                	importAnyway = confirm("File is incorrect, import anyway?");
	                }
	                if(importAnyway || (result.length==0)){
	                	var name = file.name.substr(0, file.name.lastIndexOf('.'));
	                	var projectAlreadyExists = that.isProjectExists(name);
	                	if(projectAlreadyExists){
	                		importAnyway = confirm("Project already exists, override?");	                	
	                		if(!importAnyway){
	                			alert("Import action was canceled");
	            				return false;
	                		}
	                	}	                
		        		old = that.currentProject;
		        		that.currentProject = name;
		        		project = dojo.fromJson(contents);
		        		that.saveJason(project);
		        		//that.currentProject = old;
		        		if(!projectAlreadyExists){
		        			that.publishNewProject(name);
		        		}
		        		that.loadProject(name, true);
		        		return true;
	                }else{ 
	                	alert("Import action was canceled");
        				return false;
	                }
        		} finally {
        			dojo.byId("fileUpload").value = "";
        		}
            };
        })(file);
        r.readAsText(file);
	},
	
	isProjectExists: function(projectName){
		var projectExists=false;
		dojo.xhrGet({
		    url:this.projectsURL + projectName,
		    handleAs:"json",
		    sync:true,
		    load: function(jsonData){
		    	projectExists=true;
		    },
			error: function(error){
		    	projectExists=false;
			}
		});
		return projectExists;
	},
	
	errorListResize: function() {
		alert("hi");
	}
});
