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
package org.springframework.integration.comet.core.transport;

import org.springframework.integration.comet.core.CometMessage;
import org.springframework.integration.comet.core.CometMessagingException;

/**
 * Exception thrown by the {@link CometMessagingTransport} if there was some exception encountered
 * while sending or receiving a {@link CometMessage} over the transport 
 * @author Amol Nayak
 *
 */
public class CometMessagingTransportException extends CometMessagingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The endpoint URL which to which the transport failed 
	 */
	private String endpointURL;
	
	/**
	 * The CometMessage that failed to transport, relevant only in case of send
	 */
	private CometMessage cometMessage;	


	/**
	 * @param endpointURL
	 * @param cometMessage
	 */
	public CometMessagingTransportException(String endpointURL,
			CometMessage cometMessage) {
		super();
		this.endpointURL = endpointURL;
		this.cometMessage = cometMessage;
	}
	
	/**
	 * @param message
	 * @param cause
	 * @param endpointURL
	 * @param cometMessage
	 */
	public CometMessagingTransportException(String message, Throwable cause,
			String endpointURL, CometMessage cometMessage) {
		super(message, cause);
		this.endpointURL = endpointURL;
		this.cometMessage = cometMessage;
	}
	
	/**
	 * @param message
	 * @param endpointURL
	 * @param cometMessage
	 */
	public CometMessagingTransportException(String message, String endpointURL,
			CometMessage cometMessage) {
		super(message);
		this.endpointURL = endpointURL;
		this.cometMessage = cometMessage;
	}
	
	/**
	 * @param cause
	 * @param endpointURL
	 * @param cometMessage
	 */
	public CometMessagingTransportException(Throwable cause,
			String endpointURL, CometMessage cometMessage) {
		super(cause);
		this.endpointURL = endpointURL;
		this.cometMessage = cometMessage;
	}

	/**
	 * Gets the endpoint URL to which the transport failed to send or receive a message from 
	 * @return
	 */
	public String getEndpointURL() {
		return endpointURL;
	}

	/**
	 * Gets the comet message that failed to transport in case of send operation
	 * @return
	 */
	public CometMessage getCometMessage() {
		return cometMessage;
	}	
}
