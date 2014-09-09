package scommands;

import shocky3.Shocky;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.GenericUserMessageEvent;

public class CmdDie extends Command {
	public CmdDie(Plugin plugin) {
		super(plugin, "die");
	}
	
	public void call(Shocky botApp, GenericUserMessageEvent<Bot> e, String trigger, String args) {
		if (!Plugin.pluginIdent.userHasPermission(e, "Shocky.SCommands.Die")) return;
		botApp.stop();
	}
}