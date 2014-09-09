package sfactoids;

import java.util.regex.Pattern;
import pl.shockah.json.JSONObject;
import scommands.ICommand;
import shocky3.Shocky;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public abstract class FactoidParser {
	public static final int
		TYPE_STRING_CODE = 1,
		TYPE_ICOMMAND = 2;
	public static final Pattern
		REGEX_PARSER = Pattern.compile("^\\{(.+?)\\}(.*)$");
	
	public final String id;
	
	public FactoidParser(String id) {
		this.id = id;
	}
	
	public abstract int resultType();
	public String parseStringCode(JSONObject j, Shocky botApp, GenericUserMessageEvent<Bot> e, String trigger, String args, String code) {
		return code;
	}
	public ICommand parseICommand(JSONObject j, Shocky botApp, GenericUserMessageEvent<Bot> e, String trigger, String args, String code) {
		return null;
	}
}