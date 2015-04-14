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
package com.ibm.hrl.proton.utilities.asynchronousWork;

/**
 * Interface for work manager - utility for creating and running asynchronous tasks
* <code>IWorkManager</code>.
* 
*
 */
public interface IWorkManager
{
    /**
     * Create async task from the given work item
     * @param work
     * @return
     */
    public Runnable createWork(IWorkItem work);
    
    /**
     * Run the async task in a separate thread
     * @param work
     * @throws AsynchronousExecutionException
     */
    public void runWork(Runnable work) throws AsynchronousExecutionException;
}
