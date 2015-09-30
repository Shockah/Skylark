package pl.shockah;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SelectList<T> implements List<T> {
	protected final List<T> list;
	protected T current = null;
	protected int currentI = -1;
	
	public SelectList(List<T> list) {
		this.list = list;
	}
	
	protected void setup() {
		if (currentI == -1) {
			if (list.isEmpty()) return;
			currentI = 0;
			current = list.get(currentI);
		} else {
			if (currentI <= list.size()-1) return;
			currentI = 0;
			current = list.get(currentI);
		}
	}
	
	public T next() {
		setup();
		currentI++;
		if (currentI > list.size()-1) currentI -= list.size();
		current = list.get(currentI);
		return current;
	}
	public T previous() {
		setup();
		currentI--;
		if (currentI < 0) currentI += list.size();
		current = list.get(currentI);
		return current;
	}
	public void setCurrent(T el) {
		int index = list.indexOf(el);
		if (index == -1) return;
		currentI = index;
		current = list.get(currentI);
	}
	public void setCurrentIndex(int index) {
		if (index > list.size()-1 || index < 0) return;
		currentI = index;
		current = list.get(currentI);
	}
	public T getCurrent() {
		setup();
		return current;
	}
	public int getCurrentIndex() {
		setup();
		return currentI;
	}

	public boolean add(T t) {return list.add(t);}
	public void add(int index, T t) {
		if (index <= currentI) currentI++;
		list.add(index,t);
	}
	public boolean addAll(Collection<? extends T> c) {return list.addAll(c);}
	public boolean addAll(int index, Collection<? extends T> c) {
		if (index <= currentI) currentI += c.size();
		return list.addAll(index,c);
	}
	public void clear() {list.clear();}
	public boolean contains(Object o) {return list.contains(o);}
	public boolean containsAll(Collection<?> c) {return list.containsAll(c);}
	public T get(int index) {return list.get(index);}
	public int indexOf(Object o) {return list.indexOf(o);}
	public boolean isEmpty() {return list.isEmpty();}
	public Iterator<T> iterator() {return list.iterator();}
	public int lastIndexOf(Object o) {return list.lastIndexOf(o);}
	public ListIterator<T> listIterator() {return list.listIterator();}
	public ListIterator<T> listIterator(int index) {return list.listIterator(index);}
	public boolean remove(Object o) {return list.remove(o);}
	public T remove(int index) {return list.remove(index);}
	public boolean removeAll(Collection<?> c) {return list.removeAll(c);}
	public boolean retainAll(Collection<?> c) {return list.retainAll(c);}
	public T set(int index, T t) {return list.set(index,t);}
	public int size() {return list.size();}
	public List<T> subList(int start, int end) {return list.subList(start,end);}
	public Object[] toArray() {return list.toArray();}
	public <R> R[] toArray(R[] ta) {return list.toArray(ta);}
}