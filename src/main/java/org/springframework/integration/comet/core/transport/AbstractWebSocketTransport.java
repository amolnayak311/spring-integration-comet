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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.springframework.integration.comet.core.CometMessage;
import org.springframework.integration.comet.core.CometMessagingException;
import org.springframework.integration.comet.core.serializers.CometMessageSerializer;
import org.springframework.util.StringUtils;

/**
 * The common super class for the Web socket transports
 * @author Amol Nayak
 *
 */
public abstract class AbstractWebSocketTransport extends
		AbstractCometMessagingTransport {

	//TODO:Currently we donot passing content types for web socket, see if we can do that.
	
	private String protocol = "default";
	/**
	 * The overridden init method performing the necessary checks and initialization for the
	 * web socket transport, child classes should ensure that the super.init() is called when they
	 * override the init method
	 */
	@Override
	protected void init() throws Exception {
		if(!StringUtils.hasText(protocol))
			throw new CometMessagingException("Non null, non empty value of protocol expected");
	}
	
	
	public String getProtocol() {
		return protocol;
	}
	
	
	/**
	 * Sets the web socket protocol that would be used
	 * @param protocol
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * The method to be used by the subclasses to deserialize the message received
	 * @param serializedMessage
	 * @return
	 */
	protected CometMessage deserializeMessage(String serializedMessage) {
		String decodedMessage = null;
		try {
			decodedMessage = URLDecoder.decode(serializedMessage, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//Should never reach here
			logger.error("Unexpected exception caught while decoding URL", e);
		}
		CometMessageSerializer serializer = getDefaultMessageSerializer();
		return serializer.deserialize(decodedMessage);
	}
}
