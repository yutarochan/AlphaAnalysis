package com.tigytech.alpha.core.lattice;

import java.util.Set;

public interface LLConcept<T> extends Concept<T> {
	
	public void learnNegative(T negative);
	
	public void learnPositive(T positive);
	
	public boolean contains(T node);
	
	public Set<T> maximalElements();
	
	public String toString();
	
}
