package com.tigytech.alpha.core;

public class StoryProcessor {
	
	private static StoryProcessor storyProcessor;
	
	public static StoryProcessor getStoryProcessor() {
		if (storyProcessor == null) {
			storyProcessor = new StoryProcessor();
			// Initialize wiring? wtf is a wiring???
		}
		return storyProcessor;
	}
	
	public void submitTextFile(final String textDirectory) {
		new Thread() {
			public void run() {
				
			}
		}.start();
	}
	
}
