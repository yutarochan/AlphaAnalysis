package com.tigytech.alpha.core.lattice;

import java.util.HashSet;
import java.util.Set;

public abstract class Lattice<T> {

	abstract Iterable<T> getParents(T param);

	public Set<T> getAncestors(T node) {
		Set<T> ancestors = new HashSet();
		ancestors.add(node);
		for (T parent : getParents(node))
			ancestors.addAll(getAncestors(parent));
		return ancestors;
	}
	
	public boolean leq(Object obj, T parent) {
		return getAncestors(obj).contains(parent);
	}
	
}
