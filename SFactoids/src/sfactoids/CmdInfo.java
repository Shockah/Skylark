package sfactoids;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import pl.shockah.Pair;
import pl.shockah.json.JSONObject;
import scommands.Command;
import shocky3.JSONUtil;
import shocky3.TimeDuration;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;
import sident.IdentHandler;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class CmdInfo extends Command {
	public CmdInfo(Plugin plugin) {
		super(plugin, "factoidinfo", "finfo");
	}
	
	public void call(GenericUserMessageEvent e, String trigger, String args) {
		//String originalArgs = args;
		String[] spl = args.split("\\s");
		String context = "global";
		String name = null;
		
		if (spl.length < 1) {
			return;
		}
		
		if (spl[0].startsWith("@")) {
			context = spl[0].substring(1).toLowerCase();
			args = args.substring(spl[0].length() + 1);
			spl = args.split("\\s");
			
			if (spl.length < 1) {
				return;
			}
			
			if (!context.equals("global")) {
				String serverName = e.<Bot>getBot().manager.name;
				if (context.equals("server")) {
					context = "server:" + serverName;
				} else if (context.equals("channel") && e.getChannel() != null) {
					context = String.format("channel:%s@%s", e.getChannel().getName(), serverName);
				}
			}
		}
		context = context.toLowerCase();
		
		name = args;
		
		DBCollection dbc = e.<Bot>getBot().botApp.collection(plugin);
		DBCursor dbcur = dbc.find(JSONUtil.toDBObject(JSONObject.make(
			"name", name,
			"context", context,
			"forgotten", false
		))).sort(JSONUtil.toDBObject(JSONObject.make(
			"timestamp", -1
		))).limit(1);
		if (dbcur.hasNext()) {
			JSONObject j = JSONUtil.fromDBObject(dbcur.next());
			
			e.respond(String.format("%s added %s ago", name, TimeDuration.format(new Date(j.getInt("timestamp") * 1000l))));
			
			JSONObject jAuthor = j.getObject("author");
			List<Pair<IdentHandler, String>> list = new LinkedList<>();
			for (String jAuthorKey : jAuthor.keys()) {
				list.add(new Pair<>(Plugin.pluginIdent.getIdentHandlerFor(null, jAuthorKey), jAuthor.getString(jAuthorKey)));
			}
			e.respond(String.format("> ident: %s", Plugin.pluginIdent.formatIdent(list, "%_%")));
			
			e.respond(String.format("> source: %s", j.getString("code")));
		} else {
			e.getUser().send().notice("No factoid.");
		}
	}
}