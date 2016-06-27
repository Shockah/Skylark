package io.shockah.skylark.factoids.db;

import io.shockah.skylark.UnexpectedException;
import io.shockah.skylark.db.DbObject;
import java.sql.SQLException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "io_shockah_skylark_factoids_factoidident")
public class FactoidIdent extends DbObject<FactoidIdent, Integer> {
	public static final String FACTOID_COLUMN = "factoid_id";
	
	@DatabaseField(canBeNull = false)
	public String prefix;
	
	@DatabaseField(canBeNull = false)
	public String account;
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = FACTOID_COLUMN)
	private Factoid factoid;
	
	@Deprecated //ORMLite-only
	protected FactoidIdent() {
	}
	
	public FactoidIdent(Dao<FactoidIdent, Integer> dao) {
		super(dao);
	}
	
	public Factoid getFactoid() {
		try {
			if (factoid != null)
				factoid.refresh();
			return factoid;
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public void setFactoid(Factoid factoid) {
		this.factoid = factoid;
	}
}