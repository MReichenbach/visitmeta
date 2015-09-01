package de.hshannover.f4.trust.visitmeta.gui.nodeinformation.panel.policy.action;

import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.hshannover.f4.trust.visitmeta.graphDrawer.PolicyActionGraphicWrapper;
import de.hshannover.f4.trust.visitmeta.gui.dialog.LayoutHelper;

public class ActionStatus extends JPanel {

	private static final long serialVersionUID = -5535418108978820158L;

	public static final String LABEL_ACTION_COUNT = "Action-Count:";

	public static final String LABEL_LAST_EXECUTED_DEVICE = "Last executed Device:";

	private PolicyActionGraphicWrapper mSelectedNode;

	private JLabel mjlActionCount;

	private JLabel mjlActionCountValue;

	private JLabel mjlLastExecutedDevice;
	
	private JLabel mjlLastExecutedDeviceValue;

	private JPanel mjlWildcard;

	private JPanel mjlWildcard2;

	private DevicePanelAction mDevicePanel;


	public ActionStatus(PolicyActionGraphicWrapper selectedNode) {
		mSelectedNode = selectedNode;

		initPanel();
		initJLabels();
		initDevicePanel();
	}

	private void initPanel() {
		super.setLayout(new GridBagLayout());
		super.setBorder(BorderFactory.createTitledBorder("Action-Status"));
	}

	private void initJLabels() {

		mjlActionCount = new JLabel(LABEL_ACTION_COUNT);
		mjlLastExecutedDevice = new JLabel(LABEL_LAST_EXECUTED_DEVICE);

		mjlWildcard = new JPanel();
		mjlWildcard2 = new JPanel();

		mjlActionCountValue = new JLabel(Integer.toString(mSelectedNode.getActionTrueCount()));
		setBOLD(mjlActionCountValue);
		mjlLastExecutedDeviceValue = new JLabel(mSelectedNode.getActionLastExecutedDevice());
		setBOLD(mjlLastExecutedDeviceValue);

		LayoutHelper.addComponent(0, 0, 1, 1, 0.0, 0.0, this, mjlActionCount, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(1, 0, 1, 1, 1.0, 0.0, this, mjlActionCountValue, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(0, 1, 1, 1, 0.0, 0.0, this, mjlLastExecutedDevice, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(1, 1, 1, 1, 1.0, 0.0, this, mjlLastExecutedDeviceValue, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(1, 2, 1, 1, 0.0, 1.0, this, mjlWildcard, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(1, 4, 1, 1, 0.0, 1.0, this, mjlWildcard2, LayoutHelper.mLblInsets);
	}

	private void initDevicePanel() {
		mDevicePanel = new DevicePanelAction(mSelectedNode);

		LayoutHelper.addComponent(0, 3, 2, 1, 1.0, 0.0, this, mDevicePanel, LayoutHelper.mLblInsets);
	}

	private void setBOLD(JLabel label) {
		label.setFont(new Font(label.getFont().getFontName(), Font.BOLD, label.getFont().getSize()));
	}

}
