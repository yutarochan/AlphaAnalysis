package com.tigytech.alpha.core.representations;

import java.io.Serializable;
import java.util.Observable;

public class Entity extends Observable implements Serializable, Frame {
	
	private String nameSuffix = null;

	public Entity() {
		//setNameSuffix(NameGenerator);
	}
	
	public void setNameSuffix(String suffix) {
		this.nameSuffix = suffix;
	}

}
