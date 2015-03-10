package sfactoids;

import pl.shockah.json.JSONObject;
import scommands.CommandMatch;
import scommands.CommandProvider;
import shocky3.JSONUtil;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class FactoidCommandProvider extends CommandProvider {
	protected final Plugin factoidPlugin;
	
	public FactoidCommandProvider(Plugin plugin) {
		super(plugin, LOW_PRIORITY);
		factoidPlugin = plugin;
	}

	public CommandMatch provide(GenericUserMessageEvent e, String name, String input) {
		DBCollection dbc = e.<Bot>getBot().botApp.collection(plugin);
		name = name.toLowerCase();
		
		String serverName = e.<Bot>getBot().manager.name;
		String contextServer = "server:" + serverName;
		String contextChannel = String.format("channel:%s@%s", e.getChannel().getName(), serverName);
		
		JSONObject jGlobal = null, jServer = null, jChannel = null;
		for (DBObject dbo : JSONUtil.all(dbc.find(JSONUtil.toDBObject(
			JSONObject.make(
				"name", name,
				"forgotten", false
			)
		)).sort(JSONUtil.toDBObject(
			JSONObject.make(
				"timestamp", -1
			)
		)))) {
			JSONObject j = JSONUtil.fromDBObject(dbo);
			String jContext = j.getString("context");
			if (jContext.equals("global")) {
				if (jGlobal == null) {
					jGlobal = j;
				}
			} else if (jContext.equals(contextServer)) {
				if (jServer == null) {
					jServer = j;
				}
			} else if (jContext.equals(contextChannel)) {
				if (jChannel == null) {
					jChannel = j;
				}
			}
		}
		
		JSONObject j = jChannel != null ? jChannel : (jServer != null ? jServer : (jGlobal != null ? jGlobal : null));
		if (j != null)
			return new CommandMatch(new FactoidCommand(factoidPlugin, name, j, j.getString("code")), true, priority);
		return null;
	}
}