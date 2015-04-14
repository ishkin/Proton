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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zoharf
 *
 */
public class DataObjectMetaData implements IDataObjectMeta{

	protected String Name;
	protected String description;
	protected int minCardinality;
	protected int maxCardinality;
	protected Map<String, FieldMetaData> fieldsMetaData;
	
	public DataObjectMetaData(Collection<FieldMetaData> fields)
	{
		fieldsMetaData = new HashMap<String, FieldMetaData>();
		for (FieldMetaData f : fields)
			fieldsMetaData.put(f.getName(), f);
	}
	@Override
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getMinCardinality() {
		return minCardinality;
	}
	public void setMinCardinality(int minCardinality) {
		this.minCardinality = minCardinality;
	}
	public int getMaxCardinality() {
		return maxCardinality;
	}
	public void setMaxCardinality(int maxCardinality) {
		this.maxCardinality = maxCardinality;
	}
	@Override
	public Collection<? extends IFieldMeta> getFieldsMetaData() {
		return fieldsMetaData.values();
	}
	public void setFieldsMetaData(Collection<FieldMetaData> fieldsMetaData) {
		for (FieldMetaData f : fieldsMetaData)
			this.fieldsMetaData.put(f.getName(), f);
	}
	@Override
	public FieldMetaData getFieldMetaData(String fieldName)
	{
		return fieldsMetaData.get(fieldName);
	}
	
}
