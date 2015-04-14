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

/**
 * <code>ExecutorUtils</code>.
 * 
 * 
 * 
 */


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;




public class ExecutorUtils {


	private static Logger logger = Logger.getLogger(ExecutorUtils.class.getName());
    private static final int THREAD_POOL_SIZE = 50;
    private static final int QUEUE_BUFFER_SIZE = 50;

    protected static ExecutorServicesFactory executorServicesFactory = new DefaultExecutorServiceFactory();
     
    /**
     * @param executorServicesFactory
     *            the executorServicesFactory to set
     */
    public static void setExecutorServicesFactory(ExecutorServicesFactory executorServicesFactory) {
        ExecutorUtils.executorServicesFactory = executorServicesFactory;
    }

    private static final ThreadFactory threadFactory = new SimpleThreadFactory();

    protected static ExecutorService defaultExecutorService = executorServicesFactory.newExecutor(Integer.MAX_VALUE);
    protected static ScheduledExecutorService defaultScheduler = executorServicesFactory.newScheduledExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE);

    /**
     * Executes the given command at some time in the future. The command may
     * execute in a new thread, or in a pooled thread.
     * 
     * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
     */
    public static void execute(Runnable command) {
        defaultExecutorService.execute(command);
    }

    /**
     * 
     * @param runnable
     * @return
     */
    public static Future<?> submit(Runnable runnable) {
        return defaultExecutorService.submit(runnable);
    }

    /**
     * Creates and executes a one-shot action that becomes enabled after the
     * given delay.
     * 
     * @see java.util.concurrent.ScheduledExecutorService#schedule(java.lang.Runnable,
     *      long, java.util.concurrent.TimeUnit)
     */
    public static ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return defaultScheduler.schedule(command, delay, unit);
    }

    /**
     * Creates and executes a periodic action that becomes enabled first after
     * the given initial delay, and subsequently with the given period
     * 
     * @see java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable,
     *      long, long, java.util.concurrent.TimeUnit)
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return defaultScheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /**
     * Creates and executes a periodic action that becomes enabled first after
     * the given initial delay, and subsequently with the given delay between
     * the termination of one execution and the commencement of the next.
     * 
     * @see java.util.concurrent.ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable,
     *      long, long, java.util.concurrent.TimeUnit)
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return defaultScheduler.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    public static List<Runnable> shutdownNow() {
    	List<Runnable> awaitingExecution = new ArrayList<Runnable>();
    	awaitingExecution.addAll(defaultExecutorService.shutdownNow());
    	awaitingExecution.addAll(defaultScheduler.shutdownNow());    	
    	return awaitingExecution;
    }

    /**
     * Set the default ExecutorService to be used for task execution
     * 
     * @param executorService
     *            default ExecutorService to be used for task execution or null
     *            if internal default ExecutorService should be used
     */
    private static void setDefaultExecutorService(ExecutorService executorService) {
        if (executorService != null) {
            try {
                defaultExecutorService.shutdown();
            } catch (Exception e) {
                // do my best
                logger.warning("problem shutting down default executor service, reason: "+ e.getMessage());
            }
            defaultExecutorService = executorService;
        } else {
            defaultExecutorService = Executors.newCachedThreadPool();
        }
    }

    /**
     * Set the default ScheduledExecutorService to be used for task scheduling
     * 
     * @param scheduler
     *            default ScheduledExecutorService to be used for task
     *            scheduling or null if internal default
     *            ScheduledExecutorService should be used
     */
    private static void setDefaultScheduler(ScheduledExecutorService scheduler) {
        if (scheduler != null) {
            try {
                defaultScheduler.shutdown();
            } catch (Exception e) {
                // do my best
                logger.warning("problem shutting down default executor service, reason: "+ e.getMessage());
            }
            defaultScheduler = scheduler;
        } else {
            defaultScheduler = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
        }
    }

   


    

}

