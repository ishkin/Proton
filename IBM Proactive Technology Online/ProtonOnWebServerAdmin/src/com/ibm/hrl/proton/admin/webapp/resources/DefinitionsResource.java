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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.hrl.proton.admin.webapp.exceptions.ResponseException;
import com.ibm.hrl.proton.admin.webapp.utils.AppConstants;

@Path("/definitions")
public class DefinitionsResource {
	

	
	private static final Logger logger = Logger.getLogger(DefinitionsResource.class.getName());
 
	private static final String adminApplicationName = AppConstants.adminApplicationName;
	private static final String defsURLPath = "/" + adminApplicationName + "/resources/definitions";
	private static final String defsURLShortPath = "/definitions";

	private static final String filePathSeparator = System.getProperty("file.separator");
	private static final String defsAbsolutePath = AppConstants.definitionsRepository;
	//private static final String server = "http://localhost:8080";
	
	final static int resourceNotExistsError = 404;
	final static int internalServerError = 500;
	
	@GET
	@Produces("application/json")	
	public Response getAllDefinitions() {
		
		logger.info("starting getAllDefinitions");

		JSONArray definitions = new JSONArray();
		
		File newfile = new File(defsAbsolutePath); 
		String filelist[] = newfile.list();

		// we assume that all files at this absolute path are defs files (.json suffix)

		try {
			for(int i=0; i<filelist.length; i++) { 
				String flname = filelist[i];
				JSONObject currentDefinition = new JSONObject();
				currentDefinition.put("name",defsAbsolutePath + filePathSeparator + flname);
				String flnameWithoutSuffix = flname.substring(0,flname.indexOf('.'));
				currentDefinition.put("url",defsURLPath + "/" + flnameWithoutSuffix);			
				definitions.put(currentDefinition);
			}
	
		} catch (JSONException e) {
		    String msg = "Could not get definitions, reason: " + e + ", message: " + e.getMessage();
		    logger.severe(msg);
		    
		    throw new ResponseException(internalServerError,msg);
		}
		
		logger.info("finished getAllDefinitions");
					
		return Response.ok(definitions).type("application/json").build();
	}
		
	@POST
	@Consumes("application/json")
	public Response createNewDefinition(JSONObject newDefinition) {
		
		logger.info("starting createNewDefinition");
		
		FileWriter file = null;
		String flnameWithoutSuffix = "";
		
		try {
			// create a new file with provided definitions
			// "name" attribute resides under the "epn" first-level object
			String flname = newDefinition.getJSONObject("epn").getString("name");
			if (!flname.endsWith(".json")) {
				flname += ".json";
			}
			
			flnameWithoutSuffix = flname.substring(0,flname.indexOf('.'));		
			File defsFile = new File(defsAbsolutePath + filePathSeparator + flname);
			logger.info("new definition full path: " + defsAbsolutePath + filePathSeparator + flname);
			Boolean created = defsFile.createNewFile();
						
			if (!created) { // file already exists at the specified location
				String msg = "Definitions with given name already exist";
				logger.severe(msg);			    
				
				throw new ResponseException(internalServerError,msg);
			}
			
			// write json definitions into the newly created file
			file = new FileWriter(defsAbsolutePath + filePathSeparator + flname);
			file.write(newDefinition.toString());
			file.flush();
		
		} catch (Exception e) {
		    String msg = "Could not create new definition file, reason: " + e +
		    	", message: " + e.getMessage();
		    logger.severe(msg);	
		    
		    throw new ResponseException(internalServerError,msg);
		} finally {
			try {
				file.close();
			} catch (IOException ioe){
			    String msg = "Could not close a file " + ioe.getMessage();
			    	logger.severe(msg);	
			    
			    throw new ResponseException(internalServerError,msg);				
			}
		}
		
		String location = defsURLPath + "/" + flnameWithoutSuffix;				
		logger.info("finished createNewDefinition");
		
		return Response.ok(location).build();
	}	
	
	@GET
	@Path ("{defid}")
	@Produces("application/json")
	public Response getDefinition(@PathParam("defid") String defid) {
	 
		logger.info("starting getDefinition");
		// get json definitions for the specific defid
		String flname = defsAbsolutePath + filePathSeparator + defid + ".json";
		
		JSONObject defsJson = null;
		try {
		    InputStream defsStream = new FileInputStream(flname);
		    logger.info("file name: " + flname);
		    
            // the below trick is from here -
            // http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string			
			String defsString = new Scanner(defsStream).useDelimiter("\\A").next();
			defsJson = new JSONObject(defsString);
	    
		} catch (JSONException e) {
		    String msg = "Could not parse json of " + defid + " defintion, reason: " + e +
	    		", message: " + e.getMessage();
		    logger.severe(msg);
		    
		    throw new ResponseException(internalServerError,msg);
		} catch (FileNotFoundException e) {
		    String msg = "Could not find " + defid + " defintion, reason: " + e +
		    	", message: " + e.getMessage();
		    logger.severe(msg);				
		    
		    throw new ResponseException(resourceNotExistsError,msg);
		}
		
		logger.info("finished getDefinition");
		
		return Response.ok(defsJson).type("application/json").build();
	}	

	@PUT
	@Path ("{defid}")
	@Consumes("application/json")
	public Response updateDefinition(@PathParam("defid") String defid, JSONObject newDefinition) {
	 
		logger.info("starting updateDefinition");
		
		// if a definition file with given defid exist - update it
		// if it does not exist - create a new definition file with this defid
		String flname = defsAbsolutePath + filePathSeparator + defid + ".json";
		File defsFile = new File(flname);
		
		FileWriter file = null;
		try {
			Boolean created = defsFile.createNewFile();	
			// write json definitions into the newly created file
			file = new FileWriter(flname);
			file.write(newDefinition.toString());
			file.flush();
			
		} catch (Exception e) {
		    String msg = "Could not create or update " + defid + " definition file, reason: " +
		    	e + ", message: " + e.getMessage();
		    logger.severe(msg);		
		    
		    throw new ResponseException(internalServerError,msg);
		} finally {
			try {
				file.close();
			} catch (IOException ioe){
			    String msg = "Could not close a file " + ioe.getMessage();
			    logger.severe(msg);	
			    
			    throw new ResponseException(internalServerError,msg);				
			}
		}

		String location = defsURLPath + "/" + defid;
		logger.info("finished updateDefinition");
		
		return Response.ok(location).build();
	}	
	
	@DELETE
	@Path ("{defid}")
	public Response deleteDefinition(@PathParam("defid") String defid) {
	 
		logger.info("starting deleteDefinition");
		
		// delete definitions file named defid
		String flname = defsAbsolutePath + filePathSeparator + defid + ".json";
		logger.info("file name to delete: " + flname);
		File defsFile = new File(flname);
		Boolean deleted = defsFile.delete();
		
		if (!deleted) {
			throw new ResponseException(resourceNotExistsError,
					"Problem while deleting definition " + defid);
		}
				
		logger.info("finished updateDefinition");
		
		return Response.ok(deleted.toString()).build();
	}	
	
}
