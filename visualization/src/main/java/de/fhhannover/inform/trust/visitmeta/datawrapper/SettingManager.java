package de.fhhannover.inform.trust.visitmeta.datawrapper;

/*
 * #%L
 * ====================================================
 *   _____                _     ____  _____ _   _ _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \|  ___| | | | | | |
 *    | | | '__| | | / __| __|/ / _` | |_  | |_| | |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _| |  _  |  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_|   |_| |_|_| |_|
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
 * This file is part of VisITMeta, version 0.0.2, implemented by the Trust@FHH 
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2012 - 2013 Trust@FHH
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
/* Imports ********************************************************************/
import java.util.Observable;

import org.apache.log4j.Logger;
/* Class **********************************************************************/
/**
 * Manage the settings of the application.
 */
public class SettingManager extends Observable {
/* Attributes *****************************************************************/
	private static final Logger LOGGER = Logger.getLogger(SettingManager.class);
	/** Singleton */
	private static SettingManager mInstance = null;

	private int mNetworkInterval         = 0;
	private int mCalculationInterval     = 0;
	private int mCalculationIterations   = 0;
	private int mHighlightsTimeout       = 0;
	private int mNodeTranslationDuration = 0;
/* Constructors ***************************************************************/
	private SettingManager() {
		LOGGER.debug("Load settings.");
		mNetworkInterval = Integer.parseInt(PropertiesManager.getProperty(
				"visualizationConfig",
				"network.interval",
				"10000"
		));
		mCalculationInterval = Integer.parseInt(PropertiesManager.getProperty(
				"visualizationConfig",
				"calculation.interval",
				"3000"
		));
		mCalculationIterations = Integer.parseInt(PropertiesManager.getProperty(
				"visualizationConfig",
				"calculation.iterations",
				"100"
		));
		mHighlightsTimeout = Integer.parseInt(PropertiesManager.getProperty(
				"visualizationConfig",
				"highlights.timeout",
				"5000"
		));
		mNodeTranslationDuration = Integer.parseInt(PropertiesManager.getProperty(
				"visualizationConfig",
				"node.translation.duration",
				"1000"
		));
	}
/* Methods ********************************************************************/
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		/* Store Setting in Properties */
		LOGGER.debug("Save settings.");
		PropertiesManager.storeProperty(
				"visualizationConfig",
				"network.interval",
				String.valueOf(mNetworkInterval)
		);
		PropertiesManager.storeProperty(
				"visualizationConfig",
				"calculation.interval",
				String.valueOf(mCalculationInterval)
		);
		PropertiesManager.storeProperty(
				"visualizationConfig",
				"calculation.iterations",
				String.valueOf(mCalculationIterations)
		);
		PropertiesManager.storeProperty(
				"visualizationConfig",
				"highlights.timeout",
				String.valueOf(mHighlightsTimeout)
		);
		PropertiesManager.storeProperty(
				"visualizationConfig",
				"node.translation.duration",
				String.valueOf(mNodeTranslationDuration)
		);
	}
/* Methods - Getter ***********************************************************/
	/**
	 * Singleton Thread-Safe
	 * @return the instance of SettingManager.
	 */
	public static SettingManager getInstance() {
		LOGGER.trace("Method getInstance() called.");
		if(mInstance == null) { // DoubleCheck
			synchronized (SettingManager.class) {
				if (mInstance == null) {
					mInstance = new SettingManager();
				}
			}
		}
		return mInstance;
	}
	public synchronized int getNetworkInterval() {
		LOGGER.trace("Method getNetworkInterval() called.");
		return mNetworkInterval;
	}
	public synchronized int getCalculationInterval() {
		LOGGER.trace("Method getCalculationInterval() called.");
		return mCalculationInterval;
	}
	public synchronized int getCalculationIterations() {
		LOGGER.trace("Method getCalculationIterations() called.");
		return mCalculationIterations;
	}
	public synchronized int getHighlightsTimeout() {
		LOGGER.trace("Method getHighlightsTimeout() called.");
		return mHighlightsTimeout;
	}
	public int getNodeTranslationDuration() {
		LOGGER.trace("Method getNodeTranslationDuration() called.");
		return mNodeTranslationDuration;
	}
/* Methods - Setter ***********************************************************/
	public synchronized void setNetworkInterval(int pNetworkInterval) {
		LOGGER.trace("Method setNetworkInterval(" + pNetworkInterval + ") called.");
		mNetworkInterval = pNetworkInterval;
		setChanged();
	}
	public synchronized void setCalculationInterval(int pCalculationInterval) {
		LOGGER.trace("Method setCalculationInterval(" + pCalculationInterval + ") called.");
		mCalculationInterval = pCalculationInterval;
		setChanged();
	}
	public synchronized void setCalculationIterations(int pCalculationIterations) {
		LOGGER.trace("Method setCalculationIterations(" + pCalculationIterations + ") called.");
		mCalculationIterations = pCalculationIterations;
		setChanged();
	}
	public synchronized void setHighlightsTimeout(int pHighlightsTimeout) {
		LOGGER.trace("Method setHighlightsTimeout(" + pHighlightsTimeout + ") called.");
		mHighlightsTimeout = pHighlightsTimeout;
		setChanged();
	}
	public void setNodeTranslationDuration(int pNodeTranslationDuration) {
		LOGGER.trace("Method setNodeTranslationDuration(" + pNodeTranslationDuration + ") called.");
		mNodeTranslationDuration = pNodeTranslationDuration;
		setChanged();
	}

}
