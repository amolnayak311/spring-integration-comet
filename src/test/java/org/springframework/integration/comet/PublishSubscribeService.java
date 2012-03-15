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
package org.springframework.integration.comet;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;

/**
 * The Rest service that acts as a publish subscribe service using atmosphere framework
 * for more details about atmosphere see https://github.com/Atmosphere/atmosphere
 * 
 * Issuing a get request to the URL http(s)://{hostname}:{port}/{root}/{topicName} will create 
 * a subscriber for the topic name topicName
 * 
 * e.g. Suppose the root context is pubsub then issuing a request to
 * http://localhost:8080/pubsub/testTopic will create a subscription for the 
 * topic names testTopic
 * 
 * To post a message to a topic we need to post to the URL http(s)://{hostname}:{port}/{root}/{topicName}
 * a form with a form param name "message"  
 * 
 * @author Amol Nayak
 *
 */
@Path("/{topic}")
@Produces("text/plain;charset=ISO-8859-1")
public class PublishSubscribeService {

	/**
	 * Inject a broadcaster based on the path param.
	 */
	@PathParam("topic")
	private Broadcaster broadcaster;
	
	//No output comments needed as the client is not a WebKit browser but some java application(s)
	@GET
	@Suspend(outputComments=false)
	public Broadcastable subscribe() {
		return new Broadcastable("",broadcaster);
	}
	
	@POST
	@Broadcast
	public Broadcastable publish(@FormParam("message") String message) {
		return new Broadcastable(message, broadcaster);
	}
}
