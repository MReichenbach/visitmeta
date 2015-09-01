package de.hshannover.f4.trust.visitmeta.graphDrawer.nodepainter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.piccolo2d.nodes.PPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.hshannover.f4.trust.visitmeta.datawrapper.NodeMetadata;
import de.hshannover.f4.trust.visitmeta.datawrapper.NodeType;
import de.hshannover.f4.trust.visitmeta.graphDrawer.GraphPanel;
import de.hshannover.f4.trust.visitmeta.graphDrawer.GraphicWrapper;
import de.hshannover.f4.trust.visitmeta.interfaces.Identifier;
import de.hshannover.f4.trust.visitmeta.interfaces.Metadata;
import de.hshannover.f4.trust.visitmeta.interfaces.Propable;
import de.hshannover.f4.trust.visitmeta.util.DocumentUtils;

public class PolicyActionMetadataReference extends MouseOverNodePainter {

	public static final String POLICY_ACTION_EL_NAME = "policy-action";

	public static final String ESUKOM_FEATURE_EL_NAME = "feature";

	private GraphicWrapper mMouseOverNode;

	private Map<Element, List<Element>> mRevFeatureMetadatas;

	private boolean mValidMouseOver;

	public PolicyActionMetadataReference(GraphPanel panel) {
		super(panel);
	}

	private void resetOldMouseOverNode() {
		// only reset valid old MouseOverNodes
		if (mMouseOverNode != null && mValidMouseOver) {
			for (GraphicWrapper edgeNode : mMouseOverNode.getEdgesNodes()) {
				if (edgeNode.getNodeType() == NodeType.METADATA) {
					List<PPath> bothNodeEdges = mMouseOverNode.getEdgesBetween(edgeNode);

					// delete all edges
					for (PPath edge : bothNodeEdges) {
						super.mGraphPanel.deleteEdge(edgeNode.getPosition(), edge);
					}
				}
			}
		}
	}

	private void setFeatureMetadata(Propable newMouseOverNode) {
		Map<Element, List<Element>> featureElements = new HashMap<Element, List<Element>>();

		Document mouseOverNodeDocument = DocumentUtils.parseEscapedXmlString(newMouseOverNode.getRawData());

		Element nextElement = (Element) mouseOverNodeDocument.getDocumentElement().getFirstChild().getNextSibling()
				.getNextSibling();

		do {

			Element identifier = extractIdentifier(nextElement);
			Element esukomFeatureMetadata = extractMetadata(nextElement);

			if (identifier != null && esukomFeatureMetadata != null) {
				if (!featureElements.containsKey(identifier)) {
					featureElements.put(identifier, new ArrayList<Element>());
				}
				featureElements.get(identifier).add(esukomFeatureMetadata);
			}

			nextElement = (Element) nextElement.getNextSibling();

		} while (nextElement != null);

		mRevFeatureMetadatas = featureElements;
	}

	private Element extractMetadata(Element node) {
		Element esukomFeatureMetadata = (Element) node.getFirstChild();
		return esukomFeatureMetadata;
	}

	private Element extractIdentifier(Element node) {
		Element esukomFeatureMetadata = (Element) node.getFirstChild();
		Element identifier = (Element) esukomFeatureMetadata.getNextSibling();
		return identifier;
	}

	private boolean isNewMouseOverNode(GraphicWrapper mouseOverNode) {
		Object mouseOverNodeData = mouseOverNode.getData();

		if (mouseOverNodeData instanceof Propable) {
			if (mMouseOverNode == null) {
				return true;
			} else if (mMouseOverNode.getData() != mouseOverNodeData) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private void setNewMouseOverNode(GraphicWrapper newMouseOverNode) {
		mMouseOverNode = newMouseOverNode;
	}

	@Override
	public void paintMetadataNode(Metadata metadata, GraphicWrapper graphic) {
		GraphicWrapper mouseOverNode = mGraphPanel.getMouseOverNode();

		if (mouseOverNode == null) {
			resetOldMouseOverNode();
			setNewMouseOverNode(mouseOverNode);
			return;
		}

		// only if mouse over node a POLICY ACTION
		if (isNewMouseOverNode(mouseOverNode)) {
			resetOldMouseOverNode();
			setNewMouseOverNode(mouseOverNode);
			setIsValideMouseOver((Propable) mouseOverNode.getData());
		}

		if (!mValidMouseOver) {
			return;
		}

		// only for ESUKOM FEATURE metadata
		if (!metadata.getTypeName().equals(ESUKOM_FEATURE_EL_NAME)) {
			return;
		}

		Document paintMetadataDocument = DocumentUtils.parseEscapedXmlString(metadata.getRawData());
		Element revMetadataElement = paintMetadataDocument.getDocumentElement();

		if (containsMetadata(revMetadataElement)) {
			List<GraphicWrapper> edges = graphic.getEdgesNodes();
			// ESUKOM_FEATURE
			for (GraphicWrapper edge : edges) {
				Object data = edge.getData();
				if (data instanceof Identifier) {
					Identifier identifier = (Identifier) data;
					Document revIdentifierDocumen = DocumentUtils.parseEscapedXmlString(identifier.getRawData());
					Element revIdentifierElement = revIdentifierDocumen.getDocumentElement();

					if (containsIdentifier(revIdentifierElement)) {
						// set mouseOverColor
						graphic.setPaint(mColorMouseOverNode);

						// add edge
						super.mGraphPanel.addEdge((NodeMetadata) graphic.getPosition(), graphic.getPosition(),
								mouseOverNode.getPosition());
					}
				}
			}
		}
	}

	private void setIsValideMouseOver(Propable mouseOverNode) {
		if (mouseOverNode instanceof Metadata && mouseOverNode.getTypeName().equals(POLICY_ACTION_EL_NAME)) {
			setFeatureMetadata(mouseOverNode);
			mValidMouseOver = true;
		} else {
			mValidMouseOver = false;
		}
	}

	private boolean containsMetadata(Element revMetadataElement) {
		for (List<Element> list : mRevFeatureMetadatas.values()) {
			for (Element e : list) {
				if (e.isEqualNode(revMetadataElement)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean containsIdentifier(Element revIdentifierElement) {
		for (Element e : mRevFeatureMetadatas.keySet()) {
			if (e.isEqualNode(revIdentifierElement)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void paintIdentifierNode(Identifier identifier, GraphicWrapper graphic) {
		// nothing ToDo only for metadata
	}

}
