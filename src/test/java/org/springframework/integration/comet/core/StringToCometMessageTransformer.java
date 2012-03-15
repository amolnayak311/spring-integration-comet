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

/**
 * A test transformer that will transform a String to a comet message to be sent out
 * @author Amol Nayak
 *
 */
public class StringToCometMessageTransformer implements
		CometMessageTransformer<String> {

	/* (non-Javadoc)
	 * @see org.springframework.integration.comet.core.CometMessageTransformer#fromCometMessage(org.springframework.integration.comet.core.CometMessage)
	 */
	public String fromCometMessage(CometMessage msg) {		
		return msg.getPayload();
	}

	/* (non-Javadoc)
	 * @see org.springframework.integration.comet.core.CometMessageTransformer#toCometMessage(java.lang.Object)
	 */
	public CometMessage toCometMessage(String msg) {
		CometMessage cMsg = new CometMessage(null, msg, String.class.getName());
		return cMsg;
	}

}
