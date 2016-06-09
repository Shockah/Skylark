package io.shockah.skylark.util;

public final class Box<T> {
	protected static boolean objectsAreEqual(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return true;
		if (o1 == null)
			return o2.equals(o1);
		return o1.equals(o2);
	}
	
	public T value;
	
	public Box() {
		this(null);
	}
	
	public Box(T t) {
		value = t;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Box<?>))
			return false;
		Box<?> b = (Box<?>)o;
		return objectsAreEqual(value,b.value);
	}
	
	public String toString() {
		return String.format("[Box: %s]", value);
	}
}