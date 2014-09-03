package shocky3;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import pl.shockah.Pair;
import pl.shockah.json.JSONObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class ServerManager {
	public final Shocky botApp;
	protected List<BotManager> botManagers = Collections.synchronizedList(new LinkedList<BotManager>());
	
	public ServerManager(Shocky botApp) {
		this.botApp = botApp;
	}
	
	public List<Pair<BotManager, String>> readConfig() {
		List<Pair<BotManager, String>> ret = new LinkedList<>();
		DBCollection dbc = botApp.collection("servers");
		for (DBObject dbo : JSONUtil.all(dbc.find())) {
			JSONObject j = JSONUtil.fromDBObject(dbo);
			
			BotManager bm = new BotManager(this, j.getString("name"), j.getString("host"));
			if (j.contains("botName")) bm.botName = j.getString("botName");
			if (j.contains("channelsPerConn")) bm.channelsPerConn = j.getInt("channelsPerConn");
			if (j.contains("messageDelay")) bm.messageDelay = j.getInt("messageDelay");
			botManagers.add(bm);
			
			for (String jChannel : j.getList("channels").ofStrings()) {
				ret.add(new Pair<>(bm, jChannel));
			}
		}
		return ret;
	}
	
	public BotManager byServerName(String name) {
		for (BotManager manager : botManagers) {
			if (manager.name.equals(name)) {
				return manager;
			}
		}
		return null;
	}
	public BotManager byServerHost(String name) {
		for (BotManager manager : botManagers) {
			if (manager.host.equals(name)) {
				return manager;
			}
		}
		return null;
	}
	
	public BotManager byBot(Event<PircBotX> e) {
		return byBot(e.getBot());
	}
	public BotManager byBot(PircBotX bot) {
		for (BotManager manager : botManagers) {
			if (manager.bots.contains(bot)) {
				return manager;
			}
		}
		return null;
	}
}