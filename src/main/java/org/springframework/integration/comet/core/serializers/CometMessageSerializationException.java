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
package org.springframework.integration.comet.core.serializers;

import org.springframework.integration.comet.core.CometMessage;
import org.springframework.integration.comet.core.CometMessagingException;

/**
 * Will be thrown if message serialization to a String from a {@link CometMessage} and
 * from a {@link CometMessage} to a string fails
 *  
 * @author Amol Nayak
 *
 */
public class CometMessageSerializationException extends CometMessagingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The Comet message that failed to serialize 
	 */
	private CometMessage cometMessage;
	
	/**
	 * A Serialized string message that failed to convert to a 
	 * {@link CometMessage}
	 */
	private String serializedMessage;

	/**
	 * @param cometMessage
	 */
	public CometMessageSerializationException(CometMessage cometMessage) {
		super();
		this.cometMessage = cometMessage;
	}

	/**
	 * @param message
	 * @param cometMessage
	 */
	public CometMessageSerializationException(String message,
			CometMessage cometMessage) {
		super(message);
		this.cometMessage = cometMessage;
	}

	/**
	 * @param cause
	 * @param cometMessage
	 */
	public CometMessageSerializationException(Throwable cause,
			CometMessage cometMessage) {
		super(cause);
		this.cometMessage = cometMessage;
	}

	/**
	 * @param message
	 * @param cause
	 * @param cometMessage
	 */
	public CometMessageSerializationException(String message, Throwable cause,
			CometMessage cometMessage) {
		super(message, cause);
		this.cometMessage = cometMessage;
	}

	/**
	 * @param serializedMessage
	 */
	public CometMessageSerializationException(String serializedMessage) {
		super();
		this.serializedMessage = serializedMessage;
	}

	/**
	 * @param message
	 * @param serializedMessage
	 */
	public CometMessageSerializationException(String message,
			String serializedMessage) {
		super(message);
		this.serializedMessage = serializedMessage;
	}

	/**
	 * @param cause
	 * @param serializedMessage
	 */
	public CometMessageSerializationException(Throwable cause,
			String serializedMessage) {
		super(cause);
		this.serializedMessage = serializedMessage;
	}

	/**
	 * @param message
	 * @param cause
	 * @param serializedMessage
	 */
	public CometMessageSerializationException(String message, Throwable cause,
			String serializedMessage) {
		super(message, cause);
		this.serializedMessage = serializedMessage;
	}

	public CometMessage getCometMessage() {
		return cometMessage;
	}

	public String getSerializedMessage() {
		return serializedMessage;
	}	
}
