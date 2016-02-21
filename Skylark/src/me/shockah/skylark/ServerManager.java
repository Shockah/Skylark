package me.shockah.skylark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mongodb.DBCollection;
import io.shockah.json.JSONObject;
import me.shockah.skylark.util.JSON;

public class ServerManager {
	public static final String COLLECTION_NAME = "servers";
	
	public final App app;
	public final List<BotManager> botManagers = Collections.synchronizedList(new ArrayList<>());
	
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