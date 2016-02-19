package me.shockah.skylark;

import io.shockah.json.JSONObject;
import io.shockah.json.JSONParser;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.shockah.skylark.util.FileUtils;
import me.shockah.skylark.util.PathClassLoader;
import me.shockah.skylark.util.Sync;

public class PluginManager {
	public static final Path PLUGIN_PATH = Paths.get("plugins");
	
	public final App app;
	
	public ClassLoader pluginClassLoader = null;
	public List<Plugin.Info> pluginInfos = Collections.synchronizedList(new ArrayList<>());
	public List<Plugin> plugins = Collections.synchronizedList(new ArrayList<>());
	
	public PluginManager(App app) {
		this.app = app;
	}
	
	public Path getPluginPath() {
		return PLUGIN_PATH;
	}
	
	public void reload() {
		Sync.on(pluginInfos, plugins, () -> {
			unload();
			load();
		});
	}
	
	protected void unload() {
		Sync.on(pluginInfos, plugins, () -> {
			for (Plugin plugin : plugins) {
				plugin.onUnload();
			}
			plugins.clear();
			
			for (Plugin.Info info : pluginInfos) {
				try {
					info.close();
				} catch (Exception e) {
				}
			}
			pluginInfos.clear();
			
			pluginClassLoader = null;
		});
	}
	
	protected void load() {
		Sync.on(pluginInfos, plugins, () -> {
			pluginInfos.addAll(findPlugins());
			pluginClassLoader = createClassLoader(pluginInfos);
			
			for (Plugin.Info info : pluginInfos) {
				if (shouldEnable(info)) {
					Plugin plugin = loadPlugin(pluginClassLoader, info);
					if (plugin != null) {
						plugins.add(plugin);
						plugin.onLoad();
					}
				}
			}
			
			for (Plugin plugin : plugins) {
				setupDependencies(plugin);
			}
			
			for (Plugin plugin : plugins) {
				plugin.onAllPluginsLoaded();
			}
		});
	}
	
	protected Plugin loadPlugin(ClassLoader classLoader, Plugin.Info info) {
		try {
			Class<?> clazz = classLoader.loadClass(info.baseClass());
			Constructor<?> ctor = clazz.getConstructor(PluginManager.class, Plugin.Info.class);
			return (Plugin)ctor.newInstance(this, info);
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void setupDependencies(Plugin plugin) {
		for (Field field : plugin.getClass().getDeclaredFields()) {
			try {
				Plugin.Dependency dependencyAnnotation = field.getAnnotation(Plugin.Dependency.class);
				if (dependencyAnnotation != null) {
					if (dependencyAnnotation.packageName().equals("")) {
						Class<? extends Plugin> clazz = (Class<? extends Plugin>)field.getType();
						if (clazz == Plugin.class)
							continue;
						Plugin dependency = getPluginWithClass(clazz);
						if (dependency != null) {
							field.setAccessible(true);
							field.set(plugin, dependency);
							plugin.onDependencyLoaded(plugin);
						}
					} else {
						Plugin dependency = getPluginWithPackageName(dependencyAnnotation.packageName());
						if (dependency != null) {
							field.setAccessible(true);
							field.set(plugin, dependency);
							plugin.onDependencyLoaded(plugin);
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Plugin> T getPluginWithClass(Class<T> clazz) {
		synchronized (plugins) {
			for (Plugin plugin : plugins) {
				if (clazz.isInstance(plugin))
					return (T)plugin;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Plugin> T getPluginWithPackageName(String name) {
		synchronized (plugins) {
			for (Plugin plugin : plugins) {
				if (plugin.info.packageName().equals(name))
					return (T)plugin;
			}
		}
		return null;
	}
	
	protected List<Plugin.Info> findPlugins() {
		List<Plugin.Info> infos = new ArrayList<>();
		
		try {
			for (Path path : Files.newDirectoryStream(getPluginPath(), (path) -> path.getFileName().toString().endsWith(".jar"))) {
				Path tmpPath = FileUtils.copyAsTrueTempFile(path);
				FileSystem fs = FileSystems.newFileSystem(tmpPath, null);
				
				Path pluginJsonPath = fs.getPath("plugin.json");
				if (Files.exists(pluginJsonPath)) {
					JSONObject pluginJson = new JSONParser().parseObject(new String(Files.readAllBytes(pluginJsonPath), "UTF-8"));
					infos.add(new Plugin.Info(pluginJson, fs));
				}
			}
		} catch (Exception e) {
		}
		
		return infos;
	}
	
	protected ClassLoader createClassLoader(List<Plugin.Info> infos) {
		List<Path> paths = new ArrayList<>();
		for (Plugin.Info info : infos) {
			paths.add(info.fileSystem.getPath("/"));
		}
		return new PathClassLoader(paths);
	}
	
	protected boolean shouldEnable(Plugin.Info info) {
		return info.enabledByDefault();
	}
}