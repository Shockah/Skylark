package sbotcontrol.scommands;

import sbotcontrol.Plugin;
import scommands.CommandStack;
import scommands.TextCommand;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdDie extends TextCommand {
	public CmdDie(Plugin plugin) {
		super(plugin, "die");
	}
	
	public String call(GenericUserMessageEvent e, String input, CommandStack stack) {
		if (!Plugin.pluginIdent.userHasPermission(e, plugin, "Admin.Die")) return null;
		e.<Bot>getBot().botApp.stop();
		return null;
	}
}