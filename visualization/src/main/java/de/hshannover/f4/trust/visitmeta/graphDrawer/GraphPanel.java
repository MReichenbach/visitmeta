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
package de.hshannover.f4.trust.visitmeta.graphDrawer;




/* Imports ********************************************************************/
import java.util.List;

import javax.swing.JComponent;

import de.hshannover.f4.trust.visitmeta.datawrapper.ExpandedLink;
import de.hshannover.f4.trust.visitmeta.datawrapper.NodeIdentifier;
import de.hshannover.f4.trust.visitmeta.datawrapper.NodeMetadata;
import de.hshannover.f4.trust.visitmeta.datawrapper.NodeType;
import de.hshannover.f4.trust.visitmeta.datawrapper.Position;
import de.hshannover.f4.trust.visitmeta.interfaces.Propable;
/* Class **********************************************************************/
/**
 * A Panel that shows the graph.
 */
public interface GraphPanel {
	/* Attributes *****************************************************************/
	/* Constructors ***************************************************************/
	/* Methods ********************************************************************/
	/**
	 * Return a Panel with the graph.
	 * @return the Panel.
	 */
	public JComponent getPanel();
	/**
	 * Add an IdentifierNode to the graph.
	 * @param node the Identifier to identify the node in the graph.
	 */
	public void addIdentifier(NodeIdentifier node);
	/**
	 * Add a MetadataNode and edges to the IdentifierNode to the graph.
	 * @param identifier the identifier.
	 * @param metadata the metadata.
	 */
	public void addMetadata(NodeIdentifier identifier, NodeMetadata metadata);
	/**
	 * Add a MetadataNode and edges to the IdentifierNodes of the link to the graph.
	 * @param link the link.
	 * @param metadata the metadata.
	 */
	public void addMetadata(ExpandedLink link, NodeMetadata metadata);
	/**
	 * Update a Identifier from the graph.
	 * @param node the Identifier to identify the node in the graph.
	 */
	public void updateIdentifier(NodeIdentifier node);
	/**
	 * Update a Metadata from the graph.
	 * @param node the Metadata to identify the node in the graph.
	 */
	public void updateMetadata(NodeMetadata node);
	/**
	 * Remove the node from the panel.
	 * @param position the position object corresponding to the node.
	 */
	public void deleteNode(Position position);
	/**
	 * Highlight the node as new.
	 * @param position the position object corresponding to the node.
	 */
	public void markAsNew(Position position);
	/**
	 * Highlight the node as deleted.
	 * @param position the position object corresponding to the node.
	 */
	public void markAsDelete(Position position);
	/**
	 * Remove the highlighting of a node.
	 * @param position the position object corresponding to the node.
	 */
	public void clearHighlight(Position position);
	/**
	 * Remove all nodes and edges from the graph.
	 */
	public void clearGraph();
	/**
	 * Adjust the size of the panel depending on the number and size of nodes.
	 */
	public void adjustPanelSize();
	/**
	 * Repaint nodes of a specific type and publisher.
	 * @param pType the type of the node,
	 */
	public void repaintNodes(NodeType pType);
	/**
	 * Repaint the panel.
	 */
	public void repaint();
	/**
	 * Get a List of all known Publisher.
	 */
	public List<String> getPublisher();
	/**
	 * Set the animation time for the translation of a node to the new position.
	 * @param pNodeTranslationDuration the time.
	 */
	public void setNodeTranslationDuration(int pNodeTranslationDuration);

	/**
	 * Mark a node as selected.
	 *
	 * @param propable the {@link Propable} object that was selected
	 */
	public void selectNode(Propable propable);

	/**
	 * Unselects the former selected node.
	 */
	public void unselectNode();
}
