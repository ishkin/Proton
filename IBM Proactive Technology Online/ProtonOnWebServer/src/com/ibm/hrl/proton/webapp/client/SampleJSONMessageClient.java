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
package com.ibm.hrl.proton.webapp.client;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.json4j.JSONObject;

import com.ibm.hrl.proton.metadata.event.EventHeader;

public class SampleJSONMessageClient {

	
	
	final static int internalServerError = 500;
	
	// we can send events from this client
	// the client in turn can read from file, JMS, etc...
	public static void main(String arg[]) {
		RestClient client = new RestClient();
		try {
			System.out.println("sending events to ProtonWebApplication...");
			//Resource resource = client.resource("http://rcc-hrl-kvg-175:8080/ProtonOnWebServer-unix/rest/events");
			Resource resource = client.resource("http://localhost:8080/ProtonOnWebServer/rest/events");
			
			System.out.println("got resource " + resource.toString());
			int i=0;
			JSONObject eventJson = new JSONObject();
			while (true){				
				i++;
				eventJson.clear();
				eventJson.put(EventHeader.NAME_ATTRIBUTE,"NodeContextUpdate");
				eventJson.put("Latitud","43.4720001220703");
				eventJson.put("entityId","OUTSMART.NODE_3507");
				eventJson.put("entityType","Node");
				eventJson.put("Longitud","-3.80707001686096");
				eventJson.put("presence","1");
				eventJson.put("illuminance","260.14");
				eventJson.put("TimeInstant","2014-05-05T12:04:46.000000Z");
				eventJson.put("batteryCharge","61");
				
				
				ClientResponse res;
				res = resource.contentType("application/json").accept("application/json").post(eventJson);
				System.out.println("web app response after post: "+i+  res.getMessage() +
						" status: " + res.getStatusCode());
				
				if (res.getStatusCode() == internalServerError) {
					System.out.println("response message: " + res.getEntity(String.class));
				}
			}
						
						
		} catch (Exception e) {
			System.out.println("Could not send event, reason: " + e +
					"message: " + e.getMessage());
		}
	}
}
