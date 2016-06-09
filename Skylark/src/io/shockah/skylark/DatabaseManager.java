package io.shockah.skylark;

import java.net.UnknownHostException;
import io.shockah.json.JSONObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class DatabaseManager {
	public final App app;
	public final MongoClient client;
	public final String databaseName;
	
	public DatabaseManager(App app) throws UnknownHostException {
		this.app = app;
		
		JSONObject mongoConfig = app.config.getObjectOrEmpty("mongo");
		
		String host = mongoConfig.getString("host", "localhost");
		int port = mongoConfig.getInt("port", 0);
		
		if (port == 0) {
			client = new MongoClient(host);
		} else {
			client = new MongoClient(host, port);
		}
		databaseName = mongoConfig.getString("db", "skylark");
	}
	
	public DB db() {
		return client.getDB(databaseName);
	}
	
	public DBCollection collection(String name) {
		return db().getCollection(name);
	}
}