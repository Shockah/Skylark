package io.shockah.skylark;

import io.shockah.json.JSONObject;
import io.shockah.json.JSONParser;
import io.shockah.skylark.plugin.PluginManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
	public static final Path CONFIG_PATH = Paths.get("config.json");
	
	public static void main(String[] args) {
		new App().run();
	}
	
	public JSONObject config;
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
			throw new UnexpectedException("Failed to initialize.", e);
		}
		
		try {
			if (databaseManager != null) {
				databaseManager.close();
			}
		} catch (Exception e) {
			throw new UnexpectedException("Failed to deinitialize.", e);
		}
	}
	
	protected void loadConfig(Path path) throws IOException {
		config = new JSONParser().parseObject(new String(Files.readAllBytes(path), "UTF-8"));
	}
}