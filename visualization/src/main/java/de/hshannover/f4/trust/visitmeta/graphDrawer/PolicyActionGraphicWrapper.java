package de.hshannover.f4.trust.visitmeta.graphDrawer;

import java.util.List;

import org.w3c.dom.Element;

import de.hshannover.f4.trust.visitmeta.graphDrawer.Piccolo2DPolicyActionGraphicWrapper.RuleStatusEnum;
import de.hshannover.f4.trust.visitmeta.graphDrawer.util.ConditionElement;

public interface PolicyActionGraphicWrapper extends GraphicWrapper {

	/**
	 * Returns all other policy-action metadata ({@link PolicyActionGraphicWrapper}) contains of the same action
	 * identifier of this policy-action.
	 * 
	 * @return all other policy-action metadata List<{@link PolicyActionGraphicWrapper}>
	 */
	public List<PolicyActionGraphicWrapper> getOtherPolicyActions();

	public List<PolicyActionGraphicWrapper> getPolicyActionsSameRule();

	public List<PolicyActionGraphicWrapper> getPolicyActionsSameRuleAndDevice();

	public String getRuleId();

	public boolean getRuleResult();

	public String getDevice();

	public List<Element> getFeatureElements();

	public List<ConditionElement> getConditionElementResults();

	public RuleStatusEnum getRuleStatus();

	public int getRuleTrueCount();

	public int getRuleFalseCount();

	public String getRuleLastFiredDevice();

	public RuleStatusEnum getRuleStatusForDevice();

	public int getRuleTrueCountForDevice();

	public int getRuleFalseCountForDevice();

	public List<String> getDevicesSameRule();

	public List<String> getDevicesSameAction();

	public int getActionTrueCount();

	public int getActionFalseCount();

	public String getActionLastExecutedDevice();

	public int getActionTrueCountForDevice();

	public int getActionFalseCountForDevice();

	public List<PolicyActionGraphicWrapper> getOtherPolicyActionsSameRule();

	public List<PolicyActionGraphicWrapper> getOtherPolicyActionsSameDevice();

	public List<PolicyActionGraphicWrapper> getOtherPolicyActionsSameRuleAndDevice();

	public boolean isCurrentRuleResultForDevice();

	public PolicyActionGraphicWrapper getCurrentPolicyActionSameRule(String device);

	public PolicyActionGraphicWrapper getPreviousPolicyActionSameRule();

	public PolicyActionGraphicWrapper getPreviousPolicyActionSameAction();

	public boolean isCurrentRuleResult();

}
