package io.shockah.skylark.util;

import io.shockah.skylark.func.Action1;
import io.shockah.skylark.func.Action2;
import io.shockah.skylark.func.Action3;
import io.shockah.skylark.func.Func1;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteMap<K, V> implements Map<K, V> {
	protected final Map<K, V> map;
	protected final ReentrantReadWriteLock lock;
	
	public ReadWriteMap(Map<K, V> underlyingMap) {
		this(underlyingMap, true);
	}
	
	public ReadWriteMap(Map<K, V> underlyingMap, boolean fair) {
		map = underlyingMap;
		lock = new ReentrantReadWriteLock(fair);
	}
	
	public void readOperation(Action1<Map<K, V>> f) {
		lock.readLock().lock();
		try {
			f.call(Collections.unmodifiableMap(map));
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public <R> R readOperation(Func1<Map<K, V>, R> f) {
		lock.readLock().lock();
		try {
			return f.call(Collections.unmodifiableMap(map));
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public boolean tryReadOperation(long timeout, TimeUnit unit, Action1<Map<K, V>> f) throws InterruptedException {
		if (lock.readLock().tryLock(timeout, unit)) {
			try {
				f.call(Collections.unmodifiableMap(map));
			} finally {
				lock.readLock().unlock();
			}
			return true;
		}
		return false;
	}
	
	public void writeOperation(Action1<Map<K, V>> f) {
		lock.writeLock().lock();
		try {
			f.call(map);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public <R> R writeOperation(Func1<Map<K, V>, R> f) {
		lock.writeLock().lock();
		try {
			return f.call(map);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public boolean tryWriteOperation(long timeout, TimeUnit unit, Action1<Map<K, V>> f) throws InterruptedException {
		if (lock.writeLock().tryLock(timeout, unit)) {
			try {
				f.call(map);
			} finally {
				lock.writeLock().unlock();
			}
			return true;
		}
		return false;
	}
	
	public void iterate(Action2<K, V> f) {
		lock.readLock().lock();
		try {
			for (Map.Entry<K, V> entry : map.entrySet()) {
				f.call(entry.getKey(), entry.getValue());
			}
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public void iterateKeys(Action1<K> f) {
		lock.readLock().lock();
		try {
			for (Map.Entry<K, V> entry : map.entrySet()) {
				f.call(entry.getKey());
			}
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public void iterateValues(Action1<V> f) {
		lock.readLock().lock();
		try {
			for (Map.Entry<K, V> entry : map.entrySet()) {
				f.call(entry.getValue());
			}
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public void iterate(Action3<K, V, ReadIterator<K, V>> f) {
		lock.readLock().lock();
		try {
			new ReadIterator<K, V>(map.entrySet()).iterate(f);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public V findOne(Func1<V, Boolean> f) {
		lock.readLock().lock();
		try {
			for (V value : map.values()) {
				if (f.call(value))
					return value;
			}
		} finally {
			lock.readLock().unlock();
		}
		return null;
	}
	
	public void iterateAndWrite(Action3<K, V, WriteIterator<K, V>> f) {
		lock.writeLock().lock();
		try {
			new WriteIterator<K, V>(map.entrySet()).iterate(f);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public int size() {
		lock.readLock().lock();
		int ret = map.size();
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public boolean isEmpty() {
		lock.readLock().lock();
		boolean ret = map.isEmpty();
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public boolean containsKey(Object key) {
		lock.readLock().lock();
		boolean ret = map.containsKey(key);
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public boolean containsValue(Object value) {
		lock.readLock().lock();
		boolean ret = map.containsValue(value);
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public V get(Object key) {
		lock.readLock().lock();
		V ret = map.get(key);
		lock.readLock().unlock();
		return ret;
	}

	@Override
	public V put(K key, V value) {
		lock.writeLock().lock();
		V ret = map.put(key, value);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public V remove(Object key) {
		lock.writeLock().lock();
		V ret = map.remove(key);
		lock.writeLock().unlock();
		return ret;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		lock.writeLock().lock();
		map.putAll(m);
		lock.writeLock().unlock();
	}

	@Override
	public void clear() {
		lock.writeLock().lock();
		map.clear();
		lock.writeLock().unlock();
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}
	
	public static class ReadIterator<K, V> {
		protected final Set<Map.Entry<K, V>> set;
		protected boolean shouldStop = false;
		protected Map.Entry<K, V> currentEntry;
		
		private ReadIterator(Set<Map.Entry<K, V>> set) {
			this.set = set;
		}
		
		private void iterate(Action3<K, V, ReadIterator<K, V>> f) {
			for (Map.Entry<K, V> entry : set) {
				currentEntry = entry;
				f.call(entry.getKey(), entry.getValue(), this);
				if (shouldStop)
					break;
			}
		}
		
		public void stop() {
			shouldStop = true;
		}
	}
	
	public static class WriteIterator<K, V> extends ReadIterator<K, V> {
		private WriteIterator(Set<Map.Entry<K, V>> set) {
			super(set);
		}
		
		private void iterate(Action3<K, V, WriteIterator<K, V>> f) {
			for (Map.Entry<K, V> entry : set) {
				currentEntry = entry;
				f.call(entry.getKey(), entry.getValue(), this);
				if (shouldStop)
					break;
			}
		}
		
		public void set(V value) {
			currentEntry.setValue(value);
		}
	}
}