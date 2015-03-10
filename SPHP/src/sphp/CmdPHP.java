package sphp;

import scommands.Command;
import scommands.CommandStack;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdPHP extends Command {
	public final Plugin pluginPHP;
	
	public CmdPHP(Plugin plugin) {
		super(plugin, "php");
		this.pluginPHP = plugin;
	}
	
	public String call(GenericUserMessageEvent e, String input, CommandStack stack) {
		return pluginPHP.php.parse(e, "", input, stack);
	}
}