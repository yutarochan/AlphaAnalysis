package com.tigytech.core.memory;

import bridge.reps.entities.ClassPair;
import bridge.reps.entities.Entity;
import bridge.reps.entities.Relation;
import bridge.reps.entities.Thread;
import bridge.utils.logging.Logger;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class EntityMemory extends BasicMemory {
	Hashtable classCounts = new Hashtable();
	Hashtable classDivisionCount = new Hashtable();
	Hashtable classPairCounts = new Hashtable();
	double classCountAverage = 0.0D;
	double classCountDeviation = 0.0D;

	public Hashtable getClassCounts() {
		return this.classCounts;
	}

	public Hashtable getClassDivisionCount() {
		return this.classDivisionCount;
	}

	public Hashtable getClassPairCounts() {
		return this.classPairCounts;
	}

	public void setAverage(double d) {
		this.classCountAverage = d;
	}

	public double getAverage() {
		if (this.avgChanged) {
			calculateAverage();
		}
		return this.classCountAverage;
	}

	public double getDeviation() {
		if (this.devChanged) {
			calculateDeviation();
		}
		return this.classCountDeviation;
	}

	public void setDeviation(double d) {
		this.classCountDeviation = d;
	}

	public int getClassCount(String c) {
		Integer count = (Integer) this.classCounts.get(c);
		if (count == null) {
			return 0;
		}
		return count.intValue();
	}

	public void refresh() {
		this.classCounts = new Hashtable();
		this.classDivisionCount = new Hashtable();
		this.classPairCounts = new Hashtable();

		Vector things = getThings();
		for (int i = 0; i < things.size(); i++) {
			Entity thing = (Entity) things.get(i);
			thingModified(thing, null, thing.toString());
		}
		calculateAverage();
		calculateDeviation();
	}

	boolean avgChanged = false;
	boolean devChanged = false;
	public static final String LOGGER_GROUP = "memory";
	public static final String LOGGER_INSTANCE = "Memory";
	public static final String LOGGER = "memory.Memory";

	public void thingModified(Entity t, String oldState, String newState) {
		this.avgChanged = true;
		this.devChanged = true;

		super.thingModified(t, oldState, newState);
		Vector remove = Entity.getClassesFromString(oldState);
		Vector add = Entity.getClassesFromString(newState);

		Vector intersection = new Vector();
		intersection.addAll(remove);
		intersection.retainAll(add);

		remove.removeAll(intersection);
		add.removeAll(intersection);
		incrementClassCounts(add);
		decrementClassCounts(remove);

		remove = Thread.getClassPairsFromString(oldState);
		add = Thread.getClassPairsFromString(newState);

		intersection.clear();
		intersection.addAll(remove);
		intersection.retainAll(add);

		remove.removeAll(intersection);
		add.removeAll(intersection);
		addClassPairs(add);
		removeClassPairs(remove);
	}

	public void addClassPairs(Vector v) {
		Hashtable cpc = getClassPairCounts();
		Hashtable cdc = getClassDivisionCount();
		for (int i = 0; i < v.size(); i++) {
			String cp = (String) v.get(i);
			Integer pairCount = (Integer) cpc.get(cp);
			Integer homoCount = (Integer) cdc.get(ClassPair.getUpper(cp));
			if ((pairCount == null) && (homoCount == null)) {
				cdc.put(ClassPair.getUpper(cp), new Integer(1));
				cpc.put(cp, new Integer(1));
			} else if ((pairCount == null) && (homoCount != null)) {
				cdc.put(ClassPair.getUpper(cp),
						new Integer(homoCount.intValue() + 1));
				cpc.put(cp, new Integer(1));
			} else {
				cpc.put(cp, new Integer(pairCount.intValue() + 1));
			}
		}
	}

	public void removeClassPairs(Vector v) {
		Hashtable cpc = getClassPairCounts();
		Hashtable cdc = getClassDivisionCount();
		for (int i = 0; i < v.size(); i++) {
			String cp = (String) v.get(i);
			Integer pairCount = (Integer) cpc.get(cp);
			Integer homoCount = (Integer) cdc.get(ClassPair.getUpper(cp));
			if ((pairCount.intValue() == 1) && (homoCount.intValue() == 1)) {
				cdc.remove(ClassPair.getUpper(cp));
				cpc.remove(cp);
			} else if ((pairCount.intValue() == 1) && (homoCount != null)) {
				cdc.put(ClassPair.getUpper(cp),
						new Integer(homoCount.intValue() - 1));
				cpc.remove(cp);
			} else if ((pairCount != null) && (homoCount != null)) {
				cpc.put(cp, new Integer(pairCount.intValue() - 1));
			}
		}
	}

	public void incrementClassCounts(Vector v) {
		Hashtable cc = getClassCounts();
		for (int i = 0; i < v.size(); i++) {
			String key = (String) v.get(i);
			Integer count = (Integer) cc.get(key);
			if (count == null) {
				this.classCounts.put(key, new Integer(1));
			} else {
				this.classCounts.put(key, new Integer(count.intValue() + 1));
			}
		}
	}

	public void decrementClassCounts(Vector v) {
		for (int i = 0; i < v.size(); i++) {
			String key = (String) v.get(i);
			Integer count = (Integer) this.classCounts.get(key);
			if (count != null) {
				int c = count.intValue() - 1;
				if (c == 0) {
					this.classCounts.remove(key);
				} else {
					this.classCounts.put(key, new Integer(c));
				}
			}
		}
	}

	public double calculateAverage() {
		Collection c = getClassCounts().values();
		double sum = 0.0D;
		for (Iterator i = c.iterator(); i.hasNext();) {
			sum += ((Integer) i.next()).intValue();
		}
		double result = sum / c.size();
		setAverage(result);
		this.avgChanged = false;
		return result;
	}

	public double calculateDeviation() {
		double average = getAverage();
		Collection c = getClassCounts().values();
		double sum = 0.0D;
		for (Iterator i = c.iterator(); i.hasNext();) {
			sum += Math.pow(((Integer) i.next()).intValue() - average, 2.0D);
		}
		double variance = sum / c.size();
		double result = Math.sqrt(variance);
		setDeviation(result);
		this.devChanged = false;
		return result;
	}

	public void fireNotification() {
		super.fireNotification();
		finest("Memory fireNotification() triggered.");
	}

	public boolean store(Entity t) {
		boolean b = super.store(t);
		if (b) {
			thingModified(t, "", t.toXMLSansName(false));
		}
		return b;
	}

	public boolean forget(Entity t) {
		boolean b = super.forget(t);
		if (b) {
			thingModified(t, t.toXMLSansName(false), "");
		}
		return b;
	}

	public void clear() {
		super.clear();
		this.classCounts.clear();
	}

	public static void main(String[] arv) {
		Entity t1 = new Entity("Mark");
		t1.addType("parking");
		t1.addType("god");
		Entity t2 = new Entity("Steph");
		t2.addType("parking");
		t2.addType("dunce");
		Relation r1 = new Relation("siblings", t1, t2);
		r1.addType("related");
		r1.addType("fraternal");
		r1.addType("zygotes");
		Thread d1 = new Thread();
		d1.addType("related");
		d1.addType("fraternal");
		d1.addType("twins");
		Thread d2 = new Thread();
		d2.addType("features");
		d2.addType("the");
		r1.addThread(d2);
		r1.addThread(d1);

		System.out.println("\nRelation is: " + r1);

		EntityMemory m1 = new EntityMemory();
		m1.store(r1);
		m1.store(t1);
		m1.store(t2);
		System.out.println("\nClass counts: " + m1.getClassCounts());
		System.out.println("\nPair counts: " + m1.getClassPairCounts());
		System.out.println("\nHomogeneity counts: "
				+ m1.getClassDivisionCount());

		m1.forget(t2);
		System.out.println("\n\nClass counts: " + m1.getClassCounts());
		System.out.println("\nPair counts: " + m1.getClassPairCounts());
		System.out.println("\nHomogeneity counts: "
				+ m1.getClassDivisionCount());
	}

	public static Logger getLogger() {
		return Logger.getLogger("memory.Memory");
	}

	protected static void finest(Object s) {
		Logger.getLogger("memory.Memory").finest("Memory: " + s);
	}

	protected static void finer(Object s) {
		Logger.getLogger("memory.Memory").finer("Memory: " + s);
	}

	protected static void fine(Object s) {
		Logger.getLogger("memory.Memory").fine("Memory: " + s);
	}

	protected static void config(Object s) {
		Logger.getLogger("memory.Memory").config("Memory: " + s);
	}

	protected static void info(Object s) {
		Logger.getLogger("memory.Memory").info("Memory: " + s);
	}

	protected static void warning(Object s) {
		Logger.getLogger("memory.Memory").warning("Memory: " + s);
	}

	protected static void severe(Object s) {
		Logger.getLogger("memory.Memory").severe("Memory: " + s);
	}
}