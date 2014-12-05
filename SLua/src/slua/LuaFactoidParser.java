package slua;

import pl.shockah.json.JSONObject;
import scommands.CommandResult;
import sfactoids.FactoidParser;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class LuaFactoidParser extends FactoidParser {
	public final Plugin plugin;
	
	public LuaFactoidParser(Plugin plugin) {
		super("lua");
		this.plugin = plugin;
	}
	
	public int resultType() {
		return TYPE_STRING_CODE;
	}
	
	public String parseStringCode(JSONObject j, GenericUserMessageEvent e, String trigger, String args, String code, CommandResult result) {
		return plugin.lua.parse(e, trigger, args, code, result);
	}
}