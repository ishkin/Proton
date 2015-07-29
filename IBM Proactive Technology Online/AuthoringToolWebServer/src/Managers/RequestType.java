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
package Managers;

public enum RequestType {
	STORE,
	LOAD,
	GET_PROJECTS;
	
	@Override
	public String toString() {
		switch(this) {
		case GET_PROJECTS:
			return "getProjects";
		default:
			return super.toString();
		}
	}
	
	public static RequestType parse(String name) {
		if (name.equalsIgnoreCase("getProjects")) {
			return GET_PROJECTS;
		} else {
			return RequestType.valueOf(name);
		}
	}
}
