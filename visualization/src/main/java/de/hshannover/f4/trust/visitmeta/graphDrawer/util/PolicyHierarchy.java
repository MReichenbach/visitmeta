package de.hshannover.f4.trust.visitmeta.graphDrawer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolicyHierarchy {

	private static Map<String, PolicyHierarchy> policyHierarchy = new HashMap<String, PolicyHierarchy>();

	static {

		PolicyHierarchy policyAction = new PolicyHierarchy("policy-action");
		policyAction.childs = Collections.emptyList();

		PolicyHierarchy action = new PolicyHierarchy("action");
		action.childs.add(policyAction);

		PolicyHierarchy policyFeature = new PolicyHierarchy("policy-feature");
		policyFeature.childs = Collections.emptyList();

		PolicyHierarchy hint = new PolicyHierarchy("hint");
		hint.childs.add(policyFeature);

		PolicyHierarchy anomaly = new PolicyHierarchy("anomaly");
		anomaly.childs.add(hint);

		PolicyHierarchy signature = new PolicyHierarchy("signature");
		signature.childs.add(policyFeature);

		PolicyHierarchy condition = new PolicyHierarchy("condition");
		condition.childs.add(signature);
		condition.childs.add(anomaly);

		PolicyHierarchy rule = new PolicyHierarchy("rule");
		rule.childs.add(condition);
		rule.childs.add(action);

		PolicyHierarchy policy = new PolicyHierarchy("policy");
		policy.childs.add(rule);
		
		policyHierarchy.put(policy.type, policy);
		policyHierarchy.put(rule.type, rule);
		policyHierarchy.put(condition.type, condition);
		policyHierarchy.put(signature.type, signature);
		policyHierarchy.put(anomaly.type, anomaly);
		policyHierarchy.put(hint.type, hint);
		policyHierarchy.put(policyFeature.type, policyFeature);
		policyHierarchy.put(action.type, action);
		policyHierarchy.put(policyAction.type, policyAction);
	}

	private PolicyHierarchy(String type) {
		childs = new ArrayList<PolicyHierarchy>();
		this.type = type;
	}

	private String type;

	private List<PolicyHierarchy> childs;

	public static boolean isChild(String parent, String childToCheck) {
		return isChild(policyHierarchy.get(parent), childToCheck);
	}

	private static boolean isChild(PolicyHierarchy parent, String childToCheck) {
		if (parent == null || childToCheck == null) {
			return false;
		}

		for (PolicyHierarchy child : parent.childs) {
			if (child.type.equals(childToCheck)) {
				return true;
			}
		}

		for (PolicyHierarchy child : parent.childs) {
			return isChild(child, childToCheck);
		}

		return false;
	}

	public static boolean isParent(String child, String parentToCheck) {
		return isChild(parentToCheck, child);
	}

}
