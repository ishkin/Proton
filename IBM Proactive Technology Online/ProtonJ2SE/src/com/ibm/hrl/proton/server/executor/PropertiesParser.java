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
package com.ibm.hrl.proton.server.executor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import com.ibm.hrl.proton.server.executorServices.ExecutorUtils;
public class PropertiesParser {

	
	
	//TODO - as Inna if we want to assume default value ?
	public String metadataFileName;
	public String metadataFilePathType; //relative or absolute
	public int inputPortNumber;
	public int outputPortNumber;
	private static Logger logger = Logger.getLogger(ExecutorUtils.class.getName());
	
public PropertiesParser(String propertiesFileNamePath) throws ProtonConfigurationException{
	
	Properties properties = new Properties();
	
	try {
		
		properties.load(new FileInputStream(propertiesFileNamePath));
	} catch (FileNotFoundException e) {
		String msg = "Could not find the properties file : "+propertiesFileNamePath;
		logger.severe(msg);
        
		System.exit(1);
		
	} catch (IOException e) {
		String msg = "Could not open Proton.properties file ,reason : " +e+" message: "+e.getMessage();
        logger.severe(msg);
        System.exit(1);
	}
	loadProperties(properties);
	
	
	
	
}

public PropertiesParser(InputStream inputStream) throws ProtonConfigurationException{
	
	Properties properties = new Properties();
	
	try {
		properties.load(inputStream);
		//properties.load(new FileInputStream(propertiesFileNamePath));
	} catch (FileNotFoundException e) {
		String msg = "Could not find the properties file from stream  : "+inputStream;
		logger.severe(msg);
        
		System.exit(1);
		
	} catch (IOException e) {
		String msg = "Could not open Proton.properties file ,reason : " +e+" message: "+e.getMessage();
        logger.severe(msg);
        System.exit(1);
	}
	loadProperties(properties);
	
	
	
	
}

private void loadProperties(Properties properties)
		throws ProtonConfigurationException, NumberFormatException {
	//Load properties
	metadataFileName=properties.getProperty("metadataFileName");	
	validateProperty (metadataFileName,"metadataFileName");

	metadataFilePathType=properties.getProperty("metadataFilePathType");	
	validateProperty (metadataFilePathType,"metadataFilePathType");
	
	String sPortNum=properties.getProperty("inputPortNumber");
	validateProperty (sPortNum,"inputPortNumber");
	inputPortNumber=Integer.valueOf((sPortNum));
	
	sPortNum=properties.getProperty("outputPortNumber");
	validateProperty (sPortNum,"outputPortNumber");
	outputPortNumber=Integer.valueOf((sPortNum));
	String msg = "Properties are : \n metadatFile ="+metadataFileName+" \n" +
			" inputPortNumber ="+inputPortNumber+" \n outputPortNumber ="+outputPortNumber;
	logger.info(msg);
}


private void validateProperty(String propertyValue,String propertyName) throws ProtonConfigurationException {
	//In case metadataFileName property is missing 
	if (propertyValue==null)
	{
		throw new ProtonConfigurationException(propertyName+" property is missing in Proton.properties file");
		
	}
	if(propertyValue.equals(""))
		throw new ProtonConfigurationException(propertyName+" property is not populated in Proton.properties file");
	
}
}


// Try to run directly with main or executor name w/o the servers up....
