package skylark.commands;

import java.util.ArrayList;
import java.util.List;
import skylark.JSONThing;
import skylark.pircbotx.event.GenericUserMessageEvent;

public final class CommandStack {
	public final GenericUserMessageEvent event;
	protected final List<Command> entries = new ArrayList<>();
	
	public CommandStack(GenericUserMessageEvent event) {
		this.event = event;
	}
	
	public CommandResult execute(GenericUserMessageEvent e, Command command, String args) {
		entries.add(command);
		JSONThing json = command.parser.parse(e, args);
		return command.execute(this, e, json);
	}
	
	public CommandResult execute(GenericUserMessageEvent e, Command command, JSONThing json) {
		entries.add(command);
		return command.execute(this, e, json);
	}
}