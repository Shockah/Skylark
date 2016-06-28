package io.shockah.skylark.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;

public class DbObject<T> extends BaseDaoEnabled<T, Integer> {
	public static final String ID_COLUMN = "id";
	
	@DatabaseField(generatedId = true, columnName = ID_COLUMN)
	private int id;
	
	@Deprecated //ORMLite-only
	protected DbObject() {
	}
	
	public DbObject(Dao<T, Integer> dao) {
		setDao(dao);
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		DbObject<T> obj = (DbObject<T>)other;
		return id == obj.id;
	}
	
	public int getId() {
		return id;
	}
}