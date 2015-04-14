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
package com.ibm.hrl.proton.utilities.containers;

import java.io.Serializable;


/**
 * Container for two values
* <code>Pair</code>.
* 
* 
* @param <K>
* @param <V>
 */
public class Pair<K,V> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final K firstValue;
	private final V secondValue;
	
	public Pair(K value1,V value2){
		this.firstValue = value1;
		this.secondValue = value2;
	}
	
	 
    public K getFirstValue() {
		return firstValue;
	}


	public V getSecondValue() {
		return secondValue;
	}


	public String toString() 
    {  
           return "(" + firstValue + ", " + secondValue + ")";  
    } 

    public int hashCode() { 
        int hashFirst = firstValue != null ? firstValue.hashCode() : 0; 
        int hashSecond = secondValue != null ? secondValue.hashCode() : 0; 
 
        return (hashFirst + hashSecond) * hashSecond + hashFirst; 
    } 
 
    public boolean equals(Object pair) { 
        if (pair instanceof Pair) { 
                Pair otherPair = (Pair) pair; 
                return  
                ((  this.firstValue == otherPair.firstValue || 
                        ( this.firstValue != null && otherPair.firstValue != null && 
                          this.firstValue.equals(otherPair.firstValue))) && 
                 (      this.secondValue == otherPair.secondValue || 
                        ( this.secondValue != null && otherPair.secondValue != null && 
                          this.secondValue.equals(otherPair.secondValue))) ); 
        } 
 
        return false; 
    } 
    
    public String getConcatenatedValue()
    {
    	return firstValue.toString()+secondValue.toString();    	
    }
}
