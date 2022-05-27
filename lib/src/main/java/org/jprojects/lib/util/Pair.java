package org.jprojects.lib.util;

public class Pair<T,V> {
	
	private T object1;
	private V object2;
	
	public Pair(T first, V second) {
		this.object1 = first;
		this.object2 = second;
	}
	
	public void setFirst(T first) {
		this.object1 = first;
	}
	
	public T getFirst() {
		return object1;
	}
	
	public void setSecond(V second) {
		this.object2 = second;
	}
	
	public V getSecond() {
		return object2;
	}
}
