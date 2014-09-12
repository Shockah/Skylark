package sbotcontrol.scommands;

import sbotcontrol.Plugin;
import scommands.Command;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdDie extends Command {
	public CmdDie(Plugin plugin) {
		super(plugin, "die");
	}
	
	public void call(GenericUserMessageEvent<Bot> e, String trigger, String args) {
		if (!Plugin.pluginIdent.userHasPermission(e, plugin, "Admin.Die")) return;
		e.getBot().botApp.stop();
	}
}