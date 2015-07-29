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
package com.ibm.hrl.proton.admin.webapp.resources;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;

import com.ibm.hrl.proton.admin.webapp.exceptions.ResponseException;
import com.ibm.hrl.proton.admin.webapp.utils.AppConstants;


@Path("/instances")
public class ProtonInstancesResource {

	
	
	private static final Logger logger = Logger.getLogger(ProtonInstancesResource.class.getName());

	private static final String filePathSeparator = System.getProperty("file.separator");
	private static final String tomcatInstallationDir = System.getenv("CATALINA_HOME");
	private static final String propertiesFileName = "Proton.properties";
	private static final String tomcatWebAppsDir = "webapps";
	
	private static final String adminApplicationName = AppConstants.adminApplicationName;
	private static final String defsURLPath = "/" + adminApplicationName + "/resources/definitions";
	private static final String instanceURLPath = "/" + adminApplicationName + "/resources/instances";
	private static final String defsAbsolutePath = AppConstants.definitionsRepository;
	private static final String managerUsername = AppConstants.managerUsername;
	private static final String managerPassword = AppConstants.managerPassword;
	
	private static final String jmxPort = AppConstants.jmxPort;
	private static final String port = AppConstants.serverPort;
	private static final String host = "localhost";
	
	private static final String server = "http://" + host + ":" + port;

	final static int resourceNotExistsError = 404;
	final static int internalServerError = 500;
	final static int okResponseCode = 200;

	
	@GET
	@Produces("application/json")	
	public Response getAllInstances() {
		
		logger.info("starting getAllInstances");
		
		final String protonDisplayName = "Proton Web Application";
		JSONArray protonInstances = new JSONArray();

		JMXConnector jmxConnector = null;
		try {
			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://" + host + ":" + jmxPort + "/jmxrmi");
		
			logger.info(url.toString());
			
			jmxConnector = JMXConnectorFactory.connect(url);		 
			MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
			ObjectName query = new ObjectName("Catalina:j2eeType=WebModule,*");			
			//logger.info("before getting mbeans...");
			Set<ObjectName> mbeans = new TreeSet<ObjectName>(mbsc.queryNames(query,query));
			logger.info("after getting mbeans, number: " + mbeans.size());
			for (ObjectName mbean: mbeans) {
				String displayName = mbsc.getAttribute(mbean,"displayName").toString(); 
				logger.info("mbean display name: " + displayName);
				if (displayName.equals(protonDisplayName)) {
					JSONObject instance = new JSONObject();
					String instanceName = mbsc.getAttribute(mbean,"name").toString(); 
					instance.put("name",instanceName);
					instance.put("state","started");
					if (mbsc.getAttribute(mbean,"stateName").toString().equalsIgnoreCase("STOPPED")) {
						instance.put("state","stopped");
					}
					instance.put("url",instanceURLPath + instanceName);
					protonInstances.put(instance);
					
				}	
				//logger.info(mbean.toString());
			}					  
		} catch (Exception e) {
			String msg = "Could not manipulate MBean, message: " + e.getMessage();
			logger.severe(msg);

			throw new ResponseException(internalServerError,msg);
		} finally {
			try {
				jmxConnector.close();
			} catch (IOException ioe) {
			    String msg = "Could not close a jmx connector " + ioe.getMessage();
			    logger.severe(msg);	
			    
			    throw new ResponseException(internalServerError,msg);
			}
		}
		
		logger.info("finished getAllInstances");
		return Response.ok(protonInstances).build();
	}

	
	@PUT
	@Path ("{instanceid}")
	@Consumes("application/json")
	public Response updateInstanceState(@PathParam("instanceid") String instanceid, JSONObject newState) {
	 
		logger.info("starting updateInstanceState");
		
		// parse the state object and figure out whether we need to start/stop application,
		// update definitions path or both; application status can remain unchanged (e.g., already started)
		try {
			String requiredAction = newState.getString("action");
			// required action can be either "ChangeState" or "ChangeDefinitions"
			if (requiredAction.equals("ChangeState")) {
				// required state is either "stop" or "start"
				String requiredState = newState.getString("state");
				logger.info("perfoming action: change state to " + requiredState);
				URL url = new URL(server + "/manager/text/" + requiredState + "?path=/" + instanceid);			
				performServiceCall(url);
			} else if (requiredAction.equals("ChangeDefinitions")) {
				logger.info("perfoming action: change definitions...");
				// stop application, update defs location and start the application
				URL url = new URL(server + "/manager/text/stop?path=/" + instanceid);			
				performServiceCall(url);
				
				// update definitions file location
				String propertiesPath = tomcatInstallationDir + filePathSeparator +
					tomcatWebAppsDir + filePathSeparator + instanceid + filePathSeparator +
					propertiesFileName;				
				logger.info("properties file path:" + propertiesPath);
				
				String newDefsFileUrl = newState.getString("definitions-url");
				String newDefsFileName = newDefsFileUrl.substring(newDefsFileUrl.lastIndexOf('/')+1);
				
				String newDefsFileLocation = defsAbsolutePath + filePathSeparator + newDefsFileName + ".json";
				// we assume that the given definitions file exists at defs store
				logger.info("new definitions file path: " + newDefsFileLocation);
				
			  	Properties prop = new Properties();		  	 
		    	prop.load(new FileInputStream(propertiesPath));
		    	// note that once we overwrite a property, all the comments are gone... 
		    	prop.setProperty("metadataFileName",newDefsFileLocation);
		    	prop.setProperty("metadataFilePathType","absolute"); // defs location in the repository
		    	prop.store(new FileOutputStream(propertiesPath),null);		 
		   		
		    	// properties file is updated, now we start the application
				url = new URL(server + "/manager/text/start?path=/" + instanceid);			
				performServiceCall(url);			
				
			} else { // illegal action
			    String msg = "Illegal action, permitted actions are ChangeState and ChangeDefinitions";
				logger.severe(msg);
				
				throw new ResponseException(internalServerError,msg);
			}
		} catch (Exception e) {
		    String msg = "Could not change application state, reason : " + e +
    			", message: " + e.getMessage();
		    logger.severe(msg);
		    
			throw new ResponseException(internalServerError,msg);
		}
		
		logger.info("finished updateInstanceState");
		// return the response string (e.g., OK - Application started...)
		return Response.ok().build();
	}	

	private void performServiceCall(URL url) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			logger.info("performing request: " + url);
            httpclient.getCredentialsProvider().setCredentials(new AuthScope(
            		host,Integer.parseInt(port)),new UsernamePasswordCredentials(
            				managerUsername,managerPassword));
			
            HttpGet httpget = new HttpGet(url.toString());
            System.out.println("executing request " + httpget.getRequestLine());
            HttpResponse response = httpclient.execute(httpget);
            //HttpEntity entity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            
		} catch (Exception e) {
		    String msg = "Could not perform required call, reason : " + e +
	    		", message: " + e.getMessage();
		    logger.severe(msg);			    
		    
			throw new ResponseException(internalServerError,msg);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}
	

	@GET
	@Path ("{instanceid}")
	@Produces("application/json")
	public Response getInstanceState(@PathParam("instanceid") String instanceid) {
	 
		logger.info("starting getInstanceState");
		
		String applicationState = null;
        JSONObject appStateAndDefs = new JSONObject();
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			// compose the query for web application state (started/stopped)
			URL url = new URL(server + "/manager/jmxproxy/?get=" +
					"Catalina:j2eeType=WebModule,name=//" + host + "/" + instanceid + 
					",J2EEApplication=none,J2EEServer=none&att=stateName");
			
			// example query (can invoke from web browser)
			// http://localhost:8080/manager/jmxproxy/?get=Catalina:j2eeType=WebModule,
			// name=//localhost/ProtonOnWebServer,J2EEApplication=none,J2EEServer=none&att=stateName

			logger.info("the query: " + url.toString());
            httpclient.getCredentialsProvider().setCredentials(new AuthScope(
            		host,Integer.parseInt(port)),new UsernamePasswordCredentials(
            				managerUsername,managerPassword));
			
            HttpGet httpget = new HttpGet(url.toString());
            System.out.println("executing request " + httpget.getRequestLine());
            HttpResponse response = httpclient.execute(httpget);
             
            InputStream is = response.getEntity().getContent();
            // the below trick is from here -
            // http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
            String responseStr = new Scanner(is).useDelimiter("\\A").next();
            logger.info("response string: " + responseStr);
            if(response.getStatusLine().getStatusCode() != okResponseCode){
            	String errorMsg = "Error activating jmx proxy: ".concat(responseStr);
            	throw new ResponseException(internalServerError,errorMsg);
            }
    		applicationState = "started";
            if (responseStr.indexOf("STARTED") == -1) {
            	applicationState = "stopped";
            }
            
            logger.info("application state: " + applicationState);
            
            // get the definitions url of the specific application
			String propertiesPath = tomcatInstallationDir + filePathSeparator +
				tomcatWebAppsDir + filePathSeparator + instanceid + filePathSeparator +
				propertiesFileName;
		  	Properties prop = new Properties();		  	 
	    	prop.load(new FileInputStream(propertiesPath));
	    	String defsFileName = prop.getProperty("metadataFileName");

			String flnameWithSuffix = defsFileName.substring(defsFileName.lastIndexOf(filePathSeparator)+1);
			String flnameWithoutSuffix = flnameWithSuffix.substring(0,flnameWithSuffix.indexOf('.'));
            String definitionUrl = defsURLPath + "/" + flnameWithoutSuffix;			

            logger.info("definitions url: " + definitionUrl);            
            
            appStateAndDefs.put("state",applicationState);
            appStateAndDefs.put("definitions-url",definitionUrl);

        } catch (Exception e) {
			String msg = "Could not read instance state, message: " + e.getMessage() +
					", stack trace: " + e.getStackTrace();
			logger.severe(msg);
			
			throw new ResponseException(internalServerError,msg);
        } finally {
        	httpclient.getConnectionManager().shutdown();
        }				
		
		logger.info("finished getInstanceState");
		
		return Response.ok(appStateAndDefs).build();		
	}

}
