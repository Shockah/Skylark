package io.shockah.skylark;

import io.shockah.skylark.db.Server;
import java.sql.SQLException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseManager {
	public final App app;
	public final ConnectionSource connection;
	protected final int queryTimeout;
	
	public final Dao<Server, String> serversDao;
	
	public DatabaseManager(App app) throws SQLException {
		this.app = app;
		Config.DatabaseConfig config = app.config.getDatabaseConfig();
		connection = new JdbcConnectionSource("jdbc:sqlite:" + config.getDatabaseFilePath());
		queryTimeout = config.getQueryTimeout();
		
		serversDao = DaoManager.createDao(connection, Server.class);
		
		TableUtils.createTableIfNotExists(connection, Server.class);
	}
}