package shocky3;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
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

public class PluginManager {
	public static final File
		pluginsDir = new File("plugins"),
		libsDir = new File("libs"),
		tempDir = new File("temp");
	
	public static URLClassLoader makeClassLoader() {
		try {
			if (tempDir.exists()) {
				FileUtils.deleteDirectory(tempDir);
			}
			tempDir.mkdir();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<URL> list = new LinkedList<>();
		try {
			List<File> toCheck = new LinkedList<>();
			toCheck.add(pluginsDir);
			while (!toCheck.isEmpty()) {
				File file = toCheck.remove(0);
				if (file.isDirectory()) {
					for (File file2 : file.listFiles()) toCheck.add(file2);
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
				if (file.isDirectory()) {
					for (File file2 : file.listFiles()) toCheck.add(file2);
				} else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
					list.add(file.toURI().toURL());
				}
			}
		} catch (Exception e) {}
		return new URLClassLoader(list.toArray(new URL[0]));
	}
	
	public final Shocky botApp;
	protected List<PluginInfo> pluginInfos = Collections.synchronizedList(new LinkedList<PluginInfo>());
	protected List<Plugin> plugins = Collections.synchronizedList(new LinkedList<Plugin>());
	protected List<PluginInfo> toLoad = Collections.synchronizedList(new LinkedList<PluginInfo>());
	protected URLClassLoader currentClassLoader = null;
	
	public PluginManager(Shocky botApp) {
		this.botApp = botApp;
	}
	
	public void readPlugins() {
		if (!pluginsDir.exists()) {
			pluginsDir.mkdir();
		}
		
		List<File> toCheck = new LinkedList<>();
		toCheck.add(pluginsDir);
		while (!toCheck.isEmpty()) {
			File file = toCheck.remove(0);
			if (file.isDirectory()) {
				for (File file2 : file.listFiles()) toCheck.add(file2);
			} else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
				try (ZipFile zf = new ZipFile(file)) {
					ZipEntry ze = zf.getEntry("plugin.json");
					if (ze == null) continue;
					
					JSONObject j = new JSONParser().parseObject(IOUtils.toString(zf.getInputStream(ze), "UTF-8"));
					PluginInfo pinfo = new PluginInfo(this, file);
					pinfo.jInfo = j;
					pluginInfos.add(pinfo);
				} catch (Exception e) {}
			}
		}
		
		for (PluginInfo pinfo : pluginInfos) {
			if (pinfo.defaultState()) toLoad.add(pinfo);
		}
	}
	
	public void reload() {
		while (!plugins.isEmpty()) {
			actualUnload(plugins.get(plugins.size() - 1));
		}
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
					System.out.println(String.format("Couldn't load %s: missing dependency %s", pinfo.internalName(), s));
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
				for (Map.Entry<PluginInfo, List<PluginInfo>> entry : plugindeps.entrySet()) {
					entry.getValue().remove(pinfo);
				}
			}
			
			if (lastCount == toLoad.size()) {
				break;
			}
		}
		toLoad.clear();
		toLoad.addAll(order);
		
		currentClassLoader = makeClassLoader();
		for (PluginInfo pinfo : toLoad) {
			actualLoad(pinfo);
		}
		for (Plugin plugin : plugins) {
			setReflectionFields(plugin.pinfo);
		}
		for (Plugin plugin : plugins) {
			plugin.onLoad();
		}
		for (Plugin plugin : plugins) {
			plugin.postLoad();
			System.out.println("Loaded plugin: " + plugin.pinfo.internalName());
		}
	}
	
	public void markLoad(PluginInfo pinfo) {
		if (toLoad.contains(pinfo)) return;
		toLoad.add(pinfo);
	}
	public void markUnload(PluginInfo pinfo) {
		toLoad.remove(pinfo);
	}
	public boolean markedForLoading(PluginInfo pinfo) {
		return toLoad.contains(pinfo);
	}
	
	private void actualLoad(PluginInfo pinfo) {
		if (pinfo.loaded()) return;
		try {
			pinfo.plugin = (Plugin)currentClassLoader.loadClass(pinfo.baseClass()).getConstructor(PluginInfo.class).newInstance(pinfo);
			pinfo.plugin.preLoad();
			plugins.add(pinfo.plugin);
		} catch (Exception e) {e.printStackTrace();}
	}
	private void actualUnload(Plugin plugin) {
		try {
			plugins.remove(plugin);
			plugin.preUnload();
			plugin.onUnload();
			plugin.pinfo.plugin = null;
			System.out.println("Unloaded plugin: " + plugin.pinfo.internalName());
		} catch (Exception e) {}
	}
	
	private void setReflectionFields(PluginInfo pinfo) {
		for (Field field : pinfo.plugin.getClass().getDeclaredFields()) {
			Plugin.Dependency pluginDependency = field.getAnnotation(Plugin.Dependency.class);
			if (pluginDependency != null) {
				L: for (Plugin plugin : plugins) {
					if (field.getType() == plugin.getClass()) {
						try {
							field.setAccessible(true);
							if (Modifier.isStatic(field.getModifiers())) {
								field.set(null, plugin);
							} else {
								field.set(pinfo.plugin, plugin);
							}
							break L;
						} catch (Exception e) {e.printStackTrace();}
					}
				}
			}
		}
	}
	
	public List<Plugin> plugins() {
		return Collections.unmodifiableList(plugins);
	}
	
	public PluginInfo byPluginInfoInternalName(String name) {
		for (PluginInfo pinfo : pluginInfos) {
			if (pinfo.internalName().equals(name)) {
				return pinfo;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked") public <T extends Plugin> T byInternalName(String name) {
		for (Plugin plugin : plugins) {
			if (plugin.pinfo.internalName().equals(name)) {
				return (T)plugin;
			}
		}
		return null;
	}
}