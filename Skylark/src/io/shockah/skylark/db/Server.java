package io.shockah.skylark.db;

import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "servers")
public class Server extends DbObject<Server> {
	@DatabaseField(canBeNull = false)
	public String name;
	
	@DatabaseField(canBeNull = false)
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
	protected Server() {
		super();
	}
	
	public Server(Dao<Server, Integer> dao) {
		super(dao);
	}
}