package io.shockah.skylark.db;

import io.shockah.skylark.BotManager;
import java.util.List;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "servers")
public class Server {
	@DatabaseField(id = true)
	private String name;
	
	@DatabaseField
	private String host;
	
	@DatabaseField(canBeNull = true)
	private Integer channelsPerConnection;
	
	@DatabaseField(canBeNull = true)
	private Long messageDelay;
	
	@DatabaseField(canBeNull = true)
	private String botName;
	
	@DatabaseField(persisterClass = StringListToSpaceDelimitedStringPersister.class)
	private List<String> channelNames;
	
	public Server() {
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public Integer getChannelsPerConnection() {
		return channelsPerConnection;
	}
	
	public void setChannelsPerConnection(Integer channelsPerConnection) {
		this.channelsPerConnection = channelsPerConnection;
	}
	
	public Long getMessageDelay() {
		return messageDelay == null ? BotManager.DEFAULT_MESSAGE_DELAY : messageDelay;
	}
	
	public void setMessageDelay(Long messageDelay) {
		this.messageDelay = messageDelay;
	}
	
	public String getBotName() {
		return botName == null ? BotManager.DEFAULT_BOT_NAME : botName;
	}
	
	public void setBotName(String botName) {
		this.botName = botName;
	}
	
	public List<String> getChannelNames() {
		return channelNames;
	}
	
	public void setChannelNames(List<String> channelNames) {
		this.channelNames = channelNames;
	}
}