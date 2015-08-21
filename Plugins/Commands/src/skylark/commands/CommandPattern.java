package skylark.commands;

import skylark.pircbotx.event.GenericUserMessageEvent;

public abstract class CommandPattern {
	public final skylark.Plugin plugin;
	
	public CommandPattern(skylark.Plugin plugin) {
		this.plugin = plugin;
	}
	
	public abstract CommandMatch match(GenericUserMessageEvent e);
}