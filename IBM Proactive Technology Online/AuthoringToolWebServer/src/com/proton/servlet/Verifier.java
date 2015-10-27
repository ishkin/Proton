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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;

import com.ibm.hrl.proton.expression.facade.EEPException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.metadata.parser.ParsingException;
import com.ibm.hrl.proton.metadata.parser.ProtonParseException;
import com.ibm.hrl.proton.runtime.metadata.MetadataFacade;

@WebServlet("/Verifier/*")
public class Verifier extends HttpServlet {
	private static final long serialVersionUID = -4463999891888519118L;

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String content = request.getParameter("content");
		JSONArray errors = new JSONArray();
		try {
			Iterable<ProtonParseException> parsingErrors = verify(content);
			for (ProtonParseException error : parsingErrors) {
				errors.put(error.toJsonString());
			}

			response.getWriter().write(errors.toString());
		} catch (ParsingException e) {
			e.printStackTrace();
			response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
			response.getWriter().write("malformed json object");
		} catch (JSONException e) {
			e.printStackTrace();
			response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
			response.getWriter().write("malformed json object");
		}
	}

	public Iterable<ProtonParseException> verify(String content)
			throws ParsingException {
		EepFacade eep = null;
		MetadataFacade facade = new MetadataFacade();
		Iterable<ProtonParseException> result = new LinkedList<ProtonParseException>();
		try {
			eep = new EepFacade();
			MetadataParser parser = new MetadataParser(eep,facade);
			parser.clear();
			result = parser.parseEPN(content);
		} catch (EEPException e) {
			throw new AssertionError(e);
		}
		return result;
	}
}
