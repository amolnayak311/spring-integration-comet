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

import org.springframework.integration.comet.core.CometMessage;

/**
 * The implementation will convert the {@link CometMessage} to a String that will be posted to 
 * the comet endpoint and from a String to {@link CometMessage}  
 *  
 * @author Amol Nayak
 *
 */
public interface CometMessageSerializer {
	
	/**
	 * Serialize the given comet message ti a String that would be sent out to the
	 * comet endpoint, should throw a {@link CometMessageSerializationException} if the
	 * serialization fails 
	 * 
	 * @param message
	 * @return
	 */
	String serialize(CometMessage message);
	
	/**
	 * Deserialize the given serialized string into a comet message, should throw a 
	 * {@link CometMessageSerializationException} if the deserialization fails
	 * @param serializedString
	 * @return
	 */
	CometMessage deserialize(String serializedString);
}
