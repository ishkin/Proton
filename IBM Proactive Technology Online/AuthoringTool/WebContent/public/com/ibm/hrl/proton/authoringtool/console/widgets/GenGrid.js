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
 dojo.provide("widgets.GenGrid");
dojo.require("dojox.grid.DataGrid");
dojo.require("dojox.data.CsvStore");
dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("widgets.SectionPane");
dojo.require("dojox.data.CsvStore");
dojo.require("widgets.GridHelper");
dojo.require("dojo.data.ItemFileWriteStore");
dojo.require("dojo.data.ItemFileReadStore");
dojo.declare("widgets.GenGrid",   [dijit._Widget, dijit._Templated],
		{
			widgetsInTemplate: true,
			templatePath: dojo.moduleUrl("widgets","gen_grid.html"),
			
			grid_store: null,
			grid_inputcolumns: null,
			grid_structure: null,
			_grid: null,
			gridhelper: null,
			tblName:null,
			grid_point: null,
			addButton: null,
			refreshButton: null,
			refreshFunction:"",
			refreshObject:null,
			delButton: null,
			query: null,
			noDataMessage: "None defined",
			editable: false,
			onValueSet : null,
			lastSelectedItem: null,
			tooltip: null,
			caption: 'Default caption',
			addTooltip: null,
			//if true only required columns will be displayed in the grid, if false all columns displayed.
			showRequiredOnly: false,
			//if true a cell editor will be opened upon adding a new item
			editNewItem: true,
			
		    _buttonCells: [{
		    	field: '_del',
		        name: ' ',
				value: 'del',
		        width: '20px',
		        formatter: function(){
		    		return '<img name="Delete" title="Click to delete the row" class="table-pane-del" src="/images/delete-icon-small.gif">';
		    	}
		    }],

		    _eventHandlers: null,
		    
		    _storeEventHandlers: null,
		    
    onSelect: function(selection){
	    	 var selectedItem = this._grid.selection.getSelected();
	         if (this.lastSelectedItem==null || this.lastSelectedItem != selectedItem)
	         	{
	         		this.lastSelectedItem = selectedItem;
	         	}
	    	},
    _onClick: function(event){
    	//alert("Click");
//				this._grid.onRowClick(event);
		        if (event.target.name == "Delete") {
		            this.deleteRow();
		            return;
		        }
		       
//		       this.onSelect( this._grid.selection.getSelected());
		    },
		    
  _onDblClick: function(event){
	  if(this.grid_inputcolumns[event.cellIndex].colType==="Link"||this.grid_inputcolumns[event.cellIndex].colType==="LongLink" ){
		  var dblClickMethod=this.grid_inputcolumns[event.cellIndex].dblClickMethod;
		  if(dblClickMethod){
			  var colMetaObj=this.grid_inputcolumns[event.cellIndex].colMetaObj;
			  dojo.hitch(colMetaObj, dblClickMethod, event.rowIndex)();
		  }
	  }
    			//alert(event.cellIndex);
    			//alert(event.rowIndex);
    			//+ " " + this.grid_inputcolumns[event.target.cellIndex].colType);
		       //this.onSelect( this._grid.selection.getSelected());
		    },
		    
	deleteRow: function (){
		try{
			var items = this._grid.selection.getSelected();
	        if (items.length > 0){  
	        	var currSelectedIndex = this._grid.selection.selectedIndex;
//	        	this._grid.selection.setSelected(currSelectedIndex,false);
	        	this._doDelete(items[0]);
              // Iterate through the list of selected items.
              // The current item is available in the variable
              // "selectedItem" within the following function:
//              dojo.forEach(items, function(selectedItem) {
//                  if (selectedItem != null) {
//                      // Delete the item from the data store:
////                	  this.grid_store.deleteItem(selectedItem);
//                	  this._doDelete(selectedItem);
//                  } // end if
//              }); // end forEach
	        }
		}catch(err){
			console.log(err.message);
			return false;
		}		
	},
	_doDelete : function(item) {
	    	this.grid_store.deleteItem(item);
	    	var currSelectedIndex = this._grid.selection.selectedIndex;
	    	this.gridhelper.deleteAttrFromMetaData(this.metaObject, currSelectedIndex, this.delAttrMethod);
	    },
	
    makeDeleteButton: function(){
    	try{
			var delButton = "<div dojoType=\"dijit.form.Button\"><img name=\"Delete\" src=\"public/com/ibm/hrl/proton/authoringtool/";
			delButton = delButton + "images/delete.gif\" width=\"12\" height=\"12\" </div>";
//			delButton = delButton + " onClick=\"this.deleteRow\"></div>";
			
			return delButton;
    	}catch(err){
			console.log(err.message);
			return false;
		}		
	},
	 onRefresh: function(){
		 if (this.refreshFunction){
			 dojo.hitch(this.refreshObject, this.refreshFunction)();
		 }else{
	 		 this.populateGridStore();
			 this._grid.setStructure(this.grid_structure);
			 this._grid.setStore(this.grid_store);
		 }
	 },
	 onAdd: function(){
//	    	if(!this.editable){
//	    		return;
//	    	}
	    	this._grid.edit.apply(); // apply edit before creating a new item
//	    	this._grid.addRow();
	    	dojo.when(this.addItem(), dojo.hitch(this, function(newItem){
	    		this.itemAdded(newItem);
	    	}));
		},
	 
		//extention point - implements adding a new item to the table
		//must return the new item
	 addItem: function(){
	    	var newItem = this.createNewItem();
	    	this.grid_store.newItem(newItem);
	    	return newItem;
	    },
	 itemAdded: function(newItem){},
	 /**
		 * This is a default implementation which can be overridden by the caller.
		 */
	createNewItem: function(){
			var newItem = {};
			
			var attr;
			var layout = this.grid_structure;
			if(dojo.isArray(layout))
				{
					for(var i=0; i < layout.length; ++i){
						attr = layout[i];
						
						var field = null;
						if (attr.field){
							field = attr.field; 
						} else if (attr.name) {
							field = attr.name;
						}
						if (field) {
							if (typeof attr.defaultValue != 'undefined') {
								newItem[field] = attr.defaultValue;
							} else {
								newItem[field] = " ";
							}
						}
					}
				}
			return newItem;
		},
		onShowRequired: function(){
			if(this.showRequiredOnly==false)
				{
					var columnsArray=this.grid_structure;
					var newcolumns=[];
					for(var i=0; i < columnsArray.length; ++i){
						
						if(columnsArray[i].required==true)
							{
								newcolumns.push(columnsArray[i]);
							}
					}
					this._grid.setStructure(newcolumns);
					this.showRequiredOnly=true;
					this.showReqBtn.set('label', 'Show all columns');
				}
			else{
				this._grid.setStructure(this.grid_structure);
				this.showRequiredOnly=false;
				this.showReqBtn.set('label', 'Show Required Only');
			}
		},
		/**
		 * Sends the updated item to the metadata.
		 * @param item: the dojo data store item that was updated
		 * @param attribute: a string containing the name of the attribute that was updated.
		 * @param oldValue: a string containing the old value.
		 * @param newValue: a string containing the new value.
		 */
		updateItem: function(item, attribute, oldValue, newValue) {
			if (oldValue == newValue) 
				return;
			else
				{
					this.updateMetadata(item , attribute, newValue);
				}
			
			
		},
		/**
		 * updates new value in the metadata.
		 * @param curItem: the dojo data store item that was updated
		 * @param attribute: a string containing the name of the attribute that was updated.
		 * @param newValue: a string containing the new value.
		 * @param this.grid_inputcolumns: array of store input columns.
		 */
		updateMetadata: function(curItem, attribute, newValue ){
			var currSelectedItem = this._grid.selection.selectedIndex;
			var columnsArray=this.grid_inputcolumns;
			var currMetaObject;
			var curColumnItem;
			var baseMetaParamArray;
			for(var i=0; i < columnsArray.length; ++i){
				if(columnsArray[i].field==attribute)
					{
						currMetaObject=columnsArray[i].colMetaObj;
						curColumnItem= columnsArray[i];
					}
			}
			if(curColumnItem.method_params==null)
				{
					dojo.hitch(currMetaObject, curColumnItem.setObj, currSelectedItem, newValue)();
				}
			else{//method has more parameters - currently used by derived events 
				baseMetaParamArray=curColumnItem.method_params;
				//for one dimension array (string)
				var derivedEventIndex=baseMetaParamArray;
				dojo.hitch(currMetaObject, curColumnItem.setObj, derivedEventIndex, currSelectedItem, newValue)();
			}
		},
		populateGridStore: function(){
			
			try{
				var datastore=this.gridhelper.loadData(this.metaObject, this.metaAttrSize,  this.grid_inputcolumns);
				var mystore = new dojo.data.ItemFileWriteStore({
			        data: datastore
			    });
				this.grid_store=mystore;
		    	 dojo.connect(this.grid_store, "onSet",this,this.updateItem);
			}catch(err){
					console.log(err.message);
				}	
		},
	
	 constructor: function(args) {
		 try{
			 	this.grid_structure=null;
				dojo.mixin(this, args);
				this._eventHandlers = [];
		    	this.myObject=ATVars.CURRENT_OBJECT;
		    	this.gridhelper= new widgets.GridHelper();
		    
		 }catch(err){
				console.log(err.message);
			}		
	     },

	startup: function(){
		try{
			this.inherited(arguments);
			
			this.tableName.textContent = this.tblName;
			if(this.noShowRequiredButton){
				dojo.style(this.showReqBtn.domNode, {visibility:'hidden'});
			}else{
				this.showReqBtn.set('label', 'Show Required Only');
			}
			var addbtn = this.addbtn_grid.domNode;
			if(this.addButton==null || this.addButton==true)
				dojo.style(addbtn, {visibility:'visible'});
			else
				dojo.style(addbtn, {visibility:'hidden'});
			var refbtn = this.refreshbtn.domNode;
			if(this.refreshButton) {
				dojo.style(refbtn, {visibility:'visible'});
			} else {
				dojo.style(refbtn, {visibility:'hidden'});
			}
			
			// remove header row if it's empty
			if (!(this.tblName || this.refreshButton || this.addButton || this.addButton == null || !this.noShowRequiredButton)) {
				this.table.style.display = 'none';
			}
			this._grid.startup();
		}catch(err){
			console.log(err.message);
		}		
	},
	 // postCreate is a predefined event
    // handler that is executed when widget
    // is created and initialized
	     postCreate: function(){
	     try{
	    	 
	    	 this.grid_structure = this.grid_inputcolumns;
	    	 this.populateGridStore();
	    	 
	    	 if(this.hasDel==true){
	    		 
	    		this.delButton={ field: "_del", width: "20px", name: " ", editable: false, required:true, formatter: 
						dojo.hitch(this, this.makeDeleteButton)  } ;
	    		 if(dojo.isArray( this.grid_structure))
	    		 {
	    			 this.grid_structure.push(this.delButton);
	    		 }
	    	 }

//	    	 var view1 = {
//	    			 rows: [
//	    			        this.grid_inputcolumns
//	                    ]
//	    	 };
//	    
//             this.grid_structure = [
//                              view1
//                          ];
	    	
	    	 this._grid=new dojox.grid.DataGrid({
				 
				    store: this.grid_store,
				    structure:  this.grid_structure,
//				    query: {name: '*'},
				    singleClickEdit: true,
//				    clientSort:false,
//				    autoHeight:true,
				    height:'150px',
				    canSort:false
//				    onComplete: gotAll,
//	                onError: fetchFailed,
//				    onCellClick: dojo.hitch(this, this._onClick)
				},this.inputTable);
//	    	 this._eventHandlers.push(dojo.connect(this._grid, "onCellDblClick", this, this._onDblClick));
	    	 dojo.connect(this._grid, "onCellClick", this, this._onClick);
	    	 dojo.connect(this._grid, "onCellDblClick", this, this._onDblClick);
	    	 
	     }catch(err){
				console.log(err.message);
			}		
	     }, 
	resize: function(changeSize, resultSize) {
//		if (changeSize) {
//			changeSize.h -= 45;
//		}
		this._grid.resize(changeSize, resultSize);
		this._grid.update();
	},
	
	setColumns: function(columns) {
		for (var i = 0; i < columns.length; i++) {
			item = columns[i];
			this._grid.setCellWidth(i, item.unitWidth || item.width);
		}
		this._grid.update();
	},
	
	getStructure: function() {
		var result = this._grid.structure;
		for (var i = 0; i < result.length; i++) {
			result[i].unitWidth = this._grid.getCell(i).unitWidth;
		}
		return result;
	}
});
