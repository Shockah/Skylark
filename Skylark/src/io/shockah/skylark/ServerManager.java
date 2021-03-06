package io.shockah.skylark;

import java.sql.SQLException;
import java.util.ArrayList;
import io.shockah.skylark.db.Server;
import io.shockah.skylark.util.ReadWriteList;

public class ServerManager {
	public final App app;
	public final ReadWriteList<BotManager> botManagers = new ReadWriteList<>(new ArrayList<>());
	
	public ServerManager(App app) {
		this.app = app;
	}
	
	public void readFromDatabase() {
		try {
			/*app.databaseManager.create(Server.class, server -> {
				server.name = "esper";
				server.host = "irc.eu.esper.net";
				server.channelsPerConnection = 50;
				server.messageDelay = 333l;
				server.channelNames = Arrays.asList(new String[] {
					"#skylark"
				});
			});*/
			
			for (Server server : app.databaseManager.getDao(Server.class).queryForAll()) {
				BotManager manager = new BotManager(this, server.name, server.host);
				manager.channelsPerConnection = server.channelsPerConnection;
				manager.messageDelay = server.messageDelay == null ? BotManager.DEFAULT_MESSAGE_DELAY : server.messageDelay;
				manager.botName = server.botName == null ? BotManager.DEFAULT_BOT_NAME : server.botName;
				botManagers.add(manager);
				
				for (String channelName : server.channelNames) {
					manager.joinChannel(channelName);
				}
			}
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
}