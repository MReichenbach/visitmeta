package de.hshannover.f4.trust.visitmeta.gui.nodeinformation.panel.policy.action;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import de.hshannover.f4.trust.visitmeta.graphDrawer.PolicyActionGraphicWrapper;
import de.hshannover.f4.trust.visitmeta.gui.dialog.LayoutHelper;

public class DevicePanelAction extends JPanel {

	private static final long serialVersionUID = 1224028342347312989L;

	public static final String LABEL_STATUS_OLD = "OLD";

	public static final String LABEL_ACTION_COUNT = "Action-Count";

	private PolicyActionGraphicWrapper mSelectedNode;

	private JLabel mjlActionCount;

	private JLabel mjlActionCountValue;

	private JLabel mjlOldStatus;

	private TitledBorder mTtitel;

	public DevicePanelAction(PolicyActionGraphicWrapper selectedNode) {
		mSelectedNode = selectedNode;

		initPanel();
		initJLabels();
		
		if (!mSelectedNode.isCurrentRuleResultForDevice()) {
			mjlOldStatus.setVisible(true);
		}

		super.setMinimumSize(new Dimension(mTtitel.getMinimumSize(this).width, super.getPreferredSize().width));
	}

	private void initPanel() {
		mTtitel = BorderFactory.createTitledBorder("Device: " + mSelectedNode.getDevice());

		super.setLayout(new GridBagLayout());
		super.setBorder(mTtitel);
	}

	private void setBOLD(JLabel label) {
		label.setFont(new Font(label.getFont().getFontName(), Font.BOLD, label.getFont().getSize()));
	}

	private void initJLabels() {

		mjlActionCount = new JLabel(LABEL_ACTION_COUNT, SwingConstants.CENTER);
		mjlActionCount.setEnabled(false);
		mjlOldStatus = new JLabel(LABEL_STATUS_OLD, SwingConstants.CENTER);
		mjlOldStatus.setVisible(false);
		mjlOldStatus.setForeground(Color.RED);
		setBOLD(mjlOldStatus);

		mjlActionCountValue = new JLabel(Integer.toString(mSelectedNode.getActionTrueCountForDevice()),
				SwingConstants.CENTER);
		setBOLD(mjlActionCountValue);

		LayoutHelper.addComponent(0, 0, 1, 1, 1.0, 0.0, this, mjlActionCountValue, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(0, 1, 1, 1, 1.0, 0.0, this, mjlActionCount, LayoutHelper.mLblInsets);
		LayoutHelper.addComponent(0, 2, 1, 1, 0.0, 0.0, this, mjlOldStatus, LayoutHelper.mLblInsets);
	}

}
