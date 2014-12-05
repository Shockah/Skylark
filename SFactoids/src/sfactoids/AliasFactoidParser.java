package sfactoids;

import pl.shockah.json.JSONObject;
import scommands.CommandResult;
import scommands.ICommand;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class AliasFactoidParser extends FactoidParser {
	public AliasFactoidParser() {
		super("alias");
	}
	
	public int resultType() {
		return TYPE_ICOMMAND;
	}

	public ICommand parseICommand(JSONObject j, GenericUserMessageEvent e, String trigger, String args, String code, CommandResult result) {
		String[] spl = code.split("\\s");
		args = spl.length == 1 ? "" : code.substring(spl.length + 1);
		
		ICommand cmd = Plugin.pluginCmd.findCommand(e, spl[0], args, result);
		return cmd;
	}
}