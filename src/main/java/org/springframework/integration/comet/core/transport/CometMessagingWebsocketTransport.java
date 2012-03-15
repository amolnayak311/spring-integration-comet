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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java_websocket.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.integration.comet.core.CometMessage;
import org.springframework.integration.comet.core.CometMessageListener;
import org.springframework.integration.comet.core.CometSubscription;
import org.springframework.integration.comet.core.serializers.CometMessageSerializationException;
import org.springframework.integration.comet.core.serializers.CometMessageSerializer;



/**
 * The web socket implementation of the transport
 * 
 * @author Amol Nayak
 *
 */
public class CometMessagingWebsocketTransport extends
		AbstractCometMessagingTransport {

	/* (non-Javadoc)
	 * @see org.springframework.integration.comet.core.transport.CometMessagingTransport#subscribe(java.lang.String, java.lang.String, java.lang.String, org.springframework.integration.comet.core.CometMessageListener)
	 */
	public CometSubscription subscribe(final String topic,
			String expectedContentType, String expectedEncoding,
			CometMessageListener<CometMessage> listener) {
		final WsClient client;
		try {
			client = new WsClient(topic, listener,this);
		} catch (URISyntaxException e) {
			String errorMessage = "Invalid topic string provided \"" + topic + "\"";
			logger.error(errorMessage,e);
			throw new CometMessagingTransportException(errorMessage, e, topic, null);
		}
		client.connect();
		return new CometSubscription() {
			private String uniqueId = UUID.randomUUID().toString();
			
			public boolean unsubscribe() {				
				client.close();				
				return true;
			}
			
			public String getSubscriptionIdentifier() {
				return uniqueId;
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.springframework.integration.comet.core.transport.AbstractCometMessagingTransport#doSend(java.lang.String, java.util.Map, java.lang.String, java.lang.String, org.springframework.integration.comet.core.CometMessage)
	 */
	@Override
	protected void doSend(String endpointString,
			Map<String, String> requestHeaders, String requestMessage,
			String contentEncoding, CometMessage message) {
		CometMessageSerializer serializer = getSerializerForContentType(null);
		String serializedString = serializer.serialize(message);
		try {
			WsClient client = new WsClient(endpointString);
			client.connect();
			try {
				//TODO: Remove this later, if this is removed, the end doesn't happen, see why this happens
				Thread.sleep(2000);
				client.send(serializedString);
			} catch (InterruptedException e) {				
				throw new CometMessagingTransportException("Interrupted while sending message", e, endpointString, null);
			}
			client.close();
		} catch (URISyntaxException e) {
			String errorMessage = "Malformed endpoint URL \"" + endpointString + "\" given";
			logger.error(errorMessage, e);
			throw new CometMessagingTransportException(errorMessage,e,endpointString,null);
		}
		System.out.println("");
	}

}

class WsClient extends WebSocketClient {
	
	private String endpoint;
	private CometMessageListener<CometMessage> listener;
	private AbstractCometMessagingTransport transport;
	
	public WsClient(String endpoint) throws URISyntaxException {
		this(endpoint,null,null);
	}

	
	public WsClient(String endpoint,CometMessageListener<CometMessage> listener,AbstractCometMessagingTransport transport) throws URISyntaxException {
		super(new URI(endpoint));
		this.endpoint = endpoint;
		this.listener = listener;
		this.transport = transport;
	}

	private Log logger = LogFactory.getLog(WsClient.class);
	
	
	
	
	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		logger.info("Closing connection to endpoint \"" + endpoint + "\"");		
	}


	@Override
	public void onError(Exception e) {
		logger.error("Caught exception ",e);
		
	}


	@Override
	public void onOpen(ServerHandshake hs) {
		logger.info("Opening connection to endpoint \"" + endpoint + "\"");		
	}
	
	@Override
	public void onMessage(String message) {
		if(transport == null || listener == null)
			return;
		//This will always get the default serializer
		//TODO: How can we get the content type dynamically and lookup for a serializer
		CometMessageSerializer serializer = transport.getSerializerForContentType(null);
		try {
			CometMessage cometMessage = serializer.deserialize(message);			
			listener.onMessage(cometMessage);
		} catch (CometMessageSerializationException e) {
			logger.error("Failed to deserialize the string \""	+ message + "\"", e);
		}
	}	
}