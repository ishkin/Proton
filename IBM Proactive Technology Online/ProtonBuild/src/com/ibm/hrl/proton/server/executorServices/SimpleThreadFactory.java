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

package com.ibm.hrl.proton.server.executorServices;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>SimpleThreadFactory</code>.
 * 
 * 
 */
public class SimpleThreadFactory implements ThreadFactory
{

	
	UncaughtExceptionHandler exceptionHandler;
    private AtomicInteger threadNumber = new AtomicInteger(1);
    private final static Logger logger = Logger.getLogger(SimpleThreadFactory.class.getName());
    
    public SimpleThreadFactory()
    {
        exceptionHandler= new ProtonExceptionHandler(logger);
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(Runnable r)
    {
        
        Thread t = new Thread(r,""+threadNumber.getAndIncrement());
        t.setUncaughtExceptionHandler(exceptionHandler);
        return t;
    }
    
    private class ProtonExceptionHandler implements Thread.UncaughtExceptionHandler {
        private Logger logger;

        private ProtonExceptionHandler(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {            
            if (logger.isLoggable(Level.SEVERE)) {
                logger.severe("Uncaught exception in thread: " + t + ",exception: "+e.getMessage());
                
            }
           e.printStackTrace();

        }

    }

}
