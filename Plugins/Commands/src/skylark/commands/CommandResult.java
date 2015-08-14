package skylark.commands;

import pl.shockah.json.JSONList;
import pl.shockah.json.JSONObject;
import skylark.JSONThing;

public final class CommandResult {
	public final JSONThing json;
	public final String text;
	
	public CommandResult(JSONObject j, String text) {
		json = new JSONThing(j);
		this.text = text;
	}
	
	public CommandResult(JSONList<?> j, String text) {
		json = new JSONThing(j);
		this.text = text;
	}
}