package shocky3;

import java.io.File;
import java.util.List;
import pl.shockah.FileIO;
import pl.shockah.Pair;
import pl.shockah.Util;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import shocky3.pircbotx.Bot;
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
	public boolean running = false;
	public MongoClient mongo = null;
	protected String mongoDb = null;
	
	public Shocky() {
		settings = new Settings(this);
		serverManager = new ServerManager(this);
		pluginManager = new PluginManager(this);
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
			initializeMongo(j.contains("mongo") ? j.getObject("mongo") : new JSONObject());
			
			settings.read();
			List<Pair<BotManager, String>> channels = serverManager.readConfig();
			
			pluginManager.readPlugins();
			pluginManager.reload();
			
			for (Pair<BotManager, String> pair : channels) {
				pair.get1().joinChannel(pair.get2());
			}
			
			settings.write();
			
			while (running) {
				Util.sleep(50);
			}
		} catch (Exception e) {e.printStackTrace();}
		
		synchronized (serverManager.botManagers) {for (BotManager bm : serverManager.botManagers) {
			for (Bot bot : bm.bots) {
				bot.stopBotReconnect();
				bot.sendIRC().quitServer();
			}
		}}
	}
	
	private void initializeMongo(JSONObject j) {
		try {
			String mHost = j.contains("host") ? j.getString("host") : null;
			int mPort = j.contains("port") ? j.getInt("port") : 0;
			
			mongo = mPort == 0 ? (mHost == null ? new MongoClient() : new MongoClient(mHost)) : new MongoClient(mHost, mPort);
			mongoDb = j.getString("db");
			mongo = new MongoClient();
		} catch (Exception e) {e.printStackTrace();}
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
	public DBCollection collection(Plugin plugin) {
		return db().getCollection(plugin.pinfo.internalName());
	}
	public DBCollection collection(Plugin plugin, String sub) {
		return db().getCollection(String.format("%s.%s", plugin.pinfo.internalName(), sub));
	}
}