package de.hshannover.f4.trust.visitmeta.graphDrawer.util;

import java.util.ArrayList;
import java.util.List;

public class ConditionElement {

	public enum ConditionElementType {
		SIGNATURE,
		ANOMALY,
		HINT;
		
		public static ConditionElementType valueOfString(String type) {
			String typeLowCase = type.toLowerCase();
			if (typeLowCase.contains(SIGNATURE.toString().toLowerCase())) {
				return SIGNATURE;
			} else if (typeLowCase.contains(ANOMALY.toString().toLowerCase())) {
				return ANOMALY;
			} else if (typeLowCase.contains(HINT.toString().toLowerCase())) {
				return HINT;
			}
			return null;
		}
	}

	public ConditionElementType type;

	public String id;

	public boolean result;

	public List<ConditionElement> childs;

	public ConditionElement() {
		childs = new ArrayList<ConditionElement>();
	}

	// TODO
	// public ConditionElement(String id, boolean result) {
	// this.id = id;
	// this.result = result;
	// }
}
