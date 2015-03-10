package sfactoids;

import pl.shockah.json.JSONObject;
import scommands.Command;
import scommands.CommandStack;
import shocky3.JSONUtil;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class CmdForget extends Command {
	public CmdForget(Plugin plugin) {
		super(plugin, "forget", "f");
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
			dbc.update(dbcur.next(), JSONUtil.toDBObject(JSONObject.make(
				"$set", JSONObject.make(
					"forgotten", true
				)
			)));
			
			return "Done.";
		} else
			return "No factoid to forget.";
	}
}