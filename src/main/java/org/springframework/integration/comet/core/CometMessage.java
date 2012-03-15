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

import java.util.Map;

/**
 * The message that will be sent out and received to and from the comet end point.
 * @author Amol Nayak
 *
 */
public class CometMessage {
	
	/**
	 * Contains the header values of the message being sent out
	 */
	private Map<String, CometHeaderValueContainer> headers;
	
	/**
	 * The payload of the message converted to string.
	 */
	private String payload;
	
	/**
	 * The Type of the original payload, if byte[], then the message will be
	 * Base64 decoded, if java.lang.String, the value will be used as is
	 */
	private String originalPayloadType;

	/**
	 * @param headers
	 * @param payload
	 * @param originalPayloadType
	 */
	public CometMessage(Map<String, CometHeaderValueContainer> headers,
			String payload, String originalPayloadType) {
		super();
		this.headers = headers;
		this.payload = payload;
		this.originalPayloadType = originalPayloadType;
	}

	/**
	 * 
	 */
	public CometMessage() {
		
	}



	public Map<String, CometHeaderValueContainer> getHeaders() {
		return headers;
	}

	public String getPayload() {
		return payload;
	}

	public String getOriginalPayloadType() {
		return originalPayloadType;
	}
	

	public void setHeaders(Map<String, CometHeaderValueContainer> headers) {
		this.headers = headers;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public void setOriginalPayloadType(String originalPayloadType) {
		this.originalPayloadType = originalPayloadType;
	}

	@Override
	public String toString() {
		return "CometMessage [headers=" + headers + ", payload=" + payload
				+ ", originalPayloadType=" + originalPayloadType + "]";
	}
	
	
	
}
