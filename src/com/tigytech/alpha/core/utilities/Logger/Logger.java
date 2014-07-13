package com.tigytech.alpha.core.utilities.Logger;

import java.util.Vector;

public class Logger {
	
	private String ID = "";
	private String parentID= "";
	private Level level = null;
	private static Vector<Logger> logs = new Vector<Logger>();
	
	private Logger(String s) {
		this.ID = s;
		setParentString(this.ID);
	}

	public static Logger getLogger(String name) {
		for (Logger l : logs)
			if (l.ID.equalsIgnoreCase(name)) return l;
		Logger newLog = new Logger(name);
		newLog.setLevel(Level.OFF);
		logs.add(newLog);
	
		return newLog;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public boolean isLoggable(Level level) {
		return (getLevel().intValue() < level.intValue());
	}
	
	public void log(Level level, Object obj) {
		if (isLoggable(level)) return;
		System.out.println("[LOGGER] " + level.getName() + ":" + obj.toString());
	}
	
	public void severe(Object obj) {
		if (isLoggable(Level.SEVERE)) return;
		System.out.println("[LOGGER] Severe : " + obj.toString());
	}
	
	public void warning(Object obj) {
		if (isLoggable(Level.WARNING)) return;
		System.out.println("[LOGGER] Warning : " + obj.toString());
	}
	
	public void info(Object obj) {
		if (isLoggable(Level.INFO)) return;
		System.out.println("[LOGGER] Info : " + obj.toString());
	}
	
	public void config(Object obj) {
		if (isLoggable(Level.CONFIG)) return;
		System.out.println("[LOGGER] Severe : " + obj.toString());
	}
	
	public void fine(Object obj) {
		if (isLoggable(Level.FINE)) return;
		System.out.println("[LOGGER] Fine : " + obj.toString());
	}
	
	public void finer(Object obj) {
		if (isLoggable(Level.FINER)) return;
		System.out.println("[LOGGER] Severe : " + obj.toString());
	}
	
	public void finest(Object obj) {
		if (isLoggable(Level.FINEST)) return;
		System.out.println("[LOGGER] Severe : " + obj.toString());
	}
	
	private void setParentString(String s) {
		int index = s.lastIndexOf('.');
		if (index >= 0)
			this.parentID = s.substring(0, index);
	}
	
	private Logger getParent() {
		for (Logger l : logs)
			if (l.ID.equalsIgnoreCase(this.parentID)) return l;
		return null;
	}
	
	private Level getLevel() {
		if (this.level != null) return this.level;
		if (getParent() != null) return getParent().getLevel();
		return Level.INFO;
	}
	
	public String toString() {
		return "[LOGGER] " + ID;
	}
	
	public static void main(String[] args) {
		/**
		 * Logger Utility - Unit Testing
		 * Confirm level logging process.
		 */
		System.out.println("Unit Test A - WARNING");
		getLogger("test_a").setLevel(Level.WARNING);
		getLogger("test_a").warning("Warning level logger enabled.");
		getLogger("test_a").info("Information level logger enabled.");
		getLogger("test_a").fine("Fine level logger enabled.");
		
		System.out.println("Unit Test B - INFO");
		getLogger("test_b").setLevel(Level.INFO);
		getLogger("test_b").warning("Warning level logger enabled.");
		getLogger("test_b").info("Information level logger enabled.");
		getLogger("test_b").fine("Fine level logger enabled.");
	}
}
