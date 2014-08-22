package shocky3;

import java.io.File;
import org.pircbotx.PircBotX;
import pl.shockah.FileIO;
import pl.shockah.Util;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import shocky3.ident.IdentManager;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class Shocky {
	public static CustomPrintStream sysout;
	public static final File configFile = new File("config.json");
	
	public static void main(String[] args) {
		System.setOut(sysout = new CustomPrintStream());
		System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
		System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "[dd.MM.yyyy HH:mm:ss]");
		System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
		System.setProperty("org.slf4j.simpleLogger.showLogName", "false");
		System.setProperty("org.slf4j.simpleLogger.showShortLogName", "false");
		new Shocky().run();
	}
	
	public final Settings settings;
	public final ServerManager serverManager;
	public final PluginManager pluginManager;
	public final IdentManager identManager;
	public boolean running = false;
	public MongoClient mongo = null;
	protected String mongoDb = null;
	
	public Shocky() {
		settings = new Settings(this);
		serverManager = new ServerManager(this);
		pluginManager = new PluginManager(this);
		identManager = new IdentManager(this);
	}
	
	public void run() {
		running = true;
		
		JSONObject j = null;
		if (configFile.exists()) {
			try {
				j = new JSONParser().parseObject(FileIO.readWholeString(configFile));
			} catch (Exception e) {}
		}
		if (j == null) j = new JSONObject();
		
		try {
			JSONObject jMongo = j.contains("mongo") ? j.getObject("mongo") : new JSONObject();
			String mHost = jMongo.contains("host") ? jMongo.getString("host") : null;
			int mPort = jMongo.contains("port") ? jMongo.getInt("port") : 0;
			
			mongo = mPort == 0 ? (mHost == null ? new MongoClient() : new MongoClient(mHost)) : new MongoClient(mHost, mPort);
			mongoDb = jMongo.getString("db");
			mongo = new MongoClient();
			
			settings.read();
			
			pluginManager.readPlugins();
			pluginManager.reload();
			
			serverManager.readConfig();
			identManager.readConfig();
			
			while (running) {
				Util.sleep(50);
			}
		} catch (Exception e) {e.printStackTrace();}
		
		for (BotManager bm : serverManager.botManagers) {
			for (PircBotX bot : bm.bots) {
				bot.stopBotReconnect();
				bot.sendIRC().quitServer();
			}
		}
	}
	
	public void stop() {
		running = false;
	}
	
	public DB db() {
		return mongo.getDB(mongoDb);
	}
	public DBCollection collection(String c) {
		return db().getCollection(c);
	}
}