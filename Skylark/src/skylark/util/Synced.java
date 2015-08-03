package skylark.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.shockah.Lists;
import pl.shockah.func.Action1;
import pl.shockah.func.Action2;

public final class Synced {
	public static <T> List<T> list(List<T> list) {
		return Collections.synchronizedList(list);
	}
	
	public static <T> List<T> list() {
		return list(new ArrayList<T>());
	}
	
	public static <K, V> Map<K, V> map(Map<K, V> map) {
		return Collections.synchronizedMap(map);
	}
	
	public static <K, V> Map<K, V> map() {
		return map(new HashMap<K, V>());
	}
	
	public static <T> void forEach(List<T> list, Action1<T> func) {
		synchronized (list) {
			for (T t : list)
				func.f(t);
		}
	}
	
	public static <K, V> void forEach(Map<K, V> map, Action2<K, V> func) {
		synchronized (map) {
			for (Map.Entry<K, V> entry : map.entrySet())
				func.f(entry.getKey(), entry.getValue());
		}
	}
	
	public static <K, V> void forEach(Map<K, V> map, Action1<V> func) {
		synchronized (map) {
			for (Map.Entry<K, V> entry : map.entrySet())
				func.f(entry.getValue());
		}
	}
	
	public static <T> void iterate(List<T> list, Action2<T, Lists.IteratorHandler<T>> f) {
		synchronized (list) {
			Lists.iterate(list, f);
		}
	}
	
	private Synced() { }
}