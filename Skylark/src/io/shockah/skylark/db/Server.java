package io.shockah.skylark.db;

import java.util.List;
import java.util.Objects;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "servers")
public class Server extends BaseDaoEnabled<Server, String> {
	@DatabaseField(id = true)
	private String name;
	
	@DatabaseField
	public String host;
	
	@DatabaseField(canBeNull = true)
	public Integer channelsPerConnection;
	
	@DatabaseField(canBeNull = true)
	public Long messageDelay;
	
	@DatabaseField(canBeNull = true)
	public String botName;
	
	@DatabaseField(canBeNull = true)
	public Integer linebreakLength;
	
	@DatabaseField(canBeNull = true)
	public String ellipsis;
	
	@DatabaseField(persisterClass = StringListToSpaceDelimitedStringPersister.class)
	public List<String> channelNames;
	
	@Deprecated //ORMLite-only
	Server() {
	}
	
	public Server(Dao<Server, String> dao, String name) {
		setDao(dao);
		this.name = name;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof Server))
			return false;
		Server server = (Server)other;
		return Objects.equals(name, server.name);
	}
	
	public String getName() {
		return name;
	}
}