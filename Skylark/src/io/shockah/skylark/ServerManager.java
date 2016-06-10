package io.shockah.skylark;

import io.shockah.skylark.db.Server;
import io.shockah.skylark.util.ReadWriteList;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerManager {
	public final App app;
	public final ReadWriteList<BotManager> botManagers = new ReadWriteList<>(new ArrayList<>());
	
	public ServerManager(App app) {
		this.app = app;
	}
	
	public void readFromDatabase() throws SQLException {
		for (Server server : app.databaseManager.serversDao.queryForAll()) {
			BotManager manager = new BotManager(this, server.getName(), server.getHost());
			manager.channelsPerConnection = server.getChannelsPerConnection();
			manager.messageDelay = server.getMessageDelay();
			manager.botName = server.getBotName();
			botManagers.add(manager);
			
			for (String channelName : server.getChannelNames()) {
				manager.joinChannel(channelName);
			}
		}
	}
}