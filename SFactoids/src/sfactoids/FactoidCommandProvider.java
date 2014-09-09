package sfactoids;

import java.util.List;
import pl.shockah.Pair;
import pl.shockah.json.JSONObject;
import scommands.CommandProvider;
import scommands.ICommand;
import shocky3.JSONUtil;
import shocky3.Shocky;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.GenericUserMessageEvent;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class FactoidCommandProvider extends CommandProvider {
	public FactoidCommandBuilder builder = new FactoidCommandBuilder();
	
	public FactoidCommandProvider(Plugin plugin) {
		super(plugin);
		builder.add(
			new AliasFactoidParser()
		);
	}
	
	public void provide(List<Pair<ICommand, EPriority>> candidates, Shocky botApp, GenericUserMessageEvent<Bot> e, String trigger, String args) {
		DBCollection dbc = botApp.collection(plugin.pinfo.internalName());
		trigger = trigger.toLowerCase();
		
		String serverName = botApp.serverManager.byBot(e).name;
		String contextServer = "server:" + serverName;
		String contextChannel = String.format("channel:%s@%s", e.getChannel().getName(), serverName);
		
		JSONObject jGlobal = null, jServer = null, jChannel = null;
		for (DBObject dbo : JSONUtil.all(dbc.find(JSONUtil.toDBObject(
			JSONObject.make(
				"name", trigger,
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
		if (j != null) {
			ICommand built = builder.build(j, botApp, e, trigger, args);
			if (built != null) {
				candidates.add(new Pair<ICommand, EPriority>(built, EPriority.Medium));
			}
		}
	}
}