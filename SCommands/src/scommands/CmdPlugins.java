package scommands;

import shocky3.Shocky;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdPlugins extends Command {
	public CmdPlugins(Plugin plugin) {
		super(plugin, "reload");
	}
	
	public void call(Shocky botApp, GenericUserMessageEvent<Bot> e, String trigger, String args) {
		if (!Plugin.pluginIdent.userHasPermission(e, "Shocky.SCommands.Plugins.Reload")) return;
		e.respond("Reloading...");
		botApp.pluginManager.reload();
		e.respond("Finished.");
	}
}