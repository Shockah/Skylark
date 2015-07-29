package shocky3.settings;

import shocky3.PluginInfo;

public class Plugin extends shocky3.Plugin {
	public Settings settings = null;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		settings = new Settings(this);
		settings.read();
	}
}