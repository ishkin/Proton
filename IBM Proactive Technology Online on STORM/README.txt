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

To allow for processing of large volumes of data in a distributed fashion, we have implemented additional version of Proton, Proton on top of STORM. 

STORM is an Apache open source distributed realtime computation system. Storm allows to reliably process unbounded streams of data. Its open programming model allows creation of distributed scalalble applications for processing of event streams.

Proton on STORM implementation makes use of STORM primitives, such as spouts and bolts, to wrap around the Proton logical components. It also uses the segmentation mechanisms of STORM intra-component communication, specifically the groupings of streams, to allow segmentation of events into independent groups for processing. 
For documentation on Proton architecture and implementation on top of STORM , please see : <<>>
For documentation on how to use Proton on STORM version, please see: <<>>

The repository includes: 
	-An Eclipse project file for ProtonOnStorm. This can be easily imported into eclipse environment for reference and farther developement activities.
	-A jar file with sources. 

Proton on STORM uses apache maven as a build tool. The distribution includes a pom file containing all Proton's on STORM dependencies. Since the project is already built as maven archtype, and includes maven additions for eclipse, it can be just imported into eclipse, and then built using "mvn clean install" command from the project repository dir. 

To execute the test case included with the project, please run: 
 mvn -f pom.xml compile exec:java -Dstorm.topology=com.ibm.hrl.proton.test.ProtonTopology
For farther documentation, please see <<>>

For documentation on Proton please see: << >>