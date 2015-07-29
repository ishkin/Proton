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

public class ManageInstancesClient {
	

	public static void main(String arg[]) {
		
		getAllProtonInstances();
		//getInstanceState("ProtonOnWebServer");
		//updateInstanceState("ProtonOnWebServer");
		//getInstanceState("ProtonOnWebServer");
		
	}

	private static void getAllProtonInstances() {
		RestClient client = new RestClient();
		try {			
			System.out.println("getting all deployed proton instances...");
			Resource genResource = client.resource("http://localhost:8080/ProtonOnWebServerAdmin/" + 
					"resources/instances");
			
			//Resource genResource = client.resource("http://rcc-hrl-kvg-175:8080/ProtonOnWebServerAdmin-unix/" + 
			//	"resources/instances");			

			System.out.println("got resource " + genResource.toString());
			ClientResponse res = genResource.contentType("application/json").get();			
			System.out.println("admin response after get: " + res.getMessage() +
					" status: " + res.getStatusCode());	

			JSONArray instances = res.getEntity(JSONArray.class);
			for (int i=0; i<instances.length(); i++) {
				JSONObject proton = instances.getJSONObject(i);
				String name =	proton.getString("name");
				String state =	proton.getString("state");
				String url =	proton.getString("url");
				
				System.out.println("proton instance " + name + " with url " + url + " " + state);
			}
			
		} catch (Exception e) {
			System.out.println("Could not retrieve application state, reason: " + e +
					" message: " + e.getMessage());
		}				
	}

	private static void getInstanceState(String instanceId) {
		RestClient client = new RestClient();
		try {			
			System.out.println("getting state of a certain instance...");
			Resource genResource = client.resource("http://localhost:8080/ProtonOnWebServerAdmin/" + 
					"resources/instances/" + instanceId);			
			
			//Resource genResource = client.resource("http://rcc-hrl-kvg-175:8080/ProtonOnWebServerAdmin-unix/" + 
			//		"resources/instances/" + instanceId);			
					
			System.out.println("got resource " + genResource.toString());								
			ClientResponse res = genResource.contentType("application/json").get();			
			System.out.println("admin response after get: " + res.getMessage() +
					" status: " + res.getStatusCode());	

			JSONObject state = res.getEntity(JSONObject.class);
			System.out.println("web application definitions: " + state.getString("definitions-url"));
			System.out.println("web application state: " + state.getString("state"));
			
		} catch (Exception e) {
 			System.out.println("Could not retrieve application state, reason: " + e +
					" message: " + e.getMessage());
		}				
	}

	private static void updateInstanceState(String instanceId) {
		RestClient client = new RestClient();
		try {
			System.out.println("updating state for a certain instance...");
			Resource genResource = client.resource("http://localhost:8080/ProtonOnWebServerAdmin/" + 
					"resources/instances/" + instanceId);
			
			//Resource genResource = client.resource("http://rcc-hrl-kvg-175:8080/ProtonOnWebServerAdmin-unix/" + 
			//		"resources/instances/" + instanceId);			
			
			System.out.println("got resource " + genResource.toString());
					
			// create new definition and post it to the server
			JSONObject state = new JSONObject();
			// we assume that user does not change the definition name
			state.put("state","dummy");
			//state.put("state","start");
			state.put("definitions-url","/ProtonOnWebServerAdmin/resources/definitions/SlidingWindow");
			//state.put("definitions-url","/ProtonOnWebServerAdmin/resources/definitions/SlidingWindow4Unix");
			state.put("action","ChangeDefinitions");			
			
			ClientResponse res = genResource.contentType("application/json").put(state);			
			System.out.println("admin response after put: " + res.getMessage() +
					" status: " + res.getStatusCode());	
			//System.out.println("new defs url: " + res.getHeaders().getFirst("location"));
			
		} catch (Exception e) {
			System.out.println("Could not update application state, reason: " + e +
					" message: " + e.getMessage());
		}				
	}
	

}
