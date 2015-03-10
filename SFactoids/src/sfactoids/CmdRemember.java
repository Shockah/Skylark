package sfactoids;

import java.util.Date;
import pl.shockah.json.JSONObject;
import scommands.Command;
import scommands.CommandStack;
import shocky3.BotManager;
import shocky3.JSONUtil;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;
import sident.IdentHandler;
import com.mongodb.DBCollection;

public class CmdRemember extends Command {
	public CmdRemember(Plugin plugin) {
		super(plugin, "remember", "r");
	}
	
	public String call(GenericUserMessageEvent e, String input, CommandStack stack) {
		//String originalArgs = args;
		String[] spl = input.split("\\s");
		String context = "global";
		String name = null;
		
		if (spl.length < 2)
			return null;
		
		if (spl[0].startsWith("@")) {
			context = spl[0].substring(1).toLowerCase();
			input = input.substring(spl[0].length() + 1);
			spl = input.split("\\s");
			
			if (spl.length < 2)
				return null;
			
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
		
		name = spl[0];
		input = input.substring(spl[0].length() + 1);
		//spl = args.split("\\s");
		
		String code = input;
		
		JSONObject jAuthor = new JSONObject();
		BotManager bm = e.<Bot>getBot().manager;
		synchronized (Plugin.pluginIdent.identHandlers) {for (IdentHandler handler : Plugin.pluginIdent.identHandlers.get(null)) {
			IdentHandler handler2 = Plugin.pluginIdent.getIdentHandlerFor(bm, handler.id);
			if (handler2.isAvailable()) {
				String account = handler2.account(e.getUser());
				if (account != null) {
					jAuthor.put(handler.id, account);
				}
			}
		}}
		
		DBCollection dbc = e.<Bot>getBot().botApp.collection(plugin);
		dbc.insert(JSONUtil.toDBObject(JSONObject.make(
			"name", name,
			"context", context,
			"code", code,
			"forgotten", false,
			"author", jAuthor,
			"timestamp", (int)(new Date().getTime() / 1000l)
		)));
		
		return "Done.";
	}
}