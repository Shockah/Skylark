package shocky3.settings;

import org.pircbotx.Channel;
import shocky3.PluginInfo;

public class Plugin extends shocky3.Plugin {
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
	
	public SettingsContext getContext(shocky3.Plugin plugin) {
		return settings.getContext(plugin);
	}
	
	public SettingsContext getContext(shocky3.Plugin plugin, String server) {
		return settings.getContext(plugin, server);
	}
	
	public SettingsContext getContext(shocky3.Plugin plugin, String server, String channel) {
		return settings.getContext(plugin, server, channel);
	}
	
	public SettingsContext getContext(shocky3.Plugin plugin, Channel channel) {
		return settings.getContext(plugin, channel);
	}
}