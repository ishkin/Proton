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

import java.util.Set;
import java.util.TreeSet;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class SampleJMXClient {

	

	public SampleJMXClient() {
	
	  final String host = "localhost";
	// should be equal to the com.sun.management.jmxremote.port in startup.bat
	  final String port = "3250";

	  try {
		JMXServiceURL url = new JMXServiceURL(
				"service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
		
		JMXConnector jmxConnector = JMXConnectorFactory.connect(url);		 
		MBeanServerConnection mbsc = jmxConnector.getMBeanServerConnection();
		//ObjectName mbeanName = new ObjectName("com.jmxtest:type=TestJmx");
		 
		/*String domains[] = mbsc.getDomains();
		System.out.println("number of domains: " + mbsc.getMBeanCount());
		for (String domain : domains) {
		    System.out.println("Domain = " + domain);
		}*/	
		
		System.out.println("Query MBeanServer MBeans:");
		ObjectName query = new ObjectName("Catalina:j2eeType=WebModule,*");
		Set<ObjectName> mbeans = new TreeSet<ObjectName>(mbsc.queryNames(query,null));
		for (ObjectName mbean: mbeans) {
			String displayName = mbsc.getAttribute(mbean,"displayName").toString(); 
			if (displayName.equals("Proton Web Application")) {
				System.out.println("ObjectInstance = " + mbean.toString());

				System.out.println("Proton instance name = " +
						mbsc.getAttribute(mbean,"name").toString() + " and state: " +
						mbsc.getAttribute(mbean,"stateName").toString());

			}
		}
		
		// get MBeanServer's default domain
		//String domain = mbsc.getDefaultDomain();
		//MBeanServer server = (MBeanServer)MBeanServerFactory.findMBeanServer(null).get(0);

		//ObjectName managerMBean = new ObjectName("Catalina:j2eeType=Servlet,name=Manager," +
		//	"WebModule=//localhost/manager,J2EEApplication=none,J2EEServer=none");
		//System.out.println("Retrieving application state for " + managerMBean.toString());
		//System.out.println(mbsc.getAttribute(managerMBean,"State"));
		
		//System.out.println("Changing application state for " + managerMBean.toString());
		//mbsc.invoke(protonMBeanName,"stop",null,null);          
		  
		  
		  
		jmxConnector.close();
	  
	  } catch (Exception e) {
		  System.out.println("Could not manipulate MBean, message: " + e.getMessage() +
				  ", stack trace: " + e.getStackTrace());
		  System.exit(-1);
	  }
	}
		
	
	public static void main(String[] args) {
		
		SampleJMXClient jmxClient = new SampleJMXClient();
		
	}
}
