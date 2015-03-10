package slua;

import scommands.Command;
import scommands.CommandStack;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdLua extends Command {
	public final Plugin pluginLua;
	
	public CmdLua(Plugin plugin) {
		super(plugin, "lua");
		this.pluginLua = plugin;
	}
	
	public String call(GenericUserMessageEvent e, String input, CommandStack stack) {
		return pluginLua.lua.parse(e, "", input, stack);
	}
}