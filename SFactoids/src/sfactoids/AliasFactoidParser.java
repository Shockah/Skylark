package sfactoids;

import pl.shockah.json.JSONObject;
import scommands.Command;
import scommands.CommandStack;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class AliasFactoidParser extends FactoidParser {
	public AliasFactoidParser() {
		super("alias");
	}
	
	public String parse(JSONObject j, GenericUserMessageEvent e, String input, String code, CommandStack stack) {
		String[] spl = code.split("\\s");
		code = spl.length == 1 ? "" : code.substring(spl.length + 1);
		
		Command command = Plugin.pluginCmd.patternManager.findCommand(e, spl[0]);
		if (command != null)
			return stack.call(command, code);
		return code;
	}
}