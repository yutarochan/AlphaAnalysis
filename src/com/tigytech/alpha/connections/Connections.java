package com.tigytech.alpha.connections;

import java.net.URL;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Observable;

public class Connections extends Observable implements Network<Server> {

	private IdentityHashMap<Server, Ports> portHM;
	private static boolean verbose = false;
	private static Connections instance;
	private ArrayList<Server> servers;
	
	public static boolean isVerbose() {
		return verbose;
	}
	
	public static void setVerbose(boolean verb) {
		verbose = verb;
	}
	
	@Override
	public ArrayList<Server> getServers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Server> getTargets(Server param) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void useServer(URL serverURL) throws Connections.ServerException {
		ServerClientEP.getInstance().initialize(serverURL);
	}
	
	public class ServerException extends Exception {

		private static final long serialVersionUID = 7888387339608330417L;

		public ServerException(String e) {
			super();
		}
		
		public ServerException(Throwable cause) {
			super();
		}
	
	}
	
	public class ServerError extends Error {
		
		private static final long serialVersionUID = 8001599246525986575L;

		public ServerError(String e) {
			super();
		}
		
		public ServerError(Throwable cause) {
			super();
		}
		
	}
}