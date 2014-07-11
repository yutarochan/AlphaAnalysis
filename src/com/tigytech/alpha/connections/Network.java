package com.tigytech.alpha.connections;

import java.util.ArrayList;

public interface Network<S extends Server> {

	public abstract ArrayList<S> getServers();
	
	public abstract ArrayList<S> getTargets(S param);
	
}
