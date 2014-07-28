package com.tigytech.alpha.util.config;

public class ConfigurationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ConfigurationException(Throwable t) {
		super(t);
	}

	public ConfigurationException(String msg) {
		super(msg);
	}
}