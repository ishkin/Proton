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
package com.ibm.hrl.proton.adapters.rest.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import com.ibm.hrl.proton.adapters.interfaces.AdapterException;

public class RestClient {


	
	/**
	 * Gets a list of events from the specified producer.
	 * The producer is specified by the URL (which includes all the relevant info - the web
	 * server name, port name, web service name and the URI path 
	 * The content type can be either application/xml,application/json or plain text
	 * The content type will be specified by the specific producer, and a formatter
	 * will be supplied to create an event instance from the type
	 * 
	 * The method returns a list of String instances representing the event instances, 
	 * which will be parsed by the specific input adapter according to the producer's configuration
	 * @param url
	 * @return
	 * @throws RESTException 
	 */
	protected static List<String> getEventsFromProducer(HttpClient httpClient,GetMethod getMethod, String url,String contentType) throws RESTException
	{
		List<String> resultEvents = new ArrayList<String>();
		
        // Execute request
        try {
            
            int result = httpClient.executeMethod(getMethod);
            if (result != 200)
            {
            	throw new RESTException("Could not perform GET on producer "+url+", responce result: "+result);
            }
            
            
            InputStream input = getMethod.getResponseBodyAsStream();            
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(input));
            String output;            
            while ((output = br.readLine()) != null) 
            {
            	
            	resultEvents.add(output);
            }
                        
        }
        catch(Exception e)
        {
        	throw new RESTException(e);
        	
        }
        
        return resultEvents;
	}
	
	/**
	 * Put the specified event instance to the specified consumer.
	 * The consumer is specified by the URL (which includes all the relevant info - the web
	 * server name, port name, web service name and the URI path 
	 * The content type can be either application/xml,application/json or plain text
	 * The content type will be specified by the specific consumer, and a formatter
	 * will be supplied to format the event instance to that type
	 * 
	 * @param url
	 * @return
	 * @throws AdapterException 
	 * @throws RESTException 
	 */
	protected static void putEventToConsumer(String url, String eventInstance, String contentType) throws  RESTException {
		// Prepare HTTP PUT
        PostMethod postMethod = new PostMethod(url);        
        
        if(eventInstance != null) {

        	RequestEntity requestEntity = new ByteArrayRequestEntity(eventInstance.getBytes());
        	postMethod.setRequestEntity(requestEntity);

        
	        // Specify content type and encoding
	        // If content encoding is not explicitly specified
	        // ISO-8859-1 is assumed
        	// postMethod.setRequestHeader("Content-Type", contentType+"; charset=ISO-8859-1");
        	postMethod.setRequestHeader("Content-Type", contentType);
        	
	        // Get HTTP client
	        HttpClient httpclient = new HttpClient();
	        
	        // Execute request
	        try {
	            
	            int result = httpclient.executeMethod(postMethod);
	            	            
	            if (result < 200 || result >= 300)
	            {
	            	Header [] reqHeaders = postMethod.getRequestHeaders();
	            	StringBuffer headers = new StringBuffer();
	            	for (int i=0; i<reqHeaders.length; i++ ){
	            		headers.append(reqHeaders[i].toString());
	            		headers.append("\n");
	            	}
	            	throw new RESTException("Could not perform POST of event instance: \n"+eventInstance+ "\nwith request headers:\n" +
	            			headers + "to consumer "+ url+", responce result: "+result);
	            }
	           
	        } catch(Exception e)
	        {
	        	throw new RESTException(e);
	        }
	        finally {
	            // Release current connection to the connection pool 
	            // once you are done
	            postMethod.releaseConnection();
	        }
        } else
        {
        	System.out.println ("Invalid request");
        }
//        PutMethod putMethod = new PutMethod(url);        
// 
//        if(eventInstance != null) {
//        	RequestEntity requestEntity = new ByteArrayRequestEntity(eventInstance.getBytes());
//        	putMethod.setRequestEntity(requestEntity);
//
//        
//	        // Specify content type and encoding
//	        // If content encoding is not explicitly specified
//	        // ISO-8859-1 is assumed
//	        putMethod.setRequestHeader(
//	                "Content-type", contentType+"; charset=ISO-8859-1");
//	        
//	        // Get HTTP client
//	        HttpClient httpclient = new HttpClient();
//	        
//	        // Execute request
//	        try {
//	            
//	            int result = httpclient.executeMethod(putMethod);
//	            	            
//	            if (result < 200 || result >= 300)
//	            {
//	            	throw new RESTException("Could not perform PUT of event instance "+eventInstance+" to consumer "+ url+", responce result: "+result);
//	            }
//	           
//	        } catch(Exception e)
//	        {
//	        	throw new RESTException(e);
//	        }
//	        finally {
//	            // Release current connection to the connection pool 
//	            // once you are done
//	            putMethod.releaseConnection();
//	        }
//        } else
//        {
//        	System.out.println ("Invalid request");
//        }

	}


	
}
