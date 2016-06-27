package io.shockah.skylark.factoids.db;

import io.shockah.skylark.db.DbObject;
import java.util.Date;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "io_shockah_skylark_factoids_factoid")
public class Factoid extends DbObject<Factoid, Integer> {
	public static enum Context {
		Channel, Server, Global;
	}
	
	@DatabaseField(canBeNull = false)
	public String name;
	
	@DatabaseField(canBeNull = false)
	public String server;
	
	@DatabaseField(canBeNull = false)
	public String channel;
	
	@DatabaseField(canBeNull = false)
	public Context context;
	
	@DatabaseField(canBeNull = false)
	public Date date;
	
	@DatabaseField(canBeNull = true)
	public String raw;
	
	@DatabaseField(canBeNull = false)
	public boolean forgotten = false;
	
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