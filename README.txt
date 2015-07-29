#-----------------------------------------------------------------------
# Copyright 2014 IBM
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#-----------------------------------------------------------------------
IBM Proactive Technology Online is an open source complex event processing engine developed at IBM Research - Haifa. It provides language primitives for defining, submitting, and executing event processing networks. The goal of the system is to respond to raw events and identify meaningful events within contexts. The system comes with a set of built-in operators (such as sequence, all, etc.) for determining CEP patterns. It also has extendable APIs for adding additional custom operators. The system comes with existing source/sink adapters, allowing it to extract raw events from files or pull them from RESTful services. It also provides extendable APIs for adding more adapter types. 

The repository includes: 
	Source files for:
	-The Proton standalone version. This includes:
		-The Proton J2SE version, which is just a simple Java app running the Proton engine. This can be found under ProtonJ2SE project.
		-The Proton web version, providing a RESTful interface for sending events, and also the admin app for managing Proton apps. Those are the ProtonOnWebServer and ProtonOnWebServerAdmin projects respectively.
			This is a version of the engine adapted to run on a web server. It allows users to push RESTful events to the engine in addition to pulling 
			RESTful events option provided by the REST adapter. This version also provides 	REST APIs for managing the engine’s instance lifecycle, and managing the definition repository.
			For instructions on how to configure and install IBM Proactive Technology Online on 	Tomcat, see: https://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/CEP_GE_IBM_Proactive_Technology_Online_Installation_and_Administration_Guide
		-The Proton web UI, providing authoring environment for Proton applications. Those are the AuthoringTool and AuthoringToolWebServer projects.
	-The Proton on STORM version, which allows to run the engine in a distributed manner on multiple machines using STORM infrastructure.

	The projects are maven projects, they can be build using the "clean install" targets by running mvn command on the parent pom (located in the "Proton) directory. This command will build all the Proton projects.

	Additionaly, by executing the "mvn deploy"	command of the same parent pom after performing clean install, the target jars will be installed into local repository named "mvn-repo" (/Proton/maven-repo).
	The ProtonJ2SE artifact build will also include a .zip distribution, with ProtonJ2SE executable jar, launch script, sample and docs directory. 
	




