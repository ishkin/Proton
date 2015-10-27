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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Managers.StringFileSaver;

@WebServlet("/FileServer/*")
public class FileServer extends ProjectManager {
	
	private static final int	BUFSIZE	= 1024;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileName = retrieveFileName(request) + StringFileSaver.FILE_EXTENSION;
		downloadFile(response, fileName);
	}

	private String retrieveFileName(HttpServletRequest request) {
		String url = request.getRequestURL().toString(); 
		return url.substring(url.lastIndexOf("/") + 1);
	}
	
	private void downloadFile(HttpServletResponse response, String fileName) throws IOException {
		File f = new File(getSavedDir() + "/" + fileName);
		int length = 0;
		ServletOutputStream op = response.getOutputStream();
		ServletContext context = getServletConfig().getServletContext();
		String mimetype = context.getMimeType(fileName);

		response.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
		response.setContentLength((int)f.length());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		// Stream to the requester.
		byte[] bbuf = new byte[BUFSIZE];
		DataInputStream in = new DataInputStream(new FileInputStream(f));

		while ((length = in.read(bbuf)) != -1) {
			op.write(bbuf, 0, length);
		}

		in.close();
		op.flush();
		op.close();
	}
}
