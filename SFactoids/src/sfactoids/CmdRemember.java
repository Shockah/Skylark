package sfactoids;

import java.util.Date;
import pl.shockah.json.JSONObject;
import scommands.Command;
import shocky3.BotManager;
import shocky3.JSONUtil;
import shocky3.Shocky;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;
import sident.IdentHandler;
import com.mongodb.DBCollection;

public class CmdRemember extends Command {
	public CmdRemember(Plugin plugin) {
		super(plugin, "remember", "r");
	}
	
	public void call(Shocky botApp, GenericUserMessageEvent<Bot> e, String trigger, String args) {
		//String originalArgs = args;
		String[] spl = args.split("\\s");
		String context = "global";
		String name = null;
		
		if (spl.length < 2) {
			return;
		}
		
		if (spl[0].startsWith("@")) {
			context = spl[0].substring(1).toLowerCase();
			args = args.substring(spl[0].length() + 1);
			spl = args.split("\\s");
			
			if (spl.length < 2) {
				return;
			}
			
			if (!context.equals("global")) {
				String serverName = botApp.serverManager.byBot(e).name;
				if (context.equals("server")) {
					context = "server:" + serverName;
				} else if (context.equals("channel") && e.getChannel() != null) {
					context = String.format("channel:%s@%s", e.getChannel().getName(), serverName);
				}
			}
		}
		context = context.toLowerCase();
		
		name = spl[0];
		args = args.substring(spl[0].length() + 1);
		//spl = args.split("\\s");
		
		String code = args;
		
		JSONObject jAuthor = new JSONObject();
		BotManager bm = botApp.serverManager.byBot(e);
		for (IdentHandler handler : Plugin.pluginIdent.identHandlers.get(null)) {
			IdentHandler handler2 = Plugin.pluginIdent.getIdentHandlerFor(bm, handler.id);
			if (handler2.isAvailable()) {
				String account = handler2.account(e.getUser());
				if (account != null) {
					jAuthor.put(handler.id, account);
				}
			}
		}
		
		DBCollection dbc = botApp.collection(plugin);
		dbc.insert(JSONUtil.toDBObject(JSONObject.make(
			"name", name,
			"context", context,
			"code", code,
			"forgotten", false,
			"author", jAuthor,
			"timestamp", (int)(new Date().getTime() / 1000l)
		)));
		
		e.getUser().send().notice("Done.");
	}
}