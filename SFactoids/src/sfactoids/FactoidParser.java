package sfactoids;

import java.util.regex.Pattern;
import pl.shockah.json.JSONObject;
import scommands.CommandStack;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public abstract class FactoidParser {
	public static final Pattern
		REGEX_PARSER = Pattern.compile("^\\{(.+?)\\}(.*)$");
	
	public final String id;
	
	public FactoidParser(String id) {
		this.id = id;
	}
	
	public abstract String parse(JSONObject j, GenericUserMessageEvent e, String input, String code, CommandStack stack);
}