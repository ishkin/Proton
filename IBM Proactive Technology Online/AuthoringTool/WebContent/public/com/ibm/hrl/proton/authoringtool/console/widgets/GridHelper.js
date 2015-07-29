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
 /**
 * 
 */

dojo.provide("widgets.GridHelper");
dojo.declare("widgets.GridHelper",null,{
	
	constructor:function(){
	},
	//setColumn method updates tblColumns data structure which hols all table structure
	//colName - column Label
	//colType  -  column type : String, Number, Selection, CheckBox, icon, link, Expression (free text box)
	//colEditMode - Column value is read only or editable
	//colAdvMode - advanced Column Type( Column requered =true, optional=false)
	//colOptionList - if it's selection type column this array holds option value list.
	//method_setColumnData- Set column data method name to update column data values in metadata
	//method_getColumnData- Set column data method name to retrieve column data values from metadata
	//colmetaObject - a column specific object, in case this column refer to a different object than the general table object (the get/set methods would be activated on it)
	//paramsArray -  if setColumnData and getColumnData require more then one parameter then this attribute will be used ( by default null);
	setColumn : function(colName, colType, colEditMode, colAdvMode, colOptionList, method_setColumnData, method_getColumnData, colmetaObject, paramsArray, dblClickMethod ) {
		var curColumn;
		switch(colType){
			case  "String": 
				curColumn={field: colName, width: "150px", name: colName, editable: colEditMode, required:colAdvMode,  setObj: method_setColumnData, getObj: method_getColumnData, colMetaObj: colmetaObject, method_params: paramsArray, colType: colType };
				return curColumn;
				break;
			case  "Integer":
				curColumn={field: colName, width: "150px", name: colName, editable: colEditMode, required:colAdvMode, setObj: method_setColumnData, getObj: method_getColumnData, colMetaObj: colmetaObject, method_params: paramsArray, colType: colType};
				return curColumn;
				break;
			case  "Double":
				curColumn={field: colName, width: "150px", name: colName, editable: colEditMode, required:colAdvMode, setObj: method_setColumnData, getObj: method_getColumnData, colMetaObj: colmetaObject, method_params: paramsArray, colType: colType};
				return curColumn;
				break;
			case  "Boolean":
				curColumn={field: colName, width: "50px", name: colName, editable: colEditMode, required:colAdvMode,  type: dojox.grid.cells.Bool, setObj: method_setColumnData, getObj: method_getColumnData, colMetaObj: colmetaObject, method_params: paramsArray, colType: colType};
				return curColumn;
				break;
			case  "DateTime":
				curColumn={field: colName, width: "50px", name: colName, editable: colEditMode, required:colAdvMode,  type: dojox.grid.cells.Bool, setObj: method_setColumnData, getObj: method_getColumnData, colMetaObj: colmetaObject, method_params: paramsArray, colType: colType};
				return curColumn;
				break;
			case  "Selection":
				curColumn={field: colName, width: "100px", name: colName, editable: colEditMode, required:colAdvMode,
				    type: dojox.grid.cells.Select, options: colOptionList, setObj: method_setColumnData, getObj: method_getColumnData, colMetaObj: colmetaObject, method_params: paramsArray, colType: colType};
				return curColumn;
				break;
			case  "CheckBox":
				curColumn={field: colName, width: "50px", name: colName, editable: colEditMode, required:colAdvMode,
				    type: dojox.grid.cells.Bool, formatter: function(value) { 
				    											console.log(value);
				    											return value;
				    										}, 
				    setObj: method_setColumnData, getObj: method_getColumnData, colMetaObj: colmetaObject, method_params: paramsArray, colType: colType};
				return curColumn;
				break;
			case  "Expression":
				curColumn={field: colName, width: "150px", name: colName, editable: colEditMode, required:colAdvMode, setObj: method_setColumnData, getObj: method_getColumnData, colMetaObj: colmetaObject, method_params: paramsArray, colType: colType};
				return curColumn;
				break;
			case  "Object":
				curColumn={field: colName, width: "150px", name: colName, editable: colEditMode, required:colAdvMode, setObj: method_setColumnData, getObj: method_getColumnData, colMetaObj: colmetaObject, method_params: paramsArray, colType: colType};
				return curColumn;
				break;
			case  "Icon":
				curColumn={field: colName, width: "20px", name: " ", formatter: this.formatIcon,  editable: colEditMode, required:colAdvMode, setObj: method_setColumnData, getObj: method_getColumnData, colMetaObj: colmetaObject, method_params: paramsArray, colType: colType};
				return curColumn;
				break;
			case  "Link":
				curColumn={field: colName, width: "150px", name: colName, editable: colEditMode, required:colAdvMode, setObj: method_setColumnData, getObj: method_getColumnData, colMetaObj: colmetaObject, method_params: paramsArray, colType: colType, dblClickMethod: dblClickMethod,styles: 'color: blue; text-decoration:underline;'};
				return curColumn;
				break;
			case  "LongLink":
				curColumn={field: colName, width: "600px", name: colName, editable: colEditMode, required:colAdvMode, setObj: method_setColumnData, getObj: method_getColumnData, colMetaObj: colmetaObject, method_params: paramsArray, colType: colType, dblClickMethod: dblClickMethod,styles: 'color: blue; text-decoration:underline;'};
				return curColumn;
				break;
				
			default: break;
		}
	},
	// Loading Data from metadata according to metadata object type.Object type is passed in constructor into myObject varable
	// Metadata type is defined in project dependent json file.
	loadData : function(curObject, attrSizeMethod, columnsArray){

		var odata=[];
		var curRow={};
		try{
			var identifier=columnsArray[0].field;
			var attributecount=dojo.hitch(curObject, attrSizeMethod )();
	//		dojo.hitch(myObj, "method", "baz")
			for (var i= 0, len=attributecount; i<len; i++) {
				curRow={};
				for(var j=0; j<columnsArray.length; j++){
					if(columnsArray[j].method_params==null)
						{
							curRow[columnsArray[j].field] = dojo.hitch(columnsArray[j].colMetaObj, columnsArray[j].getObj, i)() ;
						}
					else{
						curRow[columnsArray[j].field] = this.buildcomplexRow(columnsArray[j].colMetaObj, columnsArray[j].getObj,columnsArray[j].method_params, i) ;
					}
				}
				odata.push(curRow);
			}
			var datastore={
//							identifier: identifier,
							label: identifier,
							items: odata
							};
			return datastore;
		}catch(err){
			console.log(err.message);
		}	
	},
	buildcomplexRow: function(currObject, currMethod, paramsArray, index){
		
		var field=null;
		if(typeof(paramsArray)=='string'){
			{field= dojo.hitch(currObject, currMethod,paramsArray, index)() ;}
		}else{
			if (paramsArray.length==1)
				{field= dojo.hitch(currObject, currMethod,paramsArray[0], index)() ;}
			else if (paramsArray.length==2)
				{field= dojo.hitch(currObject, currMethod,paramsArray[0],paramsArray[1], index)() ;}
			else if (paramsArray.length==3)
				{field= dojo.hitch(currObject, currMethod,paramsArray[0],paramsArray[1],paramsArray[2], index)() ;}
			else if (paramsArray.length==4)
			{field= dojo.hitch(currObject, currMethod,paramsArray[0],paramsArray[1],paramsArray[2],paramsArray[3], index)() ;}
		}
		return field;
		
	},

	updateDataStructire: function(curWidget,oldColumns, newColumns ,curObject, attrSizeMethod){
		var updatedColumns = [];
		if(oldColumns){
			for (var i= 0, end= oldColumns.length-1; i<end; i++) {
				updatedColumns.push(oldColumns[i]);
			}
			dojo.forEach(newColumns, function(x){
				updatedColumns.push(x);
			});
			var delButton=oldColumns[oldColumns.length-1];
			updatedColumns.push(delButton);
		}else{
			updatedColumns=newColumns;
			updatedColumns.push(curWidget.delButton);
		}
		var datastore=this.loadData(curObject, attrSizeMethod,  updatedColumns);
		var mystore = new dojo.data.ItemFileWriteStore({
	        data: datastore
	    });
		
		curWidget._grid.setStructure(updatedColumns);
		curWidget._grid.setStore(mystore);
		curWidget.grid_store = mystore;
		curWidget._grid.setStructure(updatedColumns);
		curWidget.showRequiredOnly=false;
		curWidget.showReqBtn.set('label', 'Show Required Only');
		curWidget.grid_inputcolumns=updatedColumns;
		curWidget.grid_structure=updatedColumns;
		
		 dojo.connect(mystore, "onSet",curWidget,curWidget.updateItem);
		
	},
	deleteAttrFromMetaData: function(curObject, delItem, delAttrMethod){
		
		try{
			if (!isNaN(delItem))
				{
				dojo.hitch(curObject, delAttrMethod, delItem)() ;
				}
		}catch(err){
			console.log(err.message);
		}	
		
		
	},
	formatIcon : function(value){
        switch(value){
            case "WARNING" :
                return "<img src=\"public/com/ibm/hrl/proton/authoringtool/images/warn_tbl.gif\" alt=\"Warning\"  width=\"14\" height=\"14\">";
            case "ERROR" :
                return "<img src=\"public/com/ibm/hrl/proton/authoringtool/images/error_tbl.gif\" alt=\"Error\"  width=\"14\" height=\"14\">";
        }
    }

});
