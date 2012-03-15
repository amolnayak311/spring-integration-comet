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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.integration.comet.core.providers.AtmosphereCometServiceProvider;
import org.springframework.integration.comet.core.providers.CometServiceProvider;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestHandler;

/**
 * This class provides all the services from {@link CometMessagingServices}
 * additionally it also provides the server side services where the class acts
 * as a comet messaging endpoint so that it can receive requests for subscription and 
 * publish comet messages to these subscribers.
 * 
 * In order for this service to act as the comet service provider it should have an
 * implementation of {@link CometServiceProvider}
 * 
 * @author Amol Nayak
 *
 */
public class CometMessagingServer<T> extends CometMessagingServices<T> implements HttpRequestHandler {

	/**
	 * The implementation that will be used to provide comet services to the remote clients 
	 */
	//Default is atmosphere implementation
	private CometServiceProvider cometProvider = new AtmosphereCometServiceProvider();
	
	//TODO: Have provision to receive unsubscription too
	
	/**
	 * Invoked when this service acts as a Comet service provider.
	 * This method will be invoked whenever a remote client wants to subscribe to 
	 * the comet events being posted out from this service which acts a comet messaging
	 * provider. Will throw an exception if {@link CometServiceProvider} instance is not set.
	 * 
	 */
	public void receiveSubscription(HttpServletRequest request,HttpServletResponse response) {
		Assert.notNull(request, "Non null instance of HttpServletRequest is required");
		if(cometProvider == null)
			throw new CometMessagingException("Cannot receive subscriptions as no CometServiceProvider registered");
		
		cometProvider.receiveSubscription(request, response);		
	}

	/**
	 * The method implemented from the {@link HttpRequestHandler} interface
	 * delegates to either broadcast message or receiveSubscription based on the reques
	 * type
	 * @param request
	 * @param response
	 * 
	 */
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String requestMethod = request.getMethod();
		if("GET".equalsIgnoreCase(requestMethod)) {
			receiveSubscription(request, response);
		} else if("POST".equalsIgnoreCase(requestMethod)){
			//TODO: Broadcast
		}
		
	}
	

	@Override
	public void afterPropertiesSet() throws Exception {		
		super.afterPropertiesSet();
		if(cometProvider == null)
			throw new BeanDefinitionStoreException("A non null comet service provider is mandatory");
	}

	public CometServiceProvider getCometProvider() {
		return cometProvider;
	}

	public void setCometProvider(CometServiceProvider cometProvider) {
		this.cometProvider = cometProvider;
	}
	
}
