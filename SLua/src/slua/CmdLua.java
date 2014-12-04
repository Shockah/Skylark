package slua;

import scommands.Command;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdLua extends Command {
	public final Plugin pluginLua;
	
	public CmdLua(Plugin plugin) {
		super(plugin, "lua");
		this.pluginLua = plugin;
	}
	
	public String call(GenericUserMessageEvent e, String trigger, String args, boolean chain) {
		String _s = pluginLua.lua.parse(e, trigger, "", args);
		if (!chain) e.respond(_s);
		return _s;
	}
}