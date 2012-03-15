/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.comet.core;

/**
 * The class that holds the value of the header of the comet message.
 * 
 * @author Amol Nayak
 *
 */
public class CometHeaderValueContainer {

	/**
	 * The field holds the header value serialized as string, this would either be 
	 * converted from the source object to string using spring's conversion service
	 * or be a Base64 encoded value of the byte[]
	 */	
	private String headerValue;
	
	/**
	 * Stores the original value's type of the header, the value 
	 * will be converted to and from the type to String and this type
	 * using conversion service, if the type is byte[], then the 
	 * value will be Base64 decoded, if String, it will be used as it is
	 */
	private String originalValueType;
	
	/**
	 * @param headerValue
	 * @param originalValueType
	 */
	public CometHeaderValueContainer(String headerValue,
			String originalValueType) {		
		this.headerValue = headerValue;
		this.originalValueType = originalValueType;
	}	

	/**
	 * 
	 */
	public CometHeaderValueContainer() {
		super();		
	}



	public String getHeaderValue() {
		return headerValue;
	}

	public String getOriginalValueType() {
		return originalValueType;
	}	

	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}

	public void setOriginalValueType(String originalValueType) {
		this.originalValueType = originalValueType;
	}

	@Override
	public String toString() {
		return "CometHeaderValueContainer [headerValue=" + headerValue
				+ ", originalValueType=" + originalValueType + "]";
	}
	
	
}
