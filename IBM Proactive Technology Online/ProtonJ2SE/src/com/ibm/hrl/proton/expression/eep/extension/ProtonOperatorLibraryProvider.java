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

import com.ibm.eep.IOperatorLibraryProvider;

/**
 * <code>ProtonOperatorLibraryProvider</code>.
 * 
 * 
 */
public class ProtonOperatorLibraryProvider implements IOperatorLibraryProvider
{

    private final static String[] OPERATORS = new String[] { "com.ibm.hrl.proton.expression.eep.extension.ToArrayOperator"};

    /* (non-Javadoc)
     * @see com.ibm.eep.IOperatorLibraryProvider#getOperatorClass(java.lang.String)
     */
    @Override
    public Class getOperatorClass(String operatorName)
        throws ClassNotFoundException
    {
        return getClass().getClassLoader().loadClass(operatorName);
    }

    /* (non-Javadoc)
     * @see com.ibm.eep.IOperatorLibraryProvider#getOperatorInfoClass(java.lang.String)
     */
    @Override
    public Class getOperatorInfoClass(String operatorName)
        throws ClassNotFoundException
    {
        String clazz = new String (operatorName+"Info");
        return getClass().getClassLoader().loadClass(clazz);

    }

    /* (non-Javadoc)
     * @see com.ibm.eep.IOperatorLibraryProvider#getOperatorsMap()
     */
    @Override
    public String[] getOperatorsMap()
    {
        return OPERATORS;
    }

}
