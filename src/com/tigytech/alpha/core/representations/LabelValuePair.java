package com.tigytech.alpha.core.representations;

public class LabelValuePair {
	
	private String label;
	private Object value;
	
	public LabelValuePair(String label, Object value) {
		this.label = label;
		this.value = value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public String getLabel() {
		return this.label;
	}

	public Object getValue() {
		return this.value;
	}
	
	public LabelValuePair clone(LabelValuePair pair) {
		return new LabelValuePair(pair.label, pair.value);
	}
	
	public String toXML() {
		return "<" + this.label + ", " + this.value.toString() + ">";
	}
	
	public boolean equals(LabelValuePair pair) {
		if (this.label.equals(pair.getLabel())) {
			if ((this.value instanceof String) && (this.value.equals(pair.getValue()))) return true;
			if (this.value == pair.getValue()) return true;
		}
		return false;
	}
}
