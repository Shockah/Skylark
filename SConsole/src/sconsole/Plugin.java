package sconsole;

import shocky3.PluginInfo;

public class Plugin extends shocky3.Plugin {
	public Console console = null;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
		console = new Console(this);
	}
	
	protected void onLoad() {
		console.start();
	}
	
	protected void onUnload() {
		console.stop();
	}
}