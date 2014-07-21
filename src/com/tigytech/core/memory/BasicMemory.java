package com.tigytech.core.memory;

import bridge.infrastructure.wires.Wire;
import bridge.reps.entities.Bundle;
import bridge.reps.entities.Entity;
import bridge.reps.entities.Function;
import bridge.reps.entities.NameGenerator;
import bridge.reps.entities.Relation;
import bridge.reps.entities.Sequence;
import bridge.reps.entities.Thread;
import bridge.utils.CollectionUtils;
import bridge.utils.logging.Level;
import bridge.utils.logging.Logger;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;

public class BasicMemory extends Observable implements Serializable, Observer, MemoryForget {
	
	private static BasicMemory staticMemory;
	private HashMap<String, ArrayList<Entity>> conceptHash = new HashMap();

	public static BasicMemory getStaticMemory() {
		if (staticMemory == null) {
			staticMemory = new BasicMemory();
			warning("Creating static memory: " + staticMemory);
		}
		return staticMemory;
	}

	public BasicMemory() {
	}

	public void setInput(Object o, Object port) {
		if (!(o instanceof Entity)) {
			return;
		}
		if (Wire.INPUT.equals(port)) {
			store((Entity) o);
		} else if (RECURSIVE.equals(port)) {
			storeRecursively((Entity) o);
		} else if (STORE.equals(port)) {
			if (!(o instanceof String)) {
				Logger.warning(this,
						"Store port on memory expected a file name");
				return;
			}
			storeInFile((String) o);
		} else if (RESTORE.equals(port)) {
			if (!(o instanceof String)) {
				Logger.warning(this,
						"Store port on memory expected a file name");
				return;
			}
			restoreFromFile((String) o);
		}
	}

	public BasicMemory(boolean notification) {
		System.out.println("Hello basic memory with argument");
		setNotification(notification);
	}

	public static final Object SAVE_STATE_THING = new Object();
	public static final Object FIRE_NOTIFICATION_THING = new Object();
	public static final Object SAVE_STATE = new Object();
	public static final Object FIRE_NOTIFICATION = new Object();
	public static final int ACTION_STORE = 0;
	public static final int ACTION_FORGET = 0;
	Vector<Entity> things = new Vector();
	Hashtable instances = new Hashtable();
	public static Object RECURSIVE = "Recursive";
	public static Object STORE = "Store in file";
	public static Object RESTORE = "Restore from file";
	Entity lastChanged;

	public Object getOutput(Object o) {
		return null;
	}

	public Vector<Entity> getThings() {
		return this.things;
	}

	public Vector getThings(String type) {
		Vector result = new Vector();
		Vector fodder = getThings();
		for (int i = 0; i < fodder.size(); i++) {
			Entity thing = (Entity) fodder.elementAt(i);
			if (thing.isA(type)) {
				result.add(thing);
			}
		}
		return result;
	}

	public Vector getThingsOfSupertype(String supertype) {
		Vector result = new Vector();

		Vector fodder = getThings();
		for (int i = 0; i < fodder.size(); i++) {
			Entity thing = (Entity) fodder.elementAt(i);
			if (thing.getSupertype().equals(supertype)) {
				result.add(thing);
			}
		}
		info("Result is: " + result);
		return result;
	}

	public Hashtable getInstances() {
		return this.instances;
	}

	public void setThings(Vector t) {
		saveState();
		this.things = t;
		fireNotification();
	}

	public void setInstances(Hashtable t) {
		saveState();
		this.instances = t;
		fireNotification();
	}

	public Vector getState() {
		Vector v = new Vector();
		v.add(getInstances());
		v.add(getThings());
		return v;
	}

	public void setState(Vector v) {
		if (v == null) {
			return;
		}
		saveState();
		try {
			setInstances((Hashtable) v.get(0));
			setThings((Vector) v.get(1));
		} catch (ClassCastException e) {
			warning("State not compatible.");
		}
		fireNotification();
	}

	public void thingModified(Entity t, String oldState, String newState) {
		if (!this.notification) {
			return;
		}
		fireNotification();
	}

	public void update(Observable o, Object arg) {
		this.lastChanged = ((Entity) o);
		if (arg == Entity.SAVE_STATE) {
			changed(SAVE_STATE_THING);
		} else if (arg == Entity.FIRE_NOTIFICATION) {
			changed(FIRE_NOTIFICATION_THING);
		}
	}

	public void changed() {
		changed(null);
	}

	public void changed(Object o) {
		setChanged();
		notifyObservers(o);
	}

	boolean notification = false;

	public void setNotification(boolean b) {
		this.notification = b;
	}

	public void setNotificationOnDependents(boolean b) {
		setNotification(b);
		Vector v = getThings();
		for (int i = 0; i < v.size(); i++) {
			((Entity) v.get(i)).setNotificationOnDependents(b);
		}
	}

	public boolean getNotification() {
		return this.notification;
	}

	public void saveState() {
		if (this.notification) {
			changed(SAVE_STATE);
		}
	}

	protected void fireNotification() {
		if (this.notification) {
			changed(FIRE_NOTIFICATION);
		}
	}

	protected void fireNotificationStore(Entity t) {
		if (this.notification) {
			fireNotification();
			changed(new ActionObject(t, 0));
		}
	}

	protected void fireNotificationForget(Entity t) {
		if (this.notification) {
			fireNotification();
			changed(new ActionObject(t, 0));
		}
	}

	public boolean store(Entity t) {
		if (getInstances().get(t.getNameSuffix()) == null) {
			saveState();

			t.setNotificationOnDependents(getNotification());
			t.addObserver(this);
			getInstances().put(t.getNameSuffix(), t);
			getThings().add(t);

			fireNotificationStore(t);
			return true;
		}
		fine("Failed to store thing: " + t.getName());
		return false;
	}

	public void storeRecursively(Entity t) {
		fine("Storing thing recursively: " + t.getName());
		Vector superThings = new Vector();
		superThings.addAll(t.getSubjectOf());
		superThings.addAll(t.getObjectOf());
		superThings.addAll(t.getElementOf());
		for (int i = 0; i < superThings.size(); i++) {
			Entity superThing = (Entity) superThings.get(i);
			storeRecursively(superThing);
		}
		if (!store(t)) {
			return;
		}
		if (t.functionP()) {
			storeRecursively(t.getSubject());
		} else if (t.relationP()) {
			storeRecursively(t.getSubject());
			storeRecursively(t.getObject());
		} else if (t.sequenceP()) {
			Vector v = t.getElements();
			for (int i = 0; i < v.size(); i++) {
				Entity element = (Entity) v.get(i);
				storeRecursively(element);
			}
		}
	}

	public boolean forget(Entity t) {
		if (isForgettable(t)) {
			saveState();

			getInstances().remove(t.getNameSuffix());

			getThings().remove(t);
			t.deleteObserver(this);
			fireNotificationForget(t);
			return true;
		}
		warning("Thing could not be forgotten:" + t.getName());
		return false;
	}

	public boolean forgetRecursively(Entity t) {
		if (t.functionP()) {
			forgetRecursively(t.getSubject());
		} else if (t.relationP()) {
			forgetRecursively(t.getSubject());
			forgetRecursively(t.getObject());
		} else if (t.sequenceP()) {
			Vector elements = new Vector();
			elements.addAll(t.getElements());
			for (int i = 0; i < elements.size(); i++) {
				forgetRecursively((Entity) elements.get(i));
			}
		}
		return forget(t);
	}

	boolean fascistForgetting = true;
	public static final String LOGGER_GROUP = "memory";
	public static final String LOGGER_INSTANCE = "BasicMemory";
	public static final String LOGGER = "memory.BasicMemory";

	public void setFascistForgetting(boolean b) {
		this.fascistForgetting = b;
	}

	public boolean isForgettable(Entity thing) {
		if (!this.fascistForgetting) {
			return true;
		}
		for (Iterator i = thing.getParents().iterator(); i.hasNext();) {
			Entity parent = (Entity) i.next();
			if (getInstances().keySet().contains(parent.getNameSuffix())) {
				return false;
			}
		}
		return true;
	}

	public boolean findThingRecursively(Entity g, Entity t) {
		finest("Looking in and at " + g.getName() + " for " + t.getName());
		if (g == t) {
			finest("Found thing!");
			return true;
		}
		if (g.getClass() == Function.class) {
			return findThingRecursively(((Function) g).getSubject(), t);
		}
		if (g.getClass() == Relation.class) {
			Relation r = (Relation) g;
			return (findThingRecursively(r.getSubject(), t))
					|| (findThingRecursively(r.getObject(), t));
		}
		if (g.getClass() == Sequence.class) {
			Vector elements = ((Sequence) g).getElements();
			for (int i = 0; i < elements.size(); i++) {
				if (findThingRecursively((Entity) elements.get(i), t)) {
					return true;
				}
			}
		}
		return false;
	}

	public Entity findThingInMemory(int id) {
		String suffix = "-" + id;
		return (Entity) getInstances().get(suffix);
	}

	public Entity findThingInMemory(Entity thing) {
		String suffix = NameGenerator.extractSuffixFromName(thing.getType());
		if (suffix != null) {
			Object o = getInstances().get(suffix);
			if (o != null) {
				return (Entity) o;
			}
		}
		return null;
	}

	public Entity findThingInMemory(String name) {
		String suffix = extractSuffixFromName(name);
		if (suffix != null) {
			Object o = getInstances().get(suffix);
			if (o != null) {
				fine("Found in memory: " + name);
				return (Entity) o;
			}
			warning("Failed to find in memory: " + name);
		}
		return null;
	}

	public Vector getThingsOfType(String type, Vector things) {
		Vector result = new Vector();
		for (int i = 0; i < things.size(); i++) {
			Entity thing = (Entity) things.elementAt(i);
			if (thing.getType().equals(type)) {
				result.add(thing);
			}
		}
		return result;
	}

	public List getThingsOfSupertype(String supertype, List things) {
		List result = new Vector();
		for (int i = 0; i < things.size(); i++) {
			Entity thing = (Entity) things.get(i);
			String suptype = thing.getSupertype();
			if ((suptype != null) && (suptype.equals(supertype))) {
				result.add(thing);
			}
		}
		return result;
	}

	public Entity getReferenceX(Entity thing, String butNot) {
		return findMatchingThingX(thing, butNot);
	}

	public Entity findMatchingThingX(Entity thing, String butNot) {
		fine("Looking for match to " + thing.getName());
		if (!thing.entityP()) {
			fine("...but not thing ");
			return null;
		}
		Collection theseTypes = thing.getAllTypesForFindMatchingThing();
		Vector possibilities = fetchThings(thing.getType());
		possibilities.remove(this);
		if (possibilities.isEmpty()) {
			return null;
		}
		int matches = 0;
		Entity result = null;
		for (int i = 0; i < possibilities.size(); i++) {
			Entity possibility = (Entity) possibilities.elementAt(i);
			Collection thoseTypes = possibility
					.getAllTypesForFindMatchingThing();
			Collection intersection = CollectionUtils.intersection(theseTypes,
					thoseTypes);
			Collection difference = CollectionUtils.difference(theseTypes,
					thoseTypes);
			int newMatches = intersection.size();
			if ((butNot == null) || (!possibility.isA(butNot))) {
				if (!possibility.isA("clone")) {
					if ((!(possibility instanceof Function))
							&& (!(possibility instanceof Sequence))) {
						if (difference.isEmpty()) {
							if (newMatches > matches) {
								result = possibility;
								matches = newMatches;
							} else if (newMatches == matches) {
								result = possibility;
								matches = newMatches;
							}
						}
					}
				}
			}
		}
		if (result != null) {
			fine("Looks like " + thing.getName() + " best matches "
					+ result.getName());
			fine("Current: " + this);
			fine("Antecedant: " + result);
		}
		return result;
	}

	public void extendVia(Entity thing, String via) {
		Thread thread = thing.getPrimedThread();
		if (thread.size() == 0) {
			return;
		}
		if (!((String) thread.elementAt(0)).equalsIgnoreCase("thing")) {
			return;
		}
		if (thread.size() == 1) {
			return;
		}
		String hook = null;
		if (thread.size() == 1) {
			hook = (String) thread.elementAt(0);
		} else {
			hook = (String) thread.elementAt(1);
		}
		Logger.getLogger("extender").fine("Hook is " + hook);
		Logger.getLogger("extender").fine(
				"Total thing instances: " + getThings().size());

		Vector goodThings = fetchThings(hook, via);

		Logger.getLogger("extender").fine(
				"Thing instances belonging to via class: " + goodThings.size());
		Vector threads = new Vector();
		for (int i = 0; i < goodThings.size(); i++) {
			Entity goodThing = (Entity) goodThings.get(i);
			threads.addAll(goodThing.getBundle());
		}
		Logger.getLogger("extender").fine("Total threads: " + threads.size());
		Vector goodThreads = new Vector();
		for (int i = 0; i < threads.size(); i++) {
			Thread candidateThread = (Thread) threads.elementAt(i);
			Logger.getLogger("extender").fine("Candidate: " + candidateThread);
			if ((!candidateThread.isEmpty())
					&& (((String) candidateThread.firstElement())
							.equalsIgnoreCase("thing"))
					&& (candidateThread.contains(via))) {
				goodThreads.add(candidateThread);
			}
		}
		Logger.getLogger("extender").fine(
				"Threads containing via class: " + threads.size());
		for (int i = 0; i < goodThreads.size(); i++) {
			Thread extendingThread = (Thread) goodThreads.elementAt(i);

			Thread newThread = thread.copyThread();

			int insertionIndex = 0;
			if (((String) newThread.elementAt(0)).equalsIgnoreCase("thing")) {
				insertionIndex = 1;
			}
			int sourceIndex = 0;
			if (((String) extendingThread.elementAt(0))
					.equalsIgnoreCase("thing")) {
				sourceIndex = 1;
			}
			if (extendingThread.size() - 2 >= sourceIndex) {
				for (int j = extendingThread.size() - 2; j >= sourceIndex; j--) {
					newThread.add(insertionIndex,
							(String) extendingThread.elementAt(j));
				}
				thing.getBundle().addThread(newThread);
			}
		}
		thing.getBundle().prune();
	}

	protected Vector<Entity> fetchThings(String key) {
		return fetchThings(key, "thing");
	}

	protected Vector<Entity> fetchThings(String hook, String via) {
		Vector<Entity> vector = getThings();
		Vector<Entity> result = new Vector();
		for (int i = 0; i < vector.size(); i++) {
			Entity thing = (Entity) vector.elementAt(i);
			if (thing.entityP()) {
				Bundle threads = thing.getBundle();
				for (int j = 0; j < threads.size(); j++) {
					Thread thread = (Thread) threads.get(j);
					if ((thread != null)
							&& (!thread.isEmpty())
							&& (hook.equalsIgnoreCase((String) thread
									.lastElement())) && (thing.isA(via))) {
						result.add(thing);
					}
				}
			}
		}
		return result;
	}

	public Entity getReference(String name) {
		return findThingInMemory(name);
	}

	public int getLargestID() {
		int result = 0;
		for (int i = 0; i < getThings().size(); i++) {
			Entity thing = (Entity) getThings().get(i);
			result = Math.max(result, thing.getID());
		}
		return result;
	}

	protected static String extractSuffixFromName(String name) {
		int index = name.lastIndexOf('-');
		if (index >= 0) {
			return name.substring(index);
		}
		return null;
	}

	public void clear() {
		saveState();
		getInstances().clear();
		getThings().clear();
		NameGenerator.clearNameMemory();
		fireNotification();
	}

	public void storeInFile(String file) {
		try {
			FileOutputStream f = new FileOutputStream(file);
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(getState());
			o.close();
			f.close();
			Logger.info(this, "Wrote serialized file " + file);
		} catch (Exception f) {
			Logger.warning(this,
					"Encountered exception while writing serialized data file");
			f.printStackTrace();
		}
	}

	public void restoreFromFile(String file) {
		try {
			FileInputStream stream = new FileInputStream(file);
			ObjectInputStream o = new ObjectInputStream(stream);
			Vector v = (Vector) o.readObject();
			setState(v);
			o.close();
			NameGenerator.setNameMemory(getLargestID() + 1);
		} catch (Exception f) {
			Logger.warning(this,
					"Encountered exception while writing serialized data file");
			f.printStackTrace();
		}
	}

	public class ActionObject {
		final Object object;
		final int action;

		public ActionObject(Object o, int a) {
			this.object = o;
			this.action = a;
		}

		public Object getObject() {
			return this.object;
		}

		public int getAction() {
			return this.action;
		}
	}

	public String fetchAndShow(String key) {
		return showThings(fetchThings(key));
	}

	public String contentsAsStrings() {
		return showThings(getThings());
	}

	public String showThings(Vector<Entity> v) {
		String result = "";
		for (Entity t : v) {
			result = result + "\nThing: " + t.asString();
		}
		return result;
	}

	public static void main(String[] arv) {
		getLogger().setLevel(Level.All);
		boolean f = false;
		getStaticMemory().setNotificationOnDependents(f);
		System.out.println("Notification switch:" + f);
		Entity t1 = new Entity("man");
		t1.addType("Patrick");
		Entity t2 = new Entity("boy");
		t2.addType("Mark");
		BasicMemory m = getStaticMemory();
		EntityMemory m1 = new EntityMemory();

		Relation r1 = new Relation("friend", t1, t2);

		m.storeRecursively(r1);

		System.out
				.println("--------------Testing Memory/Thing memberships -- MAF.14.Jan.04");
		System.out.println("Thing we are manipulating is as follows: "+ r1.asString());
		System.out.println("\nContents of Static Memory: "
				+ m.contentsAsStrings());
		System.out.println("\nContents of First Memory: "
				+ m1.contentsAsStrings());

		System.out.println("Patrick: " + t1);
		System.out.println("Mark: " + t2);

		System.out.println("\nMoving thing from static to first...");
		m1.store(t1);

		System.out.println("\nContents of Static Memory: "
				+ m.contentsAsStrings());
		System.out.println("\nContents of First Memory: "
				+ m1.contentsAsStrings());

		System.out.println("Man:" + m.fetchAndShow("man"));

		System.out.println("Boy:" + m.fetchAndShow("boy"));

		System.out.println("Patrick:" + m.fetchAndShow("Patrick"));

		System.out.println("Mark:" + m.fetchAndShow("Mark"));
	}

	public static Logger getLogger() {
		return Logger.getLogger("memory.BasicMemory");
	}

	protected static void finest(Object s) {
		Logger.getLogger("memory.BasicMemory").finest("BasicMemory: " + s);
	}

	protected static void finer(Object s) {
		Logger.getLogger("memory.BasicMemory").finer("BasicMemory: " + s);
	}

	protected static void fine(Object s) {
		Logger.getLogger("memory.BasicMemory").fine("BasicMemory: " + s);
	}

	protected static void config(Object s) {
		Logger.getLogger("memory.BasicMemory").config("BasicMemory: " + s);
	}

	protected static void info(Object s) {
		Logger.getLogger("memory.BasicMemory").info("BasicMemory: " + s);
	}

	protected static void warning(Object s) {
		Logger.getLogger("memory.BasicMemory").warning("BasicMemory: " + s);
	}

	protected static void severe(Object s) {
		Logger.getLogger("memory.BasicMemory").severe("BasicMemory: " + s);
	}

	public String getName() {
		return "Basic Memory";
	}

	public void storeConcept(Entity t) {
		if (!t.entityP()) {
			store(t);
		}
		ArrayList<Entity> recorded = new ArrayList();
		storeConcept(t, t, recorded);
	}

	public void storeConcept(Entity t, Entity parent, ArrayList<Entity> recorded) {
		if (!recorded.contains(t)) {
			if (t.entityP()) {
				hashConcept(t, parent);
				recorded.add(t);
			} else if (t.functionP()) {
				storeConcept(t.getSubject(), parent, recorded);
			} else if (t.relationP()) {
				storeConcept(t.getSubject(), parent, recorded);
				storeConcept(t.getObject(), parent, recorded);
			} else if (t.sequenceP()) {
				Vector<Entity> v = t.getElements();
				for (int i = 0; i < v.size(); i++) {
					Entity element = (Entity) v.get(i);
					storeConcept(element, parent, recorded);
				}
			}
		}
	}

	public ArrayList<Entity> retrieveConcept(Entity t) {
		String type = t.getType();
		ArrayList current = (ArrayList) this.conceptHash.get(type);
		if (current == null) {
			current = new ArrayList();
			this.conceptHash.put(type, current);
		}
		return current;
	}

	private void hashConcept(Entity t, Entity parent) {
		String type = t.getType();
		ArrayList current = (ArrayList) this.conceptHash.get(type);
		if (current == null) {
			current = new ArrayList();
			this.conceptHash.put(type, current);
		}
		current.add(parent);
	}

	public Set<String> getTypes() {
		return this.conceptHash.keySet();
	}

	public ArrayList<Entity> retrieveConceptByType(String type) {
		ArrayList current = (ArrayList) this.conceptHash.get(type);
		if (current == null) {
			current = new ArrayList();
			this.conceptHash.put(type, current);
		}
		return current;
	}
}
