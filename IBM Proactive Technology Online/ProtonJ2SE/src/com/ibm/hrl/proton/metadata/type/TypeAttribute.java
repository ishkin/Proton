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
package com.ibm.hrl.proton.metadata.type;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.ibm.eep.exceptions.ParseException;
import com.ibm.hrl.proton.expression.facade.EEPException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObjectMeta;
import com.ibm.hrl.proton.metadata.epa.basic.IFieldMeta;
import com.ibm.hrl.proton.metadata.event.IEventType;
import com.ibm.hrl.proton.metadata.type.enums.AttributeTypesEnum;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.runtime.event.EventInstance;

public class TypeAttribute implements Serializable, IFieldMeta{

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected static final AttributeTypesEnum DEFAULT_TYPE = AttributeTypesEnum.STRING;
	public static final int DEFAULT_DIMENSION = 0;
	public static final int HIGHEST_DIMENSION = 2;
	
	protected Object defaultValue; 
	protected String name;
	protected AttributeTypesEnum type;
	protected int dimension; // 0-single value, 1-array, 2-matrix2D, ...
	// consider adding "not used" flag for attribute
	
	public int getDimension() {
		return dimension;
	}

	public TypeAttribute(String name)
	{
		this(name, DEFAULT_TYPE);
	}
	
	public TypeAttribute(String name, AttributeTypesEnum type)
	{
		this(name, type, DEFAULT_DIMENSION, type.getTypeDefaultValue());
	}
	
	public TypeAttribute(String name, AttributeTypesEnum type, int dimension)
	{
		this(name, type, dimension, type.getTypeDefaultValue());
	}
	
	public TypeAttribute(String name, AttributeTypesEnum type,  Object defVal)
	{
		this(name,type, DEFAULT_DIMENSION,defVal);		
	}
	
	public TypeAttribute(String name, AttributeTypesEnum type, int dim, Object defVal)
	{
		this.name = name;
		this.type = type;
		this.dimension = dim;
		defaultValue = defVal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AttributeTypesEnum getTypeEnum() {
		return type;
	}

	public void setType(AttributeTypesEnum type) {
		this.type = type;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
	    return " "+name+": "+ type;
	}
	
	@Override
	public String getType()
	{
		return getTypeEnum().toString();
	}
	
	/**
	 * Parsing attribute's constant value - either for parsing the default value,
	 * or for parsing the event instance attributes
	 * @param attrValue
	 * @param objectMetadata
	 * @return
	 * @throws ParseException
	 * @throws EEPException
	 */
	public static Object parseConstantValue(String attrValue,String attrName,IDataObjectMeta objectMetadata,DateFormat dateFormatter) throws EEPException{
		return AttributeValueParser.parseConstantValue(attrValue, attrName, objectMetadata, dateFormatter);
	}
	
	/**
     * @param attrValue
     */
    /*public static Object parseDouble(String attrValue)
    {

        Object returnValue = null;
        //check if it is a simple double or a distribution
        //in case we have a "()" chars in the string this is a distribution
        int bracketsIndex = attrValue.indexOf("(");
        if (bracketsIndex == -1) //brackets not found  - simple double 
        {
            returnValue = Double.valueOf(attrValue);            
        }else
        {
            //a distribution
            String distrTypeString = attrValue.substring(0,bracketsIndex);
            if (distrTypeString.equals("N")){
                //normal distribution
                int bracketsEndIndex = attrValue.indexOf(")");
                String distParams = attrValue.substring(bracketsIndex+1,bracketsEndIndex);
                
                //find the mu and the variance
                int commaIndex = distParams.indexOf(",");
                String muStringValue = distParams.substring(0,commaIndex);
                String varianceStringValeu = distParams.substring(commaIndex+1);
                
                returnValue =  new NormalDistribution(Float.valueOf(muStringValue),Float.valueOf(varianceStringValeu));
            }
            
            //TODO: add more disributions here
        }
        
        return returnValue;
    }*/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeAttribute other = (TypeAttribute)obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	}

	
