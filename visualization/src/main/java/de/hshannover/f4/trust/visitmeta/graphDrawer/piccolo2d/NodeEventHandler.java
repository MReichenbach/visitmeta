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
package de.hshannover.f4.trust.visitmeta.graphDrawer.piccolo2d;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.ifmapj.exception.UnmarshalException;
import de.hshannover.f4.trust.visitmeta.datawrapper.NodeIdentifier;
import de.hshannover.f4.trust.visitmeta.datawrapper.NodeMetadata;
import de.hshannover.f4.trust.visitmeta.datawrapper.Position;
import de.hshannover.f4.trust.visitmeta.graphDrawer.Piccolo2DPanel;
import de.hshannover.f4.trust.visitmeta.gui.GraphConnection;
import de.hshannover.f4.trust.visitmeta.gui.search.PolicyRevMetadataSearch;
import de.hshannover.f4.trust.visitmeta.gui.search.SimpleSearchAndNoFilter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

public class NodeEventHandler extends PDragEventHandler {

	private static final int MOUSE_LEFT_BUTTON = 1;

	private static final Logger LOGGER = Logger.getLogger(NodeEventHandler.class);
	GraphConnection mConnection = null;
	Piccolo2DPanel mPanel = null;

	public NodeEventHandler(GraphConnection connection, Piccolo2DPanel pPanel) {
		mConnection = connection;
		mPanel = pPanel;
	}

	@Override
	protected void startDrag(PInputEvent e) {
		LOGGER.trace("Method startDrag(" + e + ") called.");
		PNode pickedNode = e.getPickedNode();
		if (pickedNode instanceof PComposite) {
			super.startDrag(e);
			e.setHandled(true);
			pickedNode.moveToFront();
			Position position = (Position) pickedNode.getAttribute("position");
			if (position != null) {
				position.setInUse(true);
			}

			PActivity activity = (PActivity) pickedNode.getAttribute("activity");
			if (activity != null) {
				activity.terminate();
			}
		}
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	protected void drag(PInputEvent e) {
		LOGGER.trace("Method drag(" + e + ") called.");
		PNode pickedNode = e.getPickedNode();
		if (pickedNode instanceof PComposite) {
			super.drag(e);
			PComposite vNode = (PComposite) pickedNode;

			/* Redraw edges */
			ArrayList<PPath> vEdges = (ArrayList<PPath>) vNode.getAttribute("edges");
			for (PPath vEdge : vEdges) {
				mPanel.updateEdge(vEdge);
			}
			/* TODO Redraw the shadow */
			PPath vShadow = (PPath) vNode.getAttribute("glow");
			if (vShadow != null) {
				vShadow.setOffset(vNode.getOffset());
			}
		}
	}

	@Override
	protected void endDrag(PInputEvent e) {
		LOGGER.trace("Method endDrag(" + e + ") called.");
		PNode pickedNode = e.getPickedNode();
		if (pickedNode instanceof PComposite) {
			super.endDrag(e);
			/* Set new position */
			Point2D vPoint = e.getPickedNode().getOffset();
			boolean pinNode = e.isControlDown() ? true : false;
			Position vNode = (Position) e.getPickedNode().getAttribute("position");
			mConnection.updateNode(vNode, // Position-Object
					(vPoint.getX() / mPanel.getAreaWidth()) + mPanel.getAreaOffsetX(), // x
					(vPoint.getY() / mPanel.getAreaHeight()) + mPanel.getAreaOffsetY(), // y
					0.0, // z
					pinNode
					);
			vNode.setInUse(false);
		}
	}

	@Override
	public void mouseEntered(PInputEvent e) {
		LOGGER.trace("Method mouseEntered(" + e + ") called.");
		super.mouseEntered(e);

		if (!mConnection.isPropablePicked()) {
			PNode pickedNode = e.getPickedNode();
			if (pickedNode instanceof PComposite) {
				Object vNode = pickedNode.getAttribute("position");
				if (vNode instanceof NodeIdentifier) {
					mConnection.showProperty(((NodeIdentifier) vNode).getIdentifier());
				} else if (vNode instanceof NodeMetadata) {
					mConnection.showProperty(((NodeMetadata) vNode).getMetadata());
					if (PolicyRevMetadataSearch.POLICY_FEATURE_EL_NAME.equals(((NodeMetadata) vNode).getMetadata()
							.getTypeName())) {
						PolicyRevMetadataSearch policySearch = new PolicyRevMetadataSearch(mPanel);
						try {
							policySearch.setPickedNode(((NodeMetadata) vNode).getMetadata());
						} catch (UnmarshalException ee) {
							LOGGER.error(ee.toString());
						}
						mPanel.setSearchAndFilterStrategy(policySearch);
						mPanel.setHideSearchMismatches(true);
					}
				}
			} else {
				mPanel.setSearchAndFilterStrategy(new SimpleSearchAndNoFilter(mPanel));
				mPanel.setHideSearchMismatches(false);
			}
		}
	}

	@Override
	public void mouseClicked(PInputEvent e) {
		LOGGER.trace("Method mouseClicked (" + e + ") called.");
		super.mouseClicked(e);

		if (e.getButton() == MOUSE_LEFT_BUTTON) {
			PNode pickedNode = e.getPickedNode();
			if (pickedNode instanceof PComposite) {
				Position vNode = (Position) pickedNode.getAttribute("position");
				if (vNode instanceof NodeIdentifier) {
					mConnection.pickAndShowProperties(((NodeIdentifier) vNode).getIdentifier());
				} else if (vNode instanceof NodeMetadata) {
					mConnection.pickAndShowProperties(((NodeMetadata) vNode).getMetadata());
				}
			} else {
				mConnection.clearProperties();
			}
		}
	}
}
