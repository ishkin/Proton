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

import java.io.*;
import java.util.*;

public class StringFileSaver implements ISaver<String> {
	public static final String FILE_EXTENSION = ".json";
	private final static String LOG_FILE = "log.txt";
	
	private final String saveDir;
	private final ILogger _logger;
	private String getFileName(String id) {
		return saveDir + "/" + id + ".json"; //supports both windows and Linux 
		
	}
	
	
	public StringFileSaver(String saveDir) {
		new File(saveDir).mkdir();
		this.saveDir = saveDir;
    	Collection<ILogger> loggers = new LinkedList<ILogger>();
    	loggers.add(new ConsoleLogger());
    	loggers.add(new FileLogger(saveDir + "/" + LOG_FILE)); //supports both windows and Linux
    	_logger = new CompositeLogger(loggers);
	}
	
	@Override
	public void store(String id, String element) {
		FileWriter fstream = null;
		try {
			String fileName = getFileName(id);
			File file = new File(fileName);
			if (file.exists() == false) {
				file.createNewFile();
			}
			
			fstream = new FileWriter(fileName);
			fstream.write(element);
			_logger.log(id + " created with requested data at: " + file.getAbsolutePath());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (fstream != null) {
				try {
					fstream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String load(String id) throws IllegalArgumentException {
		FileInputStream inputStream = null;
		try {
			File file = new File(getFileName(id));
			byte[] buffer = new byte[(int) file.length()];
			inputStream = new FileInputStream(file);
			inputStream.read(buffer);
			_logger.log(id + " loaded sucessfully");
			return new String(buffer);
		} catch (IOException e) {
			throw new IllegalArgumentException("could not find element with id " + id);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	@Override
	public Collection<String> getIDs() {
		Collection<String> result = new LinkedList<String>();
		File[] files =  new File(saveDir).listFiles(new FileFilter() {
			@Override
			public boolean accept(File arg0) {
				return arg0.getName().endsWith(FILE_EXTENSION);
			}
		});
		
		for (File file: files) {
			String fileName = file.getName();
			result.add(fileName.substring(0, fileName.lastIndexOf('.')));
		}
		return result;
	}


	@Override
	public boolean exists(String id) {
		return new File(getFileName(id)).exists();
	}


	@Override
	public boolean delete(String id) {
		boolean result = new File(getFileName(id)).delete();
		_logger.log("delete attempt of " + id + " was successful: " + result);
		return result;
	}

}
