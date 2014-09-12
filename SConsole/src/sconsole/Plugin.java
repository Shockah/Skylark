package sconsole;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import pl.shockah.Util;
import shocky3.PluginInfo;

public class Plugin extends shocky3.Plugin {
	public boolean running = false, stopped = false;
	public ConsoleThread thread = null;
	protected final List<IConsolePluginListener> listeners = Collections.synchronizedList(new LinkedList<IConsolePluginListener>());
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	public void add(IConsolePluginListener... icpls) {
		synchronized (listeners) {for (IConsolePluginListener icpl : icpls) {
			if (!listeners.contains(icpl)) {
				listeners.add(icpl);
			}
		}}
	}
	public void remove(IConsolePluginListener... icpls) {
		synchronized (listeners) {for (IConsolePluginListener icpl : icpls) {
			listeners.remove(icpl);
		}}
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