/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of visitmeta-dataservice, version 0.4.0,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2012 - 2015 Trust@HsH
 * %%
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
 * #L%
 */
package de.hshannover.f4.trust.visitmeta.dataservice.rest;

import java.io.IOException;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.hshannover.f4.trust.ifmapj.messages.SubscribeRequest;
import de.hshannover.f4.trust.ironcommon.properties.PropertyException;
import de.hshannover.f4.trust.visitmeta.dataservice.Application;
import de.hshannover.f4.trust.visitmeta.exceptions.ifmap.ConnectionException;
import de.hshannover.f4.trust.visitmeta.ifmap.SubscriptionHelper;
import de.hshannover.f4.trust.visitmeta.interfaces.ifmap.ConnectionManager;

/**
 * For each request a new object of this class will be created. The resource is
 * accessible under the path <tt>{connectionName}/subscribe</tt>. About this
 * interface can be update and delete subscriptions. Untder the path
 * {connectionName}/subscribe/active return a list for all active subscriptions.
 *
 * @author Marcel Reichenbach
 *
 */

@Path("{connectionName}/subscribe")
public class SubscribeResource {

	private static final Logger log = Logger.getLogger(SubscribeResource.class);

	@QueryParam("deleteAll")
	@DefaultValue("false")
	private boolean mDeleteAll = false;

	@QueryParam("onlyActive")
	@DefaultValue("false")
	private boolean mOnlyActive = false;

	/**
	 * Returns a JSONArray with the subscriptions for the dataservice about the default connection.
	 *
	 * Example-URL: <tt>http://example.com:8000/default/subscribe</tt>
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Object getSubscriptions(@PathParam("connectionName") String name) {
		JSONArray jaSubscriptions;
		if (!mOnlyActive) {
			try {
				jaSubscriptions = new JSONArray(Application.getConnectionManager().getSubscriptions(name));
			} catch (ConnectionException e) {
				log.error("error at getSubscriptions from " + name, e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
			}

		} else {

			try {
				jaSubscriptions = new JSONArray(Application.getConnectionManager().getActiveSubscriptions(name));
			} catch (ConnectionException e) {
				log.error("error at getActiveSubscriptions from " + name, e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
			}

		}

		return jaSubscriptions;
	}

	/**
	 * Send a subscribeUpdate to the MAP-Server with a JSONObject. Max-Depth and
	 * Max-Size have the defaultValue 1000 and 1000000000. To change this values
	 * use the params "maxDepth" and "maxSize", or set this in the JSONObject.
	 *
	 * On identifier type: ip-address: "[type],[value]" e.g. "IPv4,10.1.1.1"
	 * device: "[name]" access-request: "[name]" mac-address: "[value]"
	 *
	 * Use the valid JSON-Keys for subscriptions: SUBSCRIBE NAME =
	 * "subscribeName" IDENTIFIER = "identifier" IDENTIFIER-TYPE =
	 * "identifierType" MAX-DEPTH = "maxDepth" MAX-SIZE = "maxSize" LINKS-FILTER
	 * = "linksFilter" RESULT-FILTER = "resultFilter" TERMINAL-IDENTIFIER-TYPES
	 * = "terminalIdentifierTypes"
	 *
	 * Example-URL: <tt>http://example.com:8000/default/subscribe/update</tt>
	 *
	 * Example-JSONObject: { subscribeName:exampleSub, identifierType:device,
	 * identifier:device12 }
	 */
	@PUT
	@Path("update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("connectionName") String name, JSONObject jObj) {
		ConnectionManager manager = Application.getConnectionManager();
		try {

			Iterator<String> i = jObj.keys();
			while (i.hasNext()) {
				String jKey = i.next();
				JSONObject moreSubscribes = jObj.getJSONObject(jKey);
				try {

					SubscribeRequest request = SubscriptionHelper
							.buildRequest(moreSubscribes);
					manager.subscribe(name, request);
					manager.storeSubscription(name, SubscriptionHelper
							.buildSubscribtion(moreSubscribes));

				} catch (ConnectionException | IOException | PropertyException e) {
					log.error("error while multiple subscribeUpdate from "
							+ name, e);
					return Response
							.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(e.toString()).build();
				}
			}
		} catch (JSONException e) {
			try {
				SubscribeRequest request = SubscriptionHelper
						.buildRequest(jObj);
				manager.subscribe(name, request);
				manager.storeSubscription(name,
						SubscriptionHelper.buildSubscribtion(jObj));

			} catch (ConnectionException | IOException | PropertyException ee) {
				log.error("error while single subscribeUpdate from " + name, ee);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ee.toString()).build();
			}
		}
		return Response.ok().entity("INFO: subscribe successfully").build();
	}

	/**
	 * Send a subscribeDelete for all active subscriptions to the MAP-Server.
	 * You must set the deleteAll value of true.
	 *
	 * Example-URL: <tt>http://example.com:8000/default/subscribe/delete?deleteAll=true</tt>
	 */
	@DELETE
	@Path("delete")
	public Response delete(@PathParam("connectionName") String name) {
		if (mDeleteAll) {
			try {
				Application.getConnectionManager().deleteAllSubscriptions(name);
			} catch (ConnectionException e) {
				log.error("error while delete all subscriptions from " + name, e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
			}
			return Response.ok().entity("INFO: delete all active subscriptions successfully").build();
		} else {
			return Response.status(Response.Status.NOT_MODIFIED)
					.entity("INFO: deleteAll value is not true, nothing were deleted").build();
		}
	}

	/**
	 * Send a subscribeDelete for the active subscription to the MAP-Server.
	 *
	 * Example-URL: <tt>http://example.com:8000/default/subscribe/delete/{subscriptionName}</tt>
	 */
	@DELETE
	@Path("delete/{subscriptionName}")
	public Response delete(@PathParam("connectionName") String name,
			@PathParam("subscriptionName") String subscriptionName) {
		try {
			Application.getConnectionManager().deleteSubscription(name, subscriptionName);
		} catch (ConnectionException e) {
			log.error("error while delete " + subscriptionName + " from " + name, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		}

		return Response.ok().entity("INFO: delete subscription(" + subscriptionName + ") successfully").build();
	}
}
