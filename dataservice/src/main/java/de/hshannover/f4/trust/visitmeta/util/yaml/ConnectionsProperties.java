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
 * This file is part of visitmeta dataservice, version 0.1.2,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2012 - 2014 Trust@HsH
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
package de.hshannover.f4.trust.visitmeta.util.yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hshannover.f4.trust.visitmeta.dataservice.Application;
import de.hshannover.f4.trust.visitmeta.exceptions.ifmap.ConnectionException;
import de.hshannover.f4.trust.visitmeta.ifmap.SubscriptionImpl;
import de.hshannover.f4.trust.visitmeta.interfaces.Subscription;
import de.hshannover.f4.trust.visitmeta.interfaces.ifmap.Connection;
import de.hshannover.f4.trust.visitmeta.interfaces.ifmap.ConnectionManager;
import de.hshannover.f4.trust.visitmeta.util.Same;
import de.hshannover.f4.trust.visitmeta.util.properties.Properties;
import de.hshannover.f4.trust.visitmeta.util.properties.PropertyException;

public class ConnectionsProperties extends Properties{
	
	private static final Properties mCONFIG = Application.getConfig();

	// TODO the dataservice supports only basic authentication this value is not in use
	public static final boolean DEFAULT_AUTHENTICATION_BASIC = true;

	public static final boolean DEFAULT_STARTUP_CONNECT = mCONFIG.getBoolean("ifmap.defaultConnectionSettings.useConnectionAsStartup", false);

	public static final boolean DEFAULT_STARTUP_SUBSCRIPTION = mCONFIG.getBoolean("ifmap.defaultConnectionSettings.useSubscriptionAsStartup", false);

	public static final String DEFAULT_TRUSTSTORE_PATH = mCONFIG.getString("ifmap.defaultConnectionSettings.truststorePath", "/visitmeta.jks");

	public static final String DEFAULT_TRUSTSTORE_PASSWORD = mCONFIG.getString("ifmap.defaultConnectionSettings.truststorePassword", "visitmeta");

	public static final int DEFAULT_MAX_POLL_RESULT_SIZE = mCONFIG.getInt("ifmap.defaultConnectionSettings.maxPollResultSize", 1000000000);

	public static final int DEFAULT_SUBSCRIPTION_MAX_DEPTH = mCONFIG.getInt("ifmap.defaultConnectionSettings.subscriptionMaxDepth", 1000);

	private ConnectionManager mManager;

	public ConnectionsProperties(ConnectionManager manager, String fileName) throws PropertyException {
		super(fileName);
		mManager = manager;
	}

	public Connection buildConnection(String connectionName) throws ConnectionException, PropertyException{
		// read values from property
		String ifmapServerUrl = getPropertyIfmapServerUrl(connectionName);
		String userName = getPropertyUserName(connectionName);
		String userPassword = getPropertyUserPassword(connectionName);

		String truststorePath = getPropertyTruststorePath(connectionName);
		String truststorePassword = getPropertyTruststorePassword(connectionName);
		boolean authenticationBasic = isPropertyAuthenticationBasic(connectionName);
		boolean startupConnect = isPropertyStartupConnect(connectionName);
		int maxPollResultSize = getPropertyMaxPollResultSize(connectionName);

		// build connection
		Connection newConnection = mManager.createConnection(connectionName, ifmapServerUrl, userName, userPassword);

		// build/set subscription list, if exists
		List<Subscription> subscribtionList = buildSubscribtion(connectionName);
		if (subscribtionList != null) {
			for (Subscription s : subscribtionList) {
				newConnection.addSubscription(s);
			}
		}

		// set other values
		newConnection.setTruststorePath(truststorePath);
		newConnection.setTruststorePassword(truststorePassword);
		newConnection.setAuthenticationBasic(authenticationBasic);
		newConnection.setStartupConnect(startupConnect);
		newConnection.setMaxPollResultSize(maxPollResultSize);

		return newConnection;
	}

	private List<Subscription> buildSubscribtion(String connectionName) throws PropertyException {
		// try to read SubscribeList from Connection, if not exists return null
		Properties propertySubscribeList;
		try {
			propertySubscribeList = getPropertySubscribeList(connectionName);
		} catch (PropertyException e){
			return null;
		}

		List<Subscription> subscribtionList = new ArrayList<Subscription>();

		// for all Subscriptions
		for(String subscribeName: propertySubscribeList.getKeySet()){
			// build new Subscription
			Subscription subscribtion = new SubscriptionImpl();
			// set required values
			subscribtion.setName(subscribeName);
			subscribtion.setStartIdentifier(getPropertySubscriptionStartIdentifier(connectionName, subscribeName));
			subscribtion.setIdentifierType(getPropertySubscriptionIdentifierType(connectionName, subscribeName));
			// set optional values
			subscribtion.setMatchLinksFilter(getPropertySubscriptionMatchLinksFilter(connectionName, subscribeName));
			subscribtion.setResultFilter(getPropertySubscriptionResultFilter(connectionName, subscribeName));
			subscribtion.setTerminalIdentifierTypes(getPropertySubscriptionTerminalIdentifierTypes(connectionName, subscribeName));
			subscribtion.setMaxDepth(getPropertySubscriptionMaxDepth(connectionName, subscribeName));
			subscribtion.setMaxSize(getPropertySubscriptionMaxSize(connectionName, subscribeName));
			subscribtion.setStartupSubscribe(getPropertySubscriptionStartupSubscribe(connectionName, subscribeName));

			// add new Subscription
			subscribtionList.add(subscribtion);
		}

		return subscribtionList;
	}

	public void persistConnections() throws PropertyException {
		Map<String, Connection> connectionMap = mManager.getSavedConnections();

		for(Connection connection: connectionMap.values()){
			persistConnection(connection);
		}
	}

	private void persistConnection(Connection connection) throws PropertyException{
		// read values from Connection
		String name = connection.getConnectionName();
		String ifmapServerUrl = connection.getIfmapServerUrl();
		String userName = connection.getUserName();
		String userPassword = connection.getUserPassword();

		String truststorePath = connection.getTruststorePath();
		String truststorePassword = connection.getTruststorePassword();
		boolean authenticationBasic = connection.isAuthenticationBasic();
		boolean startupConnect = connection.doesConnectOnStartup();
		int maxPollResultSize = connection.getMaxPollResultSize();

		// set required values
		setPropertyIfmapServerUrl(name, ifmapServerUrl);
		setPropertyUserName(name, userName);
		setPropertyuserPassword(name, userPassword);

		// set Optional default values to connection, if value == default nothing to set
		if(!Same.check(truststorePath, DEFAULT_TRUSTSTORE_PATH)){
			setPropertyTruststorePath(name, truststorePath);
		}
		if(!Same.check(truststorePassword, DEFAULT_TRUSTSTORE_PASSWORD)){
			setPropertyTruststorePassword(name, truststorePassword);
		}
		if(!Same.check(authenticationBasic, DEFAULT_AUTHENTICATION_BASIC)){
			setPropertyAuthenticationBasic(name, authenticationBasic);
		}
		if(!Same.check(startupConnect, DEFAULT_STARTUP_CONNECT)){
			setPropertyStartupConnect(name, startupConnect);
		}
		if(!Same.check(maxPollResultSize, DEFAULT_MAX_POLL_RESULT_SIZE)){
			setPropertyMaxPollResultSize(name, maxPollResultSize);
		}

		// set subscription
		persistSubscriptions(connection);
	}

	private void persistSubscriptions(Connection connection) throws PropertyException{
		String connectionName = connection.getConnectionName();

		// read Subscriptions from Connection
		List<Subscription> subscribeList = connection.getSubscriptions();

		for(Subscription subscription: subscribeList){
			// read values from Subscription
			String subscriptionName = subscription.getName();
			String startIdentifier = subscription.getStartIdentifier();
			String identifierType = subscription.getIdentifierType();
			String matchLinksFilter = subscription.getMatchLinksFilter();
			String resultFilter = subscription.getResultFilter();
			String terminalIdentifierTypes = subscription.getTerminalIdentifierTypes();
			boolean startupSubscribe = subscription.isStartupSubscribe();
			int maxDepth = subscription.getMaxDepth();
			int maxSize = subscription.getMaxSize();

			setPropertySubscriptionStartIdentifier(connectionName, subscriptionName, startIdentifier);
			setPropertySubscriptionIdentifierType(connectionName, subscriptionName, identifierType);

			if(matchLinksFilter != null && !matchLinksFilter.isEmpty()){
				setPropertySubscriptionMatchLinksFilter(connectionName, subscriptionName, matchLinksFilter);
			}
			if(resultFilter != null){
				setPropertySubscriptionResultFilter(connectionName, subscriptionName, resultFilter);
			}
			if(terminalIdentifierTypes != null){
				setPropertySubscriptionTerminalIdentifierTypes(connectionName, subscriptionName, terminalIdentifierTypes);
			}
			if(!Same.check(startupSubscribe, DEFAULT_STARTUP_SUBSCRIPTION)){
				setPropertySubscriptionStartupSubscribe(connectionName, subscriptionName, startupSubscribe);
			}
			if(!Same.check(maxDepth, DEFAULT_SUBSCRIPTION_MAX_DEPTH)){
				setPropertySubscriptionMaxDepth(connectionName, subscriptionName, maxDepth);
			}
			if(!Same.check(maxSize, DEFAULT_MAX_POLL_RESULT_SIZE)){
				setPropertySubscriptionMaxSize(connectionName, subscriptionName, maxSize);
			}
		}
	}
	
	/**
	 * Get the sub-Properties(subscriptions) for connectionName.
	 * @param connectionName property key
	 * @return Properties
	 * @throws PropertyException
	 */
	private Properties getPropertySubscribeList(String connectionName) throws PropertyException {
		return super.get(connectionName).get("subscriptions");
	}

	private String getPropertyIfmapServerUrl(String connectionName) throws PropertyException{
		return super.get(connectionName).getString("ifmapServerUrl");
	}

	private void setPropertyIfmapServerUrl(String connectionName, String ifmapServerUrl) throws PropertyException{
		super.set(connectionName + ".ifmapServerUrl", ifmapServerUrl);
	}

	private String getPropertyUserName(String connectionName) throws PropertyException{
		return super.get(connectionName).getString("userName");
	}

	private void setPropertyUserName(String connectionName, String userName) throws PropertyException{
		super.set(connectionName + ".userName", userName);
	}

	private String getPropertyUserPassword(String connectionName) throws PropertyException{
		return super.get(connectionName).getString("userPassword");
	}

	private void setPropertyuserPassword(String connectionName, String userPassword) throws PropertyException{
		super.set(connectionName + ".userPassword", userPassword);
	}

	private String getPropertyTruststorePath(String connectionName) throws PropertyException{
		return super.get(connectionName).getString("truststorePath", DEFAULT_TRUSTSTORE_PATH);
	}

	private void setPropertyTruststorePath(String connectionName, String truststorePath) throws PropertyException{
		super.set(connectionName + ".truststorePath", truststorePath);
	}

	private String getPropertyTruststorePassword(String connectionName) throws PropertyException{
		return super.get(connectionName).getString("truststorePassword", DEFAULT_TRUSTSTORE_PASSWORD);
	}

	private void setPropertyTruststorePassword(String connectionName, String truststorePassword) throws PropertyException{
		super.set(connectionName + ".truststorePassword", truststorePassword);
	}

	private boolean isPropertyAuthenticationBasic(String connectionName) throws PropertyException{
		return super.get(connectionName).getBoolean("authenticationBasic", DEFAULT_AUTHENTICATION_BASIC);
	}

	private void setPropertyAuthenticationBasic(String connectionName, boolean authenticationBasic) throws PropertyException{
		super.set(connectionName + ".authenticationBasic", authenticationBasic);
	}

	private boolean isPropertyStartupConnect(String connectionName) throws PropertyException{
		return super.get(connectionName).getBoolean("useConnectionAsStartup", DEFAULT_STARTUP_CONNECT);
	}

	private void setPropertyStartupConnect(String connectionName, boolean startupConnect) throws PropertyException{
		super.set(connectionName + ".useConnectionAsStartup", startupConnect);
	}

	private int getPropertyMaxPollResultSize(String connectionName) throws PropertyException{
		return super.get(connectionName).getInt("maxPollResultSize", DEFAULT_MAX_POLL_RESULT_SIZE);
	}

	private void setPropertyMaxPollResultSize(String connectionName, int maxPollResultSize) throws PropertyException{
		super.set(connectionName + ".maxPollResultSize", maxPollResultSize);
	}

	private String getPropertySubscriptionStartIdentifier(String connectionName, String subscriptionName) throws PropertyException{
		return getPropertySubscribeList(connectionName).get(subscriptionName).getString("startIdentifier");
	}

	private void setPropertySubscriptionStartIdentifier(String connectionName, String subscriptionName, String startIdentifier) throws PropertyException{
		set(connectionName + ".subscriptions." + subscriptionName + ".startIdentifier", startIdentifier);
	}

	private String getPropertySubscriptionIdentifierType(String connectionName, String subscriptionName) throws PropertyException{
		return getPropertySubscribeList(connectionName).get(subscriptionName).getString("identifierType");
	}

	private void setPropertySubscriptionIdentifierType(String connectionName, String subscriptionName, String identifierType) throws PropertyException{
		set(connectionName + ".subscriptions." + subscriptionName + ".identifierType", identifierType);
	}

	private String getPropertySubscriptionMatchLinksFilter(String connectionName, String subscriptionName) throws PropertyException{
		return getPropertySubscribeList(connectionName).get(subscriptionName).getString("matchLinksFilter", null);
	}

	private void setPropertySubscriptionMatchLinksFilter(String connectionName, String subscriptionName, String matchLinksFilter) throws PropertyException{
		set(connectionName + ".subscriptions." + subscriptionName + ".matchLinksFilter", matchLinksFilter);
	}

	private String getPropertySubscriptionResultFilter(String connectionName, String subscriptionName) throws PropertyException{
		return getPropertySubscribeList(connectionName).get(subscriptionName).getString("resultFilter", null);
	}

	private void setPropertySubscriptionResultFilter(String connectionName, String subscriptionName, String resultFilter) throws PropertyException{
		set(connectionName + ".subscriptions." + subscriptionName + ".resultFilter", resultFilter);
	}

	private String getPropertySubscriptionTerminalIdentifierTypes(String connectionName, String subscriptionName) throws PropertyException{
		return getPropertySubscribeList(connectionName).get(subscriptionName).getString("terminalIdentifierTypes", null);
	}

	private void setPropertySubscriptionTerminalIdentifierTypes(String connectionName, String subscriptionName, String terminalIdentifierTypes) throws PropertyException{
		set(connectionName + ".subscriptions." + subscriptionName + ".terminalIdentifierTypes", terminalIdentifierTypes);
	}

	private int getPropertySubscriptionMaxDepth(String connectionName, String subscriptionName) throws PropertyException{
		return getPropertySubscribeList(connectionName).get(subscriptionName).getInt("maxDepth", DEFAULT_SUBSCRIPTION_MAX_DEPTH);
	}

	private void setPropertySubscriptionMaxDepth(String connectionName, String subscriptionName, int maxDepth) throws PropertyException{
		set(connectionName + ".subscriptions." + subscriptionName + ".maxDepth", maxDepth);
	}

	private int getPropertySubscriptionMaxSize(String connectionName, String subscriptionName) throws PropertyException{
		return getPropertySubscribeList(connectionName).get(subscriptionName).getInt("maxSize", DEFAULT_MAX_POLL_RESULT_SIZE);
	}

	private void setPropertySubscriptionMaxSize(String connectionName, String subscriptionName, int maxSize) throws PropertyException{
		set(connectionName + ".subscriptions." + subscriptionName + ".maxSize", maxSize);
	}

	private boolean getPropertySubscriptionStartupSubscribe(String connectionName, String subscriptionName) throws PropertyException{
		return getPropertySubscribeList(connectionName).get(subscriptionName).getBoolean("useSubscriptionAsStartup", DEFAULT_STARTUP_SUBSCRIPTION);
	}

	private void setPropertySubscriptionStartupSubscribe(String connectionName, String subscriptionName, boolean startupSubscribe) throws PropertyException{
		set(connectionName + ".subscriptions." + subscriptionName + ".useSubscriptionAsStartup", startupSubscribe);
	}
}