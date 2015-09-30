package pl.shockah;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

public class SortedLinkedList<E> extends LinkedList<E> {
	private static final long serialVersionUID = -4751208078850005267L;
	
	protected final Comparator<E> comparator;
	
	public SortedLinkedList() {
		super();
		comparator = null;
	}
	public SortedLinkedList(Comparator<E> comparator) {
		super();
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
	public boolean offer(E e) {
		add(e);
		return true;
	}
	public void push(E e) {
		add(e);
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
	public void addFirst(E e) {
		throw new UnsupportedOperationException();
	}
	public void addLast(E e) {
		throw new UnsupportedOperationException();
	}
	public boolean offerFirst(E e) {
		throw new UnsupportedOperationException();
	}
	public boolean offerLast(E e) {
		throw new UnsupportedOperationException();
	}
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}
}