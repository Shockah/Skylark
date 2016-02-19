package skylark.commands;

import me.shockah.skylark.event.GenericUserMessageEvent;
import pl.shockah.json.JSONObject;

public class SimpleCommandInputParser extends CommandInputParser {
	public JSONObject parse(GenericUserMessageEvent e, String arg) {
		return JSONObject.make("arg", arg);
	}
}