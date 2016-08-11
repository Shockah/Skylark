package io.shockah.skylark.factoids.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.json.JSONObject;
import io.shockah.skylark.db.DbObject;

@DatabaseTable(tableName = "io_shockah_skylark_factoids_factoidstore")
public class FactoidStore extends DbObject<FactoidStore> {
	public static final String NAME_COLUMN = "name";
	public static final String SERVER_COLUMN = "server";
	public static final String CHANNEL_COLUMN = "channel";
	public static final String CONTEXT_COLUMN = "context";
	
	@DatabaseField(canBeNull = false, columnName = NAME_COLUMN)
	public String name;
	
	@DatabaseField(canBeNull = false, columnName = SERVER_COLUMN)
	public String server;
	
	@DatabaseField(canBeNull = false, columnName = CHANNEL_COLUMN)
	public String channel;
	
	@DatabaseField(canBeNull = false, columnName = CONTEXT_COLUMN)
	public Factoid.Context context;
	
	@DatabaseField(canBeNull = true)
	public JSONObject data;
	
	@Deprecated //ORMLite-only
	protected FactoidStore() {
	}
	
	public FactoidStore(Dao<FactoidStore, Integer> dao) {
		super(dao);
	}
}