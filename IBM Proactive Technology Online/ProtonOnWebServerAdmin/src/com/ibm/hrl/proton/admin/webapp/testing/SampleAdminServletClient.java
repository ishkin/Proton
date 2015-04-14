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

import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class SampleAdminServletClient {


	
	public static void main(String[] args) {
		
		String application = "ProtonOnWebServer";
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			URL url = new URL( "http://localhost:8080/manager/text/stop?path=/" + application);
            httpclient.getCredentialsProvider().setCredentials(new AuthScope("localhost",8080),
                    new UsernamePasswordCredentials("manager","manager"));
			
            HttpGet httpget = new HttpGet(url.toString());
            System.out.println("executing request " + httpget.getRequestLine());
            HttpResponse response = httpclient.execute(httpget);
            //HttpEntity entity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
        } catch (Exception e) {
			System.out.println("Could not manipulate MBean, message: " + e.getMessage() +
					", stack trace: " + e.getStackTrace());
			System.exit(-1);
        } finally {
        	httpclient.getConnectionManager().shutdown();
        }
	}

}
