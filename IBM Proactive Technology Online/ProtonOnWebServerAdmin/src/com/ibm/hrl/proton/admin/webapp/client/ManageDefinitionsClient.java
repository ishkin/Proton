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
package com.ibm.hrl.proton.admin.webapp.client;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;

public class ManageDefinitionsClient {


	
	final static int resourceNotExistsError = 404;
	final static int internalServerError = 500;
	
	public static void main(String arg[]) {
		
		getAllDefinitions();
		//postNewDefinition("NewApplication");				
		//getSpecificDefinition("NotExisting");
		//updateSpecificDefinition("NewApplication");
		//getSpecificDefinition("NewApplication");
		//deleteDefinition("SlidingWindow-v2");			
		//getAllDefinitions();
	}

	private static void deleteDefinition(String definitionId) {
		RestClient client = new RestClient();
		try {
			System.out.println("managing defintions for proton application...");
			Resource genResource = client.resource("http://localhost:8080/ProtonOnWebServerAdmin/" + 
					"resources/definitions/" + definitionId);			
			System.out.println("got resource " + genResource.toString());
					
			// delete the definition from the server
			ClientResponse res = genResource.contentType("application/json").delete();			
			System.out.println("admin response: " + res.getMessage() +
					" status: " + res.getStatusCode());		
			
			System.out.println("the definition deletion status: " + res.getEntity(String.class));
			
		} catch (Exception e) {
			System.out.println("Could not delete definition, reason: " + e +
					" message: " + e.getMessage());
		}
	}

	private static void updateSpecificDefinition(String definitionId) {
		RestClient client = new RestClient();
		try {
			System.out.println("managing defintions for proton application...");
			Resource genResource = client.resource("http://localhost:8080/ProtonOnWebServerAdmin/" + 
					"resources/definitions/" + definitionId);			
			System.out.println("got resource " + genResource.toString());
					
			// create new definition and post it to the server
			JSONObject definition = new JSONObject();
			// we assume that user does not change the definition name
			definition.put("changed-objects","all the rest...");
			
			ClientResponse res = genResource.contentType("application/json").put(definition);			
			System.out.println("admin response: " + res.getMessage() +
					" status: " + res.getStatusCode());	
			System.out.println("new defs url: " + res.getEntity(String.class));
			
		} catch (Exception e) {
			System.out.println("Could not update definition, reason: " + e +
					" message: " + e.getMessage());
		}
	}

	private static void getSpecificDefinition(String definitionId) {
		RestClient client = new RestClient();
		try {
			System.out.println("managing defintions for proton application...");
			Resource genResource = client.resource("http://localhost:8080/ProtonOnWebServerAdmin/" + 
					"resources/definitions/" + definitionId);			
			System.out.println("got resource " + genResource.toString());
					
			// create new definition and post it to the server
			ClientResponse res = genResource.accept("application/json").get();			
			System.out.println("admin response: " + res.getMessage() +
					" status: " + res.getStatusCode());	

			JSONObject definition = res.getEntity(JSONObject.class);
			System.out.println("retrieved definition: " + definition.toString());
			
		} catch (Exception e) {
			System.out.println("Could not get definition, reason: " + e +
					" message: " + e.getMessage());
		}
	}

	private static void postNewDefinition(String definitionId) {
		RestClient client = new RestClient();
		try {
			System.out.println("managing defintions for proton application...");
			Resource genResource = client.resource("http://localhost:8080/ProtonOnWebServerAdmin/" + 
					"resources/definitions");			
			System.out.println("got resource " + genResource.toString());
					
			// create new definition and post it to the server
			JSONObject definition = new JSONObject();
			definition.put("name",definitionId);
			definition.put("all-other-objects","all the rest...");
			
			ClientResponse res = genResource.contentType("application/json").post(definition);			
			System.out.println("admin response: " + res.getMessage() +
					" status: " + res.getStatusCode());	
			
			if (res.getStatusCode() == internalServerError || res.getStatusCode() == resourceNotExistsError) {
				System.out.println("internal server error: " + res.getEntity(String.class));
			}

			System.out.println("new defs url: " + res.getEntity(String.class));
			
		} catch (Exception e) {
			System.out.println("Could not post new definitions, reason: " + e +
					" message: " + e.getMessage());
		}
	}

	private static void getAllDefinitions() {
		RestClient client = new RestClient();
		try {
			System.out.println("managing defintions for proton application...");
			Resource genResource = client.resource("http://localhost:8080/ProtonOnWebServerAdmin/" + 
					"resources/definitions");			
			System.out.println("got resource " + genResource.toString());
					
			// get all existing definitions
			ClientResponse res = genResource.get();
			//String res = genResource.accept("text/plain").get(String.class);
			//System.out.println("result: " + res);
			
			System.out.println("admin response: " + res.getMessage() +
					" status: " + res.getStatusCode());
			
			JSONArray definitions = res.getEntity(JSONArray.class);
			System.out.println("retrieved definitions number: " + definitions.length());
			for (int i=0; i<definitions.length(); i++) {
				JSONObject definition = definitions.getJSONObject(i);
				String name =	definition.getString("name");
				String url =	definition.getString("url");
				
				System.out.println("retrieved definition " + name + " with url " + url);
			}
			
		} catch (Exception e) {
			System.out.println("Could not get definitions, reason: " + e +
					" message: " + e.getMessage());
		}
	}

}
