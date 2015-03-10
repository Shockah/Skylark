package sfactoids;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import pl.shockah.Pair;
import pl.shockah.json.JSONObject;
import scommands.Command;
import scommands.CommandStack;
import shocky3.JSONUtil;
import shocky3.MultilineString;
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
	
	public String call(GenericUserMessageEvent e, String input, CommandStack stack) {
		//String originalArgs = args;
		String[] spl = input.split("\\s");
		String context = "global";
		String name = null;
		
		if (spl.length < 1)
			return null;
		
		if (spl[0].startsWith("@")) {
			context = spl[0].substring(1).toLowerCase();
			input = input.substring(spl[0].length() + 1);
			spl = input.split("\\s");
			
			if (spl.length < 1)
				return null;
			
			if (!context.equals("global")) {
				String serverName = e.<Bot>getBot().manager.name;
				if (context.equals("server"))
					context = "server:" + serverName;
				else if (context.equals("channel") && e.getChannel() != null)
					context = String.format("channel:%s@%s", e.getChannel().getName(), serverName);
			}
		}
		context = context.toLowerCase();
		
		name = input;
		
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
			
			//TODO: return the whole thing
			MultilineString str = new MultilineString();
			str.add(String.format("%s added %s ago", name, TimeDuration.format(new Date(j.getInt("timestamp") * 1000l))));
			
			JSONObject jAuthor = j.getObject("author");
			List<Pair<IdentHandler, String>> list = new LinkedList<>();
			for (String jAuthorKey : jAuthor.keys())
				list.add(new Pair<>(Plugin.pluginIdent.getIdentHandlerFor(null, jAuthorKey), jAuthor.getString(jAuthorKey)));
			str.add(String.format("> ident: %s", Plugin.pluginIdent.formatIdent(list, "%_%")));
			
			str.add(String.format("> source: %s", j.getString("code")));
			return str.toString();
		} else
			return "No factoid.";
	}
}