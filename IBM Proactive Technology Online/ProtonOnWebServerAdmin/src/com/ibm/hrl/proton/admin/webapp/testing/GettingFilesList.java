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

import java.io.File;

public class GettingFilesList {



	public static void main(String[] args) {
		
		final String defsAbsolutePath = "D:\\Proton\\ProtonDefinitions";		
		File newfile = new File(defsAbsolutePath);
		String filelist[] = newfile.list();

		for(int i=0; i<filelist.length; i++) { 
			String flname = filelist[i];
			System.out.println("file name " + i + ": " + flname);			
		}		
	}
}
