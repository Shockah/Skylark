package io.shockah.skylark.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.BaseDaoEnabled;

public class DbObject<T, ID> extends BaseDaoEnabled<T, ID> {
	@Deprecated //ORMLite-only
	protected DbObject() {
	}
	
	public DbObject(Dao<T, ID> dao) {
		setDao(dao);
	}
}