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
package com.ibm.hrl.proton.metadata.parser;

enum DefinitionType {
	ACTION,
	EVENT,
	TEMPORAL_CONTEXT,
	CONSUMER,
	PRODUCER,
	SEGMENTATION_CONTEXT,
	COMPOSITE_CONTEXT,
	EPA,
	PRA;
	
	@Override
	public String toString() {
		switch (this) {
			case ACTION: return "Action";
			case EVENT: return "Event";
			case TEMPORAL_CONTEXT: return "Temporal Context";
			case SEGMENTATION_CONTEXT: return "Segmentation Context";
			case COMPOSITE_CONTEXT: return "Composite Context";
			case CONSUMER: return "Consumer";
			case PRODUCER: return "Producer";
			case EPA: return "EPA";
			case PRA: return "PRA";
			default:  throw new IllegalArgumentException();
		}
	}
}
