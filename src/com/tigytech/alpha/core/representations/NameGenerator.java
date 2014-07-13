package com.tigytech.alpha.core.representations;

import com.tigytech.alpha.core.utilities.Logger.Logger;

public class NameGenerator {
	
	protected static int memory = 0;
	
	public String getNewName() {
		String result = "-" + memory;
		increment();
		return null;
	}
	
	private static void increment() {
		fine("Incrementing memory from " + memory);
		memory += 1;
	}
	
	protected static void fine(Object s) {
		// Logger.getLogger("thing.NameGenerator").fine("NameGenerator: " + s);
	}
}
