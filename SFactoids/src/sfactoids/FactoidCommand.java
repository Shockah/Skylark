package sfactoids;

import java.util.regex.Matcher;
import pl.shockah.json.JSONObject;
import scommands.Command;
import scommands.CommandStack;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class FactoidCommand extends Command {
	public final Plugin factoidPlugin;
	public final JSONObject j;
	public final String source;
	
	public FactoidCommand(Plugin plugin, String name, JSONObject j, String source) {
		super(plugin, name, new String[0]);
		factoidPlugin = plugin;
		this.j = j;
		this.source = source;
	}
	
	public String call(GenericUserMessageEvent e, String input, CommandStack stack) {
		Matcher m = FactoidParser.REGEX_PARSER.matcher(source);
		if (m.find()) {
			String name = m.group(1);
			FactoidParser parser = factoidPlugin.findParserByID(name);
			if (parser != null) {
				String output = parser.parse(j, e, input, m.group(2), stack);
				return stack.call(new FactoidCommand(factoidPlugin, name, j, output), input);
			}
		}
		return source;
	}
}