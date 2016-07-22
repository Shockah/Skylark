package io.shockah.skylark.db;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import io.shockah.json.JSONObject;
import io.shockah.json.JSONParser;
import io.shockah.json.JSONPrettyPrinter;
import io.shockah.skylark.App;
import io.shockah.skylark.UnexpectedException;
import io.shockah.skylark.func.Action1;

public class DatabaseManager implements Closeable {
	public static final Path TABLE_VERSIONS_PATH = Paths.get("tableVersions.json");
	
	public final App app;
	protected final ConnectionSource connection;
	
	private final Object lock = new Object();
	private final List<Class<?>> createdTables = new ArrayList<>();
	private final JSONObject tableVersions;
	
	public DatabaseManager(App app) {
		this.app = app;
		try {
			connection = new JdbcConnectionSource("jdbc:" + app.config.getObject("database").getString("databasePath"));
			DataPersisterManager.registerDataPersisters(new PatternPersister());
			
			if (Files.exists(TABLE_VERSIONS_PATH))
				tableVersions = new JSONParser().parseObject(new String(Files.readAllBytes(TABLE_VERSIONS_PATH), "UTF-8"));
			else
				tableVersions = new JSONObject();
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> Dao<T, Integer> getDao(Class<T> clazz) {
		return getDao(clazz, Integer.class);
	}
	
	public <T extends DbObject<T>, ID> Dao<T, ID> getDao(Class<T> clazz, Class<ID> clazzId) {
		try {
			synchronized (lock) {
				Dao<T, ID> dao = DaoManager.lookupDao(connection, clazz);
				if (dao == null)
					dao = DaoManager.createDao(connection, clazz);
				createTableIfNeeded(dao, clazz);
				return dao;
			}
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	private <T extends DbObject<T>, ID> void createTableIfNeeded(Dao<T, ID> dao, Class<T> clazz) {
		synchronized (lock) {
			if (createdTables.contains(clazz))
				return;
			try {
				TableUtils.createTableIfNotExists(connection, clazz);
				String databaseTable = getDatabaseTable(clazz);
				int tableVersion = getTableVersion(clazz);
				int oldTableVersion = tableVersions.getInt(databaseTable, 0);
				if (tableVersion > oldTableVersion) {
					if (oldTableVersion != 0) {
						try {
							Method method = clazz.getMethod("migrate", Dao.class, int.class, int.class);
							if (Modifier.isStatic(method.getModifiers())) {
								method.invoke(null, dao, oldTableVersion, tableVersion);
								tableVersions.put(databaseTable, tableVersion);
							}
						} catch (Exception e) {
							throw new UnexpectedException(e);
						}
					}
					saveTableVersions();
				}
			} catch (SQLException e) {
				throw new UnexpectedException(e);
			}
			createdTables.add(clazz);
		}
	}
	
	private <T extends DbObject<T>> String getDatabaseTable(Class<T> clazz) {
		DatabaseTable databaseTable = clazz.getAnnotation(DatabaseTable.class);
		return databaseTable == null ? clazz.getSimpleName().toLowerCase() : databaseTable.tableName();
	}
	
	private <T extends DbObject<T>> int getTableVersion(Class<T> clazz) {
		DbObject.TableVersion tableVersion = clazz.getAnnotation(DbObject.TableVersion.class);
		return tableVersion == null ? 1 : tableVersion.value();
	}
	
	private void saveTableVersions() {
		try {
			Files.write(TABLE_VERSIONS_PATH, new JSONPrettyPrinter().toString(tableVersions).getBytes("UTF-8"));
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> T make(Class<T> clazz) {
		try {
			T obj = clazz.getConstructor(Dao.class).newInstance(getDao(clazz, Integer.class));
			obj.manager = new WeakReference<>(this);
			return obj;
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
	
	public <T extends DbObject<T>> T get(Class<T> clazz, int id) {
		try {
			T obj = getDao(clazz).queryForId(id);
			if (obj != null)
				obj.manager = new WeakReference<>(this);
			return obj;
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> List<T> query(Class<T> clazz, SQLExceptionWrappedAction1<QueryBuilder<T, Integer>> f) {
		try {
			QueryBuilder<T, Integer> builder = getDao(clazz).queryBuilder();
			f.call(builder);
			List<T> objs = builder.query();
			for (T obj : objs) {
				obj.manager = new WeakReference<>(this);
			}
			return objs;
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> List<T> query(Class<T> clazz, SQLExceptionWrappedAction2<QueryBuilder<T, Integer>, WhereBuilder> f) {
		try {
			QueryBuilder<T, Integer> builder = getDao(clazz).queryBuilder();
			f.call(builder, new WhereBuilder(builder.where()));
			List<T> objs = builder.query();
			for (T obj : objs) {
				obj.manager = new WeakReference<>(this);
			}
			return objs;
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> T queryFirst(Class<T> clazz, SQLExceptionWrappedAction1<QueryBuilder<T, Integer>> f) {
		try {
			QueryBuilder<T, Integer> builder = getDao(clazz).queryBuilder();
			f.call(builder);
			T obj = builder.queryForFirst();
			if (obj != null)
				obj.manager = new WeakReference<>(this);
			return obj;
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public <T extends DbObject<T>> T queryFirst(Class<T> clazz, SQLExceptionWrappedAction2<QueryBuilder<T, Integer>, WhereBuilder> f) {
		try {
			QueryBuilder<T, Integer> builder = getDao(clazz).queryBuilder();
			f.call(builder, new WhereBuilder(builder.where()));
			T obj = builder.queryForFirst();
			if (obj != null)
				obj.manager = new WeakReference<>(this);
			return obj;
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