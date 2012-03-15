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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import org.springframework.integration.comet.core.CometMessage;
import org.springframework.integration.comet.core.CometMessageListener;
import org.springframework.integration.comet.core.CometSubscription;
import org.springframework.integration.comet.core.serializers.CometMessageSerializationException;
import org.springframework.integration.comet.core.serializers.CometMessageSerializer;
import org.springframework.util.StringUtils;

/**
 * The detault transport which use {@link HttpURLConnection} for communication
 * with the endpoint over HTTP
 * 
 * @author Amol Nayak
 * 
 */
public class CometMessagingDefaultTransport extends
		AbstractCometMessagingTransport {

	private HttpClient client;

	/**
	 * 
	 */
	public CometMessagingDefaultTransport() {
//		MultiThreadedHttpConnectionManager mgr = new MultiThreadedHttpConnectionManager();
//		// hardcoding to large values
//		mgr.getParams().setDefaultMaxConnectionsPerHost(1000);
//		mgr.getParams().setMaxTotalConnections(1000);
		//TODO: See how to set the thread pool details as we did above for v3.1
		ClientConnectionManager connManager = new ThreadSafeClientConnManager(); 
		client = new DefaultHttpClient(connManager);
	}

	@Override
	protected void doSend(String endpointString,
			Map<String, String> requestHeaders, String requestMessage,
			String charset,CometMessage cometMessage) {

		HttpPost method = new HttpPost(endpointString);
		for (Entry<String, String> entry : requestHeaders.entrySet()) {			
			method.addHeader(entry.getKey(), entry.getValue());
		}
		try {
			StringEntity entity = new StringEntity(requestMessage, 
											requestHeaders.get(CONTENT_TYPE), charset);
			method.setEntity(entity);
			HttpResponse response = client.execute(method);
			InputStream in = response.getEntity().getContent();
			//Read of the content
			byte[] bytes = new byte[1024];
			@SuppressWarnings("unused")
			int read = 0;
			while((read = in.read(bytes)) != -1);
			in.close();	
		
		} catch (IOException e) {
			throw new CometMessagingTransportException(
					"An IO Exception has occurred while posting the request to the endpoint, "
							+ "see root exception for more details", e,
							endpointString, cometMessage);
		}
	}

	public CometSubscription subscribe(final String endpointUrl, String expectedContentType,
			String expectedEncoding, final CometMessageListener<CometMessage> listener) {
		final String stringEncoding;
		if (StringUtils.hasText(expectedEncoding))
			stringEncoding = expectedEncoding;
		else
			stringEncoding = DEFAULT_ENCODING;

		final HttpGet get = new HttpGet(endpointUrl);
		
		// TODO: The error handing, retrying to connect to the broken
		// connections etc all
		// needs to be incorporated, use common client's retry connect mechanism
		// TODO: If the endpoint sends comments like say atmosphere sends for
		// webkit browsers
		// that needs to be identified
		final InputStream in;
		final HttpResponse response;		
		try {
			response = client.execute(get);			
			in = response.getEntity().getContent();		
		} catch (IOException e) {
			throw new CometMessagingTransportException(
					"An HttpException has occurred while getting the response to the endpoint, "
					+ "\"" + endpointUrl + "\" see root exception for more details", e,
					endpointUrl, null);
		}

		executor.execute(new Runnable() {
			/**
			 * Connect to the endpoint and receive events
			 */
			public void run() {
				try {
					CometMessageSerializer serializer;
					Header header = response.getFirstHeader(CONTENT_TYPE);
					String contentType = null;
					if (header != null)
						contentType = header.getValue();
					
					serializer = getSerializerForContentType(contentType);
								
					outer:
					while (true) {	

						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						boolean allRead = false;
						byte[] bytes = new byte[1024];
						do {							
							int read = in.read(bytes);
							if(read == -1) {
								in.close();
								break outer;
							}
							baos.write(bytes,0,read);
							//lets see if we have more
							int available = in.available();
							if(available == 0)
								allRead = true;
							else
								bytes = new byte[available];	// read all								
						}while(!allRead);
												
						String chunk = new String(baos.toByteArray(),stringEncoding);
						try {
							CometMessage message = serializer
									.deserialize(chunk);
							listener.onMessage(message);
						} catch (CometMessageSerializationException e) {
							logger.error("Failed to deserialize the string \""
									+ chunk + "\"", e);
						}
					}
				} catch (IOException e) {
					throw new CometMessagingTransportException(
							"An IO Exception has occurred while reading the chunked responses from the server, "
									+ "see root exception for more details", e,
							endpointUrl, null);
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore, just warn
						logger.warn(
								"An Exception occurred while closing the input stream",
								e);
					}
					//TODO: Check if any cleanup is needed 
					//get.releaseConnection();
				}
			}
		});
		return new CometSubscription() {

			private String uuid = UUID.randomUUID().toString();
			
			public String getSubscriptionIdentifier() {				
				return uuid;
			}

			public boolean unsubscribe() {
				//TODO: Implement the logic here to close the stream and terminate the reading				
				return false;
			}			
		};
	}
}

