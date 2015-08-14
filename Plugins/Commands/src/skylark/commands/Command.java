package skylark.commands;

import skylark.JSONThing;
import skylark.pircbotx.event.GenericUserMessageEvent;

public abstract class Command {
	public abstract CommandResult execute(GenericUserMessageEvent e, JSONThing json);
}