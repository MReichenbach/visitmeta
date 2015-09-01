package de.hshannover.f4.trust.visitmeta.graphDrawer.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.piccolo2d.nodes.PPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.hshannover.f4.trust.visitmeta.graphDrawer.GraphicWrapper;
import de.hshannover.f4.trust.visitmeta.interfaces.Propable;
import de.hshannover.f4.trust.visitmeta.util.DocumentUtils;

public class PolicyNode {

	public static Set<GraphicWrapper> getParents(GraphicWrapper selectedNode) {
		Set<GraphicWrapper> parents = new HashSet<GraphicWrapper>();
		Propable selectedPropable = (Propable) selectedNode.getData();
		List<GraphicWrapper> edgesNodes = selectedNode.getEdgesNodes();

		String identStartingType;

		switch (selectedNode.getNodeType()) {
		// If selected node = IDENTIFIER
			case IDENTIFIER:
				identStartingType = extractExtendetIdentifierTypeName(selectedPropable);
				for (GraphicWrapper edgeNode : edgesNodes) {
					// check for edges between two policy identifier
					List<GraphicWrapper> tmpEdgesNodes = edgeNode.getEdgesNodes();
					for (GraphicWrapper tmpEdgeNode : tmpEdgesNodes) {
						// now check the identifier edges
						Propable propable = (Propable) tmpEdgeNode.getData();
						String typeName = extractExtendetIdentifierTypeName(propable);
						if (PolicyHierarchy.isParent(identStartingType, typeName)) {
							parents.add(tmpEdgeNode);
							parents.add(edgeNode);
						}
					}
				}
				break;
			case METADATA:
				// If selected node = METADATA
				for (GraphicWrapper identifierNode : edgesNodes) {
					Propable identifier = (Propable) identifierNode.getData();
					identStartingType = extractExtendetIdentifierTypeName(identifier);

					// if selected policy-action or policy-feature metadata
					String metadataTypeName = selectedPropable.getTypeName();
					if (PolicyHierarchy.isParent(metadataTypeName, identStartingType)) {
						parents.add(identifierNode);
					}

					// for metadata between two policy identifier
					// check who is the child
					for (GraphicWrapper otherIdentifierNode : edgesNodes) {
						if (identifierNode != otherIdentifierNode) {
							Propable otherIdentifier = (Propable) otherIdentifierNode.getData();
							String typeName = extractExtendetIdentifierTypeName(otherIdentifier);
							if (PolicyHierarchy.isParent(identStartingType, typeName)) {
								parents.add(otherIdentifierNode);
							}
						}
					}
				}
				break;
			default:
				break;
		}

		return parents;
	}

	public static Set<GraphicWrapper> getAllChilds(GraphicWrapper selectedNode) {
		Set<GraphicWrapper> allNodeChilds = new HashSet<GraphicWrapper>();
		
		Set<GraphicWrapper> selectedNodeChilds = getChilds(selectedNode);
		allNodeChilds.addAll(selectedNodeChilds);

		do{

			Set<GraphicWrapper> tmpNodeChilds = new HashSet<GraphicWrapper>();
			
			for (GraphicWrapper nodeChild : selectedNodeChilds) {
				tmpNodeChilds.addAll(getChilds(nodeChild));
			}

			if (!tmpNodeChilds.isEmpty()) {
				allNodeChilds.addAll(tmpNodeChilds);
			}

			selectedNodeChilds = tmpNodeChilds;

		} while (!selectedNodeChilds.isEmpty());

		return allNodeChilds;
	}

	public static Set<GraphicWrapper> getAllParents(GraphicWrapper selectedNode) {
		Set<GraphicWrapper> allNodeParents = new HashSet<GraphicWrapper>();

		Set<GraphicWrapper> selectedNodeParents = getParents(selectedNode);
		allNodeParents.addAll(selectedNodeParents);

		do {

			Set<GraphicWrapper> tmpNodeParents = new HashSet<GraphicWrapper>();

			for (GraphicWrapper nodeParent : selectedNodeParents) {
				tmpNodeParents.addAll(getParents(nodeParent));
			}

			if (!tmpNodeParents.isEmpty()) {
				allNodeParents.addAll(tmpNodeParents);
			}

			selectedNodeParents = tmpNodeParents;

		} while (!selectedNodeParents.isEmpty());
		
		return allNodeParents;
	}

	public static String extractExtendetIdentifierTypeName(Propable propable) {
		String identityName = propable.valueFor("/identity[@name]");

		if (identityName != null) {
			Document document = DocumentUtils.parseEscapedXmlString(identityName);

			if (document != null) {
				Element extendetInformation = document.getDocumentElement();
				String typeName = extendetInformation.getNodeName();
				return typeName;
			}
		}

		return null;
	}

	public static Set<GraphicWrapper> getChilds(GraphicWrapper selectedNode) {
		Set<GraphicWrapper> childs = new HashSet<GraphicWrapper>();
		Propable selectedPropable = (Propable) selectedNode.getData();
		List<GraphicWrapper> edgesNodes = selectedNode.getEdgesNodes();

		switch (selectedNode.getNodeType()) {
			case IDENTIFIER:
				// If selected node = IDENTIFIER
				String identStartingType = extractExtendetIdentifierTypeName(selectedPropable);
				for (GraphicWrapper edgeNode : edgesNodes) {

					// check for policy-action or policy-feature metadata
					Propable metadata = (Propable) edgeNode.getData();
					String metadataTypeName = metadata.getTypeName();
					if (PolicyHierarchy.isChild(identStartingType, metadataTypeName)) {
						childs.add(edgeNode);
					}

					// check for edges between two policy identifier
					List<GraphicWrapper> tmpEdgesNodes = edgeNode.getEdgesNodes();
					for (GraphicWrapper tmpEdgeNode : tmpEdgesNodes) {
						// now check the identifier edges
						Propable propable = (Propable) tmpEdgeNode.getData();
						String typeName = extractExtendetIdentifierTypeName(propable);
						if (PolicyHierarchy.isChild(identStartingType, typeName)) {
							childs.add(tmpEdgeNode);
							childs.add(edgeNode);
						}
					}
				}
				break;
			case METADATA:
				// If selected node = METADATA
				// for metadata between two policy identifier
				for (GraphicWrapper identifierNode : edgesNodes) {
					Propable identifier = (Propable) identifierNode.getData();
					String metaStartingType = extractExtendetIdentifierTypeName(identifier);
					// check who is the child
					for (GraphicWrapper otherIdentifierNode : edgesNodes) {
						if (identifierNode != otherIdentifierNode) {
							Propable otherIdentifier = (Propable) otherIdentifierNode.getData();
							String typeName = extractExtendetIdentifierTypeName(otherIdentifier);
							if (PolicyHierarchy.isChild(metaStartingType, typeName)) {
								childs.add(otherIdentifierNode);
							}
						}
					}
				}
				break;
			default:
				break;
		}

		return childs;
	}

	public static Set<PPath> getEdges(Set<GraphicWrapper> policyNodes) {
		Set<PPath> edges = new HashSet<PPath>();

		for (GraphicWrapper policyNode : policyNodes) {
			List<PPath> tmpEdges = policyNode.getEdges();
			edges.addAll(tmpEdges);
		}

		return edges;
	}
}
