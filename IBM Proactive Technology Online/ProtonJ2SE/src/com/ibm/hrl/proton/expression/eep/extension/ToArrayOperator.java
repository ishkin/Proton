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

package com.ibm.hrl.proton.expression.eep.extension;

import java.util.ArrayList;

import com.ibm.eep.Element;
import com.ibm.eep.exceptions.EepRetriableException;
import com.ibm.eep.exceptions.IncompatibleTypeException;
import com.ibm.eep.operand.ArrayOperand;
import com.ibm.eep.operator.AbstractArrayOperator;
import com.ibm.eep.util.EepUtil;
import com.ibm.eep.util.ErrorMessages;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

/**
 * <code>ToArray</code>.
 * 
 *  Class : ArrayFlattenAttributes
 * 
 * Description : Operator ToArrayOperator(arrayOperand, value).
 *               The operator goes over the values in the arrayOperand,extract
 *               the value of the attribute specified by the value String object,
 *               returns an array of values of this attribute.
 * 
 *               Operands : - arrayOperand  is array.
 *                          - value the value of String type.
 *               Return value of array of Objects type.
 */
public class ToArrayOperator extends AbstractArrayOperator
{
    public static final long serialVersionUID = 0;


    public final static String TO_ARRAY = "ToArray";

    private final static int TWO_OPERANDS = 2;

    /**
     * Constructor for ArrayContains.
     */
    public ToArrayOperator() {
        super();
    }
    
    /**
     * Returns this operator's name.
     *
     * @return      this operator's name
     */
    public String getName()
    {    
        return TO_ARRAY;
    }
    
    /**
     * Returns this operator's return type.
     *
     * @return      this operator's return type.
     */
   /* public String getReturnType()
    {
        return NATIVE_OBJECT_ARRAY;
    }*/
    /* (non-Javadoc)
     * @see com.ibm.eep.operator.AbstractOperator#calculateValue()
     */
    @Override
    protected void calculateValue()
        throws IncompatibleTypeException, EepRetriableException
    {
        try{
            
            if (operands[0] == null
                || operands[1] == null) {
                value = null;
                return;
            }
            
            Object[] arrayOperand = getArrayValue(operands[0]);     
            Object valueOperand = null;
            if (operands[1] instanceof Element) {
                valueOperand = ((Element)operands[1]).getValue();
            } else {
               
                if (operands[1] instanceof String) {
                    valueOperand = EepUtil.getStringValue(operands[1]);
                }                 
            }
    
            value = createArray(arrayOperand[0], valueOperand);
            
            return;
        }catch (EepRetriableException ere){
            throw ere;
        }       
        catch(Exception e)
        {
            throw new IncompatibleTypeException(ErrorMessages.RUNTIME_ERROR(getName(),e.getMessage()));
        }

    }
    
    
    private ArrayList createArray( Object array, Object val ) 
    {
        boolean contains = false;
        
        if (array == null 
            || val == null) {
            return null;
        }
        
        /* The array operand should be represented by ArrayList. Otherwise,
         * we will rich this line only if the array collection does not
         * contains the specified value. */
        if ((! (array instanceof ArrayList)) && (! (val instanceof String))) {
            return null;
        }
        
        
        
        ArrayList inputArray = (ArrayList) array;
        ArrayList<Object> returnArray = new ArrayList<Object>();
        for (Object arrayEntry : inputArray)
        {            
            //get the value of the specified attribute
            Object attributeValue = ((IEventInstance)arrayEntry).getEventAttribute((String)val);
            //add the value to the return array
            returnArray.add(attributeValue);
        }        
                
        return returnArray; 
    }    
    /**
     * Check, whether the operator expression is valid.
     * The method overrrides the value of the superclass, since the 
     * the number of operands is restricted to be 1.
     * 
     * @return null if legal, error message - otherwise
     */
    
     public String checkLegality()
     {
        String arrayEntryType = null, valueType = null;

        /* The operator requires two operands. 
         * The first one is array and the second one is value.*/
        if (operands == null
            || operands.length != TWO_OPERANDS
            || operands[0] == null
            || operands[1] == null) {
            return ErrorMessages.ILLEGAL_OPERANDS_NUMBER_ERROR(getName());
        }

        /* Check the validity of the first array Operand. */        
        if (operands[0] instanceof Element) {
            String errorMsg = ((Element)operands[0]).checkLegality();
            if (errorMsg != null) {
                return errorMsg;
            }
            
            String returnType = ((Element)operands[0]).getReturnType();
        
            if(!returnType.equals(ArrayOperand.NATIVE_BOOLEAN_ARRAY)
                && !returnType.equals(ArrayOperand.NATIVE_DATE_ARRAY)
                && !returnType.equals(ArrayOperand.NATIVE_INT_ARRAY)
                && !returnType.equals(ArrayOperand.NATIVE_DOUBLE_ARRAY)
                && !returnType.equals(ArrayOperand.NATIVE_OBJECT_ARRAY)
                && !returnType.equals(ArrayOperand.NATIVE_STRING_ARRAY)
                && !returnType.equals(ArrayOperand.ARRAY_OPERAND)) {
                return ErrorMessages.OPERAND_NOT_ARRAY_ERROR(getName(), returnType, 0);
            }
                
            arrayEntryType = ((ArrayOperand)(operands[0])).getEntryType();
        } else {
            if(!(operands[0] instanceof Object[])) {
                return ErrorMessages.OPERAND_NOT_ARRAY_ERROR(getName(), operands[0].getClass().getName(),0);
            }            
            
        }
        
        /* Check the validity of the second primitive type operand. */
        if(operands[1] instanceof Element){
            String errorMsg = ((Element)operands[1]).checkLegality();
            if (errorMsg != null) {
                return errorMsg;
            }
            
            /* Check, whether the array entry type is suitable with
             * the value type. */
            valueType = ((Element)operands[1]).getReturnType();
            if (! (valueType.equals(NATIVE_STRING))) {
                return ErrorMessages.INCOMPATBLE_VALUE_TYPE_ERROR(getName(), valueType, 1);
            }
        } else {
            /* Check, whether the array entry type is suitable with
             * the value type. */
            
            if (!(operands[1] instanceof String)) 
            {
                return ErrorMessages.INCOMPATBLE_VALUE_TYPE_ERROR(getName(), "String", 1);
            }            
                    
        }

        return null;
     } 

}
