package de.hshannover.f4.trust.visitmeta.graphDrawer;

import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;

import de.hshannover.f4.trust.visitmeta.interfaces.Propable;

/**
 * Factory class that creates {@link Piccolo2DGraphicWrapper} based on a given selected node {@link PPath} typName.
 *
 * @author Marcel Reichenbach
 *
 */
public class Piccolo2DGraphicWrapperFactory {

	/**
	 * Creates a new {@link Piccolo2DGraphicWrapper}.
	 *
	 * @param selectedNode the {@link PPath} of the selected node
	 * @param selectedNodeText the {@link PText} of the selected node
	 * @return a new {@link Piccolo2DGraphicWrapper}
	 */
	public static Piccolo2DGraphicWrapper create(PPath selectedNode, PText selectedNodeText) {
		String typName = getTypeName(selectedNode);

		if (typName == null) {
			return new Piccolo2DGraphicWrapper(selectedNode, selectedNodeText);
		}

		switch (typName) {
			case "policy-action":
				return new Piccolo2DPolicyActionGraphicWrapper(selectedNode, selectedNodeText);
			case "identity":
				return new Piccolo2DIdentityGraphicWrapper(selectedNode, selectedNodeText);

			default:
				return new Piccolo2DGraphicWrapper(selectedNode, selectedNodeText);
		}
	}

	private static String getTypeName(PPath node) {
		if (node != null) {
			Object data = node.getParent().getAttribute("data");
			if (data instanceof Propable) {
				Propable propable = (Propable) data;
				String typName = propable.getTypeName();
				return typName;
			}
		}
		return null;
	}
}
