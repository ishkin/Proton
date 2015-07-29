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
package com.proton.servlet;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.RestClient;
import org.apache.wink.json4j.*;

/**
 * Servlet implementation class ProtonOnWebServerAdminProxy
 */
@Resource
@Path("/proxy")
public class ProtonOnWebServerAdminProxy {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDefinitions(@QueryParam("url") String url) {
		RestClient rc = new RestClient();
		ClientResponse definitions = rc.resource(url).get();
		return Response.ok(definitions.getEntity(JSONArray.class)).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putDef(@QueryParam("url") String url, JSONObject content) {
		RestClient rc = new RestClient();
		rc.resource(url).contentType(MediaType.APPLICATION_JSON).put(content);
		return Response.ok().build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postDef(@QueryParam("url") String url, JSONObject content) {
		RestClient rc = new RestClient();
		rc.resource(url).contentType(MediaType.APPLICATION_JSON).post(content);
		return Response.ok().build();
	}
}
