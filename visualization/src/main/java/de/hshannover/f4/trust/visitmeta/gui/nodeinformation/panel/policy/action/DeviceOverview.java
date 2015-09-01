package de.hshannover.f4.trust.visitmeta.gui.nodeinformation.panel.policy.action;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import de.hshannover.f4.trust.visitmeta.graphDrawer.PolicyActionGraphicWrapper;
import de.hshannover.f4.trust.visitmeta.gui.dialog.LayoutHelper;

public class DeviceOverview extends JPanel {


	private static final long serialVersionUID = 1908694561716598784L;

	public static final String LABEL_RULE = "RULE";

	public static final String LABEL_ACTION = "ACTION";

	public static final String LABEL_TRUE_COUNT = "true-Count:";

	public static final String LABEL_FALSE_COUNT = "false-Count:";

	public static final String LABEL_LAST_FIRED_DEVICE = "last fired Device:";

	private PolicyActionGraphicWrapper mSelectedNode;

	private List<DevicePanelRule> mRuleDevicePanels;

	private List<DevicePanelAction> mActionDevicePanels;

	private JScrollPane mjScpRuleDeviceOverview;

	private JScrollPane mjScpActionDeviceOverview;

	private JPanel mjpRuleMain;

	private JPanel mjpActionMain;

	private JPanel mjpRuleScrollPaneMain;

	private JPanel mjpActionScrollPaneMain;

	private JLabel mjlRule;

	private JLabel mjlAction;

	public DeviceOverview(PolicyActionGraphicWrapper selectedNode) {
		mSelectedNode = selectedNode;
		mRuleDevicePanels = new ArrayList<DevicePanelRule>();
		mActionDevicePanels = new ArrayList<DevicePanelAction>();

		initPanel();
		initJPanels();
		initDevicePanels();
		addDevicePanelRules();
		addDevicePanelActions();

		addPanels();

	}

	private void addPanels() {
		mjScpRuleDeviceOverview = new JScrollPane(mjpRuleMain);
		mjScpRuleDeviceOverview.setMinimumSize(new Dimension(mjpRuleMain.getPreferredSize().width + 30, 0));

		mjScpActionDeviceOverview = new JScrollPane(mjpActionMain);
		mjScpActionDeviceOverview.setMinimumSize(new Dimension((mjpActionMain.getPreferredSize().width + 95), 0));

		LayoutHelper.addComponent(0, 1, 1, 1, 1.0, 1.0, this, mjScpRuleDeviceOverview, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(1, 1, 1, 1, 1.0, 1.0, this, mjScpActionDeviceOverview, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(0, 0, 1, 1, 0.0, 0.0, this, mjlRule, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(1, 0, 1, 1, 0.0, 0.0, this, mjlAction, LayoutHelper.mLblInsets);
	}

	private void initPanel() {
		super.setLayout(new GridBagLayout());
		super.setBorder(BorderFactory.createTitledBorder("Device-Overview"));
	}

	private void initJPanels() {

		mjlRule = new JLabel(LABEL_RULE, SwingConstants.CENTER);
		mjlAction = new JLabel(LABEL_ACTION, SwingConstants.CENTER);

		mjpRuleMain = new JPanel();
		mjpRuleMain.setLayout(new GridBagLayout());

		mjpActionMain = new JPanel();
		mjpActionMain.setLayout(new GridBagLayout());

		mjpRuleScrollPaneMain = new JPanel();
		mjpRuleScrollPaneMain.setLayout(new GridBagLayout());

		mjpActionScrollPaneMain = new JPanel();
		mjpActionScrollPaneMain.setLayout(new GridBagLayout());

	}

	private void initDevicePanels() {
		mRuleDevicePanels = new ArrayList<DevicePanelRule>();

		List<String> devices = mSelectedNode.getDevicesSameRule();

		for (String device : devices) {
			PolicyActionGraphicWrapper currentPolicyAction = mSelectedNode.getCurrentPolicyActionSameRule(device);

			DevicePanelRule devicePanelRule = new DevicePanelRule(currentPolicyAction);
			mRuleDevicePanels.add(devicePanelRule);

			DevicePanelAction devicePanelAction = new DevicePanelAction(currentPolicyAction);
			mActionDevicePanels.add(devicePanelAction);
		}
	}

	private void addDevicePanelRules() {
		for (int i = 0; i < mRuleDevicePanels.size(); i++) {
			LayoutHelper.addComponent(0, i, 1, 1, 1.0, 0.0, mjpRuleMain, mRuleDevicePanels.get(i),
				LayoutHelper.mLblInsets);
		}
	}

	private void addDevicePanelActions() {
		for (int i = 0; i < mActionDevicePanels.size(); i++) {
			LayoutHelper.addComponent(0, i, 1, 1, 1.0, 0.0, mjpActionMain, mActionDevicePanels.get(i),
					LayoutHelper.mLblInsets);
		}
	}

}
