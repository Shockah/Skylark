package shocky3;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.bson.types.ObjectId;
import pl.shockah.json.JSONList;
import pl.shockah.json.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public final class JSONUtil {
	public static DBObject toDBObject(JSONObject j) {
		BasicDBObject bdbo = new BasicDBObject();
		for (String key : j.keys()) {
			Object o = j.get(key);
			if (o instanceof JSONObject) o = toDBObject((JSONObject)o);
			bdbo.append(key, o);
		}
		return bdbo;
	}
	public static JSONObject fromDBObject(DBObject dbo) {
		JSONObject j = new JSONObject();
		for (String key : dbo.keySet()) {
			Object o = dbo.get(key);
			if (o instanceof ObjectId) continue;
			else if (o instanceof List) o = fromList((List<?>)o);
			else if (o instanceof DBObject) {
				JSONObject jo = fromDBObject((DBObject)o);
				if (jo.contains("0")) {
					JSONList<Object> jl = new JSONList<>();
					int count = jo.size();
					for (int i = 0; i < count; i++) {
						jl.add(jo.get("" + i));
					}
					o = jl;
				} else {
					o = jo;
				}
			}
			j.put(key, o);
		}
		return j;
	}
	
	public static org.json.JSONObject toOrgObject(JSONObject j) {
		org.json.JSONObject ret = new org.json.JSONObject();
		try {
			for (String key : j.keys()) {
				Object o = j.get(key);
				if (o instanceof JSONObject) o = toOrgObject((JSONObject)o);
				else if (o instanceof JSONList<?>) o = toOrgArray((JSONList<?>)o);
				ret.put(key, o);
			}
		} catch (Exception e) {e.printStackTrace();}
		return ret;
	}
	public static org.json.JSONArray toOrgArray(JSONList<?> j) {
		org.json.JSONArray ret = new org.json.JSONArray();
		try {
			for (Object o : j) {
				Object o2 = o;
				if (o2 instanceof JSONObject) o2 = toOrgObject((JSONObject)o);
				else if (o2 instanceof JSONList<?>) o2 = toOrgArray((JSONList<?>)o);
				ret.put(o2);
			}
		} catch (Exception e) {e.printStackTrace();}
		return ret;
	}
	
	public static JSONObject fromOrgObject(org.json.JSONObject j) {
		JSONObject ret = new JSONObject();
		try {
			Iterator<?> it = j.keys();
			while (it.hasNext()) {
				String key = (String)it.next();
				Object o = j.get(key);
				if (o instanceof org.json.JSONObject) o = fromOrgObject((org.json.JSONObject)o);
				else if (o instanceof org.json.JSONArray) o = fromOrgArray((org.json.JSONArray)o);
				ret.put(key, o);
			}
		} catch (Exception e) {e.printStackTrace();}
		return ret;
	}
	public static JSONList<?> fromOrgArray(org.json.JSONArray j) {
		JSONList<Object> ret = new JSONList<Object>();
		try {
			for (int i = 0; i < j.length(); i++) {
				Object o = j.get(i);
				if (o instanceof org.json.JSONObject) o = fromOrgObject((org.json.JSONObject)o);
				else if (o instanceof org.json.JSONArray) o = fromOrgArray((org.json.JSONArray)o);
				ret.add(o);
			}
		} catch (Exception e) {e.printStackTrace();}
		return ret;
	}
	
	public static JSONList<?> fromList(List<?> list) {
		return JSONList.make(list.toArray(new Object[0]));
	}
	
	public static List<DBObject> all(DBCursor dbc) {
		List<DBObject> list = new LinkedList<>();
		while (dbc.hasNext()) list.add(dbc.next());
		return list;
	}
	
	private JSONUtil() {}
}