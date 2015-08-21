package skylark.commands;

import skylark.JSONThing;
import skylark.pircbotx.event.GenericUserMessageEvent;

public abstract class CommandInputParser {
	public abstract JSONThing parse(GenericUserMessageEvent e, String arg);
}