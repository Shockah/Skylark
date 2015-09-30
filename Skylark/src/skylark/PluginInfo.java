package skylark;

import java.io.File;
import java.util.List;
import pl.shockah.json.JSONObject;

public final class PluginInfo {
	public final Skylark botApp;
	public final PluginManager manager;
	public final File pluginFile;
	public JSONObject jInfo = null;
	protected Plugin plugin = null;
	
	public PluginInfo(PluginManager manager, File pluginFile) {
		this.botApp = manager.botApp;
		this.manager = manager;
		this.pluginFile = pluginFile;
	}
	
	public String baseClass() { return jInfo.getString("baseClass"); }
	public String packageName() { return jInfo.getString("packageName"); }
	public List<String> dependsOn() { return jInfo.getListOrNew("dependsOn").ofStrings(); }
	public String name() { return jInfo.getString("name", null); }
	public String version() { return jInfo.getString("version", null); }
	public String author() { return jInfo.getString("author", null); }
	public boolean defaultState() { return jInfo.getBoolean("defaultState", true); }
	
	public void markLoad() {
		if (markedForLoading())
			return;
		manager.markLoad(this);
	}
	public void markUnload() {
		if (!markedForLoading())
			return;
		manager.markUnload(this);
	}
	public boolean markedForLoading() {
		return manager.markedForLoading(this);
	}
	public boolean loaded() {
		return plugin != null;
	}
}