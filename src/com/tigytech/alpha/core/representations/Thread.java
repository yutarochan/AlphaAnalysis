package com.tigytech.alpha.core.representations;

import java.util.Vector;

public class Thread extends Vector<String> {
	
	public Thread() {}
	
	public Thread(Thread t) {
		super(t);
	}

	public Thread(String type) {
		addType(type);
	}
	
	public String getThreadType() {
		if (size() < 0) return null;
		return get(0);
	}
	
	public void add(int index, String element) {
		super.add(index, element);
	}
	
	public void addType(String type) {
		// Moves the entity to the end of the list if present.
		if (contains(type)) remove(type);
		add(type);
	}
	
	public void addTypeTop(String type) {
		// Moves the entity to the top of the list.
		if (contains(type)) remove(type);
		add(0, type);
	}
	
	public String getType() {
		if (size() == 0) return "NULL";
		return lastElement();
	}
	
	public String getSuperType() {
		if (size() < 2) return "NULL";
		return get(size() - 2);
	}
	
	public boolean contains(String type) {
		return super.contains(type);
	}
	
	public boolean contains(String[] types) {
		for (String s : this)
			if (contains(s)) return true;
		return false;
	}
	
	public boolean containsAll(String[] types) {
		for (String s : this)
			if (!contains(s)) return false;
		return true;
	}
	
	public String toString() {
		String data = "";
		for (int i = 0; i < size(); i++) {
			data += elementAt(i);
			if (i < size()-1) data += " ";
		}
		return data;
	}
	
	public static void main(String[] args) {
	}
}
