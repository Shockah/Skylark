package io.shockah.skylark.factoids.db;

import java.util.Date;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "io_shockah_skylark_factoids_factoid")
public class Factoid extends BaseDaoEnabled<Factoid, Integer> {
	public static enum Context {
		Channel, Server, Global;
	}
	
	@DatabaseField(generatedId = true)
	private int id;
	
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
	
	@DatabaseField
	public String raw;
	
	@DatabaseField
	public boolean forgotten = false;
	
	@Deprecated //ORMLite-only
	Factoid() {
	}
	
	public Factoid(Dao<Factoid, Integer> dao) {
		setDao(dao);
		date = new Date();
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof Factoid))
			return false;
		Factoid factoid = (Factoid)other;
		return id == factoid.id;
	}
	
	public int getId() {
		return id;
	}
}