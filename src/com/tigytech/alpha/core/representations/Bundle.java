package com.tigytech.alpha.core.representations;

import java.util.Vector;

public class Bundle extends Vector<Thread> {
	
	private Entity parentObject = null;
	
	public Bundle() {}
	
	public Bundle (Thread t) {
		this();
		addThread(t);
	}

	public Bundle copy() {
		Bundle temp = new Bundle();
		temp.addAll(this);
		return temp;
	}
	
	public Object clone() {
		Bundle b = new Bundle();
		// Fill With methods.
		return b;
	}
	
	private void addThread(Thread t) {
		
	}
	
}
