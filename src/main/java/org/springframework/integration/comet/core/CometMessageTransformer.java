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
 * The implementing class will provide an implementation to convert the
 * custom message to {@link CometMessage} and vice versa
 * Should throw a {@link CometMessageConversionException} if either of the conversion fails
 * @author Amol Nayak
 *
 */
public interface CometMessageTransformer<T> {

	/**
	 * The implementing class will convert the comet message of its implementation
	 * type message
	 * @param msg
	 * @return The 
	 */
	T fromCometMessage(CometMessage msg);
	
	/**
	 * The implementing class will convert the custom message to a {@link CometMessage}
	 * @param msg
	 * @return The 
	 */
	CometMessage toCometMessage(T msg);	
}
