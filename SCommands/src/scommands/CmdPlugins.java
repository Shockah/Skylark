package scommands;

import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdPlugins extends Command {
	public CmdPlugins(Plugin plugin) {
		super(plugin, "reload");
	}
	
	public String call(GenericUserMessageEvent e, String trigger, String args, boolean chain) {
		if (!Plugin.pluginIdent.userHasPermission(e, plugin, "Admin.Plugin.Reload")) return "";
		if (!chain) e.respond("Reloading...");
		e.<Bot>getBot().botApp.pluginManager.reload();
		if (!chain) e.respond("Finished.");
		return "Finished";
	}
}