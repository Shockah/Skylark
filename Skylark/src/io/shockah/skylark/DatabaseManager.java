package io.shockah.skylark;

import io.shockah.skylark.db.DbObject;
import io.shockah.skylark.db.PatternPersister;
import io.shockah.skylark.db.SQLExceptionWrappedAction1;
import io.shockah.skylark.db.SQLExceptionWrappedAction2;
import io.shockah.skylark.db.WhereBuilder;
import io.shockah.skylark.func.Action1;
import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
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
	
	public <T> Dao<T, Integer> getDao(Class<T> clazz) {
		return getDao(clazz, Integer.class);
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
	
	public <T extends DbObject<T>> T make(Class<T> clazz) {
		try {
			return clazz.getConstructor(Dao.class).newInstance(getDao(clazz, Integer.class));
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> T create(Class<T> clazz, Action1<T> f) {
		try {
			T obj = make(clazz);
			f.call(obj);
			obj.create();
			return obj;
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> List<T> query(Class<T> clazz, SQLExceptionWrappedAction1<QueryBuilder<T, Integer>> f) {
		try {
			QueryBuilder<T, Integer> builder = getDao(clazz).queryBuilder();
			f.call(builder);
			return builder.query();
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> List<T> query(Class<T> clazz, SQLExceptionWrappedAction2<QueryBuilder<T, Integer>, WhereBuilder> f) {
		try {
			QueryBuilder<T, Integer> builder = getDao(clazz).queryBuilder();
			f.call(builder, new WhereBuilder(builder.where()));
			return builder.query();
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> T queryFirst(Class<T> clazz, SQLExceptionWrappedAction1<QueryBuilder<T, Integer>> f) {
		try {
			QueryBuilder<T, Integer> builder = getDao(clazz).queryBuilder();
			f.call(builder);
			return builder.queryForFirst();
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> T queryFirst(Class<T> clazz, SQLExceptionWrappedAction2<QueryBuilder<T, Integer>, WhereBuilder> f) {
		try {
			QueryBuilder<T, Integer> builder = getDao(clazz).queryBuilder();
			f.call(builder, new WhereBuilder(builder.where()));
			return builder.queryForFirst();
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> int delete(Class<T> clazz, SQLExceptionWrappedAction1<DeleteBuilder<T, Integer>> f) {
		try {
			DeleteBuilder<T, Integer> builder = getDao(clazz).deleteBuilder();
			f.call(builder);
			return builder.delete();
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> int delete(Class<T> clazz, SQLExceptionWrappedAction2<DeleteBuilder<T, Integer>, WhereBuilder> f) {
		try {
			DeleteBuilder<T, Integer> builder = getDao(clazz).deleteBuilder();
			f.call(builder, new WhereBuilder(builder.where()));
			return builder.delete();
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}

	@Override
	public void close() throws IOException {
		connection.close();
	}
}