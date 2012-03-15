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
package org.springframework.integration.comet;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * The embedded comet server that uses jetty internally, other tests instantiates, configures and starts it.
 *   
 * @author Amol Nayak
 *
 */
public class EmbeddedCometServer {
	
	private Server server;
	
//	public static void main(String[] args) throws Exception {
//		EmbeddedCometServer server = new EmbeddedCometServer();
//		server.initialize(8080);
//		AtmosphereServlet servlet = new AtmosphereServlet();
//		servlet.setCometSupport(new Jetty7CometSupport(servlet.getAtmosphereConfig()));
//		servlet.addInitParameter("com.sun.jersey.config.property.packages", server.getClass().getPackage().getName());	
//		server.addServlet(servlet, "/pubsub","/*");
//		server.start();
//		server.join();
//	}
	
	public void initialize(int port) {
		server = new Server(port);
	}
	
	public void addServlet(Class<Servlet> servlet,String contextPath,String urlMapping) {
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		handler.addServlet(new ServletHolder(servlet), urlMapping);
		handler.setContextPath(contextPath);
		server.setHandler(handler);
	}	
	
	//No null checks, assuming initialize called
	public void addServlet(Servlet servlet,String contextPath,String urlMapping) {
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		handler.addServlet(new ServletHolder(servlet), urlMapping);
		handler.setContextPath(contextPath);
		server.setHandler(handler);
	}	
	
	public void setWebappResourceBase(String webappBase,String webappContext) {
		WebAppContext webapp = new WebAppContext();
		webapp.setResourceBase(webappBase);
		webapp.setContextPath(webappContext);
		webapp.setDescriptor("/WEB-INF/web.xml");
		server.setHandler(webapp);
	}
	
	public void start() throws Exception {		
		server.start();
	}
	
	public void join() throws Exception{
		server.join();
	}
	
	public void stop() throws Exception {
		server.stop();
	}
}
