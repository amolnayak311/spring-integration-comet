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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;

/**
 * The web socket servlet
 * @author Amol Nayak
 *
 */
public class WSServlet extends HttpServlet {
	
	private WebSocketFactory wsFactory;
	private final Set<EchoWebSocket> members = new HashSet<EchoWebSocket>(20);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {		
		wsFactory = new WebSocketFactory(new WebSocketFactory.Acceptor() {
			
			public WebSocket doWebSocketConnect(HttpServletRequest req, String protocol) {
				if("echo".equals(protocol))
					return new EchoWebSocket();
				return null;
			}
			
			public boolean checkOrigin(HttpServletRequest req, String protocol) {				
				return true;
			}
		});
		wsFactory.setBufferSize(4096);
		wsFactory.setMaxIdleTime(60000);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if(wsFactory.acceptWebSocket(req, resp))
			return;
		else
			resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Websockets only, service unavailable");
	}
	
	class EchoWebSocket implements WebSocket.OnTextMessage {

		private Connection conn;
		private ReentrantLock lock = new ReentrantLock();

		public void onClose(int closeCode, String message) {
			lock.lock();
			members.remove(this);
			lock.unlock();
		}

		public void onOpen(Connection conn) {
			this.conn = conn;
			lock.lock();
			members.add(this);
			lock.unlock();
		}

		public void onMessage(String message) {
			lock.lock();
			for(EchoWebSocket sock:members) {		
				try {
					sock.conn.sendMessage(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			lock.unlock();
		}	
	}
}

