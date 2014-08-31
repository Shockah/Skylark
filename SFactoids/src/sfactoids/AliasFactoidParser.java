package sfactoids;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.json.JSONObject;
import scommands.ICommand;
import shocky3.Shocky;

public class AliasFactoidParser extends FactoidParser {
	public AliasFactoidParser() {
		super("alias");
	}
	
	public int resultType() {
		return TYPE_ICOMMAND;
	}

	public ICommand parseICommand(JSONObject j, Shocky botApp, MessageEvent<PircBotX> e, String trigger, String args, String code) {
		String[] spl = code.split("\\s");
		args = spl.length == 1 ? "" : code.substring(spl.length + 1);
		
		scommands.Plugin pluginCmd = botApp.pluginManager.byInternalName("Shocky.SCommands");
		ICommand cmd = pluginCmd.findCommand(botApp, e, trigger, args);
		return cmd;
	}
}