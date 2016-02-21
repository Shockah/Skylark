package me.shockah.skylark.plugin;

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
import io.shockah.json.JSONObject;
import io.shockah.json.JSONParser;
import me.shockah.skylark.App;
import me.shockah.skylark.Bot;
import me.shockah.skylark.BotManager;
import me.shockah.skylark.ServerManager;
import me.shockah.skylark.util.FileUtils;
import me.shockah.skylark.util.PathClassLoader;
import me.shockah.skylark.util.Sync;

public class PluginManager {
	public static final Path PLUGIN_PATH = Paths.get("plugins");
	
	public final App app;
	
	public ClassLoader pluginClassLoader = null;
	public List<Plugin.Info> pluginInfos = Collections.synchronizedList(new ArrayList<>());
	public List<Plugin> plugins = Collections.synchronizedList(new ArrayList<>());
	public List<BotManagerService> botManagerServices = Collections.synchronizedList(new ArrayList<>());
	public List<BotService> botServices = Collections.synchronizedList(new ArrayList<>());
	
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
			clearServices();
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
			List<Plugin.Info> infos = findPlugins();
			infos = dependencySort(infos);
			pluginInfos.addAll(infos);
			pluginClassLoader = createClassLoader(pluginInfos);
			
			for (Plugin.Info info : pluginInfos) {
				if (shouldEnable(info)) {
					Plugin plugin = loadPlugin(pluginClassLoader, info);
					if (plugin != null) {
						plugins.add(plugin);
						plugin.onLoad();
						setupServices(plugin);
					}
				}
			}
			
			for (Plugin plugin : plugins) {
				setupDependencyFields(plugin);
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
	protected void setupDependencyFields(Plugin plugin) {
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
	
	protected void setupServices(Plugin plugin) {
		if (plugin instanceof BotManagerService) {
			BotManagerService service = (BotManagerService)plugin;
			botManagerServices.add(service);
			
			ServerManager serverManager = app.serverManager;
			synchronized (serverManager.botManagers) {
				for (BotManager botManager : serverManager.botManagers) {
					botManager.services.add(service.createService(botManager));
				}
			}
		}
		if (plugin instanceof BotService) {
			BotService service = (BotService)plugin;
			botServices.add(service);
			
			ServerManager serverManager = app.serverManager;
			synchronized (serverManager.botManagers) {
				for (BotManager botManager : serverManager.botManagers) {
					synchronized (botManager.bots) {
						for (Bot bot : botManager.bots) {
							bot.services.add(service.createService(bot));
						}
					}
				}
			}
		}
	}
	
	protected void clearServices() {
		ServerManager serverManager = app.serverManager;
		synchronized (serverManager.botManagers) {
			for (BotManager botManager : serverManager.botManagers) {
				botManager.services.clear();
				synchronized (botManager.bots) {
					for (Bot bot : botManager.bots) {
						bot.services.clear();
					}
				}
			}
		}
		
		botManagerServices.clear();
		botServices.clear();
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
	
	protected List<Plugin.Info> dependencySort(List<Plugin.Info> input) {
		input = new ArrayList<>(input);
		List<Plugin.Info> output = new ArrayList<>(input.size());
		List<String> loadedPackageNames = new ArrayList<>(input.size());
		
		while (!input.isEmpty()) {
			int oldSize = input.size();
			
			for (int i = 0; i < input.size(); i++) {
				Plugin.Info info = input.get(i);
				
				boolean allDependenciesLoaded = true;
				for (String dependency : info.dependsOn()) {
					if (!loadedPackageNames.contains(dependency)) {
						allDependenciesLoaded = false;
						break;
					}
				}
				if (allDependenciesLoaded) {
					loadedPackageNames.add(info.packageName());
					output.add(info);
					input.remove(i--);
				}
			}
			
			if (oldSize == input.size()) {
				//TODO: log plugins with missing dependencies (the ones left in $input)
				break;
			}
		}
		
		return output;
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