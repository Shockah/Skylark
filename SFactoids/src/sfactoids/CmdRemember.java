package sfactoids;

import java.util.Date;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.json.JSONObject;
import scommands.Command;
import shocky3.BotManager;
import shocky3.JSONUtil;
import shocky3.Shocky;
import shocky3.ident.IdentHandler;
import com.mongodb.DBCollection;

public class CmdRemember extends Command {
	public CmdRemember(Plugin plugin) {
		super(plugin, "remember", "r");
	}
	
	public void call(Shocky botApp, MessageEvent<PircBotX> e, String trigger, String args) {
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
			
			if (context.equals("server")) {
				context = "server:" + botApp.serverManager.byBot(e).host;
			} else if (context.equals("channel")) {
				context = String.format("channel:%s@%s", e.getChannel().getName(), botApp.serverManager.byBot(e).host);
			}
		}
		context = context.toLowerCase();
		
		name = spl[0];
		args = args.substring(spl[0].length() + 1);
		//spl = args.split("\\s");
		
		String code = args;
		
		JSONObject jAuthor = new JSONObject();
		for (IdentHandler handler : botApp.identManager.identHandlers.get(null)) {
			if (handler.isAvailable()) {
				IdentHandler handler2 = handler;
				BotManager bm = botApp.serverManager.byBot(e);
				if (botApp.identManager.identHandlers.containsKey(bm)) {
					handler2 = botApp.identManager.getIdentHandlerFor(bm, handler.id);
				}
				String account = handler2.account(e.getUser());
				if (account != null) {
					jAuthor.put(handler.id, account);
				}
			}
		}
		
		DBCollection dbc = botApp.collection(plugin.pinfo.internalName());
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