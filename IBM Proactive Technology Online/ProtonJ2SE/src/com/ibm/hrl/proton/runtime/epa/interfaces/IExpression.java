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
package com.ibm.hrl.proton.runtime.epa.interfaces;

import java.io.Serializable;
import java.util.List;

import com.ibm.eep.Eep;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObjectMeta;

public interface IExpression  extends Serializable{

	public Object evaluate(IDataObject dataInstance);
	public Object evaluate(List<? extends IDataObject> dataInstances);
	public boolean isComplexExpression(IDataObjectMeta variableType);
	public String getStringExpression();
	public List<IDataObjectMeta> getSignature();
	public List<String> getSignatureNames();
	public Eep getEep();
}
