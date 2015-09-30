package pl.shockah;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BiMap<K,V> implements Map<K,V> {
	public static <A> BiMap<A, A> create(Class<? extends Map<A, A>> cls) {
		return create(cls, cls);
	}
	public static <A, B> BiMap<A, B> create(Class<? extends Map<A, B>> cls1, Class<? extends Map<B, A>> cls2) {
		try {
			return new BiMap<>(cls1.newInstance(), cls2.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected final Map<K, V> map1;
	protected final Map<V, K> map2;
	
	public BiMap() {
		this(new HashMap<K, V>(), new HashMap<V, K>());
	}
	
	public BiMap(Map<K, V> map) {
		this.map1 = map;
		map2 = new HashMap<V, K>();
		for (Map.Entry<K, V> entry : map.entrySet())
			map2.put(entry.getValue(), entry.getKey());
	}
	
	protected BiMap(Map<K, V> map1, Map<V, K> map2) {
		this.map1 = map1;
		this.map2 = map2;
	}
	
	public void clear() {
		map1.clear();
		map2.clear();
	}

	public boolean containsKey(Object key) {
		return map1.containsKey(key);
	}
	public boolean containsValue(Object value) {
		return map2.containsKey(value);
	}

	public Set<Entry<K, V>> entrySet() {
		return map1.entrySet();
	}
	public Set<Entry<V, K>> entrySet2() {
		return map2.entrySet();
	}

	public V get(Object key) {
		return map1.get(key);
	}
	public K get2(Object key) {
		return map2.get(key);
	}

	public boolean isEmpty() {
		return map1.isEmpty();
	}

	public Set<K> keySet() {
		return map1.keySet();
	}
	public Set<V> keySet2() {
		return map2.keySet();
	}

	public V put(K key, V value) {
		map2.put(value,key);
		return map1.put(key,value);
	}
	public K put2(V key, K value) {
		map1.put(value,key);
		return map2.put(key,value);
	}

	public void putAll(Map<? extends K,? extends V> m) {
		for (Entry<? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}
	public void putAll2(Map<? extends V,? extends K> m) {
		for (Entry<? extends V, ? extends K> e : m.entrySet())
			put2(e.getKey(),e.getValue());
	}

	public V remove(Object key) {
		V ret = map1.remove(key);
		if (ret != null)
			map2.remove(ret);
		return ret;
	}
	public K remove2(Object key) {
		K ret = map2.remove(key);
		if (ret != null)
			map1.remove(ret);
		return ret;
	}

	public int size() {
		return map1.size();
	}

	public Collection<V> values() {
		return map1.values();
	}
	public Collection<K> values2() {
		return map2.values();
	}
}