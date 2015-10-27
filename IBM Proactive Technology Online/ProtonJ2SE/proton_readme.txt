#-------------------------------------------------------------------------------
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
#-------------------------------------------------------------------------------
How to run the application ?
To run the application with the sample definition file run launchProton.bat
What output should you expect ?
In case you haven't changed the default setting,  In ./sample directory you will see 3 output files:
DoSAttack_TrafficReport.txt -> a file representing a consumer for the traffic report events that are sent as an input
DoSAttack_PredictedCrash.txt -->  a file representing a consumer for the predicted crash future event that is generated during the run 
DoSAttack_Actions.txt--> a file representing a consumer for the actions generated during the run. the sample scenario simulates a situation where Block90Action is expected.
How to change the logging level ?
under ./config you can find logging.properties file.
currently the console logging level is set to INFO as following :
java.util.logging.ConsoleHandler.level=INFO
Change that according to your preferences. 
Proton.zip includes the following files and folders:
launchProton.bat - a sample bat file that launches proton application. by default the application uses as an input  .\config\Proton.properties file
any other properties file should be given as an input.
ProtonApp.jar - proton application jar
config folder- 
contains Proton.properties. properties file for Proton configuration. contains path to metadata file and port numbers for Proton socket servers
sample folder
Contains a sample definition and scenario.more about that in the user guide.
docs folder -
Contains Proton user guide
lib folder -
contains external libraries required by Proton application
test
