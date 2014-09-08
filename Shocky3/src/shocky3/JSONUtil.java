package shocky3;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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
			if (o instanceof JSONList<?>) o = toDBList((JSONList<?>)o);
			bdbo.append(key, o);
		}
		return bdbo;
	}
	public static List<?> toDBList(JSONList<?> j) {
		ListIterator<Object> lit = (ListIterator<Object>)j.listIterator();
		while (lit.hasNext()) {
			Object o = lit.next();
			if (o instanceof JSONObject) lit.set(toDBObject((JSONObject)o));
			else if (o instanceof JSONList<?>) lit.set(toDBList((JSONList<?>)o));
		}
		return j;
	}
	public static JSONObject fromDBObject(DBObject dbo) {
		JSONObject j = new JSONObject();
		for (String key : dbo.keySet()) {
			Object o = dbo.get(key);
			if (o instanceof ObjectId) continue;
			else if (o instanceof List) o = fromDBList((List<?>)o);
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
	public static JSONList<?> fromDBList(List<?> list) {
		ListIterator<Object> lit = (ListIterator<Object>)list.listIterator();
		while (lit.hasNext()) {
			Object o = lit.next();
			if (o instanceof DBObject) lit.set(fromDBObject((DBObject)o));
			else if (o instanceof List<?>) lit.set(fromDBList((List<?>)o));
		}
		return fromList(list);
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