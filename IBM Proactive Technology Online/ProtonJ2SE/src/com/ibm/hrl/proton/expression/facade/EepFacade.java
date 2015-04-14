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

package com.ibm.hrl.proton.expression.facade;

import java.util.List;

import com.ibm.eep.Eep;
import com.ibm.eep.IOperatorLibraryProvider;
import com.ibm.eep.exceptions.ParseException;
import com.ibm.eep.exceptions.UnloadableElementException;
import com.ibm.hrl.proton.expression.eep.EepExpression;
import com.ibm.hrl.proton.expression.eep.extension.ProtonOperatorLibraryProvider;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObjectMeta;
import com.ibm.hrl.proton.runtime.epa.interfaces.IExpression;

/**
 * <code>EepFacade</code>.
 * 
 * 
 */
public class EepFacade
{
    private Eep eep;
    private static EepFacade instance;
    
    private EepFacade() throws EEPException
    {
        try
        {
            IOperatorLibraryProvider[] operatorProvidersList = new IOperatorLibraryProvider[1];
            operatorProvidersList[0] = new ProtonOperatorLibraryProvider();
            eep = new Eep(operatorProvidersList);
        }
        catch (UnloadableElementException e)
        {
            throw new EEPException(e.getMessage());
        }
    }
    
    public synchronized static EepFacade getInstance() throws EEPException
    {
        if (null == instance)
            instance = new EepFacade();
          return instance;
    }
    
    
    
    public IExpression createExpression(String expression, List<IDataObjectMeta> objectsTypes) throws ParseException
    {
        return new EepExpression(expression,objectsTypes,eep);
    }
    
    /**
     * Create expression with possible names of the variables (for example if we have e1 with alias name e11
     * and expression "e11.x = 'a'" then one of the entries in signatureName array should be 'e11' and corresponding entry on
     * signatureType should be e1 type object)
     * @param expression
     * @param signatureType
     * @param signatureName
     * @return
     * @throws ParseException
     */
    public IExpression createExpression(String expression, List<IDataObjectMeta> signatureType, List<String> signatureName) throws ParseException
    {
        return new EepExpression(expression,signatureType,signatureName,eep);
    }
}
