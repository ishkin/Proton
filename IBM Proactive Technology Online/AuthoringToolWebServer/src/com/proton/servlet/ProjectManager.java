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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import Managers.ErrorType;
import Managers.ISaver;
import Managers.StringFileSaver;

@WebServlet("/ProjectManager/*")
public class ProjectManager extends HttpServlet {
	private final static String SAVED_DIR = "SavedProjects";
	protected ISaver<String> _saver;
	private static final long serialVersionUID = 1L;
	
	protected String getSavedDir() {
		return this.getServletContext().getRealPath("//") + "/" + SAVED_DIR;
	}
	
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	// initialize saver to save files on the saver
    	_saver = new StringFileSaver(getSavedDir());
    }
    
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String retrieveId = retrieveId(request);
		String result = "";
		if (retrieveId.length() > 0) { // retrieve
			result = retrieve(retrieveId);
		} else { // list
			result = list();
		}
		response.getWriter().write(result);
	}

	@SuppressWarnings("deprecation")
	private String retrieveId(HttpServletRequest request) {
		String url = request.getRequestURL().toString(); 
		String retrieveId = url.substring(url.lastIndexOf("/") + 1, url.length());
		return URLDecoder.decode(retrieveId);
	}
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String data = br.readLine();
		JSONObject json;
		try {
			 json = new JSONObject(data);
			 String id = json.getString("name");
			 String content = json.getString("content");
			 if (update(id, content) == false) {
				 response.setStatus(ErrorType.DOES_NOT_EXIST.toErorStatus());
				 response.getWriter().write("Project does not exist, use post(create) instead");
			 }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//TODO get id and content
		String id = request.getParameter("name");
		String content = request.getParameter("content");
		if (create(id, content) == false) {
			response.setStatus(ErrorType.ALREADY_EXISTS.toErorStatus());
			response.getWriter().write("Project already exists, use put(update) instead");
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = retrieveId(request);
		if (id.length()==0) {
			return;
		}
		_saver.delete(id);
		return;
	}

	private boolean update(String id, String content) {
		_saver.store(id, content);
		return true;
	}
	
	private boolean create(String id, String content) {
		if (_saver.exists(id)) {
			return false;
		}
		
		_saver.store(id, content);
		return true;
	}

	private String list() {
		String result;
		Iterable<String> projectsNames = _saver.getIDs();
		result = projectsToJson(projectsNames);
		return result;
	}
	
	
	private String retrieve(String retrieveId) {
		try {
			return _saver.load(retrieveId);
		} catch (IllegalArgumentException e) {
			return e.getMessage();
		}
	}

	private String projectsToJson(Iterable<String> projectsNames) {
		String result = "";
		try {
			JSONObject workspace = new JSONObject();
			workspace.put("name", "WorkSpace1");
			JSONArray projectJSON = new JSONArray();
			int projectIndex = 0;
			for (String projectName: projectsNames) {
				JSONObject project = new JSONObject();
				project.put("name", projectName);
				project.put("id", ++projectIndex + "");
				projectJSON.put(project);
			}
			workspace.put("Projects", projectJSON);
			JSONObject finalJSON = new JSONObject();
			finalJSON.put("WorkSpace", workspace);
			result = finalJSON.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.toString();
	}
}
