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
import org.springframework.integration.comet.core.CometMessageListener;
import org.springframework.integration.comet.core.CometSubscription;

/**
 * The implementation class provides the transport for the {@link CometMessage} to
 * the endpoint and from the endpoint to the callback registered. This interface provides
 * an abstraction over the means of transporting the comet messages
 * 
 * @author Amol Nayak
 *
 */
public interface CometMessagingTransport {
	
	/**
	 * Implementation class will transport this {@link CometMessage} to the endpoint provided
	 * throws {@link CometMessagingTransportException} if the message cannot be transported
	 * 
	 * @param endpointUrl The endpoint URL to which the message is to be sent
	 * @param contentType The content type of the request to be sent
	 * @param message the Comet Message to be posted
	 * 
	 */
	void send(String endpointUrl,String contentType,CometMessage message);
	
	/**
	 * Subscribe to the given endpoint URL receive the messages over
	 * throws {@link CometMessagingTransportException} if the received message is garbled
	 * and/or cannot be delivered to the listener for any other reason
	 * 
	 * @param topic
	 * @param expectedContentType
	 * @param expectedEncoding
	 * @param listener delivered comet messages to the callback method onMessage of the listener
	 */
	CometSubscription subscribe(String topic,String expectedContentType,String expectedEncoding,CometMessageListener<CometMessage> listener);
}
