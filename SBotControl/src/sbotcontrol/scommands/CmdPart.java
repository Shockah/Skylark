package sbotcontrol.scommands;

import sbotcontrol.Plugin;
import scommands.CommandStack;
import scommands.TextCommand;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdPart extends TextCommand {
	public CmdPart(Plugin plugin) {
		super(plugin, "part");
	}
	
	public String call(GenericUserMessageEvent e, String input, CommandStack stack) {
		if (!Plugin.pluginIdent.userHasPermission(e, plugin, "Admin.Part")) return null;
		e.<Bot>getBot().manager.partChannel(input);
		return null;
	}
}