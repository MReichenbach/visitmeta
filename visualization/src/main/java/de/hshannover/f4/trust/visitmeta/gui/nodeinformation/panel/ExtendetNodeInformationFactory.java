package de.hshannover.f4.trust.visitmeta.gui.nodeinformation.panel;

import de.hshannover.f4.trust.visitmeta.graphDrawer.GraphicWrapper;
import de.hshannover.f4.trust.visitmeta.gui.nodeinformation.panel.policy.action.PolicyActionExtendetNodeInformation;

/**
 * Factory class that creates {@link ExtendetNodeInformationPanel} based on a given selected node {@link GraphicWrapper}
 * typName.
 *
 * @author Marcel Reichenbach
 *
 */
public class ExtendetNodeInformationFactory {

	/**
	 * Creates a new {@link ExtendetNodeInformationPanel}.
	 *
	 * @param selectedNode the {@link GraphicWrapper} of the selected node
	 * @return a new {@link ExtendetNodeInformationPanel}
	 * @throws IllegalArgumentException if no ExtendetNodeInformationPanel for given {@link GraphicWrapper} found
	 */
	public static ExtendetNodeInformationPanel create(GraphicWrapper selectedNode) throws IllegalArgumentException {
		String typName = selectedNode.getNodeTypeName();

		if (typName == null) {
			return null;
		}

		switch (typName) {
			case "policy-action":
				return new PolicyActionExtendetNodeInformation(selectedNode);

			default:
				throw new IllegalArgumentException("No extendet node information for given type '" + typName
						+ "' found");
		}
	}

}
