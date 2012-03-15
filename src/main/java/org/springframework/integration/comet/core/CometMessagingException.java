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
 * The base class exception for all comet exceptions
 * @author Amol Nayak
 *
 */
public class CometMessagingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public CometMessagingException() {
		super();		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CometMessagingException(String message, Throwable cause) {
		super(message, cause);		
	}

	/**
	 * @param message
	 */
	public CometMessagingException(String message) {
		super(message);		
	}

	/**
	 * @param cause
	 */
	public CometMessagingException(Throwable cause) {
		super(cause);		
	}
	
}
