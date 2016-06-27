package io.shockah.skylark;

import io.shockah.skylark.db.DbObject;
import io.shockah.skylark.db.PatternPersister;
import io.shockah.skylark.func.Action1;
import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseManager implements Closeable {
	public final App app;
	protected final ConnectionSource connection;
	
	private final Object lock = new Object();
	
	public DatabaseManager(App app) {
		this.app = app;
		try {
			connection = new JdbcConnectionSource("jdbc:" + app.config.getObject("database").getString("databasePath"));
			DataPersisterManager.registerDataPersisters(new PatternPersister());
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	protected <T> Dao<T, ?> getDao(Class<T> clazz) {
		try {
			synchronized (lock) {
				Dao<T, ?> dao = DaoManager.lookupDao(connection, clazz);
				if (dao == null) {
					dao = DaoManager.createDao(connection, clazz);
					TableUtils.createTableIfNotExists(connection, clazz);
				}
				return dao;
			}
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T, ID> Dao<T, ID> getDao(Class<T> clazz, Class<ID> clazzId) {
		try {
			synchronized (lock) {
				Dao<T, ID> dao = DaoManager.lookupDao(connection, clazz);
				if (dao == null) {
					dao = DaoManager.createDao(connection, clazz);
					TableUtils.createTableIfNotExists(connection, clazz);
				}
				return dao;
			}
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	protected <T extends DbObject<T, ID>, ID> T make(Class<T> clazz) {
		try {
			return clazz.getConstructor(Dao.class).newInstance(getDao(clazz));
		} catch (Exception e) {
			return null;
		}
	}
	
	public <T extends DbObject<T, ID>, ID> T make(Class<T> clazz, Class<ID> clazzId) {
		try {
			return clazz.getConstructor(Dao.class).newInstance(getDao(clazz, clazzId));
		} catch (Exception e) {
			return null;
		}
	}
	
	public <T extends DbObject<T, ID>, ID> T create(Class<T> clazz, Class<ID> clazzId, Action1<T> f) {
		try {
			T obj = make(clazz, clazzId);
			f.call(obj);
			obj.create();
			return obj;
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}

	@Override
	public void close() throws IOException {
		connection.close();
	}
}