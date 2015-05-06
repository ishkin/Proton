package com.ibm.hrl.proton.webapp.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.hrl.proton.adapters.rest.client.RESTException;

public class SampleJSONMessageFileSender {
	public static final String DELIMETER = ",";
	public static final String TAG_SEPARATOR = ":";
	protected static final String NULL_STRING = "null";
	final static int internalServerError = 500;
	
	
	private static JSONObject createJSONObject(String sCurrentLine) throws IOException, JSONException {
			
		JSONObject json = new JSONObject();
		sCurrentLine = sCurrentLine.substring(1,sCurrentLine.length()-1);		
		String[] tagValuePairs = sCurrentLine.split(DELIMETER);
		
		for (String tagValue : tagValuePairs) 
		{
			//separate the tag from the value using the tagDataSeparator
			int tagSeparatorIndex = tagValue.indexOf(TAG_SEPARATOR);
			String[] separatedPair = tagValue.split(TAG_SEPARATOR);
			String attrName = "";
			if (tagSeparatorIndex != -1){
				attrName = tagValue.substring(0,tagSeparatorIndex);
			}
						
			if (attrName != null && !attrName.equals("")) {
				attrName = attrName.trim();
				attrName = attrName.substring(1, attrName.length()-1);
			}else
			{
				continue;
			}
			

			//some attributes might not have value specified at all
			if (separatedPair.length < 2)
			{
	        	json.put(attrName, "null");
	        	continue;

			}

			String attrStringValue = tagValue.substring(tagSeparatorIndex+1,tagValue.length());
			if (attrStringValue != null) {
				attrStringValue = attrStringValue.trim();
				attrStringValue = attrStringValue.substring(1, attrStringValue.length()-1);
			}

			if (attrStringValue.equals(NULL_STRING))
	        {
	        	//the attribute has a value of null
				json.put(attrName, "null");
	        	continue;
	        }
			json.put(attrName,attrStringValue);
		}
		
		return json;
	}
	
	
	
		
	private static void readFile(String fileName,Resource resource,Integer count) throws RESTException, InterruptedException, JSONException
	{
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(fileName));
 
			while ((sCurrentLine = br.readLine()) != null) {
				JSONObject event = createJSONObject(sCurrentLine);
				count ++;
				ClientResponse res;
				res = resource.contentType("application/json").accept("application/json").post(event);
				System.out.println("web app response after post : " +count + res.getMessage() +
						" status: " + res.getStatusCode());
				
				if (res.getStatusCode() == internalServerError) {
					System.out.println("response message: " + res.getEntity(String.class));
				}
				Thread.sleep(500);
				
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String arg[]) throws RESTException, InterruptedException, JSONException {
		RestClient client = new RestClient();
		Integer count = 0;
		System.out.println("sending events to ProtonWebApplication...");
		//Resource resource = client.resource("http://rcc-hrl-kvg-175:8080/ProtonOnWebServer-unix/rest/events");
		Resource resource = client.resource("http://localhost:8080/ProtonOnWebServer/rest/events");
		
		String fileName = arg[0];
		
		//reading file in a loop
		while (true){
			readFile(fileName,resource,count);			
		}
		
	}
}
