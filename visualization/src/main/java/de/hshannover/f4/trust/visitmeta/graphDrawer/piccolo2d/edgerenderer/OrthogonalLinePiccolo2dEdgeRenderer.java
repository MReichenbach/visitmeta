package de.hshannover.f4.trust.visitmeta.graphDrawer.piccolo2d.edgerenderer;

import java.awt.geom.Point2D;

import de.hshannover.f4.trust.visitmeta.interfaces.Identifier;
import de.hshannover.f4.trust.visitmeta.interfaces.Metadata;
import edu.umd.cs.piccolo.nodes.PPath;

public class OrthogonalLinePiccolo2dEdgeRenderer implements Piccolo2dEdgeRenderer {

	@Override
	public void drawEdge(PPath pEdge, Point2D vStart, Point2D vEnd, Metadata metadata,
			Identifier identifier) {
		float xStart = (float) vStart.getX();
		float yStart = (float) vStart.getY();
		float xEnd = (float) vEnd.getX();
		float yEnd = (float) vEnd.getY();
		float xMid = Math.abs(xEnd
				- xStart)
				/ 2.0f;
		float yMid = Math.abs(yEnd
				- yStart)
				/ 2.0f;

		pEdge.moveTo(xStart, yStart);
		pEdge.lineTo(xStart, yMid);
		pEdge.lineTo(xEnd, yMid);
		pEdge.lineTo(xEnd, yEnd);
	}

}
