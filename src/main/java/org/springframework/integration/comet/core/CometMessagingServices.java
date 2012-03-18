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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.comet.core.transport.AbstractCometMessagingTransport;
import org.springframework.integration.comet.core.transport.CometMessagingDefaultTransport;
import org.springframework.integration.comet.core.transport.CometMessagingTransport;
import org.springframework.util.StringUtils;

/**
 * The core class that will be used to send a CometMessage to comet end point
 * A {@link CometMessageTransformer} which is registered with this class
 * is responsible for converting the message from the provided type to 
 * a {@link CometMessage}. 
 * Having a CometMessageTransformer is mandatory else the bean initialization
 * will fail.
 *  
 * It can be used to post notifications/messages to 
 * some remote comet endpoint specified and receive updates by subscribing to these 
 * remote comet endpoint. In this case it just publishes the message to the remote comet
 * endpoint and closes the connection. It is the responsibility of the remote comet endpoint
 * to publish these messages to subscribes subscribers
 *         
 * @author Amol Nayak
 *
 */
public class CometMessagingServices<T> implements InitializingBean {

	
	protected final Log logger = LogFactory.getLog(getClass());
	
	/**
	 * The Endpoing URL for the comet messaging system. This URL should exclude 
	 * the topic name
	 */
	private String endpointUrl;
	
	/**
	 * The default topic on the given endpoint url to which we wish to subscribe
	 * the URL to which the message will be posted is formed by containing the
	 * endpoint URL and the topicName
	 * 
	 * e.g.  If the endpoint url is http://abc.com:8080/pubsub and the topic name is "topic"
	 * the URL formed will be 
	 * http://abc.com:8080/pubsub/topic
	 */
	private String defaultTopicName;
	
	/**
	 * The valid mime type that is used to serialize the message to the
	 * endpointProvided 
	 */
	private String defaultMimeType = "application/json";	

	/**
	 * Transformer that will be used to convert the message from a user provided type T to 
	 * a CometMessage that will be sent over the wire to the comet endpoint
	 * This would also be used to convert the CometMessage that came in as a publish
	 * from the comet endpoint to the user mesage type T 
	 */
	private CometMessageTransformer<T> transformer;
	
	/**
	 * The transport implementation that is used to transport comet messages 
	 * to the endpoint and subscribe to receive messages
	 */
	private CometMessagingTransport transport = new CometMessagingDefaultTransport();
	
	
	//TODO: Validate the input parameters of the methods
	//TODO:Donot bind the service to one URL, let the endpoint URL be default URL
	//And let user also provide any random URL to subscribe and post calls
	
	
	public void afterPropertiesSet() throws Exception {		
		if(!StringUtils.hasText(endpointUrl))
			throw new BeanDefinitionStoreException("attributes \"endpointUrl\" is mandatory");
		
		if(transport instanceof AbstractCometMessagingTransport) {
			((AbstractCometMessagingTransport)transport).initializeTransport();
		}
		
		if(transformer == null)
			throw new BeanDefinitionStoreException("A Transformer is required to convert the messages" +
					" from a user provided type to a CometMessage and from CometMessage to the user message class");
		
		if(transport == null)
			throw new BeanDefinitionStoreException("A Transport implementation is madatory or sending" +
					" the comet message over to the endpoint");
	}	
	
	/**
	 * Convert a message of type T to {@link CometMessage} and send
	 * to the default topic on the endpoint specified and using the defaultMimeType.
	 * The registered {@link CometMessageTransformer} will be used to convert the
	 * message passed to a {@link CometMessage}. if the defaultTopicName should be
	 * non empty non null, else will result in a {@link CometMessagingException}
	 * If the defaultMimeType is null
	 * invocation will result in a {@link CometMessagingException}
	 * 
	 * @param message
	 */
	public void convertAndSend(T message) {		
		convertAndSend(message, defaultTopicName, defaultMimeType);
	}
	
	/**
	 * Converts the message to a {@link CometMessage} using the serializer of the 
	 * defaultMimeType and send to the topic specified.
	 * The registered {@link CometMessageTransformer} will be used to convert the
	 * message passed to a {@link CometMessage}. 
	 * If the defaultMimeType is null
	 * invocation will result in a {@link CometMessagingException}
	 * @param message
	 * @param topic
	 */
	public void convertAndSend(T message,String topic) {
		convertAndSend(message, topic, defaultMimeType);
	}	

	
	/**
	 * Converts a message to a {@link CometMessage} and send to the specified
	 * topic and using the serialization dependent on the type provided in the mimeType
	 * Will result in a {@link CometMessagingException} when the provided message is null
	 * or the given topic or mimeType is empty or null 
	 * 
	 * @param message
	 * @param topic
	 * @param mimeType
	 */
	public void convertAndSend(T message,String topic,String mimeType) {
		if(message == null)
			throw new CometMessagingException("A non null instance of CometMessage is needed to be sent");
		if(!StringUtils.hasText(topic))
			throw new CometMessagingException("A non null, non empty topic needs to be provided");
		if(!StringUtils.hasText(mimeType))
			throw new CometMessagingException("A non null, non empty mimeType needs to be provided");
		
		//First convert the user provided message to a CometMessage using user provided transformer
		CometMessage cometMessage = transformer.toCometMessage(message);
		
		
		transport.send(getCompleteURL(topic), mimeType, cometMessage);		
	}	
	 
	/**
	 * Subscribe to the given topic URL and get notifications on the given listener
	 * for the messages coming in
	 * @param endpointUrl
	 * @param listener
	 */
	public CometSubscription subscribe(String endpointUrl,CometMessageListener<T> listener) {
		return subscribe(endpointUrl,null,listener);
	}
	
	/**
	 * Subscribe to the given topic and get notifications on the given listener
	 * for the messages coming in
	 * @param topic
	 * @param expectedContentType
	 * @param listener
	 */
	public CometSubscription subscribe(String topic,String expectedContentType,CometMessageListener<T> listener) {
		return subscribe(topic, expectedContentType, "UTF-8", listener);		
	}
	
	/**
	 * Subscribe to the given topic and get notifications on the given listener
	 * for the messages coming in
	 * 
	 * @param topic
	 * @param expectedContentType
	 * @param expectedEncoding
	 * @param listener
	 */
	public CometSubscription subscribe(String topic,String expectedContentType,
								String expectedEncoding,final CometMessageListener<T> listener) {
		String completeURL = getCompleteURL(topic);
		return transport.subscribe(completeURL, expectedContentType, 
				expectedEncoding, new CometMessageListener<CometMessage>() {					
					public void onMessage(CometMessage message) {
						if(message != null) {
							T transformedBack = transformer.fromCometMessage(message);
							if(transformedBack != null) {
								listener.onMessage(transformedBack);
							} else
								logger.warn("Comet message was transformed to a null instance, comet message is  " + message);
						} else {
							logger.warn("A null comet message was delivered");
						}
					}
				});
	}	
	
	/**
	 * Concats the endpoint URL and the topic name to generate the complete endpoint URL to 
	 * which the message needs to be posted to
	 * @param topic
	 * @return the complete concatenated endpoint URL to which the message will be posted
	 */
	private String getCompleteURL(String topic) {		
		StringBuilder builder = new StringBuilder(80);
		if(endpointUrl.trim().endsWith("/"))
			builder.append(endpointUrl).append(topic);
		else
			builder.append(endpointUrl).append("/").append(topic);
		return builder.toString();
	}
	
	/**
	 * The valid URL that represents the endpoint of the topic  
	 * @return
	 */
	public String getEndpointUrl() {
		return endpointUrl;
	}

	public void setEndpointUrl(String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}

	/**
	 * The default topic to which the messages will be posted if no topic is specified
	 * while posting a message 
	 * @return
	 */
	public String getDefaultTopicName() {
		return defaultTopicName;
	}

	public void setDefaultTopicName(String defaultTopicName) {
		this.defaultTopicName = defaultTopicName;
	}


	/**
	 * The default mime type of the request posted to the topic on the
	 * specified endpoint. 
	 * @return
	 */
	public String getDefaultMimeType() {
		return defaultMimeType;
	}

	public void setDefaultMimeType(String defaultMimeType) {
		this.defaultMimeType = defaultMimeType;
	}

	/**
	 * Gets the transformer instance that will be used to convert the message from the
	 * user provided type to a {@link CometMessage}
	 * @return
	 */
	public CometMessageTransformer<T> getTransformer() {
		return transformer;
	}

	public void setTransformer(CometMessageTransformer<T> transformer) {
		this.transformer = transformer;
	}

	public void setTransport(CometMessagingTransport transport) {
		this.transport = transport;
	}
	
	
}
