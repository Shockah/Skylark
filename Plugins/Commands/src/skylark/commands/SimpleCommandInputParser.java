package skylark.commands;

import pl.shockah.json.JSONObject;
import skylark.JSONThing;
import skylark.pircbotx.event.GenericUserMessageEvent;

public class SimpleCommandInputParser extends CommandInputParser {
	public JSONThing parse(GenericUserMessageEvent e, String arg) {
		return new JSONThing(JSONObject.make(
			"arg", arg
		));
	}
}