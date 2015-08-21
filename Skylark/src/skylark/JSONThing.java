package skylark;

import pl.shockah.json.JSONList;
import pl.shockah.json.JSONObject;

public final class JSONThing {
	protected final JSONObject object;
	protected final JSONList<?> list;
	
	public JSONThing(JSONObject object) {
		this.object = object;
		list = null;
	}
	
	public JSONThing(JSONList<?> list) {
		this.list = list;
		object = null;
	}
	
	public JSONObject object() {
		return object;
	}
	
	public JSONList<?> list() {
		return list;
	}
	
	public JSONObject asObject() {
		return object == null ? JSONObject.make("args", list) : object;
	}
	
	public JSONList<?> asList() {
		if (list == null) {
			if (object.size() == 1 && object.contains("args"))
				return object.getList("args");
			return JSONList.make(object);
		}
		return list;
	}
}