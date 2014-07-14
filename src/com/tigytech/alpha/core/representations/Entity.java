package com.tigytech.alpha.core.representations;

import java.io.Serializable;
import java.util.Observable;
import java.util.Vector;

public class Entity extends Observable implements Serializable, Frame {

	public static String pathElem = "pathElement";
	
	public static String[] placeList = {"above", "at", "below", "under", "farFrom", "in", "near", "nextTo", "rightOf", "leftOf", "on", "over", "by", "top", "bottom" }; 
	public static String[] pathList = { "to", "from", "toward", "awayFrom", "down", "up", "via" };
	public static String[] roleList = { "with", "by", "for" };
	public static String[] changeList = { "increase", "decrease", "change", "appear", "disappear", "notIncrease", "notDecrease", "notChange", "notAppear", "notDisappear", "blank" };
	
	public static final char TYPECHAR_ENTITY = 'E';
	public static final char TYPECHAR_FUNCTION = 'F';
	public static final char TYPECHAR_RELATION = 'R';
	public static final char TYPECHAR_SEQUENCE = 'S';
	public static final String MARKER_ENTITY = "entity";
	public static final String MARKER_ACTION = "action";
	public static final String MARKER_DESCRIPTION = "description";
	public static final String MARKER_OWNERS = "owners";
	public static final String MARKER_FEATURE = "feature";
	public static final String MARKER_DETERMINER = "determiner";
	public static final String MARKER_WORD = "word";
	public static final String MARKER_COMPLETE = "complete";

	protected String name;
	protected String nameSuffix;
	protected Bundle bundle;
	private Vector<LabelValuePair> propertyList;
	
	public Entity() {
		appendType();
	}
	
	private void appendType() {
	}

	public void setNameSuffix(String suffix) {
		this.nameSuffix = suffix;
	}
	
	public String getNameSuffix() {
		return this.nameSuffix;
	}

	public String getIdentifier() {
		return this.nameSuffix.substring(1);
	}
	
	public int getID() {
		return new Integer(getIdentifier());
	}
	
	public String getName() {
		if (this.name != null) return this.name;
		return getType() + this.nameSuffix;
	}

	public String getExplicitName() {
		return this.name;
	}
	
	public Vector<String> getKeys() {
		Vector<String> keys = new Vector();
		for (LabelValuePair lp : getPropertiesList())
			keys.add(lp.getLabel());
	}
	
	public Vector<LabelValuePair> getPropertyList() {
		if (this.propertyList == null) this.propertyList = new Vector();
		return this.propertyList;
	}
	
	private String getType() {
		// TODO Auto-generated method stub
		return null;
	}
}
