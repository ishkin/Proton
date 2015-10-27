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
/**
 * 
 */
package com.ibm.hrl.proton.metadata.type.enums;

/**
 * @author zoharf
 *
 */
public enum ChrononEnum {

	MILISECOND (1.),
	SECOND (1000.),
	MINUTE (60000.),
	HOUR (3600000.);
	
	protected double miliRatio;
	private ChrononEnum(double miliRatio)
	{
		this.miliRatio = miliRatio;
	}
	
	public double toMiliSecond(double time)
	{
		return miliRatio * time;
	}
	
	
}
