package stell;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import shocky3.JSONUtil;
import shocky3.PluginInfo;
import shocky3.pircbotx.Bot;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Plugin extends shocky3.ListenerPlugin {
	@Dependency protected static scommands.Plugin pluginCmd;
	@Dependency protected static sident.Plugin pluginIdent;
	
	public final List<Tell> tells = Collections.synchronizedList(new LinkedList<Tell>());
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		pluginCmd.provider.add(
			new CmdTell(this),
			new CmdTells(this)
		);
		
		DBCollection dbc;
		
		dbc = botApp.collection(String.format("%s.Settings", pinfo.internalName()));
		if (dbc.count() != 0) {
			Tell.nextID = JSONUtil.fromDBObject(dbc.findOne()).getInt("nextID");
		}
		
		dbc = botApp.collection(pinfo.internalName());
		for (DBObject dbo : JSONUtil.all(dbc.find())) {
			tells.add(Tell.read(botApp, JSONUtil.fromDBObject(dbo)));
		}
	}
	
	protected void onMessage(MessageEvent<Bot> e) {
		ListIterator<Tell> lit = tells.listIterator();
		while (lit.hasNext()) {
			Tell tell = lit.next();
			if (tell.matches(botApp.serverManager.byBot(e), e.getUser())) {
				String[] spl = tell.buildMessage().split("\\n");
				for (String s : spl) {
					e.getUser().send().notice(s);
				}
				Tell.removeDB(this, tell);
				lit.remove();
			}
		}
	}
	
	protected void onJoin(JoinEvent<Bot> e) {
		int count = 0;
		for (Tell tell : tells) {
			if (tell.matches(botApp.serverManager.byBot(e), e.getUser())) {
				count++;
			}
		}
		
		if (count != 0) {
			e.getUser().send().notice(String.format("You have %d unread .tell%s. Become active or use the .tells command to read them.", count, count == 1 ? "" : "s"));
		}
	}
}