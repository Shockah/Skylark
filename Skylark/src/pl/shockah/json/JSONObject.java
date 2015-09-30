package pl.shockah.json;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JSONObject {
	public static JSONObject make(Object... os) {
		if (os.length % 2 != 0)
			throw new IllegalArgumentException();
		
		JSONObject j = new JSONObject();
		for (int i = 0; i < os.length; i += 2) {
			if (!(os[i] instanceof String))
				throw new IllegalArgumentException();
			j.put((String)os[i], os[i + 1]);
		}
		return j;
	}
	
	protected Map<String, Object> map = Collections.synchronizedMap(new LinkedHashMap<String, Object>());
	
	public JSONObject copy() {
		JSONObject ret = new JSONObject();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			Object v = entry.getValue();
			if (v instanceof JSONList<?>)
				ret.map.put(entry.getKey(), ((JSONList<?>)v).copy());
			else if (v instanceof JSONObject)
				ret.map.put(entry.getKey(), ((JSONObject)v).copy());
			else
				ret.map.put(entry.getKey(), v);
		}
		return ret;
	}
	
	public void clear() {
		map.clear();
	}
	
	public boolean contains(String key) {
		return map.containsKey(key);
	}
	public List<String> keys() {
		return new LinkedList<>(map.keySet());
	}
	public List<Map.Entry<String,Object>> entries() {
		return new LinkedList<>(map.entrySet());
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	public int size() {
		return map.size();
	}
	
	public boolean isNull(String key) {
		return map.containsKey(key) && map.get(key) == null;
	}
	public Object get(String key, Object def) { return contains(key) ? get(key) : def; }
	public Object get(String key) {
		if (!map.containsKey(key))
			throw new IllegalArgumentException("No key '" + key + "' exists.");
		return map.get(key);
	}
	public String getString(String key, String def) { return contains(key) ? getString(key) : def; }
	public String getString(String key) {
		Object o = get(key);
		if (o == null)
			return null;
		if (o instanceof String)
			return (String)o;
		throw new IllegalArgumentException("Key '" + key + "' doesn't hold a string.");
	}
	public boolean getBoolean(String key, boolean def) { return contains(key) ? getBoolean(key) : def; }
	public boolean getBoolean(String key) {
		Object o = get(key);
		if (o instanceof Boolean)
			return (Boolean)o;
		throw new IllegalArgumentException("Key '" + key + "' doesn't hold a boolean.");
	}
	public Number getNumber(String key, Number def) { return contains(key) ? getNumber(key) : def; }
	public Number getNumber(String key) {
		Object o = get(key);
		if (o instanceof Number)
			return (Number)o;
		throw new IllegalArgumentException("Key '" + key + "' doesn't hold a number.");
	}
	public int getInt(String key, int def) { return contains(key) ? getInt(key) : def; }
	public int getInt(String key) { return getNumber(key).intValue(); }
	public long getLong(String key, long def) { return contains(key) ? getLong(key) : def; }
	public long getLong(String key) { return getNumber(key).longValue(); }
	public float getFloat(String key, float def) { return contains(key) ? getFloat(key) : def; }
	public float getFloat(String key) { return getNumber(key).floatValue(); }
	public double getDouble(String key, double def) { return contains(key) ? getDouble(key) : def; }
	public double getDouble(String key) { return getNumber(key).doubleValue(); }
	
	public JSONObject getObjectOrNew(String key) { return contains(key) ? getObject(key) : new JSONObject(); }
	public JSONObject getObject(String key, JSONObject def) { return contains(key) ? getObject(key) : def; }
	public JSONObject getObject(String key) {
		Object o = get(key);
		if (o instanceof JSONObject)
			return (JSONObject)o;
		throw new IllegalArgumentException("Key '" + key + "' doesn't hold a JSONObject.");
	}
	public JSONList<?> getListOrNew(String key) { return contains(key) ? getList(key) : new JSONList<>(); }
	public JSONList<?> getList(String key, JSONList<?> def) { return contains(key) ? getList(key) : def; }
	public JSONList<?> getList(String key) {
		Object o = get(key);
		if (o instanceof JSONList<?>)
			return (JSONList<?>)o;
		throw new IllegalArgumentException("Key '" + key + "' doesn't hold a JSONList.");
	}
	
	public JSONObject putNewObject(String key) {
		JSONObject j = new JSONObject();
		put(key,j);
		return j;
	}
	public JSONList<?> putNewList(String key) {
		JSONList<?> j = new JSONList<>();
		put(key,j);
		return j;
	}
	public void put(String key, Object o) {
		if (!(o == null || o instanceof String || o instanceof Number || o instanceof Boolean || o instanceof JSONObject || o instanceof JSONList<?>))
			throw new IllegalArgumentException("Can't store this type of object.");
		map.put(key,o);
	}
	
	public Object remove(String key) {
		return map.remove(key);
	}
}