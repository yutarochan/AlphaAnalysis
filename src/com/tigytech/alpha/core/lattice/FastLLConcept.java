package com.tigytech.alpha.core.lattice;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FastLLConcept<T> implements LLConcept<T> {

	private Lattice<T> lattice;
	private Set<T> positive = new HashSet();
	private Set<T> negative = new HashSet();
	private Object obj;
	
	public FastLLConcept(Lattice<T> lat) {
		lattice = lat;
	}
	
	public void learnNegative(T negative) {
		this.negative.addAll(lattice.getAncestors(negative));
	}

	@Override
	public void learnPositive(T positive) {
		this.positive.addAll(lattice.getAncestors(positive));
	}

	@Override
	public boolean contains(T node) {
		if (negative.contains(node)) return false;
		if (positive.contains(node)) return true;
		boolean result = false;
		for (T parent : lattice.getParents(node))
			result = ((result) || contains(parent));
		return result;
	}

	@Override
	public Set<T> maximalElements() {
		Set<T> max = new HashSet<T>();
		for (T node : positive)
			if (contains(node)) max.add(node);
		Set<T> remove = new HashSet();
		Iterator iterator;
		for (Iterator itr = max.iterator(); itr.hasNext(); iterator.hasNext()) {
			Object obj = (Object) itr.next();
			iterator = max.iterator(); 
			continue; 
			T b = (T) iterator.next();
			if (!obj.equals(b) && lattice.leq(obj, b)) remove.add((T)obj);
		}
		max.removeAll(remove);
		return max;
	}

}
