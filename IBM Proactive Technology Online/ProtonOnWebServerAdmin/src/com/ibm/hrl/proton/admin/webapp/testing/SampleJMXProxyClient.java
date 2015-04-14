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
package com.ibm.hrl.proton.admin.webapp.testing;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

public class SampleJMXProxyClient {


	
	public static void main(String[] args) {
		
		  final String host = "localhost";
		// should be equal to the com.sun.management.jmxremote.port in startup.bat
		  final String port = "3250";

		try {
			
			// create url for querying the jmx proxy				
			
			// the jmx query syntax (before it is rewritten for jmx proxy)
			//<jmx:invoke
			//name="Catalina:type=Manager,context=/servlets-examples,host=localhost"
			//operation="stop"/>
			
			// jmx proxy command syntax
			//http://localhost:3250/manager/jmxproxy/?invoke=BEANNAME&op=METHODNAME&ps=COMMASEPARATEDPARAMETERS
			//String url = "http://localhost:3250/manager/jmxproxy/?get=java.lang:type=Memory&att=HeapMemoryUsage";					
			String url = "http://localhost:8080/manager/jmxproxy/?invoke=Catalina:type=Manager," + 
				"context=/SampleWebApplication,host=localhost&op=stop";		
			
	        HttpHost targetHost = new HttpHost("localhost",3250,"http");
	        DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getCredentialsProvider().setCredentials(
            		new AuthScope(targetHost.getHostName(),targetHost.getPort()),
                    new UsernamePasswordCredentials("manager","manager"));
			
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost,basicAuth);

            // Add AuthCache to the execution context
            BasicHttpContext localcontext = new BasicHttpContext();
            localcontext.setAttribute(ClientContext.AUTH_CACHE,authCache);
		
            HttpPost httppost = new HttpPost(url);
            System.out.println("jmx proxy request: " + url);
            HttpResponse response = httpclient.execute(targetHost,httppost,localcontext);
						
            System.out.println("after request: " + response.getStatusLine().toString());
            // we can parse the content now to get the response (if there exists)
            
            			  
		} catch (Exception e) {
			System.out.println("Could not manipulate MBean, message: " + e.getMessage() +
					", stack trace: " + e.getStackTrace());
			System.exit(-1);
		}
	}

}
