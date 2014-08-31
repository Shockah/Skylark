package sfactoids;

import java.util.regex.Pattern;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.json.JSONObject;
import shocky3.Shocky;

public abstract class FactoidParser {
	public static final Pattern
		REGEX_PARSER = Pattern.compile("^\\{(.+?)\\}(.*)$");
	
	public final String id;
	
	public FactoidParser(String id) {
		this.id = id;
	}
	
	public abstract String parse(JSONObject j, Shocky botApp, MessageEvent<PircBotX> e, String trigger, String code, String message);
}