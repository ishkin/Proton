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

public class SamplePlainTextMessageClient {

	
	
	// we send events from this client
	// the client in turn can read from file, JMS, etc...
	public static void main(String arg[]) {
		RestClient client = new RestClient();
		
		System.out.println("sending events to ProtonWebApplication...");
		Resource resource = client.resource("http://localhost:8080/ProtonOnWebServer/rest/events-plain-text");
		
		System.out.println("got resource " + resource.toString());
		
		//String response = resource.accept("text/plain").get(String.class);		
		//System.out.println("web app: " + response);
		
		//ClientResponse res = resource.contentType("text/plain").accept("text/plain").post(
		//		"message from client - today's date: " + new Date().toString());
		//System.out.println("web app response after post: " + res.getMessage() + 
		//		" status: " + res.getStatusCode());
		
		//String newResponse = resource.accept("text/plain").get(String.class);		
		//System.out.println("web app after update: " + newResponse);
		
		// the below scenario should result in three SellAfterBuy detections
		
		ClientResponse res;
		String event1 = "Name=StockBuy;id=111;amount=100;price=5000";
		res = resource.contentType("text/plain").accept("text/plain").post(event1);
		System.out.println("web app response after post: " + res.getMessage() +
				" status: " + res.getStatusCode());
		
		/*try {
			InputStream is = new ByteArrayInputStream(event1.getBytes("UTF-8"));
			res = resource.contentType("text/plain").accept("text/plain").post(is);
			System.out.println("web app response after post: " + res.getMessage() +
					" status: " + res.getStatusCode());
		}
		catch (Exception e) {
			    String message = "Could not convert event string to input stream " + event1 +
						", reason: " + e.getMessage();
			    System.out.println(message);					
		}*/		
		
		String event2 = "Name=StockSell;id=111;amount=100;price=6000";
		res = resource.contentType("text/plain").accept("text/plain").post(event2);
		System.out.println("web app response after post: " + res.getMessage() +
				" status: " + res.getStatusCode());		
		/*String event3 = "Name=StockBuy;id=222;amount=100;price=5000";
		res = resource.contentType("text/plain").accept("text/plain").post(event3);
		System.out.println("web app response after post: " + res.getMessage() +
				" status: " + res.getStatusCode());
		String event4 = "Name=StockBuy;id=333;amount=100;price=5000";
		res = resource.contentType("text/plain").accept("text/plain").post(event4);
		System.out.println("web app response after post: " + res.getMessage() +
				" status: " + res.getStatusCode());
		String event5 = "Name=StockSell;id=222;amount=100;price=6000";
		res = resource.contentType("text/plain").accept("text/plain").post(event5);
		System.out.println("web app response after post: " + res.getMessage() +
				" status: " + res.getStatusCode());
		String event6 = "Name=StockSell;id=333;amount=100;price=6000";
		res = resource.contentType("text/plain").accept("text/plain").post(event6);
		System.out.println("web app response after post: " + res.getMessage() +
				" status: " + res.getStatusCode());
		*/
	}
}
