#Proactive Technology Online Installation and Administration Guide

This document describes how to install and configure the Proactive Technology Online (aka Proton) on a web server. This is the way the Proactive Technology Online is operated as the CEP GE in FIWARE. 

The Proton runtime engine detects patterns on incoming events, and the Proton authoring tool is a web-based user interface in which CEP applications can be defined and deployed to the engine.

##Prerequisites

The Proactive Technology Online is a standard web application. It requires the previous installation of the following:

1. Java SE 7 or later
2. Apache Tomcat 7  

Proton was tested on Apache Tomcat 7.0.26 with Java SE 7, and apache-tomcat-7.0.59 with Java SE 8. Please note that newer Java version requires newer apache-tomcat version.

###<a name="setup"></a>Setup Apache Tomcat for Management
* In Linux, make sure CATALINA_HOME is defined as an environment variable pointing to the Apache Tomcat directory (e.g., /opt/apache-tomcat-7.0.26)
* Configure manager access to application. Instructions are available in the Apache Tomcat 7 section on [Configuring Manager Application Access]. 

As a suggested reference, include the following in the file  **./conf/tomcat-users.xml** stored under the Apache Tomcat directory:

    <tomcat-users>
        <role rolename="manager-gui" />
        <role rolename="manager-status" />
        <role rolename="manager-script" />
        <role rolename="manager-jmx" />
        <user username="manager" password="manager" roles="manager-gui,manager-status,manager-script,manager-jmx" />
        <role rolename="admin-gui" />
        <user username="admin" password="admin" roles="admin-gui" /> 
    </tomcat-users>

* Enable JMX access on Apache Tomcat. Instructions are available in the Apache Tomcat 7 section on [Enabling JMX Remote]. 

**In Windows:** As a suggested reference for a Windows system, add the following to the file  **./bin/startup.bat** located in the Apache Tomcat directory:
 
    set CATALINA_OPTS=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8686 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false
    set JRE_HOME={java_install_dir}\Java\jre

**In Linux:** As a suggested reference for a Linux system, add the following to the file **./bin/startup.sh** located in the Apache Tomcat directory: 

    CATALINA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8686 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
    JRE_HOME={java_install_dir}/jre

**Note:** The actual **jmxremote.port** number maybe different. The Apache Tomcat default JMX port number is 8686. This value is used in [configuring the Proton administration application](#config-admin-app).

##Installation

The CEP requires 4 wars (ProtonOnWebServerAdmin.war, ProtonOnWebServer.war, AuthoringTool.war, AuthoringToolWebServer.war).
Download the 4 wars from the [CEP open source repository]. The repository also contains a [sample folder] that you can use.

Deploy the four war files (ProtonOnWebServerAdmin.war, ProtonOnWebServer.war, AuthoringTool.war, AuthoringToolWebServer.war) to the Apache Tomcat server. 
Instructions on how to deploy applications to an Apache Tomcat server can be found at [How to Deploy]. As a suggested reference, you may drop the four war files in the Apache Tomcat installation directory under **./webapps** while the server is running. They should be deployed automatically by the server soon after. 

**Note:** You need to perform the [configuration](#config) instructions before the Proactive Technology Online will work as expected.

###Installing a New Proton Instance
In case you need more than one instance of Proton to run on the same web server, follow the next steps.
1. Rename **ProtonOnWebServer.war**, provided by the install package.
2. Deploy the renamed war on the Apache Tomcat server.
3. Follow the [configuration instructions for an engine instance](#config-engine). 
4. Restart the application if required.

##<a name="config"></a>Configuration

### <a name="config-admin-app"></a> Configuring the Administration Application

The Proton administration application is used for the management of definitions, event processing networks, the repository (adding, updating, and deleting definitions), and the multiple Proton instances (updating an instance definition, retrieving an instance state, and starting\stopping an instance). The prerequisite installation instructions for setting up Apache Tomcat for management are crucial for the appropriate functioning of the admin application. 

After successful deployment of the admin application, the following must be configured in the **ProtonAdmin.properties** file located under the Apache Tomcat directory in **./webapps/ProtonOnWebServerAdmin**:

* Location of the definitions repository. The Proton projects definition files are stored in and read from this directory (make sure there are relevant credentials for read\write access).

        definitions-repository={directory_of_choice}

* Apache Tomcat manager authentication details, according to the suggested reference in the [Setup Apache Tomcat for Management](#setup) section.

        manager-username=manager
        manager-password=manager 

* Server port number of the Apache Tomcat server (default for Apache Tomcat is **8080**)

        tomcat-server-port={port}

* JMX services port of the Apache Tomcat server (default for Apache Tomcat is **8686** and should be the same as for the **jmxremote.port** property set in [Setup Apache Tomcat for Management](#setup).

        tomcat-jmx-port={port}

###<a name="config-engine"></a>Configuring an Engine Instance 

There are two files used for configuring an engine instance. Both files are located in the Apache Tomcat directory under **./webapps/{instance_name}**, where **{instance_name}** is an identifier of a Proton instance , e.g., **ProtonOnWebServer**.

* The file **Proton.properties** contains two port properties for the input and output adapters. Each engine instance should have a different value for these properties. The other properties should not be manually configured.
* The file **Logging.properties**

###Configuring the Authoring Tool

No configuration is required.

###Configuring Input and Output Adapters

Instructions for defining the input and output adapters to receive raw events and send derived events are described in [the programmer guide]. The sample application provided with the installation uses pre-defined file input and output adapters that are ready for a sanity check.

##Running

Once the prerequisites, installation, and configurations instructions have been completed, Proton should be up and running. In general, starting up the Apache Tomcat server and starting the applications (AuthoringTool, AuthoringToolWebServer, ProtonOnWebServerAdmin, and Proton engine instances) constitutes a running product.

* The authoring tool can be accessed through the following link (after completing the host and port values): http://{host}:{port}/AuthoringTool/Main.html.
* Administrating the product and pushing events to the engine instances are described in the preliminary version of the [Complex Event Processing Open RESTful API specification]. 

#Sanity Check Procedures
##End-to-End Testing
To verify that the Proactive Technology Online is running:

1. Access the Apache Tomcat administrator tool via http://{host}:{port}/manager and log in with the user and password you configured in [Setup Apache Tomcat for management](#setup). Identify that all applications are installed and running (AuthoringTool, AuthoringToolWebServer, ProtonOnWebServerAdmin, and all Proton engine instances, e.g., ProtonOnWebServer).
2. Access the authoring tool via http://{host}:{port}/AuthoringTool/Main.html (tested on Google Chrome).

If you want to test the engine on a sample definition project, run [unit test 1]. 

##List of Running Processes
* Apache Tomcat server running as a Java process.

##Network Interfaces Up and Open
* Apache Tomcat server and JMX ports are in use (default 8080 and 8686 respectively)
* Each Proton engine instance uses two ports, one for input and one for output adapters, as configured in [Configuring an Engine Instance](#config-engine). The single instance called **ProtonOnWebServer** provided with the base installation has the following ports as default: 3002, 3302.

##Databases
No database is used in this release.

#Diagnostic Procedures
Proactive Technology Online uses Apache Tomcat logging. The log files are located in the Apache Tomcat directory **./logs**. 

##Resource Availability
* The required RAM is dependent on the event processing patterns defined by the event processing network and by the size and number of events that need to be held on to for detecting the patterns. Fortunately, a basic box available off-the-shelf is sufficient for most of the applications.
* Usually the disk size required during run time is negligible, unless the application uses adapters of type "File" and the input files or the generated output files are very big.

##Remote Service Access
Currently, the CEP GE has integration with the Context Broker GE.

##Resource Consumption
The resource consumption is highly dependent on the defined CEP application and on the event streams that are processed. There are no typical numbers.

##I/O Flows
* Ports 8080 and 8686 are used for Proton administration and working with the authoring tool.
* The input (e.g., 3002) and output (e.g., 3302) ports configured for engine instances (see [Configuring an Engine Instance](#config-engine) are used for receiving and sending notifications about events and integrating with other systems as producers and consumers of events. Most traffic is observed on these ports.



[Configuring Manager Application Access]: http://tomcat.apache.org/tomcat-7.0-doc/manager-howto.html#Configuring_Manager_Application_Access
[Enabling JMX Remote]: http://tomcat.apache.org/tomcat-7.0-doc/monitoring.html#Enabling_JMX_Remote
[How to Deploy]: http://tomcat.apache.org/tomcat-7.0-doc/deployer-howto.html
[CEP open source repository]: https://github.com/ishkin/Proton/tree/master/artifacts
[sample folder]: https://github.com/ishkin/Proton/tree/master/documentation/sample 
[the programmer guide]: https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/CEP_GE_-_IBM_Proactive_Technology_Online_User_and_Programmer_Guide#Programmer_Guide
[Complex Event Processing Open RESTful API specification]: https://forge.fiware.org/plugins/mediawiki/wiki/fiware/index.php/Complex_Event_Processing_Open_RESTful_API_Specification_(PRELIMINARY)
[unit test 1]: https://forge.fi-ware.org/plugins/mediawiki/wiki/fiware/index.php/CEP_GE_-_IBM_Proactive_Technology_Online_Unit_Testing_Plan#Unit_Test_1 