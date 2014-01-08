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
 * This file is part of visitmeta dataservice, version 0.0.3,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2012 - 2013 Trust@HsH
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

package de.hshannover.f4.trust.visitmeta.ifmap;

import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.ifmapj.IfmapJHelper;
import de.hshannover.f4.trust.ifmapj.exception.CommunicationException;
import de.hshannover.f4.trust.ifmapj.exception.EndSessionException;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.messages.PollResult;
import de.hshannover.f4.trust.ifmapj.messages.Requests;
import de.hshannover.f4.trust.ifmapj.messages.SubscribeDelete;
import de.hshannover.f4.trust.ifmapj.messages.SubscribeElement;
import de.hshannover.f4.trust.ifmapj.messages.SubscribeRequest;
import de.hshannover.f4.trust.visitmeta.dataservice.Application;
import de.hshannover.f4.trust.visitmeta.dataservice.factories.InMemoryIdentifierFactory;
import de.hshannover.f4.trust.visitmeta.dataservice.factories.InMemoryMetadataFactory;
import de.hshannover.f4.trust.visitmeta.dataservice.graphservice.SimpleGraphService;
import de.hshannover.f4.trust.visitmeta.dataservice.util.ConfigParameter;
import de.hshannover.f4.trust.visitmeta.ifmap.exception.ActiveDumpingException;
import de.hshannover.f4.trust.visitmeta.ifmap.exception.ConnectionCloseException;
import de.hshannover.f4.trust.visitmeta.ifmap.exception.ConnectionEstablishedException;
import de.hshannover.f4.trust.visitmeta.ifmap.exception.ConnectionException;
import de.hshannover.f4.trust.visitmeta.ifmap.exception.IfmapConnectionException;
import de.hshannover.f4.trust.visitmeta.ifmap.exception.NoActiveDumpingException;
import de.hshannover.f4.trust.visitmeta.ifmap.exception.NotConnectedException;
import de.hshannover.f4.trust.visitmeta.persistence.neo4j.Neo4JDatabase;
import de.hshannover.f4.trust.visitmeta.util.PropertiesReaderWriter;

public class Connection {

	private static final PropertiesReaderWriter config = Application.getIFMAPConfig();
	
	private Logger log = Logger.getLogger(Connection.class);


	private Neo4JDatabase mNeo4JDb;

	private UpdateService mUpdateService;

	private DumpingService mDumpingService;


	private Thread mUpdateThread;

	private Thread mDumpingThread;


	private ThreadSafeSsrc mSsrc;

	private boolean connected = false;


	private String mConnectionName;

	private String mUrl;

	private String mUser;

	private String mUserPass;

	private String mTruststore;

	private String mTruststorePass;


	private Set<String> activeSubscriptions;


	public Connection(String name, String url, String user, String userPass, String truststore, String truststorePass) {
		log.trace("new Connection() ...");

		mConnectionName = name;
		mUrl = url;
		mUser = user;
		mUserPass = userPass;
		mTruststore = truststore;
		mTruststorePass = truststorePass;

		mNeo4JDb = new Neo4JDatabase(name);
		activeSubscriptions = new HashSet<String>();

		log.trace("... new Connection() OK");
	}

	/**
	 * Connect to the MAP-Server.
	 * 
	 * @throws ConnectionException
	 */
	public void connect() throws ConnectionException {

		isConnectionDisconnected();

		initSsrc(mUrl, mUser, mUserPass, mTruststore, mTruststorePass);
		initSession();

	}

	private void initSsrc(String url, String user, String userPass, String truststore, String truststorePass) throws IfmapConnectionException {
		log.debug("init SSRC ...");

		try {

			TrustManager[] tms = IfmapJHelper.getTrustManagers(UpdateService.class.getResourceAsStream(truststore), truststorePass);
			mSsrc = new ThreadSafeSsrc(url, user, userPass, tms);

		} catch (InitializationException e) {

			log.error("Description: " + e.getDescription() + " | ErrorMessage: " + e.getMessage(), e);

			resetConnection();

			throw new IfmapConnectionException();
		}

		log.debug("... init SSRC OK");
	}

	private void initSession() throws IfmapConnectionException {
		log.debug("creating new SSRC session ...");

		try {

			mSsrc.newSession(Integer.parseInt(config.getProperty(ConfigParameter.IFMAP_MAX_SIZE)));

			setConnected(true);

		} catch (IfmapErrorResult e) {

			log.error("ErrorCode: " + e.getErrorCode() + " | ErrorString: " + e.getErrorString(), e);

			resetConnection();

			throw new IfmapConnectionException();

		} catch (IfmapException e) {

			log.error("Description: " + e.getDescription() + " | ErrorMessage: " + e.getMessage(), e);

			resetConnection();

			throw new IfmapConnectionException();
		}

		log.debug("... new SSRC session OK");
	}

	/**
	 * Close a connection to the MAP-Server.
	 * 
	 * @throws ConnectionException
	 */
	public void disconnect() throws ConnectionException {

		isConnectionEstablished();

		try {

			log.debug("endSession() ...");
			mSsrc.endSession();
			log.debug("... endSession() OK");

			log.debug("closeTcpConnection() ...");
			mSsrc.closeTcpConnection();
			log.debug("... closeTcpConnection() OK");

		} catch (IfmapErrorResult e) {

			log.error("ErrorCode: " + e.getErrorCode() + " | ErrorString: " + e.getErrorString(), e);

			throw new IfmapConnectionException();

		} catch (CommunicationException e) {

			log.error("ErrorMessage: " + e.getMessage(), e);

			throw new IfmapConnectionException();

		} catch (IfmapException e) {

			log.error("Description: " + e.getDescription() + " | ErrorMessage: " + e.getMessage(), e);

			throw new IfmapConnectionException();

		}finally{

			resetConnection();
		}
	}

	private void startUpdateService() throws ConnectionException{

		if(mUpdateThread == null || !mUpdateThread.isAlive()){

			log.trace("start UpdateService from connection " + mConnectionName + " ...");

			isConnectionEstablished();

			if(mUpdateService == null){
				mUpdateService = new UpdateService(this, mNeo4JDb.getWriter(), new InMemoryIdentifierFactory(), new InMemoryMetadataFactory());
			}

			mUpdateThread = new Thread(mUpdateService, "UpdateThread-" + mConnectionName);

			mUpdateThread.start();

			log.debug("UpdateService for connection " + mConnectionName + " started");
		}
	}

	/**
	 * Start a Dumping-Service.
	 * 
	 * @throws ConnectionException
	 */
	public void startDumpingService() throws ConnectionException{

		isConnectionEstablished();

		isDumpingStopped();

		startUpdateService();

		if(mDumpingService == null){
			mDumpingService = new DumpingService(this, mSsrc);
		}

		if(mDumpingThread == null || !mDumpingThread.isAlive()){

			mDumpingThread = new Thread(mDumpingService, "DumpingThread-" + mConnectionName);

			mDumpingThread.start();
		}
	}

	/**
	 * Stopped a active Dumping-Service.
	 * 
	 * @throws ConnectionException
	 */
	public void stopDumpingService() throws ConnectionException{

		isConnectionEstablished();

		isDumpingActive();

		mDumpingThread.interrupt();
		mDumpingThread = null;
	}

	/**
	 * Send a SubscribeRequest to the map server.
	 * 
	 * @param request
	 * @throws ConnectionException
	 */
	public void subscribe(SubscribeRequest request) throws ConnectionException {

		isConnectionEstablished();

		isDumpingStopped();

		startUpdateService();

		if(request == null || !request.getSubscribeElements().isEmpty()){

			try {

				mSsrc.subscribe(request);

			} catch (IfmapErrorResult e) {

				log.error("ErrorCode: " + e.getErrorCode() + " | ErrorString: " + e.getErrorString(), e);

				throw new IfmapConnectionException();

			} catch (IfmapException e) {

				log.error("Description: " + e.getDescription()+ " | ErrorMessage: " + e.getMessage(), e);

				throw new IfmapConnectionException();
			}

			for(SubscribeElement r: request.getSubscribeElements()){

				activeSubscriptions.add(r.getName());
			}

		}else{

			log.warn("Subscribe-Request was null or empty.");
		}
	}

	/**
	 * A poll-request over the ARC
	 * 
	 * @return PollResult
	 * @throws ConnectionException
	 */
	public PollResult poll() throws ConnectionException {

		isConnectionEstablished();

		try {

			return mSsrc.getArc().poll();

		} catch (IfmapErrorResult e) {

			log.error("ErrorCode: " + e.getErrorCode() + " | ErrorString: " + e.getErrorString(), e);

			throw new IfmapConnectionException();

		} catch (EndSessionException e) {

			//	Nothing to log while disconnecting from MAP-Server

			throw new ConnectionCloseException();

		} catch (IfmapException e) {

			log.error("Description: " + e.getDescription()+ " | ErrorMessage: " + e.getMessage(), e);

			throw new IfmapConnectionException();
		}
	}

	/**
	 * Delete a subscription with his name.
	 * 
	 * @param sName The subscription name.
	 * @throws ConnectionException
	 */
	public void deleteSubscribe(String sName) throws ConnectionException {

		SubscribeRequest request = Requests.createSubscribeReq();
		SubscribeDelete subscribe = Requests.createSubscribeDelete(sName);

		request.addSubscribeElement(subscribe);

		subscribe(request);

		activeSubscriptions.remove(sName);
	}

	/**
	 * Delete all active subscriptions.
	 * 
	 * @throws ConnectionException
	 */
	public void deleteSubscriptions() throws ConnectionException{

		SubscribeRequest request = Requests.createSubscribeReq();

		for(String sKey: activeSubscriptions){

			SubscribeDelete subscribe = Requests.createSubscribeDelete(sKey);
			request.addSubscribeElement(subscribe);
		}

		subscribe(request);

		activeSubscriptions.clear();
	}

	@Override
	public String toString(){

		StringBuilder sb = new StringBuilder();

		sb.append("Connection: ").append(mConnectionName).append(" | URL: ");
		sb.append(mUrl).append(" | User: ").append(mUser);

		return sb.toString();
	}

	/**
	 * @return The session-id of the SSRC
	 * @throws ConnectionException
	 */
	public String getSessionId() throws ConnectionException {

		isConnectionEstablished();

		return mSsrc.getSessionId();
	}

	/**
	 * @return The publisher-id of the SSRC
	 * @throws ConnectionException
	 */
	public String getPublisherId() throws ConnectionException{

		isConnectionEstablished();

		return mSsrc.getPublisherId();
	}

	public boolean isConnectionEstablished() throws NotConnectedException{

		if(mSsrc == null || !isConnected() || mSsrc.getSessionId() == null){

			NotConnectedException e = new NotConnectedException();

			log.error(e.getClass().getSimpleName());

			resetConnection();

			throw e;
		}

		return true;
	}

	private boolean isConnectionDisconnected() throws ConnectionEstablishedException {

		if(mSsrc != null && isConnected() && mSsrc.getSessionId() != null){

			ConnectionEstablishedException e = new ConnectionEstablishedException();

			log.error(e.getClass().getSimpleName());

			throw e;
		}

		return true;
	}

	private boolean isDumpingActive() throws NoActiveDumpingException {

		if(mDumpingThread == null || !mDumpingThread.isAlive()){

			NoActiveDumpingException e = new NoActiveDumpingException();

			log.error(e.getClass().getSimpleName());

			throw e;
		}

		return true;
	}

	private boolean isDumpingStopped() throws ActiveDumpingException {

		if(mDumpingThread != null && mDumpingThread.isAlive()){

			ActiveDumpingException e = new ActiveDumpingException();

			log.error(e.getClass().getSimpleName());

			throw e;
		}

		return true;
	}

	/**
	 * @return A Set<String> with the active subscriptions.
	 * @throws ConnectionException
	 */
	public Set<String> getActiveSubscriptions() throws ConnectionException {

		isConnectionEstablished();

		isDumpingStopped();

		HashSet<String> temp = new HashSet<String>(activeSubscriptions);

		log.trace("getActiveSubscriptions(): " + temp);

		return temp;
	}

	private void resetConnection() {
		log.trace("resetConnection() ...");

		setConnected(false);
		mSsrc = null;
		activeSubscriptions.clear();

		log.trace("... resetConnection() OK");
	}

	private void setConnected(boolean b){
		connected = b;
	}

	/**
	 * @return True when the connection to a MAP-Server is active.
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @return A SimpleGraphService from the Neo4J-Database.
	 */
	public SimpleGraphService getGraphService() {
		return mNeo4JDb.getGraphService();
	}
}
