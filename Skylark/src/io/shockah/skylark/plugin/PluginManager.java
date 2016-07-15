package io.shockah.skylark.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.managers.ListenerManager;
import io.shockah.json.JSONObject;
import io.shockah.json.JSONParser;
import io.shockah.skylark.App;
import io.shockah.skylark.ServerManager;
import io.shockah.skylark.UnexpectedException;
import io.shockah.skylark.util.FileUtils;
import io.shockah.skylark.util.ReadWriteList;

public class PluginManager {
	public static final Path LIBS_PATH = Paths.get("libs");
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
	
	public Path getLibsPath() {
		return LIBS_PATH;
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
			System.out.println(String.format("Unloaded plugin: %s", plugin.info.packageName()));
		});
		clearListenerPlugins();
		clearServices();
		plugins.clear();
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
					try {
						setupRequiredDependencyFields(plugin);
						plugin.onLoad();
						plugins.add(plugin);
						if (plugin instanceof ListenerPlugin)
							setupListenerPlugin((ListenerPlugin)plugin);
						setupServices(plugin);
						System.out.println(String.format("Loaded plugin: %s", pluginInfo.packageName()));
					} catch (Exception e) {
						throw new UnexpectedException(e);
					}
				}
			}
		});
		
		plugins.iterate(plugin -> {
			setupOptionalDependencyFields(plugin);
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
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void setupRequiredDependencyFields(Plugin plugin) {
		for (Field field : plugin.getClass().getDeclaredFields()) {
			try {
				Plugin.Dependency dependencyAnnotation = field.getAnnotation(Plugin.Dependency.class);
				if (dependencyAnnotation != null) {
					if (dependencyAnnotation.value().equals("")) {
						Class<? extends Plugin> clazz = (Class<? extends Plugin>)field.getType();
						if (clazz == Plugin.class)
							continue;
						Plugin dependency = getPluginWithClass(clazz);
						if (dependency != null) {
							field.setAccessible(true);
							field.set(plugin, dependency);
							plugin.onDependencyLoaded(plugin);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void setupOptionalDependencyFields(Plugin plugin) {
		for (Field field : plugin.getClass().getDeclaredFields()) {
			try {
				Plugin.Dependency dependencyAnnotation = field.getAnnotation(Plugin.Dependency.class);
				if (dependencyAnnotation != null) {
					if (!dependencyAnnotation.value().equals("")) {
						Plugin dependency = getPluginWithPackageName(dependencyAnnotation.value());
						if (dependency != null) {
							field.setAccessible(true);
							field.set(plugin, dependency);
							plugin.onDependencyLoaded(plugin);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void setupListenerPlugin(ListenerPlugin plugin) {
		app.serverManager.botManagers.iterate(botManager -> {
			botManager.bots.iterate(bot -> {
				bot.getConfiguration().getListenerManager().addListener(plugin.listener);
			});
		});
	}
	
	protected void clearListenerPlugins() {
		app.serverManager.botManagers.iterate(botManager -> {
			botManager.bots.iterate(bot -> {
				ListenerManager manager = bot.getConfiguration().getListenerManager();
				for (Listener listener : manager.getListeners()) {
					if (listener instanceof ListenerPlugin.MyListener)
						manager.removeListener(listener);
				}
			});
		});
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
		return (T)plugins.filterFirst(plugin -> clazz.isInstance(plugin));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Plugin> T getPluginWithPackageName(String name) {
		return (T)plugins.filterFirst(plugin -> plugin.info.packageName().equals(name));
	}
	
	protected List<Plugin.Info> findPlugins() {
		List<Plugin.Info> infos = new ArrayList<>();
		
		try {
			for (Path path : Files.newDirectoryStream(getPluginPath(), path -> path.getFileName().toString().endsWith(".jar"))) {
				Path tmpPath = FileUtils.copyAsTrueTempFile(path);
				
				try (ZipFile zf = new ZipFile(tmpPath.toFile())) {
					ZipEntry ze = zf.getEntry("plugin.json");
					if (ze == null)
						continue;
					
					JSONObject pluginJson = new JSONParser().parseObject(new String(IOUtils.toByteArray(zf.getInputStream(ze)), "UTF-8"));
					infos.add(new Plugin.Info(pluginJson, tmpPath.toUri().toURL()));
				} catch (Exception e) {
					throw new UnexpectedException(e);
				}
			}
		} catch (Exception e) {
			throw new UnexpectedException(e);
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
	
	protected ClassLoader createClassLoader(ReadWriteList<Plugin.Info> infos) {
		List<URL> urls = new ArrayList<>();
		try {
			for (Path path : Files.newDirectoryStream(getLibsPath(), path -> path.getFileName().toString().endsWith(".jar"))) {
				Path tmpPath = FileUtils.copyAsTrueTempFile(path);
				urls.add(tmpPath.toUri().toURL());
			}
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
		infos.iterate(info -> urls.add(info.url));
		return new URLClassLoader(urls.toArray(new URL[0]));
	}
	
	protected boolean shouldEnable(Plugin.Info info) {
		return info.enabledByDefault();
	}
}