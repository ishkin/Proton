/*******************************************************************************
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
package com.ibm.hrl.proton.metadata.epa.basic;

import java.io.Serializable;

/**
 * @author zoharf
 *
 */
public class DataObjectField implements Serializable{

	protected DataObjectMetaData object;
	protected FieldMetaData field;
	
	public DataObjectField(DataObjectMetaData object, FieldMetaData field)
	{
		setObject(object);
		setField(field);
	}
	
	public DataObjectMetaData getObject() {
		return object;
	}
	public void setObject(DataObjectMetaData object) {
		this.object = object;
	}
	public FieldMetaData getField() {
		return field;
	}
	public void setField(FieldMetaData field) {
		this.field = field;
	}
	
	
}
