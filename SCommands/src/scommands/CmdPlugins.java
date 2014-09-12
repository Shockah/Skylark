package scommands;

import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdPlugins extends Command {
	public CmdPlugins(Plugin plugin) {
		super(plugin, "reload");
	}
	
	public void call(GenericUserMessageEvent<Bot> e, String trigger, String args) {
		if (!Plugin.pluginIdent.userHasPermission(e, plugin, "Admin.Plugin.Reload")) return;
		e.respond("Reloading...");
		e.getBot().botApp.pluginManager.reload();
		e.respond("Finished.");
	}
}