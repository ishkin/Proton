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

import java.util.Map;

/**
 * @author zoharf
 *
 */
public class DataObject implements IDataObject {

	protected DataObjectMetaData metadata;
	protected Map<String, Object> fieldValues;
	
	public DataObject(){		
	}
	
	public DataObject(DataObjectMetaData metaData)
	{
		setMetadata(metaData);
	}
	@Override
	public DataObjectMetaData getMetadata() {
		return metadata;
	}

	public void setMetadata(DataObjectMetaData metadata) {
		this.metadata = metadata;
	}
	
	public Map<String, Object> getFieldValues() {
		return fieldValues;
	}
	
	//@Override
	public void setFieldValue(String fieldName, Object value)
	{
		fieldValues.put(fieldName, value);
	}
	@Override
	public Object getFieldValue(String fieldName)
	{
		return fieldValues.get(fieldName);
	}
	@Override
	public IFieldMeta getFieldMetaData(String fieldName) {
		// TODO Auto-generated method stub
		return metadata.getFieldMetaData(fieldName);
	}
}
