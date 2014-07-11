package com.tigytech.alpha.connections;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * TODO: Find best implementation of system through a SOAP protocol.
 */
public class ServerClientEP {	
	public static final String API_VERSION = "alpha";
	private static ServerClientEP instance;
	private URL serverURL;
	
	public ServerClientEP() {
	}
	
	public boolean initialize(URL url) {
		String surl = url.toString();
		surl = surl.endsWith("/") ? surl.substring(0, surl.length() - 1) : surl;
	
		/** TODO: Pass API key to SOAP client? */
		
		try {
			url = new URL(surl + "/soap");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return setServerURL(url);
	}
	
	protected synchronized boolean setServerURL(URL url) {
		if ((this.serverURL != null) && (!url.equals(this.serverURL))) {
			return true;
		} else if(this.serverURL == null) {
			this.serverURL = url;
			return true;
		}
		return false;
	}
	
	public synchronized URL getServerURL() { 
		return this.serverURL;
	}

	public static synchronized ServerClientEP getInstance() {
		if (instance == null) instance = new ServerClientEP();
		return instance;
	}
}