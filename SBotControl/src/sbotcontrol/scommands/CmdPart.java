package sbotcontrol.scommands;

import sbotcontrol.Plugin;
import scommands.Command;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdPart extends Command {
	public CmdPart(Plugin plugin) {
		super(plugin, "part");
	}
	
	public void call(GenericUserMessageEvent<Bot> e, String trigger, String args) {
		if (!Plugin.pluginIdent.userHasPermission(e, plugin, "Admin.Part")) return;
		e.getBot().manager.partChannel(args);
	}
}