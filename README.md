## Synapsis
IBM Proactive Technology Online is an open source complex event processing engine developed at IBM Research - Haifa. It provides language primitives for defining,
submitting, and executing event processing networks. The goal of the system is to respond to raw events and identify meaningful events within contexts. 
The system comes with a set of built-in operators (such as sequence, all, etc.) for determining CEP patterns. 
It also has extendable APIs for adding additional custom operators. The system comes with existing source/sink adapters, allowing it to extract raw events from files or pull 
them from RESTful services. It also provides extendable APIs for adding more adapter types. 

FIWARE related information.

This project is part of https://www.fiware.org/[FIWARE].
More information about this project is provided in the http://catalogue.fiware.org/enablers/complex-event-processing-cep-proactive-technology-online[FIWARE catalogue]. 

- In the catalogue you can find several ways to use the Proactive Technology Online Generic Enabler instance (creating instance, using existing instances, downloads the code). 
- If you like to install the Proactive Technology Online, follow the http://proactive-technology-online.readthedocs.org/en/latest/Proton-InstallationAndAdminGuide/index.html[installation guide at ReadTheDocs]. This guide includes running instructions and sanity checks procedures. 
- You can also install the Proactive Technology Online using a docker installation that is provided in the docker folder.
- For more advanced tests, you can follow the https://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/CEP_GE_-_IBM_Proactive_Technology_Online_Unit_Testing_Plan[unit tests].
- General information on this technology can be found in the http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.Data.CEP[CEP open specification page].
- A high level description of the technology and an api overview can be found as part of the https://www.fiware.org/devguides/real-time-processing-of-context-events/[FIWARE developers’ tour guide].
- A complete set of the REST api can be found http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Complex_Event_Processing_Open_RESTful_API_Specification[here], or in apiary format http://htmlpreview.github.io/?https://github.com/ishkin/Proton/blob/master/documentation/apiary/CEP-apiary-blueprint.html[here].
- The http://proactive-technology-online.readthedocs.org/en/latest/index.html[Proactive Technology Online documents] are published at ReadTheDocs. You can find there the technology http://proactive-technology-online.readthedocs.org/en/latest/ProtonUserGuide_FI_WAREv4_4_1/index.html[User Guide] and its http://proactive-technology-online.readthedocs.org/en/latest/ProtonProgrammerGuide_FI_WAREv4_4_1/index.html[Programmer Guide]. 
- More documentation is provided under the documentation folder, as described below. 
- Education material can be found at the https://edu.fiware.org/course/view.php?id=58[FIWARE academy].

## Installation
There are several options:
.The Proton standalone version:
- The Proton J2SE version,  is which is just a simple Java app running the Proton engine. This can be found under ProtonJ2SE project.
- The Proton web version provides a RESTful interface for sending events, as well as the admin app for managing Proton apps; Those are the ProtonOnWebServer and ProtonOnWebServerAdmin projects, respectively.
This version of the engine is adapted to run on a web server. It allows users to push RESTful events to the engine in addition to pulling 
RESTful events options provided by the REST adapter. This version also provides REST APIs for managing the engine’s instance lifecycle, and to manage the definition repository.
- The Proton web UI,  provides an authoring environment for Proton applications; Those are the AuthoringTool and AuthoringToolWebServer projects.
- Puppet installation script. Download and unzip the puppet.zip file. This folder is used for installation of the web version on new ubuntu machine with or without puppet installed on it. To run without puppet installed, run the script miscalleneous/CEP_Install_via_puppet.sh

- The Proton on STORM version, which allows to run the engine in a distributed manner on multiple machines using the STORM infrastructure.

To work with artifacts, 
The https://github.com/ishkin/Proton/tree/master/mvn-repo/com/ibm/hrl/proton contains all the built artifacts. Those can be used as dependencies in maven builds. 
In case you would like to work with simple artifacts, you can find those under :  https://github.com/ishkin/Proton/tree/master/artifacts

For standalone engines, the ProtonJ2SE artifact (located in https://github.com/ishkin/Proton/tree/master/artifacts/ProtonJ2SE.zip )   is a .zip distribution, with ProtonJ2SE executable jar, launch script, sample, and docs directory. 

For instructions on how to configure and install IBM Proactive Technology Online on Tomcat (web version), see the following guide: https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/CEP_GE_-_IBM_Proactive_Technology_Online_Installation_and_Administration_Guide

## API Reference
Detailed online documentation for installation, testing, web authoring of rules and web APIs are located in https://readthedocs.org/projects/proactive-technology-online/builds/
Documentations is also available under the /documentation folder, and includes the Proton's User Guide (explaining the language building blocks and use of the authoring tool use) and Programmer's Guide (explaining possible extension points to the Proton's programming model)

## Contributors 
For working with source code, note that the projects are maven projects. 
They can be built using the "clean install" targets by running the mvn command on the parent pom (located in the "Proton”) directory. 
This command will build all the Proton projects.

In addition, by executing the "mvn deploy" 	command of the same parent pom after performing clean install, the target jars will be installed into a local repository named "mvn-repo" (/Proton/maven-repo).

## License
IBM Proactive Technology Online is licenced under the Apache Licence Version 2.0. For more information see the LICENCE.md
