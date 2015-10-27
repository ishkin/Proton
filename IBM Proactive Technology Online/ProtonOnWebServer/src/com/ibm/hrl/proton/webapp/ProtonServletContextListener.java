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
package com.ibm.hrl.proton.webapp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.ibm.hrl.proton.agentQueues.queuesManagement.AgentQueuesManager;
import com.ibm.hrl.proton.context.facade.ContextServiceFacade;
import com.ibm.hrl.proton.epaManager.EPAManagerFacade;
import com.ibm.hrl.proton.eventHandler.EventHandler;
import com.ibm.hrl.proton.eventHandler.IEventHandler;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.metadata.parser.ParsingException;
import com.ibm.hrl.proton.metadata.parser.ProtonParseException;
import com.ibm.hrl.proton.router.EventRouter;
import com.ibm.hrl.proton.router.IEventRouter;
import com.ibm.hrl.proton.runtime.metadata.IMetadataFacade;
import com.ibm.hrl.proton.server.adapter.InputServer;
import com.ibm.hrl.proton.server.adapter.OutputServer;
import com.ibm.hrl.proton.server.adapter.ProtonServerException;
import com.ibm.hrl.proton.server.adapter.eventHandlers.StandaloneDataSender;
import com.ibm.hrl.proton.server.executor.PropertiesParser;
import com.ibm.hrl.proton.server.executorServices.ExecutorUtils;
import com.ibm.hrl.proton.server.timerService.TimerServiceFacade;
import com.ibm.hrl.proton.server.workManager.WorkManagerFacade;
import com.ibm.hrl.proton.utilities.facadesManager.IFacadesManager;

@WebListener
public class ProtonServletContextListener implements ServletContextListener {

	

	private ExecutorService executor;		
	//private volatile boolean isRunning = false;			
	private static final Logger logger = Logger.getLogger(ProtonServletContextListener.class.getName());	 
	//private static Calendar calendar  = Calendar.getInstance();
	private static final int BACKLOG_SIZE = 1000;
	
	private OutputServer outputServer;
	private InputServer inputServer;
	IFacadesManager facadesManager;
		
	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		logger.info("shutting down ProtonServletContextListener");
		
		try {
		    // currently exceptions are thrown on stopping servers
			// they are related to the threads closing exceptions that are thrown at Proton shutdown
		    inputServer.stopServer();
		    outputServer.stopServer();
		    facadesManager.getTimerServiceFacade().destroyTimers();
		    ExecutorUtils.shutdownNow();
		    logger.info("finished stopping servers successfully");
		} catch (Exception e) {
			logger.severe("Problem stopping servers: " + e.getMessage());
			Thread.currentThread().interrupt();
		} finally {
			executor.shutdownNow();
			ExecutorUtils.shutdownNow();
		}
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		ServletContext ctx = sce.getServletContext();			
		logger.info("initializing ProtonServletContextListener");
		try {
			initialize(new String[0],sce);
		} catch (Exception e) {
		    String msg = "Could not initialize application ,reason : " + e + "message: " + e.getMessage();
		    logger.severe(msg);
		}
	}
		
	private void initialize(String[] args, ServletContextEvent sce) throws ProtonServerException {

		final String[] arguments = args;
		final ServletContext ctx = sce.getServletContext();
		executor = Executors.newCachedThreadPool();
		executor.submit(new Runnable() {
			
		@Override
		public void run() {

			inputServer = null;
			outputServer =	null;
			PropertiesParser prop = null;
			String propertiesFileRelativePath = "/Proton.properties";
			InputStream fileInpStream = ctx.getResourceAsStream(propertiesFileRelativePath);	
			String rootPath = ctx.getRealPath("/");
			facadesManager =  WebFacadesManager.getInstance();
	    	IMetadataFacade metadataFacade = WebMetadataFacade.getInstance();
			logger.info("context real path = " +rootPath);
			EepFacade eepFacade;
			
			try
			{	
				eepFacade = new EepFacade();
				((WebFacadesManager)facadesManager).setEepFacade(eepFacade);
				//check for input parameter - the properties file name.    		
				if (arguments.length != 0) {
					String propertiesFileAbsolutePath = arguments[0];
					fileInpStream =new FileInputStream(propertiesFileAbsolutePath);
				}

				prop = 	new PropertiesParser(fileInpStream);            
			    inputServer = 	new InputServer(prop.inputPortNumber,BACKLOG_SIZE,facadesManager,metadataFacade,eepFacade);
			    outputServer = 	new OutputServer(prop.outputPortNumber,BACKLOG_SIZE,facadesManager,metadataFacade,eepFacade);			   
			    logger.info("init: initializing metadata and all the system singletons");			    
			}
			catch (Exception e)
			{
			    String msg = "Could not configure application ,reason : " + e +
			    	" message: " + e.getMessage();
			    logger.severe(msg);		
			    throw new RuntimeException("Could not configure application ,reason : " + e +
				    	" message: " + e.getMessage());
			}

			try
			{		   
				String defsFileAbsolutePath = null;
				
				if (prop.metadataFilePathType.equals("relative")) {
					defsFileAbsolutePath = ctx.getRealPath(prop.metadataFileName);
				} else { // the path to the definitions file is absolute
					defsFileAbsolutePath = prop.metadataFileName;
				}
				
				//logger.info("my current location: " + System.getProperty("user.dir"));
				//logger.info("my current context location: " + ctx.getRealPath("/"));
				//logger.info("defsFileAbsolutePath: " + defsFileAbsolutePath);
				
				Collection<ProtonParseException> exceptions = initializeMetadata(defsFileAbsolutePath,eepFacade,metadataFacade);
				logger.info("init: done initializing metadata, returned the following exceptions: ");
				for (ProtonParseException protonParseException : exceptions) {
					logger.info(protonParseException.toString());
				}
				
				TimerServiceFacade timerServiceFacade = new TimerServiceFacade();
				((WebFacadesManager)facadesManager).setTimerServiceFacade(timerServiceFacade);
	            IEventHandler eventHandler = new EventHandler(facadesManager,metadataFacade);
	            ((WebFacadesManager)facadesManager).setEventHandler(eventHandler);
	            StandaloneDataSender dataSender = new StandaloneDataSender();
	            ((WebFacadesManager)facadesManager).setDataSender(dataSender);
	            IEventRouter eventRouter = new EventRouter(dataSender,facadesManager,metadataFacade);
	            ((WebFacadesManager)facadesManager).setEventRouter(eventRouter);
	            WorkManagerFacade workManagerFacade = new WorkManagerFacade();
	            ((WebFacadesManager)facadesManager).setWorkManager(workManagerFacade);
	            EPAManagerFacade epaManagerFacade = new EPAManagerFacade(workManagerFacade, eventRouter, null,metadataFacade);
	            ((WebFacadesManager)facadesManager).setEpaManager(epaManagerFacade);
	            AgentQueuesManager agentQueuesManager = new AgentQueuesManager(timerServiceFacade, eventHandler, workManagerFacade,metadataFacade);
	            ((WebFacadesManager)facadesManager).setAgentQueuesManager(agentQueuesManager);
	            ContextServiceFacade contextService = new ContextServiceFacade(timerServiceFacade, eventHandler,metadataFacade.getContextMetadataFacade());
	            ((WebFacadesManager)facadesManager).setContextServiceFacade(contextService);                    
			    		    
			    logger.info("init: done initializing singletons , starting the servers...");
			    outputServer.startServer();
			    inputServer.startServer();            
			    
			    /*
			    //wait for user's input to shutdown the server
			    System.out.print("Press any key for servers shutdown: ");

			    //  open up standard input
			    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			    String command = null;

			    // read the command from the command-line;
			    // need to use try/catch with the readLine() method
			    try {
			       command = br.readLine();
			    } catch (IOException ioe) {              
			       System.exit(1);
			    }

			    inputServer.stopServer();
			    outputServer.stopServer();
			    */		                
			}       
			catch (Exception e)
			{
			    String msg = "Could not initialize application ,reason : " + e + "message: " + e.getMessage();
			    logger.severe(msg);
			    try {
				    inputServer.stopServer();
				    outputServer.stopServer();
				    facadesManager.getTimerServiceFacade().destroyTimers();
				    ExecutorUtils.shutdownNow();				    
			    } catch (Exception e1) {
			    	logger.severe(e1.getMessage());
			    }
			    finally {
					executor.shutdownNow();
					ExecutorUtils.shutdownNow();
				}
			    
			    
			}					
			}
		});		
	}
	
	private static Collection<ProtonParseException> initializeMetadata(
			String metadataFileName, EepFacade eep,IMetadataFacade metadataFacade) throws ParsingException {
	
		String line;
		StringBuilder sb = new StringBuilder();
		BufferedReader in = null;
		String jsonFileName = metadataFileName;
		try
		{
			in = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFileName),"UTF-8"));				
			while ((line = in.readLine()) != null)
			{
				sb.append(line);
			}		
		 	} catch(Exception e) {
			 e.printStackTrace();
		 	} finally {
			 try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 }		 
		 String jsonTxt  = sb.toString();		 
		 MetadataParser metadataParser = new MetadataParser(eep,metadataFacade); 
		 Collection<ProtonParseException> exceptions = metadataParser.parseEPN(jsonTxt);

		 return exceptions;	     
	}
	
}

