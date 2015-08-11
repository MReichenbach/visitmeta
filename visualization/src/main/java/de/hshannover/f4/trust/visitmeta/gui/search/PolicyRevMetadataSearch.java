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
 * This file is part of visitmeta-visualization, version 0.5.0,
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
package de.hshannover.f4.trust.visitmeta.gui.search;

import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.hshannover.f4.trust.ifmapj.exception.UnmarshalException;
import de.hshannover.f4.trust.visitmeta.datawrapper.NodeMetadata;
import de.hshannover.f4.trust.visitmeta.interfaces.Identifier;
import de.hshannover.f4.trust.visitmeta.interfaces.Metadata;
import de.hshannover.f4.trust.visitmeta.util.DocumentUtils;

/**
 * Implementation of the {@link SearchAndFilterStrategy}, that allows search referenced feature metadata from
 * policy-features.
 *
 * @author Marcel Reichenbach
 *
 */
public class PolicyRevMetadataSearch implements SearchAndFilterStrategy {

	public static final String POLICY_FEATURE_EL_NAME = "policy-feature";

	private Metadata mPickedMetadata;

	private Document mPickedDocument;

	private Element mPickedRevMetadataElement;

	private Element mPickedRevIdentifierElement;

	private Searchable mSearchableGraphPanel;

	/**
	 * Creates a new {@link PolicyRevMetadataSearch} instance.
	 *
	 * @param searchableGraphPanel a {@link Searchable} instance, used to visualize search terms.
	 */
	public PolicyRevMetadataSearch(Searchable searchableGraphPanel) {
		mSearchableGraphPanel = searchableGraphPanel;
	}

	@Override
	public JPanel getJPanel() {
		throw new UnsupportedOperationException("This " + SearchAndFilterStrategy.class.getSimpleName()
				+ " is only for policy-rev-metadatas. No Panel.");
	}

	/**
	 * This {@link SearchAndFilterStrategy} is only for policy-feature metadata.
	 *
	 * @param identifier either a {@link Identifier} or a {@link Metadata} object
	 * @param searchTerm a search term
	 * @return false, always
	 */
	@Override
	public boolean containsSearchTerm(Identifier identifier, String searchTerm) {
		return false;
	}

	/**
	 * Checks if the referenced metadata from the policy-feature metadata(the picked-metadata) is equal
	 *
	 * @param metadata either a {@link Identifier} or a {@link Metadata} object
	 * @param nodeMetadata
	 * @param searchTerm a search term
	 * @return true, if the metadata is the picked-metadata or the referenced metadata from the policy-feature
	 *         metadata(the picked-metadata) and the referenced identifier is equals
	 */
	// TODO noch etwas optimieren so das die methode schnell verlassen wird wenn es eh fals ist
	@Override
	public boolean containsSearchTerm(Metadata metadata, NodeMetadata nodeMetadata, String searchTerm) {
		if (metadata == mPickedMetadata) {
			return true;
		}

		Document document = DocumentUtils.parseEscapedXmlString(metadata.getRawData());
		Element revMetadataElement = document.getDocumentElement();

		if (revMetadataElement.isEqualNode(mPickedRevMetadataElement)) {
			Document revIdentifierDocumen = DocumentUtils.parseEscapedXmlString(nodeMetadata.getRichMetadata()
					.getIdentifier().getRawData());

			Element revIdentifierElement = revIdentifierDocumen.getDocumentElement();
			if (revIdentifierElement.isEqualNode(mPickedRevIdentifierElement)) {
				return true;
			}
		}
		return false;
	}

	public void setPickedNode(Metadata metadata) throws UnmarshalException {
		mPickedMetadata = metadata;
		mPickedDocument = DocumentUtils.parseEscapedXmlString(metadata.getRawData());
		mPickedRevMetadataElement = (Element) mPickedDocument.getDocumentElement().getFirstChild().getFirstChild();
		mPickedRevIdentifierElement = (Element) mPickedRevMetadataElement.getNextSibling().getFirstChild();
	}

}
