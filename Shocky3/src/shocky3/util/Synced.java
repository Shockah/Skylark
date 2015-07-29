package shocky3.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.shockah.func.Action1;

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
	
	private Synced() { }
}