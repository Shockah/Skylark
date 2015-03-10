package sphp;

import pl.shockah.json.JSONObject;
import scommands.CommandStack;
import sfactoids.FactoidParser;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class PHPFactoidParser extends FactoidParser {
	public final Plugin plugin;
	
	public PHPFactoidParser(Plugin plugin) {
		super("php");
		this.plugin = plugin;
	}
	
	public String parse(JSONObject j, GenericUserMessageEvent e, String input, String code, CommandStack stack) {
		return plugin.php.parse(e, input, code, stack);
	}
}