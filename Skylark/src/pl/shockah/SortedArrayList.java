package pl.shockah;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ListIterator;

public class SortedArrayList<E> extends ArrayList<E> {
	private static final long serialVersionUID = -7376307043586688296L;
	
	protected final Comparator<E> comparator;
	
	public SortedArrayList() {
		this(null);
	}
	public SortedArrayList(int size) {
		this(size, null);
	}
	public SortedArrayList(Comparator<E> comparator) {
		super();
		this.comparator = comparator;
	}
	public SortedArrayList(int size, Comparator<E> comparator) {
		super(size);
		this.comparator = comparator;
	}
	
	@SuppressWarnings("unchecked") protected int compareObjects(E e1, E e2) {
		if (comparator != null) return comparator.compare(e1,e2);
		return ((Comparable<E>)e1).compareTo(e2);
	}
	
	public boolean add(E e) {
		ListIterator<E> it = listIterator();
		while (it.hasNext()) {
			E next = it.next();
			if (compareObjects(e,next) < 0) {
				it.previous();
				it.add(e);
				it.next();
				return true;
			}
		}
		
		super.add(e);
		return true;
	}
	
	public boolean addAll(Collection<? extends E> c)  {
		for (E e : c) add(e);
		return true;
	}
	public Object clone() {
		SortedLinkedList<E> ret = new SortedLinkedList<>();
		for (E e : this) ret.add(e);
		return ret;
	}
	
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}
}