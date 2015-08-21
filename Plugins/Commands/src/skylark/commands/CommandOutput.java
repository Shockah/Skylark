package skylark.commands;

import pl.shockah.json.JSONList;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONPrinter;
import skylark.JSONThing;

public final class CommandOutput {
	public final JSONThing json;
	public final String text;
	
	public CommandOutput(JSONObject json) {
		this(new JSONThing(json));
	}
	
	public CommandOutput(JSONList<?> json) {
		this(new JSONThing(json));
	}
	
	public CommandOutput(JSONThing json) {
		this.json = json;
		JSONPrinter printer = new JSONPrinter();
		if (json.object() == null)
			text = printer.print(json.list());
		else
			text = printer.print(json.object());
	}
	
	public CommandOutput(String text) {
		this(JSONObject.make("arg", text), text);
	}
	
	public CommandOutput(JSONObject json, String text) {
		this(new JSONThing(json), text);
	}
	
	public CommandOutput(JSONList<?> json, String text) {
		this(new JSONThing(json), text);
	}
	
	public CommandOutput(JSONThing json, String text) {
		this.json = json;
		this.text = text;
	}
}