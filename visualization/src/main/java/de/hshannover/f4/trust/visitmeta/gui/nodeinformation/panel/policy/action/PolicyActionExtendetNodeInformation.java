package de.hshannover.f4.trust.visitmeta.gui.nodeinformation.panel.policy.action;

import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.hshannover.f4.trust.visitmeta.graphDrawer.GraphicWrapper;
import de.hshannover.f4.trust.visitmeta.graphDrawer.PolicyActionGraphicWrapper;
import de.hshannover.f4.trust.visitmeta.gui.dialog.LayoutHelper;
import de.hshannover.f4.trust.visitmeta.gui.nodeinformation.panel.ExtendetNodeInformationPanel;

public class PolicyActionExtendetNodeInformation extends ExtendetNodeInformationPanel {

	private static final long serialVersionUID = -1783049896574768484L;

	private PolicyActionGraphicWrapper mSelectedNode;

	private JPanel mRuleStatus;

	private JPanel mDeviceOverview;

	private JPanel mActionStatus;

	private JPanel mRuleResults;

	private JPanel mjpWildcard;

	private JPanel mjpWildcard2;

	private JLabel mjlTimeStamp;

	/**
	 * 
	 * @param selectedNode
	 * @throws IllegalArgumentException if is a wrong GraphicWrapper for the selected-node. Must be an
	 *             Piccolo2DPolicyActionGraphicWrapper.
	 */
	public PolicyActionExtendetNodeInformation(GraphicWrapper selectedNode) throws IllegalArgumentException {
		if (!(selectedNode instanceof PolicyActionGraphicWrapper)) {
			throw new IllegalArgumentException("Wrong GraphicWrapper for the selected-node");
		}

		mSelectedNode = (PolicyActionGraphicWrapper) selectedNode;

		initPanel();
		initRuleStatus();

	}

	private void initPanel() {
		super.setLayout(new GridBagLayout());
		super.setBorder(BorderFactory.createTitledBorder("policy-action Metadata"));
	}

	private void initRuleStatus() {
		mRuleStatus = new RuleStatus(mSelectedNode);
		mDeviceOverview = new DeviceOverview(mSelectedNode);
		mActionStatus = new ActionStatus(mSelectedNode);
		mRuleResults = new RuleResults(mSelectedNode);
		mjpWildcard = new JPanel();
		mjpWildcard2 = new JPanel();

		mjlTimeStamp = new JLabel(mSelectedNode.getTimeStampDefaultFormat(), SwingConstants.CENTER);
		mjlTimeStamp.setEnabled(false);
		setBOLD(mjlTimeStamp);

		LayoutHelper.addComponent(0, 0, 1, 1, 1.0, 0.0, this, mjpWildcard, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(1, 0, 1, 1, 0.0, 0.0, this, mRuleStatus, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(2, 0, 1, 1, 0.0, 0.0, this, mDeviceOverview, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(3, 0, 1, 1, 0.0, 0.0, this, mActionStatus, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(4, 0, 1, 1, 0.0, 0.0, this, mRuleResults, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(5, 0, 1, 1, 1.0, 0.0, this, mjpWildcard2, LayoutHelper.mLblInsets);

		LayoutHelper.addComponent(1, 1, 4, 1, 1.0, 0.0, this, mjlTimeStamp, LayoutHelper.mLblInsets);
	}

	private void setBOLD(JLabel label) {
		label.setFont(new Font(label.getFont().getFontName(), Font.BOLD, label.getFont().getSize()));
	}

}
