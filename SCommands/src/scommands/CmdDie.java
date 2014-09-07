package scommands;

import org.pircbotx.PircBotX;
import shocky3.Shocky;
import shocky3.pircbotx.NullableChannelUserEvent;

public class CmdDie extends Command {
	public CmdDie(Plugin plugin) {
		super(plugin, "die");
	}
	
	public void call(Shocky botApp, NullableChannelUserEvent<PircBotX> e, String trigger, String args) {
		if (!Plugin.pluginIdent.userHasPermission(e, "Shocky.SCommands.Die")) return;
		botApp.stop();
	}
}