package io.shockah.skylark.util;

import io.shockah.json.JSONList;
import io.shockah.json.JSONObject;
import io.shockah.skylark.func.Action1;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.bson.types.ObjectId;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public final class JSON {
	public static DBObject toDBObject(JSONObject j) {
		BasicDBObject bdbo = new BasicDBObject();
		for (String key : j.keySet()) {
			Object o = j.get(key);
			if (o instanceof JSONObject)
				o = toDBObject((JSONObject)o);
			if (o instanceof JSONList<?>)
				o = toDBList((JSONList<?>)o);
			bdbo.append(key, o);
		}
		return bdbo;
	}
	
	@SuppressWarnings("unchecked")
	public static List<?> toDBList(JSONList<?> j) {
		ListIterator<Object> lit = (ListIterator<Object>)j.listIterator();
		while (lit.hasNext()) {
			Object o = lit.next();
			if (o instanceof JSONObject)
				lit.set(toDBObject((JSONObject)o));
			else if (o instanceof JSONList<?>)
				lit.set(toDBList((JSONList<?>)o));
		}
		return j;
	}
	
	public static JSONObject fromDBObject(DBObject dbo) {
		if (dbo == null)
			return null;
		JSONObject j = new JSONObject();
		for (String key : dbo.keySet()) {
			Object o = dbo.get(key);
			if (o instanceof ObjectId) {
				continue;
			} else if (o instanceof List) {
				o = fromDBList((List<?>)o);
			} else if (o instanceof DBObject) {
				JSONObject jo = fromDBObject((DBObject)o);
				if (jo.containsKey("0")) {
					JSONList<Object> jl = new JSONList<>();
					int count = jo.size();
					for (int i = 0; i < count; i++)
						jl.add(jo.get("" + i));
					o = jl;
				} else {
					o = jo;
				}
			}
			j.put(key, o);
		}
		return j;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONList<?> fromDBList(List<?> list) {
		ListIterator<Object> lit = (ListIterator<Object>)list.listIterator();
		while (lit.hasNext()) {
			Object o = lit.next();
			if (o instanceof DBObject)
				lit.set(fromDBObject((DBObject)o));
			else if (o instanceof List<?>)
				lit.set(fromDBList((List<?>)o));
		}
		return fromList(list);
	}
	
	public static JSONList<?> fromList(List<?> list) {
		return JSONList.of(list.toArray(new Object[0]));
	}
	
	public static List<DBObject> collect(DBCursor dbc) {
		List<DBObject> list = new ArrayList<>();
		while (dbc.hasNext())
			list.add(dbc.next());
		return list;
	}
	
	public static List<JSONObject> collectJSON(DBCursor dbc) {
		List<JSONObject> list = new ArrayList<>();
		while (dbc.hasNext())
			list.add(fromDBObject(dbc.next()));
		return list;
	}
	
	public static void forEach(DBCursor dbc, Action1<DBObject> f) {
		while (dbc.hasNext())
			f.call(dbc.next());
	}
	
	public static void forEachJSONObject(DBCursor dbc, Action1<JSONObject> f) {
		while (dbc.hasNext())
			f.call(fromDBObject(dbc.next()));
	}
	
	private JSON() {
		throw new UnsupportedOperationException();
	}
}