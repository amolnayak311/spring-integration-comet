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
import org.atmosphere.container.Jetty7CometSupport;
import org.atmosphere.cpr.AtmosphereServlet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.comet.EmbeddedCometServer;

/**
 * The core comet messaging service test case
 * @author Amol Nayak
 *
 */
public class PublishSubscribeTest {
	
	private Log logger = LogFactory.getLog(PublishSubscribeTest.class);
	private CometMessagingServices<String> service;
	private EmbeddedCometServer server;
	
	@Before
	public void init() throws Exception {
		logger.info("Starting embedded server");
		server = new EmbeddedCometServer();
		server.initialize(8080);
		AtmosphereServlet servlet = new AtmosphereServlet();
		servlet.setCometSupport(new Jetty7CometSupport(servlet.getAtmosphereConfig()));
		servlet.addInitParameter("com.sun.jersey.config.property.packages", server.getClass().getPackage().getName());	
		server.addServlet(servlet, "/pubsub","/*");
		server.start();			
		
		logger.info("initializing comet service"); 
		service = new CometMessagingServices<String>();
		service.setTransformer(new StringToCometMessageTransformer());	
		//Starting TCP Mon, hence port is 8888, else change this to 8080
		service.setEndpointUrl("http://localhost:8888/pubsub/");
		service.setDefaultTopicName("topic");
		service.afterPropertiesSet();
	}
	
	@After
	public void destroy() throws Exception{
		logger.info("Stopping server");
		server.stop();
	}
	
	@Test
	public void publishAndSubscribe() throws Exception {
		new Thread(
			new Runnable() {
				public void run() {				
					service.subscribe("topic", new CometMessageListener<String>() {						
						public void onMessage(String message) {
							System.out.println("Received message \"" + message + "\"");							
						}
					});
				}
			}).start();
		Thread.sleep(1000);
		sendMessage();
		sendMessage();
		Thread.sleep(2000);
		//Some exceptions seen in the end as the embedded server also shuts down 
	}	

	public void sendMessage() throws Exception {
		String message = "String message";
		logger.info("sending message to comet endpoint");
		service.convertAndSend(message);		
	}
	
}
