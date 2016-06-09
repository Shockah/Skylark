package io.shockah.skylark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	public final App app;
	protected final Connection connection;
	protected final int queryTimeout;
	
	public DatabaseManager(App app) throws SQLException {
		this.app = app;
		Config.DatabaseConfig config = app.config.getDatabaseConfig();
		connection = DriverManager.getConnection("jdbc:sqlite:" + config.getDatabaseFilePath());
		queryTimeout = config.getQueryTimeout();
	}
	
	protected Statement createStatement() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(queryTimeout);
		return statement;
	}
}