package io.shockah.skylark.plugin;

import io.shockah.json.JSONObject;
import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.FileSystem;
import java.util.Collections;
import java.util.List;

public class Plugin {
	public final PluginManager manager;
	public final Info info;
	
	public Plugin(PluginManager manager, Info info) {
		this.manager = manager;
		this.info = info;
	}
	
	protected void onLoad() {
	}
	
	protected void onUnload() {
	}
	
	protected void onDependencyLoaded(Plugin plugin) {
	}
	
	protected void onDependencyUnloaded(Plugin plugin) {
	}
	
	protected void onAllPluginsLoaded() {
	}
	
	public static final class Info implements Closeable {
		public final JSONObject json;
		public final FileSystem fileSystem;
		
		public Info(JSONObject json, FileSystem fileSystem) {
			this.json = json;
			this.fileSystem = fileSystem;
		}
		
		public String packageName() {
			return json.getString("packageName");
		}
		
		public String baseClass() {
			return json.getString("baseClass");
		}
		
		public List<String> dependsOn() {
			return Collections.unmodifiableList(json.getListOrEmpty("dependsOn").ofStrings());
		}
		
		public boolean enabledByDefault() {
			return json.getBool("enabledByDefault", true);
		}
		
		public String name() {
			return json.containsKey("name") ? json.getString("name") : packageName();
		}
		
		public String author() {
			return json.getString("author", null);
		}
		
		public String description() {
			return json.getString("description", null);
		}

		@Override
		public void close() throws IOException {
			fileSystem.close();
		}
	}
	
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
	public static @interface Dependency {
		public String packageName() default "";
	}
}