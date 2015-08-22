package skylark.commands;

import java.util.ArrayList;
import java.util.List;
import pl.shockah.json.JSONObject;
import skylark.pircbotx.event.GenericUserMessageEvent;

public final class CommandStack {
	public final GenericUserMessageEvent event;
	protected final List<Command> entries = new ArrayList<>();
	
	public CommandStack(GenericUserMessageEvent event) {
		this.event = event;
	}
	
	public CommandOutput execute(Command command, String args) {
		entries.add(command);
		JSONObject json = command.parser.parse(event, args);
		return command.execute(this, event, json);
	}
	
	public CommandOutput execute(Command command, JSONObject json) {
		entries.add(command);
		return command.execute(this, event, json);
	}
}