package sfactoids;

import java.util.Date;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.json.JSONObject;
import scommands.Command;
import shocky3.JSONUtil;
import shocky3.Shocky;
import shocky3.TimeDuration;
import sident.IdentHandler;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class CmdInfo extends Command {
	public CmdInfo(Plugin plugin) {
		super(plugin, "factoidinfo", "finfo");
	}
	
	public void call(Shocky botApp, MessageEvent<PircBotX> e, String trigger, String args) {
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
				String serverName = botApp.serverManager.byBot(e).name;
				if (context.equals("server")) {
					context = "server:" + serverName;
				} else if (context.equals("channel")) {
					context = String.format("channel:%s@%s", e.getChannel().getName(), serverName);
				}
			}
		}
		context = context.toLowerCase();
		
		name = args;
		
		DBCollection dbc = botApp.collection(plugin.pinfo.internalName());
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
			
			StringBuilder sb = new StringBuilder();
			JSONObject jAuthor = j.getObject("author");
			for (String jAuthorKey : jAuthor.keys()) {
				IdentHandler handler = Plugin.pluginIdent.getIdentHandlerFor(null, jAuthorKey);
				if (handler != null) {
					sb.append(String.format(", %s: %s", handler.name, jAuthor.getString(jAuthorKey)));
				}
			}
			if (sb.length() != 0) {
				e.respond(String.format("> ident: %s", sb.toString().substring(2)));
			}
			
			e.respond(String.format("> source: %s", j.getString("code")));
		} else {
			e.getUser().send().notice("No factoid.");
		}
	}
}