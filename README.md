#<a name="top"></a>CEP - Complex Event Processing

[![License badge](https://img.shields.io/hexpm/l/plug.svg)](https://opensource.org/licenses/Apache-2.0)
[![Documentation badge](https://img.shields.io/badge/docs-latest-brightgreen.svg?style=flat)](http://proactive-technology-online.readthedocs.org/en/latest/Proton-InstallationAndAdminGuide/index.html)
[![Docker badge](https://img.shields.io/docker/pulls/fiware/proactivetechnologyonline.svg)](https://hub.docker.com/r/fiware/proactivetechnologyonline/)
[![Support badge]( https://img.shields.io/badge/support-sof-yellowgreen.svg)](http://stackoverflow.com/questions/tagged/fiware-cep)

* [Introduction](#introduction)
* [GEi overall description](#gei-overall-description)
* [Build and Install](#build-and-install)
* [API Reference Documentation](#api-reference-documentation)
* [License](#license)
* [Support](#support)

## Introduction
IBM Proactive Technology Online (PROTON) is an open source complex event processing engine developed at IBM Research - Haifa. PROTON was developed as a research project and extended in the scope of the following EU projects: FINEST, FISPACE, FIWARE, SPEEDD, FERRARI, PSYMBIOSYS and DATABIO. It provides language primitives for defining,
submitting, and executing event processing networks. The goal of the system is to respond to raw events and identify meaningful events within contexts. 
The system comes with a set of built-in operators (such as sequence, all, etc.) for determining CEP patterns. 
It also has extendable APIs for adding additional custom operators. The system comes with existing source/sink adapters, allowing it to extract raw events from files or pull 
them from RESTful services. It also provides extendable APIs for adding more adapter types. 

[Top](#top)

## GEi overall description

This project is part of [FIWARE](https://www.fiware.org/).
* [For more information about this project see] (http://catalogue.fiware.org/enablers/complex-event-processing-cep-proactive-technology-online) - The FIWARE catalogue. 
  * In the catalogue you can find several ways to use the Proactive Technology Online Generic Enabler instance (creating instance, using existing instances, downloads the code). 
* [To install the Proactive Technology Online] (http://proactive-technology-online.readthedocs.org/en/latest/Proton-InstallationAndAdminGuide/index.html) - An installation guide at ReadTheDocs. This guide includes running instructions and sanity checks procedures. 
* Docker installation for the Proactive Technology Online is provided in the docke/ folder.
* [For more advanced tests, follow] (https://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/CEP_GE_-_IBM_Proactive_Technology_Online_Unit_Testing_Plan) - unit tests.
* [For general information on this technology] (http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/FIWARE.OpenSpecification.Data.CEP) - CEP open specification page.
* [A high level description of the technology and an api overview can be found included in this comprehensive documentation] (https://www.fiware.org/devguides/real-time-processing-of-context-events/) - FIWARE developers’ tour guide.
* [A complete set of the REST api] (http://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Complex_Event_Processing_Open_RESTful_API_Specification), or in [apiary format] (http://htmlpreview.github.io/?https://github.com/ishkin/Proton/blob/master/documentation/apiary/CEP-apiary-blueprint.html).
* [Online documents] (http://proactive-technology-online.readthedocs.org/en/latest/index.html) - Proactive Technology Online documents are published at [ReadTheDocs] (http://proactive-technology-online.readthedocs.org/en/latest/ProtonUserGuide_FI_WARE5_4_1/index.html) - User Guide, and the [Programmer Guide] (http://proactive-technology-online.readthedocs.org/en/latest/ProtonProgrammerGuide_FI_WAREv4_4_1/index.html). 
* [A webinar introducing CEP concepts in general and Proactive Technology Online] https://github.com/ishkin/Proton/blob/master/CEP-FI-WARE-3.2-Tutorial.mp4
* More documentation is provided under the documentation folder, as described below. 
  * [Education material] (https://edu.fiware.org/course/view.php?id=58) - FIWARE academy.

[Top](#top)

## Build and Install
There are several options:
* The Proton standalone version:
  * The Proton J2SE version,  is which is just a simple Java app running the Proton engine. This can be found under ProtonJ2SE project.
  * The Proton web version provides a RESTful interface for sending events, as well as the admin app for managing Proton apps; Those are 
the ProtonOnWebServer and ProtonOnWebServerAdmin projects, respectively.
This version of the engine is adapted to run on a web server. It allows users to push RESTful events to the engine in addition to pulling 
**RESTful events** options provided by the REST adapter. This version also provides REST APIs for managing the engine’s instance lifecycle, and to manage the definition repository.
* The Proton web UI,  provides an authoring environment for Proton applications; Those are the AuthoringTool and AuthoringToolWebServer projects.
* Puppet installation script. Download and unzip the puppet.zip file. This folder is used for installation of the web version on new ubuntu machine with or without puppet installed on it. To run without puppet installed, run the script miscalleneous/CEP_Install_via_puppet.sh
* The Proton on STORM version, which allows to run the engine in a distributed manner on multiple machines using the STORM infrastructure.

**To work with artifacts** 

All build artifacts are in the [Proton github repository] (https://github.com/ishkin/Proton/tree/master/mvn-repo/com/ibm/hrl/proton). These can be used as dependencies in maven builds. 
In case you would like to work with simple artifacts, you can find these [here](https://github.com/ishkin/Proton/tree/master/artifacts)

For standalone engines, use the [ProtonJ2SE artifact] (https://github.com/ishkin/Proton/tree/master/artifacts/ProtonJ2SE.zip). This is a .zip distribution, with ProtonJ2SE executable jar, launch script, sample, and docs directory. 

For instructions on how to configure and install IBM Proactive Technology Online on Tomcat (web version), see the [Proton on Tomcat guide] (https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/CEP_GE_-_IBM_Proactive_Technology_Online_Installation_and_Administration_Guide)

[Top](#top)

## API Reference Documentation

Detailed online documentation for installation, testing, web authoring of rules and web APIs are located in https://readthedocs.org/projects/proactive-technology-online/builds/
Documentations is also available under the /documentation folder, and includes the Proton's User Guide (explaining the language building blocks and use of the authoring tool use) and Programmer's Guide (explaining possible extension points to the Proton's programming model)

[Top](#top)

## License
IBM Proactive Technology Online is licenced under the Apache Licence Version 2.0. For more information see the LICENCE.md

[Top](#top)

## Support 
For working with source code, note that the projects are maven projects. 
They can be built using the "clean install" targets by running the mvn command on the parent pom (located in the "Proton”) directory. 
This command will build all the Proton projects.

In addition, by executing the "mvn deploy" 	command of the same parent pom after performing clean install, the target jars will be installed into a local repository named "mvn-repo" (/Proton/maven-repo).

[Top](#top)
