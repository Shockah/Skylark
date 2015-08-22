package skylark.commands;

import pl.shockah.json.JSONObject;
import skylark.pircbotx.event.GenericUserMessageEvent;

public class SimpleCommandInputParser extends CommandInputParser {
	public JSONObject parse(GenericUserMessageEvent e, String arg) {
		return JSONObject.make("arg", arg);
	}
}