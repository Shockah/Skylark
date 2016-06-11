package io.shockah.skylark;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import io.shockah.skylark.db.PatternPersister;

public class DatabaseManager implements Closeable {
	public final App app;
	protected final ConnectionSource connection;
	
	public DatabaseManager(App app) throws SQLException {
		this.app = app;
		Config.DatabaseConfig config = app.config.getDatabaseConfig();
		connection = new JdbcConnectionSource("jdbc:" + config.getDatabasePath());
		DataPersisterManager.registerDataPersisters(new PatternPersister());
	}
	
	public <T> Dao<T, ?> getDao(Class<T> clazz) throws SQLException {
		Dao<T, ?> dao = DaoManager.lookupDao(connection, clazz);
		if (dao == null) {
			dao = DaoManager.createDao(connection, clazz);
			TableUtils.createTableIfNotExists(connection, clazz);
		}
		return dao;
	}
	
	public <T, ID> Dao<T, ID> getDao(Class<T> clazz, Class<ID> clazzId) throws SQLException {
		Dao<T, ID> dao = DaoManager.lookupDao(connection, clazz);
		if (dao == null) {
			dao = DaoManager.createDao(connection, clazz);
			TableUtils.createTableIfNotExists(connection, clazz);
		}
		return dao;
	}

	@Override
	public void close() throws IOException {
		connection.close();
	}
}