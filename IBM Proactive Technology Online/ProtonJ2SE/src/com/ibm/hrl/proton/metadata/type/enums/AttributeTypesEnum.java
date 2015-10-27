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
package com.ibm.hrl.proton.metadata.type.enums;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

public enum AttributeTypesEnum {
	
	INTEGER (Integer.class, Integer.valueOf(0), "integer"), 
	LONG (Long.class, Long.valueOf(0), "number"), 
	FLOAT (Float.class, Float.valueOf(0), "number"), 
	DOUBLE (Double.class, Double.valueOf(0), "double"), 
	DATE (Date.class, null, "date"), 
	STRING (String.class, "", "string"), 
	BOOLEAN (Boolean.class, Boolean.TRUE, "boolean"), 
	TIME (Time.class, null, "date"),
	OBJECT (Object.class, null, "object"),
	UUID (UUID.class, null, "string"),
	CHRONON (ChrononEnum.class, ChrononEnum.MILISECOND, "string"), // not exposed to user
	COST_UNIT(CostUnitEnum.class, CostUnitEnum.ABSOLUTE, "string"); //not exposed to user
	
	protected Class<? extends Object> typeClass;
	protected Object defaultValue;
	protected String name;
	
	AttributeTypesEnum(Class<? extends Object> theClass, Object defVal, String name)
	{
		typeClass = theClass;
		defaultValue = defVal;
		this.name = name;
	}
	
	public Class<? extends Object> getTypeClass()
	{
		return typeClass;
	}
	
	public Object getTypeDefaultValue()
	{
		return defaultValue;
	}
	
	public String getName()
	{
		return name;
	}
}
