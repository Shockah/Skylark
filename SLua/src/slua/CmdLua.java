package slua;

import scommands.old.Command;
import scommands.old.CommandResult;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdLua extends Command {
	public final Plugin pluginLua;
	
	public CmdLua(Plugin plugin) {
		super(plugin, "lua");
		this.pluginLua = plugin;
	}
	
	public void call(GenericUserMessageEvent e, String trigger, String args, CommandResult result) {
		result.add(pluginLua.lua.parse(e, trigger, "", args, result));
	}
}