package scommands;

import shocky3.pircbotx.event.GenericUserMessageEvent;

public abstract class CommandProvider {
	public static final double
		LOW_PRIORITY = 0d,
		MEDIUM_PRIORITY = 500d,
		HIGH_PRIORITY = 1000d;
	
	public final shocky3.Plugin plugin;
	public final double priority;
	
	public CommandProvider(shocky3.Plugin plugin, double priority) {
		this.plugin = plugin;
		this.priority = priority;
	}
	
	public abstract CommandMatch provide(GenericUserMessageEvent e, String name, String input);
}