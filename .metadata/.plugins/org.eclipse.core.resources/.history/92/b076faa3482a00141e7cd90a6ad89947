package sconsole;

import pl.shockah.Util;
import shocky3.PluginInfo;

public class Plugin extends shocky3.Plugin {
	public boolean running = false, stopped = false;
	public ConsoleThread thread = null;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		running = true;
		thread = new ConsoleThread(this);
		thread.start();
	}
	
	protected void onUnload() {
		running = false;
		while (!stopped) {
			Util.sleep(50);
		}
	}
}