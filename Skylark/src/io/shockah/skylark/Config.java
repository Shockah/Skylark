package io.shockah.skylark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import io.shockah.json.JSONObject;
import io.shockah.json.JSONParser;

public final class Config {
	private static final String DATABASE = "database";
	
	private final JSONObject json;
	
	protected Config(JSONObject json) {
		this.json = json;
	}
	
	public static Config fromFile(Path path) throws IOException {
		return new Config(new JSONParser().parseObject(new String(Files.readAllBytes(path), "UTF-8")));
	}
	
	public DatabaseConfig getDatabaseConfig() {
		return new DatabaseConfig(json.getObject(DATABASE));
	}
	
	public static final class DatabaseConfig {
		private static final String PATH = "path";
		
		private final JSONObject json;
		
		protected DatabaseConfig(JSONObject json) {
			this.json = json;
		}
		
		public String getDatabasePath() {
			return json.getString(PATH);
		}
	}
}