package io.shockah.skylark.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;

public class DbObject<T, ID> extends BaseDaoEnabled<T, ID> {
	@DatabaseField(generatedId = true)
	private int id;
	
	@Deprecated //ORMLite-only
	protected DbObject() {
	}
	
	public DbObject(Dao<T, ID> dao) {
		setDao(dao);
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		DbObject<T, ID> obj = (DbObject<T, ID>)other;
		return id == obj.id;
	}
	
	public int getId() {
		return id;
	}
}