package scommands;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import shocky3.Shocky;

public class CmdDie extends Command {
	public CmdDie(Plugin plugin) {
		super(plugin, "die");
	}
	
	public void call(Shocky botApp, MessageEvent<PircBotX> e, String trigger, String args) {
		if (!Plugin.pluginIdent.userHasPermission(e, "Shocky.SCommands.Die")) return;
		botApp.stop();
	}
}