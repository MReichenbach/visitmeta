package de.fhhannover.inform.trust.visitmeta.network;

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

import java.util.HashMap;

import org.apache.log4j.Logger;

import de.fhhannover.inform.trust.visitmeta.datawrapper.NodeMetadata;
import de.fhhannover.inform.trust.visitmeta.interfaces.Metadata;

/**
 * Pool class that contains a mapping of all Metadata to NodeMetadata in the current layout.
 * TODO Pool must manage the count of entities.
 */
public class PoolNodeMetadata {

	private static final Logger LOGGER = Logger.getLogger(PoolNodeMetadata.class);

	private static HashMap<Metadata,NodeMetadata> mPoolMetadataActive  = new HashMap<Metadata,NodeMetadata>();
//	private static HashMap<Metadata,NodeMetadata> mPoolMetadataSuspend = new HashMap<Metadata,NodeMetadata>();

	/**
	 * Get a NodeMetadata.
	 * @param metadata the Metadata for the NodeMetadata.
	 * @return the NodeMetadata (active) or
	 *         Null if the NodeMetadta doesn't exist.
	 */
	public static NodeMetadata getActive(Metadata metadata) {
		LOGGER.trace("Method getActive(" + metadata + ") called.");
		return mPoolMetadataActive.get(metadata);
	}

//	/**
//	 * Get a NodeMetadata and activate the NodeMetadata if suspend.
//	 * @param metadata the Metadata for the NodeMetadata.
//	 * @return the NodeMetadata (active or suspend) or
//	 *         Null if the NodeMetadta doesn't exist.
//	 */
//	public static NodeMetadata getActiveOrSuspend(Metadata metadata) {
//		LOGGER.trace("Method getActiveOrSuspend(" + metadata + ") called.");
//		NodeMetadata result = mPoolMetadataActive.get(metadata);
//		if(result == null) {
//			result = mPoolMetadataSuspend.get(metadata);
//		}
//		return result;
//	}

	public static HashMap<Metadata,NodeMetadata> get() {
		LOGGER.trace("Method get() called.");
		return mPoolMetadataActive;
	}

	/**
	 * Suspend a NodeMetadata.
	 * @param metadata the Metadata that reference the NodeMetadata.
	 */
	public static void release(Metadata metadata) {
		LOGGER.trace("Method release(" + metadata + ") called.");
//		mPoolMetadataSuspend.put(metadata, mPoolMetadataActive.get(metadata));
		mPoolMetadataActive.remove(metadata);
	}

	/**
	 * Suspend all NodeMetadata.
	 */
	public static void clear() {
		LOGGER.trace("Method clear() called.");
//		mPoolMetadataSuspend.putAll(mPoolMetadataActive);
		mPoolMetadataActive.clear();
	}

	/**
	 * Create a new NodeMetadata.
	 * @param metadata the Metadata for the NodeMetadata.
	 * @return the new NodeMetadata or null if the NodeMetadata already existed.
	 */
	public static NodeMetadata create(Metadata metadata) {
		LOGGER.trace("Method create(" + metadata + ") called.");
		if(mPoolMetadataActive.containsKey(metadata)) {
			return null;
		} else {
			LOGGER.debug("Create new metadata.");
			NodeMetadata nodeMetadata = new NodeMetadata(metadata);
			mPoolMetadataActive.put(metadata, nodeMetadata);
			return nodeMetadata;
		}
	}

//	/**
//	 * Create or get a Nodemetadata and activate the NodeMetadata if suspend.
//	 * @param metadata the Metadata for the NodeMetadata.
//	 * @return the new NodeMetadata or the already existing NodeMetadata (active and suspend).
//	 */
//	public static NodeMetadata createOrActivate(Metadata metadata) {
//		LOGGER.trace("Method createOrActive(" + metadata + ") called.");
//		if(mPoolMetadataActive.containsKey(metadata)) {
//			/* NodeMetadata is active */
//			return null;
//		} else if(mPoolMetadataSuspend.containsKey(metadata)) {
//			/* Activate NodeMetadata */
//			NodeMetadata nodeMetadata = mPoolMetadataSuspend.get(metadata);
//			mPoolMetadataActive.put(metadata, nodeMetadata);
//			mPoolMetadataSuspend.remove(metadata);
//			return nodeMetadata;
//		} else {
//			/* Create new NodeMetadata */
//			LOGGER.debug("Create new metadata.");
//			NodeMetadata nodeMetadata = new NodeMetadata(metadata);
//			mPoolMetadataActive.put(metadata, nodeMetadata);
//			return nodeMetadata;
//		}
//	}

	/**
	 * Create or get a Nodemetadata.
	 * @param metadata the Metadata for the NodeMetadata.
	 * @return the new NodeMetadata or the already existing NodeMetadata (active).
	 *         Null if the NodeMetadata is suspend.
	 */
	public static NodeMetadata createOrGet(Metadata metadata) {
		LOGGER.trace("Method createOrGet(" + metadata + ") called.");
		if(mPoolMetadataActive.containsKey(metadata)) {
			LOGGER.debug("Found existing metadata.");
			return getActive(metadata);
		} else {
			LOGGER.debug("Create new metadata.");
			NodeMetadata nodeMetadata = new NodeMetadata(metadata);
			mPoolMetadataActive.put(metadata, nodeMetadata);
			return nodeMetadata;
		}
	}
}
