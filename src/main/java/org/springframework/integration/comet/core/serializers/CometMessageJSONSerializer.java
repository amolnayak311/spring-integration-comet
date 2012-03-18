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
package org.springframework.integration.comet.core.serializers;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.integration.comet.core.CometMessage;
import org.springframework.util.StringUtils;

/**
 * The serializer that will serialize the {@link CometMessage} into a JSON String and an incoming 
 * string to a {@link CometMessage} instance.
 * 
 * @author Amol Nayak
 *
 */
public class CometMessageJSONSerializer implements CometMessageSerializer {

	private String commonPrefix = "message=";
	
	private ObjectMapper mapper;
	
	public CometMessageJSONSerializer() {
		mapper = new ObjectMapper();
		mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
	}
	
	private boolean isPrefixPresent;
	
	/* (non-Javadoc)
	 * @see org.springframework.integration.comet.core.CometMessageSerializer#serialize(org.springframework.integration.comet.core.CometMessage)
	 */
	public String serialize(CometMessage message) {		
		try {
			if(isPrefixPresent)
				return commonPrefix + mapper.writeValueAsString(message);
			else
				return mapper.writeValueAsString(message);
			
		} catch (Exception e) {
			throw new CometMessageSerializationException
							("Cannot Serialize the provided come message, see root cause for more details ",
															e, message);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.integration.comet.core.CometMessageSerializer#deserialize(java.lang.String)
	 */
	public CometMessage deserialize(String serializedString) {
		try {
			
			if(isPrefixPresent)
				serializedString = serializedString.substring(commonPrefix.length() + 1);
			
			return mapper.readValue(serializedString, CometMessage.class);
		} catch (Exception e) {
			throw new CometMessageSerializationException
			("Cannot deserialize the provided serialized string into a CometMessage", 
					e, serializedString);			
		}
	}

	/**
	 * The common prefix that would be found with the serialized string that is passed to the
	 * deserialize method. if this is null or empty string, the message will be deserialized as is
	 * if present and if the the serializedString passed contains the prefix, it would be
	 * removed before deserializing the message
	 * @return
	 */
	public String getCommonPrefix() {
		return commonPrefix;
	}

	public void setCommonPrefix(String commonPrefix) {
		this.commonPrefix = commonPrefix;
		if(!commonPrefix.endsWith("="))
			this.commonPrefix = commonPrefix + "=";
		if(StringUtils.hasText(commonPrefix))
			isPrefixPresent = true;
		else
			isPrefixPresent = false;
			
	}
}
