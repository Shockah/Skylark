package skylark.commands;

import skylark.pircbotx.event.GenericUserMessageEvent;

public abstract class CommandPattern {
	public final skylark.Plugin plugin;
	
	public CommandPattern(skylark.Plugin plugin) {
		this.plugin = plugin;
	}
	
	public abstract Call match(GenericUserMessageEvent e);
	
	public static class Call {
		public final String command;
		public final String args;
		
		public Call(String command, String args) {
			this.command = command;
			this.args = args;
		}
	}
}