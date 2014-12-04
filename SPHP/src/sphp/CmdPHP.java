package sphp;

import scommands.Command;
import scommands.CommandResult;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdPHP extends Command {
	public final Plugin pluginPHP;
	
	public CmdPHP(Plugin plugin) {
		super(plugin, "php");
		this.pluginPHP = plugin;
	}
	
	public void call(GenericUserMessageEvent e, String trigger, String args, CommandResult result) {
		result.add(pluginPHP.php.parse(e, trigger, "", args));
	}
}