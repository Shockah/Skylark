package shocky3;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import pl.shockah.json.JSONPrettyPrinter;
import shocky3.util.Synced;

public class PluginManager {
	public static final File
		pluginsDir = new File("plugins"),
		libsDir = new File("libs"),
		tempDir = new File("pluginstemp"),
		configFile = new File("pluginConfig.json");
	
	public static URLClassLoader makeClassLoader() {
		try {
			if (tempDir.exists())
				FileUtils.deleteDirectory(tempDir);
			tempDir.mkdir();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<URL> list = new ArrayList<>();
		try {
			List<File> toCheck = new LinkedList<>();
			toCheck.add(pluginsDir);
			while (!toCheck.isEmpty()) {
				File file = toCheck.remove(0);
				if (file.isDirectory()) {
					for (File file2 : file.listFiles())
						toCheck.add(file2);
				} else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
					String[] spl = file.getName().split("\\.");
					File fileTemp = new File(tempDir, "" + list.size() + "." + spl[spl.length - 1]);
					FileUtils.copyFile(file, fileTemp);
					list.add(fileTemp.toURI().toURL());
				}
			}
			
			toCheck.add(libsDir);
			while (!toCheck.isEmpty()) {
				File file = toCheck.remove(0);
				if (file.isDirectory())
					for (File file2 : file.listFiles())
						toCheck.add(file2);
				else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))
					list.add(file.toURI().toURL());
			}
		} catch (Exception e) { }
		return new URLClassLoader(list.toArray(new URL[0]));
	}
	
	public final Shocky botApp;
	protected List<PluginInfo> pluginInfos = Synced.list();
	public List<Plugin> plugins = Synced.list();
	protected List<PluginInfo> toLoad = Synced.list();
	protected URLClassLoader currentClassLoader = null;
	protected JSONObject enabledInConfig = new JSONObject();
	
	public PluginManager(Shocky botApp) {
		this.botApp = botApp;
	}
	
	public void readPlugins() {
		if (!pluginsDir.exists())
			pluginsDir.mkdir();
		try {
			if (configFile.exists())
				enabledInConfig = new JSONParser().parseObject(FileUtils.readFileToString(configFile));
			else
				enabledInConfig.clear();
		} catch (Exception e) { }
		
		synchronized (pluginInfos) {
			synchronized (toLoad) {
				List<File> toCheck = new LinkedList<>();
				toCheck.add(pluginsDir);
				while (!toCheck.isEmpty()) {
					File file = toCheck.remove(0);
					if (file.isDirectory()) {
						for (File file2 : file.listFiles())
							toCheck.add(file2);
					} else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
						try (ZipFile zf = new ZipFile(file)) {
							ZipEntry ze = zf.getEntry("plugin.json");
							if (ze == null)
								continue;
							
							JSONObject j = new JSONParser().parseObject(IOUtils.toString(zf.getInputStream(ze), "UTF-8"));
							PluginInfo pinfo = new PluginInfo(this, file);
							pinfo.jInfo = j;
							pluginInfos.add(pinfo);
						} catch (Exception e) { }
					}
				}
				
				for (PluginInfo pinfo : pluginInfos) {
					String packageName = pinfo.packageName();
					if (!enabledInConfig.contains(packageName))
						enabledInConfig.put(packageName, pinfo.defaultState());
					if (enabledInConfig.getBoolean(packageName))
						toLoad.add(pinfo);
				}
				try {
					FileUtils.write(configFile, new JSONPrettyPrinter().print(enabledInConfig));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void reload() {
		synchronized (plugins) {
			synchronized (toLoad) {
				Synced.forEach(botApp.serverManager.botManagers, bm -> {
					Synced.forEach(bm.bots, bot -> {
						for (Plugin plugin : plugins)
							if (plugin instanceof ListenerPlugin)
								bot.getConfiguration().getListenerManager().removeListener(((ListenerPlugin)plugin).listener);
					});
				});
				while (!plugins.isEmpty())
					actualUnload(plugins.get(plugins.size() - 1));
				if (currentClassLoader != null) {
					try {
						currentClassLoader.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					currentClassLoader = null;
				}
				
				List<PluginInfo> dontLoad = new LinkedList<>();
				
				Map<PluginInfo, List<PluginInfo>> plugindeps = new HashMap<>();
				L: for (PluginInfo pinfo : toLoad) {
					List<PluginInfo> deps = new LinkedList<PluginInfo>();
					for (String s : pinfo.dependsOn()) {
						PluginInfo pinfo2 = byPluginInfoInternalName(s);
						if (pinfo2 == null) {
							dontLoad.add(pinfo);
							System.out.println(String.format("Couldn't load %s: missing dependency %s", pinfo.packageName(), s));
							continue L;
						} else {
							deps.add(pinfo2);
						}
					}
					plugindeps.put(pinfo, deps);
				}
				toLoad.removeAll(dontLoad);
				
				List<PluginInfo> order = new LinkedList<>();
				while (!toLoad.isEmpty()) {
					int lastCount = toLoad.size();
					List<PluginInfo> toRemove = new LinkedList<>();
					
					for (PluginInfo pinfo : toLoad) {
						List<PluginInfo> deps = plugindeps.get(pinfo);
						if (deps.isEmpty()) {
							order.add(pinfo);
							toRemove.add(pinfo);
						}
					}
					
					for (PluginInfo pinfo : toRemove) {
						toLoad.remove(pinfo);
						for (Map.Entry<PluginInfo, List<PluginInfo>> entry : plugindeps.entrySet())
							entry.getValue().remove(pinfo);
					}
					
					if (lastCount == toLoad.size())
						break;
				}
				toLoad.clear();
				toLoad.addAll(order);
				
				currentClassLoader = makeClassLoader();
				for (PluginInfo pinfo : toLoad)
					try {
						actualLoad(pinfo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				for (Plugin plugin : plugins)
					setReflectionFields(plugin.pinfo);
				Synced.forEach(botApp.serverManager.botManagers, bm -> {
					Synced.forEach(bm.bots, bot -> {
						for (Plugin plugin : plugins)
							if (plugin instanceof ListenerPlugin)
								bot.getConfiguration().getListenerManager().addListener(((ListenerPlugin)plugin).listener);
					});
				});
				for (Plugin plugin : plugins)
					try {
						plugin.onLoad();
					} catch (Exception e) {
						e.printStackTrace();
					}
				for (Plugin plugin : plugins)
					try {
						plugin.postLoad();
						System.out.println("Loaded plugin: " + plugin.pinfo.packageName());
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
	}
	
	public void markLoad(PluginInfo pinfo) {
		synchronized (toLoad) {
			if (toLoad.contains(pinfo))
				return;
			toLoad.add(pinfo);
		}
	}
	public void markUnload(PluginInfo pinfo) {
		toLoad.remove(pinfo);
	}
	public boolean markedForLoading(PluginInfo pinfo) {
		return toLoad.contains(pinfo);
	}
	
	private void actualLoad(PluginInfo pinfo) {
		if (pinfo.loaded())
			return;
		try {
			pinfo.plugin = (Plugin)currentClassLoader.loadClass(pinfo.baseClass()).getConstructor(PluginInfo.class).newInstance(pinfo);
			plugins.add(pinfo.plugin);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void actualUnload(Plugin plugin) {
		try {
			plugins.remove(plugin);
			plugin.onUnload();
			plugin.pinfo.plugin = null;
			System.out.println("Unloaded plugin: " + plugin.pinfo.packageName());
		} catch (Exception e) { }
	}
	
	private void setReflectionFields(PluginInfo pinfo) {
		synchronized (plugins) {
			for (Field field : pinfo.plugin.getClass().getDeclaredFields()) {
				Plugin.Dependency pluginDependency = field.getAnnotation(Plugin.Dependency.class);
				if (pluginDependency != null) {
					String packageName = pluginDependency.packageName();
					if (packageName.equals(""))
						for (Plugin plugin : plugins) {
							if (field.getType() == plugin.getClass())
								try {
									field.setAccessible(true);
									if (Modifier.isStatic(field.getModifiers()))
										field.set(null, plugin);
									else
										field.set(pinfo.plugin, plugin);
									break;
								} catch (Exception e) {
									e.printStackTrace();
								}
						}
					else
						for (Plugin plugin : plugins) {
							if (packageName.equals(plugin.pinfo.packageName()))
								try {
									field.setAccessible(true);
									if (Modifier.isStatic(field.getModifiers()))
										field.set(null, plugin);
									else
										field.set(pinfo.plugin, plugin);
									break;
								} catch (Exception e) {
									e.printStackTrace();
								}
						}
				}
			}
		}
	}
	
	public PluginInfo byPluginInfoInternalName(String name) {
		synchronized (pluginInfos) {
			for (PluginInfo pinfo : pluginInfos)
				if (pinfo.packageName().equals(name))
					return pinfo;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Plugin> T byInternalName(String name) {
		synchronized (plugins) {
			for (Plugin plugin : plugins)
				if (plugin.pinfo.packageName().equals(name))
					return (T)plugin;
		}
		return null;
	}
}