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

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.springframework.integration.comet.core.CometMessage;
import org.springframework.integration.comet.core.CometMessageListener;
import org.springframework.integration.comet.core.CometSubscription;

/**
 * The transport implementation using the Jetty's web socket implementation
 * 
 * @author Amol Nayak
 *
 */
public class CometMessagingJettyWebSocketTransport extends
	AbstractWebSocketTransport {

	private WebSocketClientFactory factory;	
	
	@Override
	protected void init() throws Exception {
		super.init();
		factory = new WebSocketClientFactory();
		//TODO: Keep this configurable
		factory.setBufferSize(4096);
		factory.start();
	}

	/* (non-Javadoc)
	 * @see org.springframework.integration.comet.core.transport.CometMessagingTransport#subscribe(java.lang.String, java.lang.String, java.lang.String, org.springframework.integration.comet.core.CometMessageListener)
	 */
	public CometSubscription subscribe(String topic,
			String expectedContentType, String expectedEncoding,
			final CometMessageListener<CometMessage> listener) {
		WebSocketClient client = factory.newWebSocketClient();		
		client.setProtocol(getProtocol());
		final Connection conn;
		try {
			conn = client.open(new URI(topic), new WebSocket.OnTextMessage() {
				
				public void onOpen(Connection connection) {
									
				}
				
				public void onClose(int arg0, String arg1) {				
					
				}
				
				public void onMessage(String message) {
					//TODO: Deserialize the message and deliver to the listener here
					CometMessage msg = deserializeMessage(message);
					if(msg != null)
						listener.onMessage(msg);
				}
			}).get();
		} catch (Exception e) {
			logger.error("Caught Exception while opening a connection to the web socket");
			throw new CometMessagingTransportException(e.getMessage(), e, topic, null);
		}
		
		
		return new CometSubscription() {
			
			private String uid = UUID.randomUUID().toString();
			
			public boolean unsubscribe() {
				conn.disconnect();
				return false;
			}
			
			public String getSubscriptionIdentifier() {				
				return uid;
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
		WebSocketClient client = factory.newWebSocketClient();
		client.setProtocol(getProtocol());
		final Connection conn;
		try {
			conn = client.open(new URI(endpointString), new WebSocket.OnTextMessage() {
				
				public void onOpen(Connection connection) {
									
				}
				
				public void onClose(int arg0, String arg1) {				
					
				}
				
				public void onMessage(String message) {
									
				}
			}).get();
		} catch (Exception e) {
			logger.error("Caught Exception while opening a connection to the web socket");
			throw new CometMessagingTransportException(e.getMessage(), e, endpointString, message);
		}
		try {
			conn.sendMessage(requestMessage);
		} catch (IOException e) {
			logger.error("Exception occurred while transporting the message over the web socket", e);
			throw new CometMessagingTransportException(e.getMessage(), e, endpointString, message);
		}
		conn.disconnect();
	}	
}
