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

import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;

/**
 * The echo server that will echo the content sent to it back to the client
 * 
 * @author Amol Nayak
 *
 */
public class WebSocketEchoServerTest {
	
	private static Log logger = LogFactory.getLog(WebSocketEchoServerTest.class);
	
	public static void main(String[] args) {
		logger.info("Starting server to listen to port 8080");
		CometServer server = new CometServer(8080);
		server.start();
	}	
}


class CometServer extends WebSocketServer {
	
	private Log logger = LogFactory.getLog(CometServer.class);

	public CometServer(int port) {
		super(new InetSocketAddress(port));
	}

	
	@Override
	public void onClose(WebSocket sock, int arg1, String arg2, boolean arg3) {
		logger.info("Subscriber Left");
	}


	@Override
	public void onError(WebSocket sock, Exception e) {
		logger.error("Caught Exception ", e);
		
	}


	@Override
	public void onMessage(WebSocket sock, String message) {
		try {
			broadcast(message);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		
	}


	@Override
	public void onOpen(WebSocket arg0, ClientHandshake arg1) {
		logger.info("Subscriber joined");		
	}
	
	private void broadcast(String message) throws InterruptedException {
		for(WebSocket socket: connections()) {			
			socket.send(message);			
		}
	}
}

