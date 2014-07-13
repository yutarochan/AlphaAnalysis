package com.tigytech.alpha.summarizer;

public class Summarizer {
	
	private static Summarizer summarizer;
	
	public static Summarizer getSummarizer() {
		if (summarizer == null) summarizer = new Summarizer();
		return summarizer;
	}
	
	protected Summarizer() {
	}
	
}
