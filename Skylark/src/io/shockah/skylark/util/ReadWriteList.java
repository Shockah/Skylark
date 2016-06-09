package io.shockah.skylark.util;

import io.shockah.skylark.func.Action1;
import io.shockah.skylark.func.Action2;
import io.shockah.skylark.func.Func1;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteList<T> implements List<T> {
	protected final List<T> list;
	protected final ReentrantReadWriteLock lock;
	
	public ReadWriteList(List<T> underlyingList) {
		this(underlyingList, true);
	}
	
	public ReadWriteList(List<T> underlyingList, boolean fair) {
		list = underlyingList;
		lock = new ReentrantReadWriteLock(fair);
	}
	
	public void readOperation(Action1<List<T>> f) {
		lock.readLock().lock();
		try {
			f.call(Collections.unmodifiableList(list));
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public <R> R readOperation(Func1<List<T>, R> f) {
		lock.readLock().lock();
		try {
			return f.call(Collections.unmodifiableList(list));
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public boolean tryReadOperation(long timeout, TimeUnit unit, Action1<List<T>> f) throws InterruptedException {
		if (lock.readLock().tryLock(timeout, unit)) {
			try {
				f.call(Collections.unmodifiableList(list));
			} finally {
				lock.readLock().unlock();
			}
			return true;
		}
		return false;
	}
	
	public void writeOperation(Action1<List<T>> f) {
		lock.writeLock().lock();
		try {
			f.call(list);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public <R> R writeOperation(Func1<List<T>, R> f) {
		lock.writeLock().lock();
		try {
			return f.call(list);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public boolean tryWriteOperation(long timeout, TimeUnit unit, Action1<List<T>> f) throws InterruptedException {
		if (lock.writeLock().tryLock(timeout, unit)) {
			try {
				f.call(list);
			} finally {
				lock.writeLock().unlock();
			}
			return true;
		}
		return false;
	}
	
	public void iterate(Action1<T> f) {
		lock.readLock().lock();
		try {
			Iterator<T> iterator = list.iterator();
			while (iterator.hasNext()) {
				f.call(iterator.next());
			}
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public void iterate(Action2<T, ReadIterator<T>> f) {
		lock.readLock().lock();
		try {
			new ReadIterator<T>(list.iterator()).iterate(f);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public T findOne(Func1<T, Boolean> f) {
		lock.readLock().lock();
		try {
			for (T t : list) {
				if (f.call(t))
					return t;
			}
		} finally {
			lock.readLock().unlock();
		}
		return null;
	}
	
	public void iterateAndWrite(Action2<T, WriteIterator<T>> f) {
		lock.writeLock().lock();
		try {
			new WriteIterator<T>(list.listIterator()).iterate(f);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public int size() {
		lock.readLock().lock();
		int ret = list.size();
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public boolean isEmpty() {
		lock.readLock().lock();
		boolean ret = list.isEmpty();
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public boolean contains(Object o) {
		lock.readLock().lock();
		boolean ret = list.contains(o);
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		lock.readLock().lock();
		Object[] ret = list.toArray();
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public <R> R[] toArray(R[] a) {
		lock.readLock().lock();
		R[] ret = list.toArray(a);
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public boolean add(T e) {
		lock.writeLock().lock();
		boolean ret = list.add(e);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public boolean remove(Object o) {
		lock.writeLock().lock();
		boolean ret = list.remove(o);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		lock.readLock().lock();
		boolean ret = list.containsAll(c);
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		lock.writeLock().lock();
		boolean ret = list.addAll(c);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		lock.writeLock().lock();
		boolean ret = list.addAll(index, c);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		lock.writeLock().lock();
		boolean ret = list.removeAll(c);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		lock.writeLock().lock();
		boolean ret = list.retainAll(c);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public void clear() {
		lock.writeLock().lock();
		list.clear();
		lock.writeLock().unlock();
	}

	@Override
	public T get(int index) {
		lock.readLock().lock();
		T ret = list.get(index);
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public T set(int index, T element) {
		lock.writeLock().lock();
		T ret = list.set(index, element);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public void add(int index, T element) {
		lock.writeLock().lock();
		list.add(index, element);
		lock.writeLock().unlock();
	}

	@Override
	public T remove(int index) {
		lock.writeLock().lock();
		T ret = list.remove(index);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public int indexOf(Object o) {
		lock.readLock().lock();
		int ret = list.indexOf(o);
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public int lastIndexOf(Object o) {
		lock.readLock().lock();
		int ret = list.lastIndexOf(o);
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ReadWriteList<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
	public static class ReadIterator<T> {
		protected final Iterator<T> iterator;
		protected boolean shouldStop = false;
		
		private ReadIterator(Iterator<T> iterator) {
			this.iterator = iterator;
		}
		
		private void iterate(Action2<T, ReadIterator<T>> f) {
			while (!shouldStop && iterator.hasNext()) {
				f.call(iterator.next(), this);
			}
		}
		
		public void stop() {
			shouldStop = true;
		}
	}
	
	public static class WriteIterator<T> extends ReadIterator<T> {
		private final ListIterator<T> listIterator;
		
		private WriteIterator(ListIterator<T> iterator) {
			super(iterator);
			listIterator = iterator;
		}
		
		private void iterate(Action2<T, WriteIterator<T>> f) {
			while (!shouldStop && iterator.hasNext()) {
				f.call(iterator.next(), this);
			}
		}
		
		public void add(T e) {
			listIterator.add(e);
		}
		
		public void remove() {
			listIterator.remove();
		}
		
		public void set(T e) {
			listIterator.set(e);
		}
	}
}