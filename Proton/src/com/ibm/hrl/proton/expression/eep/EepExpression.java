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
package com.ibm.hrl.proton.expression.eep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.ibm.eep.Eep;
import com.ibm.eep.ParsedExpression;
import com.ibm.eep.exceptions.EepEvaluationException;
import com.ibm.eep.exceptions.EepRetriableException;
import com.ibm.eep.exceptions.ElementCreationFailureException;
import com.ibm.eep.exceptions.ElementNotFoundException;
import com.ibm.eep.exceptions.IncompatibleTypeException;
import com.ibm.eep.exceptions.ParseException;
import com.ibm.eep.operand.ArrayOperand;
import com.ibm.hrl.proton.expression.exceptions.ExpressionEvaluatorException;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObjectMeta;
import com.ibm.hrl.proton.metadata.epa.basic.IFieldMeta;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;
import com.ibm.hrl.proton.utilities.constants.ProtonConstants;

public class EepExpression implements IExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//TODO need to revisit this object after the tool expression is defined
	protected Eep eep;
	protected String stringExpression;
	protected ParsedExpression parsedExpression;
	protected HashMap<String, Integer> variableEventIndices = new HashMap<String, Integer>();
	protected HashMap<String, String> variableAttribute = new HashMap<String, String>();
	List<IDataObjectMeta> signature;
	List<String> signatureNames;
	
	Logger logger = Logger.getLogger(getClass().getName());
	 
	
	public EepExpression(String expression, List<IDataObjectMeta> signature, Eep eep) throws ParseException
	{
		this(expression, signature, extractTypeNames(signature), eep);
		
	}
	
	
	
	public EepExpression(String expression, List<IDataObjectMeta> signatureType, List<String> signatureName, Eep eep) throws ParseException
	{
		this.signature = new ArrayList<IDataObjectMeta>(signatureType);
		this.signatureNames = new ArrayList<String>(signatureName);
		
		stringExpression = expression;
		// may need to parse from tool string
		// from this point it is assumed that stringExpression variable will be eventTpe.attributeName
		this.eep = eep;
		if (eep == null) {
			throw new com.ibm.eep.exceptions.ParseException(
				"Static Eep object wasn't initialized. Can't parse expressions.");
		}
		parsedExpression = eep.parseExpression(expression);
		HashMap<String, Integer> indices = new HashMap<String, Integer>();
		int i = 0;
		for (String ot : signatureName)
			indices.put(ot, i++);
		try {
			reviewExpressionVariables(signatureType, indices);
		} catch (ElementNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ElementCreationFailureException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IncompatibleTypeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String	errorMsg = parsedExpression.checkLegality();
			if (errorMsg != null) {
				// throw error
			}
		} catch (IncompatibleTypeException e) {
			// throw error
		}
	}
	
	
	public String getStringExpression() {
		return stringExpression;
	}



	public List<IDataObjectMeta> getSignature() {
		return signature;
	}



	public List<String> getSignatureNames() {
		return signatureNames;
	}
	
	protected static List<String> extractTypeNames(List<IDataObjectMeta> types)
	{
		ArrayList<String> names = new ArrayList<String>(types.size());
		for (IDataObjectMeta type : types)
			names.add(type.getName());
		
		return names;
	}
	
	@Override
	public synchronized Object evaluate(IDataObject eventInstance) {
		// TODO Auto-generated method stub
		ArrayList<IDataObject> eventList = new ArrayList<IDataObject>();
		eventList.add(eventInstance);
		return evaluate(eventList);
	}

	@Override
	public synchronized Object evaluate(List<? extends IDataObject> dataObjects) {
		// TODO Auto-generated method stub
		for (Entry<String, Integer> varDOIndex : variableEventIndices.entrySet())
		{
			IDataObject obj = dataObjects.get(varDOIndex.getValue());
			IFieldMeta objFieldMeta = obj.getFieldMetaData(variableAttribute.get(varDOIndex.getKey()));
			Object objFieldValue = obj.getFieldValue(variableAttribute.get(varDOIndex.getKey()));
			String attrType = objFieldMeta.getType();
			
			/*if (attrType.equals(AttributeTypesEnum.DATE.toString()))
			{
				attrType = "Date";
			}*/
	        /*if (obj instanceof IEventInstance){
	                if (((IEventInstance)obj).getEventTypeName().equals("ETA"))
	                {
	                    logger.info("Expression: "+this+", object: "+obj+", fieldMeta"+ objFieldMeta+", fieldValue:"+objFieldValue+", attrType:"+attrType);	                    
	                }
	            }*/

			try {
				
					if (objFieldMeta.getDimension() != 0)
					{
						parsedExpression.setArrayVarType(
								varDOIndex.getKey(),
										ArrayOperand.ARRAY_OPERAND,
										attrType);
					} else 
					{
						parsedExpression.setVarType(varDOIndex.getKey(), attrType);
					}
					
				
					
				parsedExpression.setVarValue(varDOIndex.getKey(), objFieldValue);
				
			} catch (ElementNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ExpressionEvaluatorException(e);
			} catch (IncompatibleTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ExpressionEvaluatorException(e);
			} catch (EepRetriableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ExpressionEvaluatorException(e);
			} catch (ElementCreationFailureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ExpressionEvaluatorException(e);
			}
		}
		
		try {
			//if the returned expression is Date turn it into long , 
			//since all Dates represented as long in the system
			Object result = parsedExpression.getValue();
			if (result instanceof java.util.Date)
			{
				return ((java.util.Date) result).getTime();
			}else{
				return  result;
			}
				 			
		} catch (EepEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ExpressionEvaluatorException(e);
		} catch (EepRetriableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ExpressionEvaluatorException(e);
		}
				
	}

	protected void reviewExpressionVariables(List<IDataObjectMeta> objectTypes, HashMap<String, Integer> indices) throws ElementNotFoundException, ElementCreationFailureException, IncompatibleTypeException
	{
		Iterator<String> iter = parsedExpression.getTypelessVarNames();
		while (iter.hasNext()) {
			String variable = (String) iter.next();
			String eventTypeName = getVariableEventType(variable);
			int index = indices.get(eventTypeName);
			variableEventIndices.put(variable, index);
			String attributeName = getVariableAttribute(variable);
			variableAttribute.put(variable, attributeName);
			IDataObjectMeta objectType = objectTypes.get(index);
			//EventAttributeTypesEnum attType = objectType.getEventTypeAttributeSet().getAttribute(attributeName).getType();
			int attDimension = objectType.getFieldMetaData(attributeName).getDimension();
			String attrType = objectType.getFieldMetaData(attributeName).getType();
			// the following part was moved to evaluate()
			//if (attDimension > 0)
			//{
			//	// assume ==1 (array) for now
			//	parsedExpression.setArrayVarType(variable, ArrayOperand.ARRAY_OPERAND, attrType);
			//}
			//else
			//	parsedExpression.setVarType(variable, attrType);
		 }
	}
	
	public Iterator<String> getExpressionVariables()
	{
		return parsedExpression.getTypelessVarNames();
	}
	/**
	 * Checks whether the expression involves more than one variable type and the types
	 * are different than the one specified by the parameter
	 * @return <b>true</b> if the expression invovles more than one variable type 
	 * 		   <b>false</b> otherwise
	 */
	public boolean isComplexExpression(IDataObjectMeta variableType){
		String variableTypeName = variableType.getName();
		
		Iterator<String> iter = parsedExpression.getTypelessVarNames();
		//iterate over all the variables in the expression, if a variable
		//is not of the specified type than it is a complex expression - we return with  "true"
		while (iter.hasNext()) 
		{
			String variable = (String) iter.next();
			String eventTypeName = getVariableEventType(variable);
			if (!eventTypeName.equals(variableTypeName)) return true;
		}
		
		return false;
	}
	
	protected String getVariableEventType(String varName)
	{
		int separator = varName.indexOf(ProtonConstants.SEPARATOR);
		if (separator != -1) 
			;//TODO illegal - need to raise exception
			
		return varName.substring(0, separator);
	}
	
	protected String getVariableAttribute(String varName)
	{
		int separator = varName.indexOf(ProtonConstants.SEPARATOR);
		if (separator != -1) 
			;//TODO illegal - need to raise exception
		return varName.substring(separator + 1);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
	   return "Expression: "+stringExpression;
	}
}
