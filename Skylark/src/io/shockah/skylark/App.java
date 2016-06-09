package io.shockah.skylark;

import io.shockah.skylark.plugin.PluginManager;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
	public static final Path CONFIG_PATH = Paths.get("config.json");
	
	public static void main(String[] args) {
		new App().run();
	}
	
	public Config config;
	public DatabaseManager databaseManager;
	public PluginManager pluginManager;
	public ServerManager serverManager;
	
	protected Path getConfigPath() {
		return CONFIG_PATH;
	}
	
	public void run() {
		try {
			loadConfig(getConfigPath());
			
			databaseManager = new DatabaseManager(this);
			pluginManager = new PluginManager(this);
			serverManager = new ServerManager(this);
			
			pluginManager.reload();
			serverManager.readFromDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void loadConfig(Path path) throws IOException {
		config = Config.fromFile(path);
	}
}