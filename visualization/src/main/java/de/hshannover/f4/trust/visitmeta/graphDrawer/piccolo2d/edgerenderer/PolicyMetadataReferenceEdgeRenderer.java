package de.hshannover.f4.trust.visitmeta.graphDrawer.piccolo2d.edgerenderer;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import org.piccolo2d.nodes.PPath;

import de.hshannover.f4.trust.visitmeta.graphDrawer.GraphicWrapper;
import de.hshannover.f4.trust.visitmeta.graphDrawer.Piccolo2DGraphicWrapper;
import de.hshannover.f4.trust.visitmeta.graphDrawer.Piccolo2DGraphicWrapperFactory;
import de.hshannover.f4.trust.visitmeta.interfaces.Identifier;
import de.hshannover.f4.trust.visitmeta.interfaces.Metadata;
import de.hshannover.f4.trust.visitmeta.interfaces.Propable;

public class PolicyMetadataReferenceEdgeRenderer extends StraightLinePiccolo2dEdgeRenderer {

	private static final List<String> VALIDE_METADATA_TYP_NAMES = Arrays.asList(new String[] { "policy-feature",
			"policy-action", "feature" });

	@Override
	public void drawEdge(PPath pEdge, Point2D vStart, Point2D vEnd, Metadata metadata, Identifier identifier) {
		super.drawEdge(pEdge, vStart, vEnd, metadata, identifier);

		// only for edges between two metadata
		if (identifier != null) {
			return;
		}

		// only for policy-feature and policy-action metadata
		if ("policy-feature".equals(metadata.getTypeName())) {
			drawPolicyFeatureMetadataReferenceDashedLine(pEdge, vStart, vEnd);
			
		} else if ("policy-action".equals(metadata.getTypeName())) {
			drawPolicyActionMetadataReferenceDashedLine(pEdge, vStart, vEnd);
			
		} else {
			return;
		}
	}

	private void drawPolicyFeatureMetadataReferenceDashedLine(PPath pEdge, Point2D vStart, Point2D vEnd) {
		Piccolo2DGraphicWrapper edge = Piccolo2DGraphicWrapperFactory.create(pEdge, null);

		if (!checkEdgeNodes(edge)) {
			return;
		}

		StraightDashedLinePiccolo2dEdgeRenderer.drawStraightDashedLine(pEdge, vStart, vEnd);
	}

	private void drawPolicyActionMetadataReferenceDashedLine(PPath pEdge, Point2D vStart, Point2D vEnd) {
		Piccolo2DGraphicWrapper edge = Piccolo2DGraphicWrapperFactory.create(pEdge, null);

		if (!checkEdgeNodes(edge)) {
			return;
		}

		StraightDashedLinePiccolo2dEdgeRenderer.drawStraightDashedLine(pEdge, vStart, vEnd);
	}

	private boolean checkEdgeNodes(GraphicWrapper edge) {
		List<GraphicWrapper> nodes = edge.getNodes();

		if (nodes == null || nodes.size() != 2) {
			return false;
		}

		for (GraphicWrapper node : nodes) {
			Object data = node.getData();
			if (data instanceof Propable) {
				Propable propable = (Propable) data;
				String typName = propable.getTypeName();
				// only policy-feature, policy-action or feature metadata are valid
				if (!VALIDE_METADATA_TYP_NAMES.contains(typName)) {
					return false;
				}
			}
		}

		return true;
	}
}
