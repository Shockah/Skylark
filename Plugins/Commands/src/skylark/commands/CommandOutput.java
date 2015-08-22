package skylark.commands;

import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONPrinter;

public final class CommandOutput {
	public final JSONObject json;
	public final String text;
	
	public CommandOutput(JSONObject json) {
		this.json = json;
		text = new JSONPrinter().print(json);
	}
	
	public CommandOutput(String text) {
		this(JSONObject.make("arg", text), text);
	}
	
	public CommandOutput(JSONObject json, String text) {
		this.json = json;
		this.text = text;
	}
}