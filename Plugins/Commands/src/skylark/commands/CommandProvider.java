package skylark.commands;

import skylark.pircbotx.event.GenericUserMessageEvent;

public abstract class CommandProvider {
	public final skylark.Plugin plugin;
	
	public CommandProvider(skylark.Plugin plugin) {
		this.plugin = plugin;
	}
	
	public abstract Command provide(GenericUserMessageEvent e, String name);
}