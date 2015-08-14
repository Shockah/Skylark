package skylark.commands;

import java.util.ArrayList;
import java.util.List;
import skylark.pircbotx.event.GenericUserMessageEvent;

public final class CommandStack {
	public final GenericUserMessageEvent event;
	protected final List<Entry> entries = new ArrayList<>();
	
	public CommandStack(GenericUserMessageEvent event) {
		this.event = event;
	}
	
	public static final class Entry {
		public final Command command;
		public final CommandResult result;
		
		public Entry(Command command, CommandResult result) {
			this.command = command;
			this.result = result;
		}
	}
}