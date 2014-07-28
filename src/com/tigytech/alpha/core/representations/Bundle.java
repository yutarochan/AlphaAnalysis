package com.tigytech.alpha.core.representations;

import java.util.Vector;

public class Bundle extends Vector<Thread> {

	public Bundle() {}
	
	public Bundle(Thread t) {
		this();
		addThread(t);
	}
	
	public boolean addThread(Thread t) {
		add(0, t);
		return true;
	}
	
	public boolean addThreadAtEnd(Thread t) {
		add(t);
		return true;
	}
	
	public boolean removeThread(Thread t) {
		return remove(t);
	}
	
	public Thread getThread(Thread threadElement) {
		for (Thread t : this)
			if (!t.isEmpty() && t.equals(threadElement)) 
				return t;
		return null;
	}
	
	public Bundle copy() {
		Bundle b = new Bundle();
		b.addAll(this);
		return b;
	}
	
	public Bundle clone() {
		Bundle b = new Bundle();
		for (Thread t : this) b.add(t.copyThread())
		return b;
	}
}
