package skylark.settings;

import org.pircbotx.Channel;
import skylark.PluginInfo;

public class Plugin extends skylark.Plugin {
	protected Settings settings = null;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		settings = new Settings(this);
		settings.read();
	}
	
	protected void onUnload() {
		settings = null;
	}
	
	public SettingsContext getContext() {
		return settings.getContext();
	}
	
	public SettingsContext getContext(String server) {
		return settings.getContext(server);
	}
	
	public SettingsContext getContext(String server, String channel) {
		return settings.getContext(server, channel);
	}
	
	public SettingsContext getContext(Channel channel) {
		return settings.getContext(channel);
	}
	
	public SettingsContext getContext(PluginInfo pinfo) {
		return settings.getContext(pinfo);
	}
	
	public SettingsContext getContext(PluginInfo pinfo, String server) {
		return settings.getContext(pinfo, server);
	}
	
	public SettingsContext getContext(PluginInfo pinfo, String server, String channel) {
		return settings.getContext(pinfo, server, channel);
	}
	
	public SettingsContext getContext(PluginInfo pinfo, Channel channel) {
		return settings.getContext(pinfo, channel);
	}
	
	public SettingsContext getContext(skylark.Plugin plugin) {
		return settings.getContext(plugin);
	}
	
	public SettingsContext getContext(skylark.Plugin plugin, String server) {
		return settings.getContext(plugin, server);
	}
	
	public SettingsContext getContext(skylark.Plugin plugin, String server, String channel) {
		return settings.getContext(plugin, server, channel);
	}
	
	public SettingsContext getContext(skylark.Plugin plugin, Channel channel) {
		return settings.getContext(plugin, channel);
	}
	
	public <T> Setting<T> getSetting(String key) {
		return settings.getSetting(key);
	}
	
	public <T> Setting<T> getSetting(PluginInfo pinfo, String key) {
		return settings.getSetting(pinfo, key);
	}
	
	public <T> Setting<T> getSetting(skylark.Plugin plugin, String key) {
		return settings.getSetting(plugin, key);
	}
}