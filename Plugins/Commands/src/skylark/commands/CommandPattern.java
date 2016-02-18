package skylark.commands;

import skylark.old.pircbotx.event.GenericUserMessageEvent;

public abstract class CommandPattern {
	public final skylark.old.Plugin plugin;
	
	public CommandPattern(skylark.old.Plugin plugin) {
		this.plugin = plugin;
	}
	
	public abstract CommandMatch match(GenericUserMessageEvent e);
}