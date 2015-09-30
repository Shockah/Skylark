package pl.shockah.json;

public class JSONLookup {
	protected final Object j;
	
	public JSONLookup(JSONObject j) {
		this.j = j;
	}
	public JSONLookup(JSONList<?> j) {
		this.j = j;
	}
	
	public Object get(Object... lookup) {
		return get(false, lookup);
	}
	public Object get(boolean returnNullOnProblem, Object... lookup) {
		if (lookup == null || lookup.equals(""))
			return j;
		Object current = j;
		int level = 0;
		for (Object s : lookup) {
			if (current instanceof JSONObject) {
				String ss = s == null ? "null" : s.toString();
				JSONObject j2 = (JSONObject)current;
				if (j2.contains(ss)) {
					current = j2.get(ss);
				} else {
					if (returnNullOnProblem)
						return null;
					throwCantTraverseException(lookup, level);
				}
			} else if (current instanceof JSONList<?>) {
				boolean has = false;
				int index = 0;
				if (s instanceof Number) {
					has = true;
					index = ((Number)s).intValue();
				} else if (s == null) {
					if (returnNullOnProblem)
						return null;
					throwCantTraverseException(lookup, level);
				} else {
					has = true;
					try {
						index = Integer.parseInt(s.toString());
					} catch (Exception e) {
						if (returnNullOnProblem)
							return null;
						throwCantTraverseException(lookup, level);
					}
				}
				if (has) {
					JSONList<?> j2 = (JSONList<?>)current;
					if (index < j2.size()) {
						current = j2.get(index);
					} else {
						if (returnNullOnProblem)
							return null;
						throwCantTraverseException(lookup, level);
					}
				}
			} else {
				if (returnNullOnProblem)
					return null;
				throwCantTraverseException(lookup, level);
			}
			level++;
		}
		return current;
	}
	private String buildCantTraverseExceptionText(Object[] spl, int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append(".");
			sb.append(spl[i]);
		}
		return sb.toString().substring(1);
	}
	private void throwCantTraverseException(Object[] spl, int level) {
		String path = buildCantTraverseExceptionText(spl, level);
		throw new IllegalArgumentException(String.format("Can't traverse further in the JSON tree than '%s'.", path));
	}
	
	public boolean isNull(String lookup) {
		return get(lookup) == null;
	}
	
	public String getString(String key, String def) {
		String o = getString(true, key);
		return o == null ? def : o;
	}
	public String getString(String key) { return getString(false, key); }
	public String getString(boolean returnNullOnProblem, String key) {
		Object o = get(key, returnNullOnProblem);
		if (o == null)
			return null;
		if (o instanceof String)
			return (String)o;
		throw new IllegalArgumentException("Key '" + key + "' doesn't hold a string.");
	}
	
	public boolean getBoolean(String key, boolean def) {
		Boolean o = getBoolean(true, key);
		return o == null ? def : o;
	}
	public boolean getBoolean(String key) { return getBoolean(false, key); }
	public Boolean getBoolean(boolean returnNullOnProblem, String key) {
		Object o = get(key);
		if (o instanceof Boolean)
			return (Boolean)o;
		throw new IllegalArgumentException("Key '" + key + "' doesn't hold a boolean.");
	}
	
	public Number getNumber(String key, Number def) {
		Number o = getNumber(true, key);
		return o == null ? def : o;
	}
	public Number getNumber(String key) { return getNumber(false, key); }
	public Number getNumber(boolean returnNullOnProblem, String key) {
		Object o = get(key);
		if (o instanceof Number)
			return (Number)o;
		throw new IllegalArgumentException("Key '" + key + "' doesn't hold a number.");
	}
	
	public int getInt(String key, int def) { return getNumber(key, def).intValue(); }
	public int getInt(String key) { return getInt(false, key); }
	public int getInt(boolean returnNullOnProblem, String key) { return getNumber(returnNullOnProblem, key).intValue(); }
	public long getLong(String key, long def) { return getNumber(key, def).longValue(); }
	public long getLong(String key) { return getLong(false, key); }
	public long getLong(boolean returnNullOnProblem, String key) { return getNumber(returnNullOnProblem, key).longValue(); }
	public float getFloat(String key, float def) { return getNumber(key, def).floatValue(); }
	public float getFloat(String key) { return getFloat(false, key); }
	public float getFloat(boolean returnNullOnProblem, String key) { return getNumber(returnNullOnProblem, key).floatValue(); }
	public double getDouble(String key, double def) { return getNumber(key, def).doubleValue(); }
	public double getDouble(String key) { return getDouble(false, key); }
	public double getDouble(boolean returnNullOnProblem, String key) { return getNumber(returnNullOnProblem, key).doubleValue(); }
	
	public JSONObject getObjectOrNew(String key) {
		JSONObject o = getObject(true, key);
		return o == null ? new JSONObject() : o;
	}
	public JSONObject getObject(String key, JSONObject def) {
		JSONObject o = getObject(true, key);
		return o == null ? def : o;
	}
	public JSONObject getObject(String key) { return getObject(false, key); }
	public JSONObject getObject(boolean returnNullOnProblem, String key) {
		Object o = get(key, returnNullOnProblem);
		if (o == null)
			return null;
		if (o instanceof JSONObject)
			return (JSONObject)o;
		throw new IllegalArgumentException("Key '" + key + "' doesn't hold a JSONObject.");
	}
	
	public JSONList<?> getListOrNew(String key) {
		JSONList<?> o = getList(true, key);
		return o == null ? new JSONList<>() : o;
	}
	public JSONList<?> getList(String key, JSONList<?> def) {
		JSONList<?> o = getList(true, key);
		return o == null ? def : o;
	}
	public JSONList<?> getList(String key) { return getList(false, key); }
	public JSONList<?> getList(boolean returnNullOnProblem, String key) {
		Object o = get(key, returnNullOnProblem);
		if (o == null)
			return null;
		if (o instanceof JSONList<?>)
			return (JSONList<?>)o;
		throw new IllegalArgumentException("Key '" + key + "' doesn't hold a JSONList.");
	}
	
	public JSONObject getMyObject() {
		if (!(j instanceof JSONObject))
			throw new IllegalStateException("JSONLookup doesn't hold a JSONObject.");
		return (JSONObject)j;
	}
	public JSONList<?> getMyList() {
		if (!(j instanceof JSONList<?>))
			throw new IllegalStateException("JSONLookup doesn't hold a JSONList.");
		return (JSONList<?>)j;
	}
}