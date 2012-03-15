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
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.comet.core.CometMessage;
import org.springframework.integration.comet.core.serializers.CometMessageJSONSerializer;
import org.springframework.integration.comet.core.serializers.CometMessageSerializer;
import org.springframework.util.StringUtils;

/**
 * The abstract class for implementing all the common comet message transport functionality
 * @author Amol Nayak
 *
 */
public abstract class AbstractCometMessagingTransport implements
		CometMessagingTransport,InitializingBean {

	
	protected static final String CONTENT_TYPE = "Content-Type";

	protected static final String CONTENT_LENGTH = "Content-Length";

	protected Log logger = LogFactory.getLog(getClass());
	
	/**
	 * The default serializer instance that will be used to convert the Comet message to a string
	 * to be posted to a comet endpoint and deserialize the string back to a comet
	 * message that was published from the comet endpoint 
	 */
	protected CometMessageSerializer defaultMessageSerializer = new CometMessageJSONSerializer();
	
	/**
	 * The name of the message parameter which is being posted to the endpoint
	 */
	private String messageParamName = "message";
	
	/**
	 * The task executor that is used for subscribing to the endpoints used to receive
	 * the comet events/messages
	 */
	protected TaskExecutor executor = new SyncTaskExecutor();
	
	
	/**
	 * The map storing the mime type and the correponding serializer to be used for 
	 * comet message serialization
	 */
	private Map<String,CometMessageSerializer> serializerMap;	
	
	/**
	 * The default encoding for the bytes read from the chunked http response
	 * 
	 */
	public static String DEFAULT_ENCODING = "UTF-8";
	
	/* (non-Javadoc)
	 * @see org.springframework.integration.comet.core.transport.CometMessagingTransport#send(java.lang.String, java.lang.String, org.springframework.integration.comet.core.CometMessage)
	 */
	public void send(String endpointUrl, String contentType,
			String charset,CometMessage message) {
		
		if(!StringUtils.hasText(charset))
			charset = DEFAULT_ENCODING;
			
		CometMessageSerializer serializer;
		if(serializerMap != null) {
			serializer = serializerMap.get(contentType.trim());
			if(serializer == null) {
				serializer = defaultMessageSerializer;
			}
		} else {
			serializer = defaultMessageSerializer;
		}
		
		//Lets prepare the request parameters list
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put(CONTENT_TYPE, contentType);
		String serializedMessage = serializer.serialize(message);
		//Encode the message
		String encodedString = null;		
		try {
			encodedString = URLEncoder.encode(serializedMessage, charset);			
		} catch (UnsupportedEncodingException e) {
			//ignore				
			e.printStackTrace();
		}
		String requestMessage = new StringBuilder(200)
									.append(messageParamName)
									.append("=")
									.append(encodedString)
									.toString();
		doSend(endpointUrl, requestHeaders, requestMessage,charset,message);
	}	
	
	/**
	 * Send a message to the endpoint with the default encoding type
	 * 
	 * @param endpointUrl
	 * @param contentType
	 * @param message
	 */
	public void send(String endpointUrl, String contentType,
			CometMessage message) {
		send(endpointUrl, contentType, DEFAULT_ENCODING, message);
	}
	

	/**
	 * Implemented after properties set method from {@link InitializingBean}
	 */
	public final void afterPropertiesSet() throws Exception {
		if(defaultMessageSerializer == null)
			throw new BeanDefinitionStoreException("A default serializer is mandatory to send messaged to " +
					"comet endpoint, found a null serializer");
		
		if(!StringUtils.hasText(messageParamName))
			throw new BeanDefinitionStoreException("A non null non empty message parameter name is required");
		
		if(executor == null)
			throw new BeanDefinitionStoreException("A non null executor service is required");
	}



	
	
	public void setDefaultMessageSerializer(CometMessageSerializer defaultMessageSerializer) {
		this.defaultMessageSerializer = defaultMessageSerializer;
	}
	
	/**
	 * Gets the default serializer that will be used to convert the {@link CometMessage} to a String
	 * that will be posted to the comet endpoint and from String to a {@link CometMessage}
	 * 
	 */
	public CometMessageSerializer getDefaultMessageSerializer() {
		return defaultMessageSerializer;
	}
	
	/**
	 * Gets the immutable Serializable map that is contains the mapping of 
	 * the mime types to the {@link CometMessageSerializer} instance
	 * @return
	 */
	public Map<String, CometMessageSerializer> getSerializerMapping() {
		return Collections.unmodifiableMap(serializerMap);
	}


	public void setSerializerMapping(Map<String, CometMessageSerializer> serializerMap) {
		if(serializerMap != null)
			this.serializerMap = serializerMap;
		else
			logger.warn("Attempted to set a null serializer map, ignoring");
		//ignore this add
		
	}
	
	/**
	 * Convenience method that will be used to add new serializer for the given
	 * mimeType. The value if exists will be over written, passing a null serializer will 
	 * remove the mapping
	 *   
	 * @param mimeType
	 * @param serializer
	 */
	public void addSerializer(String mimeType,CometMessageSerializer serializer) {
		if(serializerMap == null)
			serializerMap = new HashMap<String, CometMessageSerializer>();
		
		if(serializer == null)
			serializerMap.remove(mimeType);
		
		if(StringUtils.hasText(mimeType))
			serializerMap.put(mimeType, serializer);
		else
			logger.warn("Null or a non empty string added as the mime type");
	}
	
	
	public String getMessageParamName() {
		return messageParamName;
	}


	public void setMessageParamName(String messageParamName) {
		this.messageParamName = messageParamName;
	}


	public TaskExecutor getExecutor() {
		return executor;
	}


	public void setExecutor(TaskExecutor executor) {
		this.executor = executor;
	}


	public Map<String, CometMessageSerializer> getSerializerMap() {
		return serializerMap;
	}


	public void setSerializerMap(Map<String, CometMessageSerializer> serializerMap) {
		this.serializerMap = serializerMap;
	}



	/**
	 * The method is to be implemented by the implementing class to send a request to the given endpoint
	 * 
	 * @param endpointString: The endpoint to which the message is to be sent out
	 * @param requestHeaders the request headers those are to be sent with the request
	 * @param requestMessage: The request message to be posted
	 * @param contentEncoding the encoding of the string to be used
	 * @param The original {@link CometMessage} that is being transported
	 * 
	 */
	protected abstract void doSend(String endpointString,
					Map<String, String> requestHeaders,
					String requestMessage,String contentEncoding,CometMessage message);
	
	/**
	 * Gets the serializer based on the content type, if no serializer is found, the
	 * default one is used
	 * 
	 * @param contentType
	 */
	protected CometMessageSerializer getSerializerForContentType(String contentType) {
		//TODO: Need to introduce a flag about what needs to be done if the no serializer
		//found for the given content type
		CometMessageSerializer serializer = null;
		if(serializerMap != null)
			serializer = serializerMap.get(contentType);
		
		if(serializer == null)
			return defaultMessageSerializer;
		else
			return serializer;			
	}
}
