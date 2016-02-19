package skylark.commands;

import me.shockah.skylark.event.GenericUserMessageEvent;
import pl.shockah.json.JSONObject;

public abstract class CommandInputParser {
	protected CommandInputParser() { }
	
	public abstract JSONObject parse(GenericUserMessageEvent e, String arg);
}