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
package com.ibm.hrl.proton.admin.webapp;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.ibm.hrl.proton.admin.webapp.exceptions.ResponseException;
import com.ibm.hrl.proton.admin.webapp.utils.AppConstants;

@WebListener
public class ProtonAdminServletContextListener implements ServletContextListener {


	
	final static int internalServerError = 500;
	
	private static final Logger logger = Logger.getLogger(ProtonAdminServletContextListener.class.getName());	 
		
	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		logger.info("shutting down ProtonAdminServletContextListener");
		
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
				
		logger.info("initializing ProtonAdminServletContextListener");
		
		// read properties from properties file and initialize constants
		String propertiesFileRelativePath = "/ProtonAdmin.properties";
		InputStream propertiesFileInputStream  =  sce.getServletContext().getResourceAsStream(propertiesFileRelativePath);
		logger.info("created popertiesFileInputStream = " +propertiesFileInputStream);
		String rootPath = sce.getServletContext().getRealPath("/");
		logger.info("getServletContext().getRealPath(/) = " +rootPath);
		Properties prop = new Properties();
		try {		  	 
	    	prop.load(propertiesFileInputStream);			 	
		} catch (Exception e) {
		    String msg = "Could not read ProtonAdmin properties file, reason : " + e +
				", message: " + e.getMessage();
		    logger.severe(msg);
	    
		    throw new ResponseException(internalServerError,msg);			
		}

		AppConstants.adminApplicationName = prop.getProperty("admin-application-name");
		AppConstants.definitionsRepository = prop.getProperty("definitions-repository");
		AppConstants.managerUsername = prop.getProperty("manager-username");
		AppConstants.managerPassword = prop.getProperty("manager-password");
		AppConstants.serverPort = prop.getProperty("tomcat-server-port");
		AppConstants.jmxPort = prop.getProperty("tomcat-jmx-port");		
		
	}
			
}

