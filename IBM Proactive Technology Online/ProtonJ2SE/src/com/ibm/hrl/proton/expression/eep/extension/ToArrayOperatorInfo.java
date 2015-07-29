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

import com.ibm.eep.Element;
import com.ibm.eep.operator.view.IOperatorArgumentInfo;
import com.ibm.eep.operator.view.IOperatorInfo;
import com.ibm.eep.operator.view.OperatorArgumentInfo;

/**
 * <code>ToArrayInfo</code>.
 * 
 * 
 */
public class ToArrayOperatorInfo implements IOperatorInfo
{

    public final static String   DESCRIPTION      = "Takes an array of event instances, takes an attribute name, and returns an array of values of this attribute.";
    public final static String[] CATEGORIES       = new String[] { "ArrayOperators"};
    public final static String   OP_NAME          = "ToArray";
    public final static String   RETURN_TYPE      = Element.NATIVE_OBJECT_ARRAY;
    public final static String[] ARG_NAMES        = new String[] { "eventInstanceArray", "attributeName"};
    public final static String[] ARG_DESCRIPTIONS = new String[] { "The event instance array", "The attribute name"};
    
    public final static String[] ARG_TYPES        = new String[] { Element.NATIVE_OBJECT_ARRAY, Element.NATIVE_STRING};
    public final static int[]    ARG_DIMENSION    = new int[] {};



    /* (non-Javadoc)
     * @see com.ibm.eep.operator.view.IOperatorInfo#getOperatorArgsNumber()
     */
    @Override
    public int getOperatorArgsNumber()
    {
        return ARG_NAMES.length;

    }

    /* (non-Javadoc)
     * @see com.ibm.eep.operator.view.IOperatorInfo#getOperatorArguments()
     */
    @Override
    public IOperatorArgumentInfo[] getOperatorArguments()
    {
        OperatorArgumentInfo[] info = new OperatorArgumentInfo[getOperatorArgsNumber()];
        for (int i = 0; i < info.length; i++) {
          info[i] = new OperatorArgumentInfo(ARG_NAMES[i], ARG_DESCRIPTIONS[i], ARG_TYPES[i], ARG_DIMENSION[i]);
        }
        return info;

    }

    /* (non-Javadoc)
     * @see com.ibm.eep.operator.view.IOperatorInfo#getOperatorCategories()
     */
    @Override
    public String[] getOperatorCategories()
    {
        return CATEGORIES;

    }

    /* (non-Javadoc)
     * @see com.ibm.eep.operator.view.IOperatorInfo#getOperatorDescription()
     */
    @Override
    public String getOperatorDescription()
    {
        return DESCRIPTION;

    }

    /* (non-Javadoc)
     * @see com.ibm.eep.operator.view.IOperatorInfo#getOperatorEllipsis()
     */
    @Override
    public boolean getOperatorEllipsis()
    {
        
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.eep.operator.view.IOperatorInfo#getOperatorName()
     */
    @Override
    public String getOperatorName()
    {
        return OP_NAME;
    }

    /* (non-Javadoc)
     * @see com.ibm.eep.operator.view.IOperatorInfo#getReturnType()
     */
    @Override
    public String getReturnType()
    {
        return RETURN_TYPE;

    }

}
