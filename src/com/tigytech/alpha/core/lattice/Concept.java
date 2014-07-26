package com.tigytech.alpha.core.lattice;

import java.util.Set;

public abstract interface Concept<T> {
	
	public abstract boolean contains(T param);
	
	public abstract void learnPositive(T param);
	
	public abstract void learnNegative(T param);
	
	public abstract Set<T> maximalElements();
	
}
