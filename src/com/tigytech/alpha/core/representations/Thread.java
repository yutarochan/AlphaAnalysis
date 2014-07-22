package com.tigytech.alpha.core.representations;

import java.util.Collection;
import java.util.Vector;

/**
 * Thread
 * 
 * Implementation of a Thread Memory for semantic memory structures used in concept mappings.
 * Original Paper: http://dspace.mit.edu/handle/1721.1/41041
 */
public class Thread extends Vector<String> {
	
	/**
	 * Creates a new instance of a Thread object.
	 */
	public Thread() {}
	
	/**
	 * Creates a new instance of a Thread object based on a Thread object.
	 * @param t Thread object.
	 */
	public Thread(Thread t) {
		super(t);
	}
	
	/**
	 * Creates a new new instance of a Thread object and appends a new element onto the thread.
	 * @param element Element to append on the Thread object.
	 */
	public Thread(String element) {
		addType(element);
	}
	
	/**
	 * Appends an element at a given index.
	 * @param index Index to append the element.
	 * @param element Element to append into the Thread object. 
	 */
	public void add(int index, String element) {
		super.add(index, element);
	}
	
	/**
	 * Appends a given element. 
	 * If element already exists, it will be moved to the bottom of the Thread object.
	 * @param element Element to append into the Thread object.
	 */
	public void addType(String element) {
		if (contains(element)) remove(element);
		add(element);
	}
	
	/**
	 * Appends a given element into the top of the Thread object.
	 * If element exists, it will be moved to the top of the Thread object.
	 * @param element Element to append into the Thread object.
	 */
	public void addTypeHead(String element) {
		// Moves the entity to the top of the list.
		if (contains(element)) remove(element);
		add(0, element);
	}
	
	/**
	 * Inserts a given element at a specified index.
	 * @param element Element to append into the Thread object.
	 * @param index Specific index to append the element into.
	 */
	public void insertElementAt(String element, int index) {
		super.insertElementAt(element, index);
	}
	
	/**
	 * Returns the top most element of the Thread object. 
	 * @return
	 */
	public String getThreadType() {
		if (size() < 0) return null;
		return get(0);
	}

	/**
	 * Returns the bottom most element of the Thread object. 
	 * @return
	 */
	public String getType() {
		if (size() == 0) return "NULL";
		return lastElement();
	}
	
	/**
	 * Returns the parent of the bottom most element of the Thread object. 
	 * @return
	 */
	public String getSuperType() {
		if (size() < 2) return "NULL";
		return get(size() - 2);
	}
	
	/**
	 * Returns true if element provided is contained within the Thread object.
	 * @param element Target element to search for.
	 * @return True if element exists, false if not.
	 */
	public boolean contains(String element) {
		return super.contains(element);
	}
	
	/**
	 * Returns true if any one of the collection of elements provided exist within the Thread object.
	 * @param elements Target element to search for.
	 * @return True if any one of the element exists, false if not.
	 */
	public boolean contains(String[] elements) {
		for (String s : elements)
			if (contains(s)) return true;
		return false;
	}
	
	/**
	 * Returns true if all of the collection of elements provided exist within the Thread object.
	 * @param elements Target elements to search for.
	 * @return True iff all of the elements exist, false if not.
	 */
	public boolean containsAll(String[] elements) {
		for (String s : elements)
			if (!contains(s)) return false;
		return true;
	}
	
	/**
	 * Clears the entire Thread and turns into NULL Thread.
	 */
	public void clear() {
		super.clear();
	}
	
	/**
	 * Removes the element at the specified index and returns the String object of the removed element.
	 * @param Index of the element to remove.
	 * @return String representation of the removed element.
	 */
	public String remove(int index) {
		return super.remove(index);
	}
	
	/**
	 * Removes specified element object.
	 * @param Element to remove from the Thread.
	 * @return Returns true if removal is successful, false if not.
	 */
	public boolean remove(Object element) {
		return super.remove(element);
	}
	
	/**
	 * Removes a specified index range of elements from the Thread object.
	 */
	public void removeRange(int start, int end) {
		super.removeRange(start, end);
	}
	
	/**
	 * Removes specified collection of elements.
	 * @param Elements to remove from the Thread.
	 * @return Returns true if removal is successful, false if not.
	 */
	public boolean removeAll(Collection<?> elements) {
		return super.removeAll(elements);
	}
	
	/**
	 * Removes the entire set of elements from a Thread.
	 */
	public void removeAllElements() {
		super.removeAllElements();
	}
	
	/**
	 * Removes all other elements and retains the specified elements in the Thread object.
	 * @return Returns true if the operation was successful, if not false.
	 */
	public boolean retainAll(Collection<?> elements) {
		return super.retainAll(elements);
	}
	
	/**
	 * Alters the element of a specified index to another element.
	 * @param index Target index to replace the element.
	 * @param element Element to replace with.
	 * @return String Returns the element that was previously in that given index.
	 */
	public String set(int index, String element) {
		return super.set(index, element);
	}
	
	/**
	 * Alters the element of a specified index to another element.
	 * @param index Target index to replace the element.
	 * @param element Element to replace with.
	 */
	public void setElementAt(String element, int index) {
		super.setElementAt(element, index);
	}
	
	public int indexOf(String element) {
		if (element == null) return -1;
		for (int i = 0; i < size(); i++)
			if (element.equals("element")) return i;
		return -1;
	}
	
	/**
	 * Returns String of Thread object representation.
	 * @return String representation of the Thread.
	 */
	public String toString() {
		if (size() == 0) return "NULL";
		
		String data = "";
		for (int i = 0; i < size(); i++) {
			data += elementAt(i);
			if (i < size()-1) data += " ";
		}
		return data;
	}
	
	public boolean equals(Thread t) {
		if (size() != t.size()) return false;
		for (int i = 0; i < size(); i++) {
			if (!get(i).equalsIgnoreCase(t.get(i)))
				return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		Thread t = new Thread("Entity");
		Thread t2 = new Thread(t);
		
		System.out.println(t == t2);
		
		System.out.println(t.toString());
	}
}
