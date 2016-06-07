package me.shockah.skylark.plugin;

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
import java.util.List;
import me.shockah.skylark.App;
import me.shockah.skylark.ServerManager;
import me.shockah.skylark.util.FileUtils;
import me.shockah.skylark.util.PathClassLoader;
import me.shockah.skylark.util.ReadWriteList;

public class PluginManager {
	public static final Path PLUGIN_PATH = Paths.get("plugins");
	
	public final App app;
	
	public ClassLoader pluginClassLoader = null;
	public ReadWriteList<Plugin.Info> pluginInfos = new ReadWriteList<>(new ArrayList<>());
	public ReadWriteList<Plugin> plugins = new ReadWriteList<>(new ArrayList<>());
	public ReadWriteList<BotManagerService.Factory> botManagerServiceFactories = new ReadWriteList<>(new ArrayList<>());
	public ReadWriteList<BotManagerService> botManagerServices = new ReadWriteList<>(new ArrayList<>());
	public ReadWriteList<BotService.Factory> botServiceFactories = new ReadWriteList<>(new ArrayList<>());
	public ReadWriteList<BotService> botServices = new ReadWriteList<>(new ArrayList<>());
	
	public PluginManager(App app) {
		this.app = app;
	}
	
	public Path getPluginPath() {
		return PLUGIN_PATH;
	}
	
	public void reload() {
		unload();
		load();
	}
	
	protected void unload() {
		plugins.iterate(plugin -> {
			plugin.onUnload();
		});
		clearServices();
		plugins.clear();
		
		pluginInfos.iterate(pluginInfo -> {
			try {
				pluginInfo.close();
			} catch (Exception e) {
			}
		});
		pluginInfos.clear();
		
		pluginClassLoader = null;
	}
	
	protected void load() {
		List<Plugin.Info> infos = findPlugins();
		infos = dependencySort(infos);
		pluginInfos.addAll(infos);
		pluginClassLoader = createClassLoader(pluginInfos);
		
		pluginInfos.iterate(pluginInfo -> {
			if (shouldEnable(pluginInfo)) {
				Plugin plugin = loadPlugin(pluginClassLoader, pluginInfo);
				if (plugin != null) {
					plugins.add(plugin);
					plugin.onLoad();
					setupServices(plugin);
				}
			}
		});
		
		plugins.iterate(plugin -> {
			setupDependencyFields(plugin);
		});
		
		plugins.iterate(plugin -> {
			plugin.onAllPluginsLoaded();
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
		if (plugin instanceof BotManagerService.Factory) {
			BotManagerService.Factory factory = (BotManagerService.Factory)plugin;
			botManagerServiceFactories.add(factory);
			
			ServerManager serverManager = app.serverManager;
			serverManager.botManagers.iterate(botManager -> {
				BotManagerService service = factory.createService(botManager);
				botManager.services.add(service);
				botManagerServices.add(service);
			});
		}
		if (plugin instanceof BotService.Factory) {
			BotService.Factory factory = (BotService.Factory)plugin;
			botServiceFactories.add(factory);
			
			ServerManager serverManager = app.serverManager;
			serverManager.botManagers.iterate(botManager -> {
				botManager.bots.iterate(bot -> {
					BotService service = factory.createService(bot);
					bot.services.add(service);
					botServices.add(service);
				});
			});
		}
	}
	
	protected void clearServices() {
		ServerManager serverManager = app.serverManager;
		serverManager.botManagers.iterate(botManager -> {
			botManager.services.clear();
			botManager.bots.iterate(bot -> {
				bot.services.clear();
			});
		});
		
		botManagerServiceFactories.clear();
		botManagerServices.clear();
		botServiceFactories.clear();
		botServices.clear();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Plugin> T getPluginWithClass(Class<T> clazz) {
		return (T)plugins.findOne(plugin -> clazz.isInstance(plugin));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Plugin> T getPluginWithPackageName(String name) {
		return (T)plugins.findOne(plugin -> plugin.info.packageName().equals(name));
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