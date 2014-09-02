package shocky3;

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