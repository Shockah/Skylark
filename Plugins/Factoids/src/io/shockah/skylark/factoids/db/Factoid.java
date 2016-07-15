package io.shockah.skylark.factoids.db;

import io.shockah.skylark.db.DbObject;
import java.util.Date;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "io_shockah_skylark_factoids_factoid")
public class Factoid extends DbObject<Factoid> {
	public static final String NAME_COLUMN = "name";
	public static final String SERVER_COLUMN = "server";
	public static final String CHANNEL_COLUMN = "channel";
	public static final String CONTEXT_COLUMN = "context";
	public static final String DATE_COLUMN = "date";
	public static final String ACTIVE_COLUMN = "active";
	
	public static enum Context {
		Channel, Server, Global;
	}
	
	@DatabaseField(canBeNull = false, columnName = NAME_COLUMN)
	public String name;
	
	@DatabaseField(canBeNull = false, columnName = SERVER_COLUMN)
	public String server;
	
	@DatabaseField(canBeNull = false, columnName = CHANNEL_COLUMN)
	public String channel;
	
	@DatabaseField(canBeNull = false, columnName = CONTEXT_COLUMN)
	public Context context;
	
	@DatabaseField(canBeNull = false, columnName = DATE_COLUMN)
	public Date date;
	
	@DatabaseField(canBeNull = false)
	public String type;
	
	@DatabaseField(dataType = DataType.LONG_STRING, canBeNull = true)
	public String raw;
	
	@DatabaseField(canBeNull = false, columnName = ACTIVE_COLUMN)
	public boolean active = true;
	
	@ForeignCollectionField(foreignFieldName = "factoid")
	private ForeignCollection<FactoidIdent> idents;
	
	@Deprecated //ORMLite-only
	protected Factoid() {
	}
	
	public Factoid(Dao<Factoid, Integer> dao) {
		super(dao);
		date = new Date();
	}
	
	public ForeignCollection<FactoidIdent> getIdents() {
		return idents;
	}
}