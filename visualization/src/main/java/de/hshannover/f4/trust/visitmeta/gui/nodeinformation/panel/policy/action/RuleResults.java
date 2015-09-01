package de.hshannover.f4.trust.visitmeta.gui.nodeinformation.panel.policy.action;

import static de.hshannover.f4.trust.visitmeta.gui.dialog.LayoutHelper.mLblInsets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import de.hshannover.f4.trust.visitmeta.graphDrawer.PolicyActionGraphicWrapper;
import de.hshannover.f4.trust.visitmeta.graphDrawer.util.ConditionElement;
import de.hshannover.f4.trust.visitmeta.graphDrawer.util.ConditionElement.ConditionElementType;
import de.hshannover.f4.trust.visitmeta.gui.dialog.LayoutHelper;

public class RuleResults extends JPanel {


	private static final long serialVersionUID = 1908694561716598784L;

	public static final String LABEL_DEVICE = "Device:";

	private PolicyActionGraphicWrapper mSelectedNode;
	
	private JLabel mjlDevice;

	private JLabel mjlDeviceValue;

	private JScrollPane mjScpMain;

	private JPanel mjpScrollPaneMain;

	private JPanel mjpResultsMain;

	public RuleResults(PolicyActionGraphicWrapper selectedNode) {
		mSelectedNode = selectedNode;

		initPanel();
		initJPanels();
		initRuleResultPanel();

		mjScpMain.setMinimumSize(new Dimension(mjpResultsMain.getPreferredSize().width, 0));
	}

	private void initPanel() {
		super.setLayout(new GridBagLayout());
		super.setBorder(BorderFactory.createTitledBorder("Rule-Results"));
	}

	private void initJPanels() {
		mjlDevice = new JLabel(LABEL_DEVICE);
		mjlDeviceValue = new JLabel(mSelectedNode.getDevice());
		setBOLD(mjlDeviceValue);
		
		mjpResultsMain = new JPanel();
		mjpResultsMain.setLayout(new GridBagLayout());

		mjpScrollPaneMain = new JPanel();
		mjpScrollPaneMain.setLayout(new GridBagLayout());

		mjScpMain = new JScrollPane(mjpScrollPaneMain);

		LayoutHelper.addComponent(0, 0, 1, 1, 0.0, 0.0, mjpScrollPaneMain, mjpResultsMain, mLblInsets);

		LayoutHelper.addComponent(0, 0, 1, 1, 0.0, 0.0, this, mjlDevice, mLblInsets);
		LayoutHelper.addComponent(1, 0, 1, 1, 1.0, 0.0, this, mjlDeviceValue, mLblInsets);
		LayoutHelper.addComponent(0, 1, 2, 1, 0.0, 1.0, this, mjScpMain, mLblInsets);
	}

	private void initRuleResultPanel() {
		List<ConditionElement> results = mSelectedNode.getConditionElementResults();

		JLabel rule = new JLabel("Rule:");
		JLabel ruleId = new JLabel(mSelectedNode.getRuleId());
		JLabel arrowRule = new JLabel("->");
		setBOLD(arrowRule);
		JLabel ruleResult = buildLabelResult(mSelectedNode.getRuleResult());
		
		LayoutHelper.addComponent(0, 0, 1, 1, 0.0, 0.0, mjpResultsMain, rule, mLblInsets);
		LayoutHelper.addComponent(3, 0, 1, 1, 0.0, 0.0, mjpResultsMain, ruleId, mLblInsets);
		LayoutHelper.addComponent(4, 0, 1, 1, 0.0, 0.0, mjpResultsMain, arrowRule, mLblInsets);
		LayoutHelper.addComponent(5, 0, 1, 1, 0.0, 0.0, mjpResultsMain, ruleResult, mLblInsets);
		
		for(int i=1; i<results.size()+1;i++){
			ConditionElement result = results.get(i-1);
			
			if(ConditionElementType.SIGNATURE == result.type){
				JLabel signature = new JLabel("Signature:", SwingConstants.CENTER);
				JLabel signatureId = new JLabel(result.id);
				JLabel arrowSignature = new JLabel("->");
				setBOLD(arrowSignature);
				JLabel signatureResult = buildLabelResult(result.result);
				
				LayoutHelper.addComponent(1, i, 1, 1, 0.0, 0.0, mjpResultsMain, signature, mLblInsets);
				LayoutHelper.addComponent(3, i, 1, 1, 0.0, 0.0, mjpResultsMain, signatureId, mLblInsets);
				LayoutHelper.addComponent(4, i, 1, 1, 0.0, 0.0, mjpResultsMain, arrowSignature, mLblInsets);
				LayoutHelper.addComponent(5, i, 1, 1, 0.0, 0.0, mjpResultsMain, signatureResult, mLblInsets);
			}else if(ConditionElementType.ANOMALY == result.type){
				JLabel anomaly = new JLabel("Anomaly:", SwingConstants.CENTER);
				JLabel anomalyId = new JLabel(result.id);
				JLabel arrowAnomaly = new JLabel("->");
				setBOLD(arrowAnomaly);
				JLabel anomalyResult = buildLabelResult(result.result);
				
				LayoutHelper.addComponent(1, i, 1, 1, 0.0, 0.0, mjpResultsMain, anomaly, mLblInsets);
				LayoutHelper.addComponent(3, i, 1, 1, 0.0, 0.0, mjpResultsMain, anomalyId, mLblInsets);
				LayoutHelper.addComponent(4, i, 1, 1, 0.0, 0.0, mjpResultsMain, arrowAnomaly, mLblInsets);
				LayoutHelper.addComponent(5, i, 1, 1, 0.0, 0.0, mjpResultsMain, anomalyResult, mLblInsets);
				
				for (int j = 1; j < result.childs.size() + 1; j++) {
					ConditionElement resultHint = result.childs.get(j - 1);
				
					JLabel hint = new JLabel("Hint:", SwingConstants.CENTER);
					JLabel hintId = new JLabel(resultHint.id);
					JLabel arrowHint = new JLabel("->");
					setBOLD(arrowHint);
					JLabel hintResult = buildLabelResult(resultHint.result);

					LayoutHelper.addComponent(2, i + j, 1, 1, 0.0, 0.0, mjpResultsMain, hint, mLblInsets);
					LayoutHelper.addComponent(3, i + j, 1, 1, 0.0, 0.0, mjpResultsMain, hintId, mLblInsets);
					LayoutHelper.addComponent(4, i + j, 1, 1, 0.0, 0.0, mjpResultsMain, arrowHint, mLblInsets);
					LayoutHelper.addComponent(5, i + j, 1, 1, 0.0, 0.0, mjpResultsMain, hintResult, mLblInsets);
				}
			}
		}
	}

	private void setBOLD(JLabel label) {
		label.setFont(new Font(label.getFont().getFontName(), Font.BOLD, label.getFont().getSize()));
	}

	private JLabel buildLabelResult(boolean result) {
		JLabel resultLabel = new JLabel(Boolean.toString(result));
		setBOLD(resultLabel);

		if (result) {
			resultLabel.setForeground(new Color(59, 186, 63));
		} else {
			resultLabel.setForeground(Color.RED);
		}

		return resultLabel;
	}
}
