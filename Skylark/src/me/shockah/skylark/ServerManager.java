package me.shockah.skylark;

import io.shockah.json.JSONObject;
import java.util.ArrayList;
import me.shockah.skylark.util.JSON;
import me.shockah.skylark.util.ReadWriteList;
import com.mongodb.DBCollection;

public class ServerManager {
	public static final String COLLECTION_NAME = "servers";
	
	public final App app;
	public final ReadWriteList<BotManager> botManagers = new ReadWriteList<>(new ArrayList<>());
	
	public ServerManager(App app) {
		this.app = app;
	}
	
	public void readFromDatabase() {
		DBCollection col = app.databaseManager.collection(COLLECTION_NAME);
		for (JSONObject j : JSON.collectJSON(col.find())) {
			String name = j.getString("name");
			String host = j.getString("host");
			
			BotManager manager = new BotManager(this, name, host);
			manager.channelsPerConnection = j.getOptionalInt("channelsPerConnection");
			manager.messageDelay = j.getLong("messageDelay", BotManager.DEFAULT_MESSAGE_DELAY);
			manager.botName = j.getString("botName", BotManager.DEFAULT_BOT_NAME);
			botManagers.add(manager);
			
			for (String channelName : j.getListOrEmpty("channels").ofStrings())
				manager.joinChannel(channelName);
		}
	}
}