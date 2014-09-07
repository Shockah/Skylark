package sfactoids;

import org.pircbotx.PircBotX;
import pl.shockah.json.JSONObject;
import scommands.ICommand;
import shocky3.Shocky;
import shocky3.pircbotx.NullableChannelUserEvent;

public class AliasFactoidParser extends FactoidParser {
	public AliasFactoidParser() {
		super("alias");
	}
	
	public int resultType() {
		return TYPE_ICOMMAND;
	}

	public ICommand parseICommand(JSONObject j, Shocky botApp, NullableChannelUserEvent<PircBotX> e, String trigger, String args, String code) {
		String[] spl = code.split("\\s");
		args = spl.length == 1 ? "" : code.substring(spl.length + 1);
		
		scommands.Plugin pluginCmd = botApp.pluginManager.byInternalName("Shocky.SCommands");
		ICommand cmd = pluginCmd.findCommand(botApp, e, spl[0], args);
		return cmd;
	}
}