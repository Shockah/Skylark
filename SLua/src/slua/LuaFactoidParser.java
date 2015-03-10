package slua;

import pl.shockah.json.JSONObject;
import scommands.CommandStack;
import sfactoids.FactoidParser;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class LuaFactoidParser extends FactoidParser {
	public final Plugin plugin;
	
	public LuaFactoidParser(Plugin plugin) {
		super("lua");
		this.plugin = plugin;
	}
	
	public String parse(JSONObject j, GenericUserMessageEvent e, String input, String code, CommandStack stack) {
		return plugin.lua.parse(e, input, code, stack);
	}
}