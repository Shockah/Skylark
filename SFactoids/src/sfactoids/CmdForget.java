package sfactoids;

import pl.shockah.json.JSONObject;
import scommands.Command;
import scommands.CommandResult;
import shocky3.JSONUtil;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class CmdForget extends Command {
	public CmdForget(Plugin plugin) {
		super(plugin, "forget", "f");
	}
	
	public void call(GenericUserMessageEvent e, String trigger, String args, CommandResult result) {
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
			dbc.update(dbcur.next(), JSONUtil.toDBObject(JSONObject.make(
				"$set", JSONObject.make(
					"forgotten", true
				)
			)));
			
			result.add(CommandResult.Type.Notice, "Done.");
		} else {
			result.add(CommandResult.Type.Notice, "No factoid to forget.");
		}
	}
}