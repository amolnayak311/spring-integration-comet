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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.comet.core.transport.CometMessagingJettyWebSocketTransport;

/**
 * The test case for the service using web socket transport
 * @author Amol Nayak
 *
 */
public class WebSocketClientTest {

	private static Log logger = LogFactory.getLog(WebSocketClientTest.class);
	
	private CometMessagingServices<String> service;
	
	@Before
	public void init() throws Exception {
		if(service == null) {
			CometMessagingJettyWebSocketTransport transport = new CometMessagingJettyWebSocketTransport();
			transport.setProtocol("echo");
			service = new CometMessagingServices<String>();
			service.setTransport(transport);
			service.setTransformer(new StringToCometMessageTransformer());
			service.setEndpointUrl("ws://localhost:8080/");		
			service.afterPropertiesSet();
		}
		
	}
	
	@Test
	public void subcribe() throws Exception  {
		logger.info("\n\nSubscribing");
		final CometSubscription subscription = service.subscribe("echo", new CometMessageListener<String>() {
			
			public void onMessage(String message) {
				logger.info("Received message \"" + message + "\"");				
			}
		});
		//close after 8 seconds
		new Thread(new Runnable() {			
			public void run() {
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
				subscription.unsubscribe();
			}
		}).start();		
	}
	
	@Test
	public void sendToEndpoint() throws Exception{
		logger.info("\n\nSending to endpoint");
		for(int i = 0; i < 5;i++) {
			service.convertAndSend("Test Message", "echo");
			logger.info("Sent to endpoint");
			Thread.sleep(2000);
		}
	}
}
