package shocky3;

import java.util.ArrayList;
import java.util.List;
import pl.shockah.json.JSONObject;
import shocky3.util.JSON;
import shocky3.util.Synced;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class ServerManager {
	public final Shocky botApp;
	public List<BotManager> botManagers = Synced.list();
	
	public ServerManager(Shocky botApp) {
		this.botApp = botApp;
	}
	
	public List<BotManagerChannelEntry> readConfig() {
		List<BotManagerChannelEntry> ret = new ArrayList<>();
		DBCollection dbc = botApp.collection("servers");
		for (DBObject dbo : JSON.all(dbc.find())) {
			JSONObject j = JSON.fromDBObject(dbo);
			
			BotManager bm = new BotManager(this, j.getString("name"), j.getString("host"));
			if (j.contains("botName"))
				bm.botName = j.getString("botName");
			if (j.contains("channelsPerConn"))
				bm.channelsPerConn = j.getInt("channelsPerConn");
			if (j.contains("messageDelay"))
				bm.messageDelay = j.getInt("messageDelay");
			botManagers.add(bm);
			
			for (String jChannel : j.getList("channels").ofStrings())
				ret.add(new BotManagerChannelEntry(bm, jChannel));
		}
		return ret;
	}
	
	public BotManager byServerName(String name) {
		synchronized (botManagers) {
			for (BotManager manager : botManagers)
				if (manager.name.equals(name))
					return manager;
		}
		return null;
	}
	public BotManager byServerHost(String name) {
		synchronized (botManagers) {
			for (BotManager manager : botManagers)
				if (manager.host.equals(name))
					return manager;
		}
		return null;
	}
	
	public static class BotManagerChannelEntry {
		public final BotManager manager;
		public final String channel;
		
		public BotManagerChannelEntry(BotManager manager, String channel) {
			this.manager = manager;
			this.channel = channel;
		}
	}
}