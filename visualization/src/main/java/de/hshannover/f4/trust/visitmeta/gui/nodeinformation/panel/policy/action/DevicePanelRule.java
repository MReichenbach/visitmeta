package de.hshannover.f4.trust.visitmeta.gui.nodeinformation.panel.policy.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.hshannover.f4.trust.visitmeta.graphDrawer.Piccolo2DPolicyActionGraphicWrapper.RuleStatusEnum;
import de.hshannover.f4.trust.visitmeta.graphDrawer.PolicyActionGraphicWrapper;
import de.hshannover.f4.trust.visitmeta.gui.dialog.LayoutHelper;

public class DevicePanelRule extends JPanel {

	private static final long serialVersionUID = 1224028342347312989L;

	public static final String LABEL_TRUE_COUNT = "TRUE-Count";

	public static final String LABEL_FALSE_COUNT = "FALSE-Count";

	public static final String LABEL_STATUS_OLD = "OLD";

	private PolicyActionGraphicWrapper mSelectedNode;

	private JLabel mjlStatus;

	private JLabel mjlTrueCount;

	private JLabel mjlTrueCountValue;

	private JLabel mjlFalseCount;

	private JLabel mjlFalseCountValue;

	private JLabel mjlOldStatus;

	public DevicePanelRule(PolicyActionGraphicWrapper selectedNode) {
		mSelectedNode = selectedNode;

		initPanel();
		initJLabels();
		
		if (!mSelectedNode.isCurrentRuleResultForDevice()) {
			mjlOldStatus.setVisible(true);
		}

		System.out.println("super.getPreferredSize()= " + super.getPreferredSize());
	}

	private void initPanel() {
		super.setLayout(new GridBagLayout());
		super.setBorder(BorderFactory.createTitledBorder("Device: " + mSelectedNode.getDevice()));
	}

	private void setBOLD(JLabel label) {
		label.setFont(new Font(label.getFont().getFontName(), Font.BOLD, label.getFont().getSize()));

	}

	private void initJLabels() {
		setRuleStatus();
		mjlTrueCount = new JLabel(LABEL_TRUE_COUNT, SwingConstants.CENTER);
		mjlTrueCount.setEnabled(false);
		mjlFalseCount = new JLabel(LABEL_FALSE_COUNT, SwingConstants.CENTER);
		mjlFalseCount.setEnabled(false);
		mjlOldStatus = new JLabel(LABEL_STATUS_OLD, SwingConstants.CENTER);
		mjlOldStatus.setVisible(false);
		mjlOldStatus.setForeground(Color.RED);
		setBOLD(mjlOldStatus);

		mjlTrueCountValue = new JLabel(Integer.toString(mSelectedNode.getRuleTrueCountForDevice()),
				SwingConstants.CENTER);
		setBOLD(mjlTrueCountValue);
		mjlFalseCountValue = new JLabel(Integer.toString(mSelectedNode.getRuleFalseCountForDevice()),
				SwingConstants.CENTER);
		setBOLD(mjlFalseCountValue);

		LayoutHelper.addComponent(0, 0, 2, 1, 0.0, 0.0, this, mjlStatus, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(0, 1, 1, 1, 1.0, 0.0, this, mjlTrueCountValue, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(0, 2, 1, 1, 0.0, 0.0, this, mjlTrueCount, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(1, 1, 1, 1, 1.0, 0.0, this, mjlFalseCountValue, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(1, 2, 1, 1, 0.0, 0.0, this, mjlFalseCount, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(0, 3, 2, 1, 0.0, 0.0, this, mjlOldStatus, LayoutHelper.mLblInsets);
	}

	private void setRuleStatus() {
		mjlStatus = new JLabel();
		mjlStatus.setHorizontalAlignment(SwingConstants.CENTER);
		mjlStatus.setFont(new Font(mjlStatus.getFont().getFontName(), Font.BOLD, mjlStatus.getFont().getSize()));

		RuleStatusEnum status = mSelectedNode.getRuleStatusForDevice();

		if (RuleStatusEnum.CHANGED_TRUE == status) {
			mjlStatus.setText("changed");
			mjlStatus.setForeground(new Color(59, 186, 63));
		} else if (RuleStatusEnum.CHANGED_FALSE == status) {
			mjlStatus.setText("changed");
			mjlStatus.setForeground(Color.RED);
		} else if (RuleStatusEnum.SAME_TRUE == status) {
			mjlStatus.setText("same");
			mjlStatus.setForeground(new Color(59, 186, 63));
		} else if (RuleStatusEnum.SAME_FALSE == status) {
			mjlStatus.setText("same");
			mjlStatus.setForeground(Color.RED);
		}
	}

}
