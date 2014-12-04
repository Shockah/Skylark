package sbotcontrol.scommands;

import sbotcontrol.Plugin;
import scommands.Command;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdJoin extends Command {
	public CmdJoin(Plugin plugin) {
		super(plugin, "join");
	}
	
	public void call(GenericUserMessageEvent e, String trigger, String args) {
		if (!Plugin.pluginIdent.userHasPermission(e, plugin, "Admin.Join")) return;
		e.<Bot>getBot().manager.joinChannel(args);
	}
}