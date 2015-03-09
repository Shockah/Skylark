package scommands;

import shocky3.Shocky;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class DefaultCommandPattern extends CommandPattern {
	public DefaultCommandPattern(Plugin plugin) {
		super(plugin, MEDIUM_PRIORITY);
	}
	
	public CommandPatternMatch match(GenericUserMessageEvent e) {
		Shocky botApp = plugin.botApp;
		String msg = e.getMessage();
		String[] spl = botApp.settings.getStringForChannel(e.getChannel(), plugin, "characters").split(" ");
		for (String s : spl) {
			if (msg.startsWith(s)) {
				msg = msg.substring(s.length());
				String name = msg.split("\\s")[0].toLowerCase();
				String input = msg.equals(name) ? "" : msg.substring(name.length() + 1).trim();
				return new CommandPatternMatch(name, input);
			}
		}
		return null;
	}
}