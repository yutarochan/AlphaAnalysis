package com.tigytech.alpha.core.utilities.Logger;

public class Level {

	public static final Level OFF = new Level("off", 7);
	public static final Level SEVERE = new Level("severe", 6);
	public static final Level WARNING = new Level("warning", 5);
	public static final Level INFO = new Level("info", 4);
	public static final Level CONFIG = new Level("config", 3);
	public static final Level FINE = new Level("fine", 2);
	public static final Level FINER = new Level("finer", 1);
	public static final Level FINEST = new Level("finest", 0);
	public static final Level ALL = new Level("all", -1);

	final String name;
	final int level;
	
	protected Level(String name, int level) {
		this.name = name;
		this.level = level;
	}
	
	public String toString() {
		return this.name;
	}

	public int intValue() {
		return this.level;
	}

	public String getName() {
		return this.toString();
	}
}