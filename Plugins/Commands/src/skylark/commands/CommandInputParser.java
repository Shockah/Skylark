package skylark.commands;

import pl.shockah.json.JSONObject;
import skylark.pircbotx.event.GenericUserMessageEvent;

public abstract class CommandInputParser {
	protected CommandInputParser() { }
	
	public abstract JSONObject parse(GenericUserMessageEvent e, String arg);
}