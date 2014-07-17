package com.tigytech.alpha.core.utilities;

import com.tigytech.alpha.core.representations.Tags;

public class XMLIterator {
	
	private String input;
	private String start;
	private String end;
	private String next;
	
	public XMLIterator(String input, String start, String end) {
		this.input = input;
		this.start = start;
		this.end = end;
	}
	
	public XMLIterator(String input, String tag) {
		this(input, tag, tag);
	}
	
	public boolean hasNext() {
		return (this.next != null);
	}
	
	public String next() {
		String result = next;
		next = Tags.untagTopLevelString();
	}
}