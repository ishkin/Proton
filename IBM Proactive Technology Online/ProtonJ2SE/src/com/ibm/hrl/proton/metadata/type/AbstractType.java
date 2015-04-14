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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ibm.hrl.proton.metadata.epa.basic.IFieldMeta;
import com.ibm.hrl.proton.metadata.type.enums.ChrononEnum;
import com.ibm.hrl.proton.metadata.type.interfaces.IBasicType;

public abstract class AbstractType implements IBasicType {
	private static final long serialVersionUID = 1L;
	private static final ChrononEnum DEFAULT_CHRONON = ChrononEnum.MILISECOND;
	protected String name;
	protected ChrononEnum chronon; // evaluate
	protected List<TypeRelation> typeRelations;
	protected TypeAttributeSet typeAttributes;
	
	
	public AbstractType()
	{
		this(new ArrayList<TypeAttribute>());
	}
	
	public AbstractType(String name)
	{
		this();
		this.name=name;
	}
	
	public AbstractType(String name,List<TypeAttribute> payload)
	{
	    this(payload);
	    this.name = name;
	}

	public AbstractType(List<TypeAttribute> payload) {		
		typeAttributes = new TypeAttributeSet(getHeaderAttributes());
		typeAttributes.addAllAttributes(payload);
	}
	
	public void setAttributes(List<TypeAttribute> payload)
	{
		typeAttributes.addAllAttributes(payload);
	}
	
	/**
	 * Get the appropriate header for this type
	 * @return
	 */
	public abstract List<TypeAttribute> getHeaderAttributes();
	
	public void addAttribute(TypeAttribute att)
	{
		typeAttributes.addAttribute(att);
	}
	
	@Override
	public String getTypeName() {
		return name;
	}

	public ChrononEnum getChronon()
	{
		return chronon;
	}
	
	@Override
	public List<TypeRelation> getTypeRelations() {
		// TODO Auto-generated method stub
		return typeRelations;
	}

	@Override
	public Collection<TypeAttribute> getTypeAttributes() {
		// TODO Auto-generated method stub
		return typeAttributes.getAllAttributes();
	}

	public TypeAttributeSet getTypeAttributeSet()
	{
		return typeAttributes;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
	    return "Type name: "+getTypeName()+", attributes: "+getTypeAttributeSet();
	}
	

	@Override
	public IFieldMeta getFieldMetaData(String fieldName) {
		// TODO Auto-generated method stub
		return typeAttributes.getAttribute(fieldName);
	}

	@Override
	public Collection<? extends IFieldMeta> getFieldsMetaData() {
		// TODO Auto-generated method stub
		return getTypeAttributes();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return getTypeName();
	}

	
	
}
