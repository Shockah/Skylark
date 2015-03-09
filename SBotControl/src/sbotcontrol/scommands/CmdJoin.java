package sbotcontrol.scommands;

import sbotcontrol.Plugin;
import scommands.CommandStack;
import scommands.TextCommand;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdJoin extends TextCommand {
	public CmdJoin(Plugin plugin) {
		super(plugin, "join");
	}
	
	public String call(GenericUserMessageEvent e, String input, CommandStack stack) {
		if (!Plugin.pluginIdent.userHasPermission(e, plugin, "Admin.Join")) return null;
		e.<Bot>getBot().manager.joinChannel(input);
		return null;
	}
}