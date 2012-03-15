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
package org.springframework.integration.comet.core.providers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The implementation class that is used to provide comet services using atmosphere framework
 *  
 * @author Amol Nayak
 *
 */
public class AtmosphereCometServiceProvider implements CometServiceProvider {

	/* (non-Javadoc)
	 * @see org.springframework.integration.comet.core.providers.CometServiceProvider#receiveSubscription(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void receiveSubscription(HttpServletRequest request,
			HttpServletResponse response) {
		

	}

}
