package sphp;

import scommands.Command;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdPHP extends Command {
	public final Plugin pluginPHP;
	
	public CmdPHP(Plugin plugin) {
		super(plugin, "php");
		this.pluginPHP = plugin;
	}
	
	public void call(GenericUserMessageEvent<Bot> e, String trigger, String args) {
		e.respond(pluginPHP.php.parse(e, trigger, "", args));
	}
}