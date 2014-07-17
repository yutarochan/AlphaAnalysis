package com.tigytech.alpha.core.representations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Vector;

import com.tigytech.alpha.core.utilities.StringUtils;

public class Entity extends Observable implements Serializable, Frame {

	private static final long serialVersionUID = -3607118541136780084L;

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

	public static String OWNER = "owner";
	public static String PROPERTY = "property";
	
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
		for (LabelValuePair lp : getPropertyList())
			keys.add(lp.getLabel());
		return keys;
	}
	
	public Vector<LabelValuePair> getPropertyList() {
		if (this.propertyList == null) this.propertyList = new Vector();
		return this.propertyList;
	}
	
	public Vector<LabelValuePair> clonePropertyList() {
		Vector<LabelValuePair> clone = new Vector();
		for (LabelValuePair pair : getPropertyList())
			clone.add(pair);
		return clone;
	}
	
	private LabelValuePair clone(LabelValuePair pair) {
		return new LabelValuePair(pair.getLabel(), pair.getValue());
	}
	
	public void addProperty(String label, Object object) {
		for (LabelValuePair pair : getPropertyList()) {
			if (pair.getLabel().equals(label)) {
				pair.setValue(object);
				return;
			}
		}
		getPropertyList().add(new LabelValuePair(label, object));
	}
	
	public Object getProperty(String label) {
		for (LabelValuePair pair : getPropertyList())
			if (pair.getLabel().equals(label)) return pair.getValue();
		return null;
	}
	
	public void removeProperty(String label) {
		LabelValuePair remove = null;
		for (LabelValuePair pair: getPropertyList()) {
			if (pair.getLabel().equals(label)) {
				remove = pair;
				break;
			}
		}
		
		if (remove != null) getPropertyList().remove(remove);
	}
	
	public boolean hasProperty(String label) {
		if (getProperty(label) != null) return true;
		return false;
	}
	
	public boolean hasProperty(String label, Object value) {
		Object val = getProperty(label);
		if (val instanceof String) return val.equals(value);
		return val == value;
	}
	
	public void addFeature(Object object) {
		for (LabelValuePair pair : getPropertyList())
			if ((pair.getLabel().equals("feature")) && (pair.getValue().equals(object))) return;
		getPropertyList().add(new LabelValuePair("feature", object));
	}
	
	public boolean removeFeature(Object object) {
		return false;
	}
	
	public boolean hasFeature(Object object) {
		return false;
	}
	
	public ArrayList<Object> getFeatures() {
		return null;
	}

	/** TODO: Configure bundle settings */
	public Bundle getBundle() {
		return this.bundle;
	}

	public void setBundle(Bundle b) {
		if (this.bundle == b) return;
		saveState();

		if (this.bundle != null)
			this.bundle.setOwnerThingNull();
		this.bundle = b;
		if (this.bundle != null)
			this.bundle.setOwnerThing(this);
		fireNotification();
	}
	/** End TODO */

	public void addThread(Thread t) {
		this.bundle.addThread(t);
	}

	public void replacePrimedThread(Thread t) {
		if (this.bundle.size() != 0) 
			this.bundle.remove(0);
		this.bundle.add(0, t);
	}

	public Thread getPrimedThread() {
		return getBundle().getPrimedThread();
	}

	public void setPrimedThread(Thread t) {
		replacePrimedThread(t);
	}

	public Thread getThread(String firstElement) {
		return this.bundle.getThread(element);
	}

	public Thread getThreadWith(String element) {
		for (Thread t : getBundle())
			if (t.contains(element)) return t;
		return null;
	}

	public Thread getThreadWith(String first, String last) {
		for (Thread t : getBundle())
			if ((((String)t.firstElement()).equals(first)) 
				&& (((String)t.lastElement()).equals(last)))
				return t;
		return null;
	}

	public void swapPrimedThread() {
		getBundle().swapPrimedThread();
	}

	public void pushPrimedThread() {
		getBundle().pushPrimedThread();
	}

	public void sendPrimedThreadToEnd() {
		getBundle().sendPrimedThreadToEnd();
	}

	public String getType() {
		return this.bundle.getType();
	}

	public String getSuperType() {
		return this.bundle.getSuperType();
	}

	public void addType(String t) {
		Thread thread = getPrimedThread();
		thread.addType(t);
	}

	public void addDeterminer(String t) {
		addType(t, "determiner");
	}

	/** Confirm Functionality */
	public Thread getDeterminer() {
		for (Thread t : this.bundle)
			if (t.contains("determiner")) return t;
		return null;
	}

	/** Confirm Functionality */
	public void addType(String type, String threadType) {
		for (Thread t : this.bundle) {
			if ((thread != null) && (thread.contains(threadType))) {
				thread.addType(type);
				return;
			}
		}
		Thread t = new Thread();
		thread.addType(ThreadType);
		thread.addType(type);
		this.bundle.addThread(thread);
	}

	public void addType(Strng t, String[] ok) {
		if (StringUtils.testType(t, ok)) addType(t);
		else System.err.println("[ENTITY - ERROR] Tried to add wrong type " + t);
	}

	public void addTypes(Vector<?> v) {
		getBundle().pushPrimedThread();
		for (Object o : v) {
			if (o instanceof String) addType((String)o);
			else System.err.println("[ENTITY - ERROR] Tried to add type of non-string.");
		}
	}

	@Override
	public boolean entityP() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean entityP(String paramString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean functionP() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean functionP(String paramString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean relationP() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean relationP(String paramString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sequenceP() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sequenceP(String paramString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isA(String paramString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAllOf(String[] paramArrayOfString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNoneOf(String[] paramArrayOfString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSubject(Entity paramEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Entity getSubject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setObject(Entity paramEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Entity getObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addElement(Entity paramEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector<Entity> getElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getElement(int paramInt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Entity> getAllComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<Entity> getModifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addModifier(Entity paramEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addModifier(int paramInt, Entity paramEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setModifiers(Vector<Entity> paramVector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearModifiers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public char getPrettyPrintType() {
		// TODO Auto-generated method stub
		return 0;
	}
}