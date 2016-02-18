package skylark.settings;

import org.pircbotx.Channel;
import skylark.old.PluginInfo;

public class Setting<T> {
	protected final Settings settings;
	protected final String prefix;
	protected final String key;
	
	protected Setting(Settings settings, String prefix, String key) {
		this.settings = settings;
		this.prefix = prefix;
		this.key = key;
	}
	
	protected Setting(Settings settings, PluginInfo pinfo, String key) {
		this(settings, pinfo.packageName(), key);
	}
	
	protected Setting(Settings settings, skylark.old.Plugin plugin, String key) {
		this(settings, plugin.pinfo, key);
	}
	
	@SuppressWarnings("unchecked")
	public T get(SettingsContext context) {
		return (T)context.get(key);
	}
	
	public T get() {
		return get(new SettingsContext(settings, prefix, null, null));
	}
	
	public T get(String server) {
		return get(new SettingsContext(settings, prefix, server, null));
	}
	
	public T get(Channel channel) {
		return get(new SettingsContext(settings, prefix, channel.getBot().getServerHostname(), channel.getName()));
	}
	
	public void put(SettingsContext context, T value) {
		context.put(key, value);
	}
	
	public void put(T value) {
		put(new SettingsContext(settings, prefix, null, null), value);
	}
	
	public void put(String server, T value) {
		put(new SettingsContext(settings, prefix, server, null), value);
	}
	
	public void put(Channel channel, T value) {
		put(new SettingsContext(settings, prefix, channel.getBot().getServerHostname(), channel.getName()), value);
	}
	
	public void putDefault(SettingsContext context, T value) {
		context.putDefault(key, value);
	}
	
	public void putDefault(T value) {
		putDefault(new SettingsContext(settings, prefix, null, null), value);
	}
	
	public void putDefault(String server, T value) {
		putDefault(new SettingsContext(settings, prefix, server, null), value);
	}
	
	public void putDefault(Channel channel, T value) {
		putDefault(new SettingsContext(settings, prefix, channel.getBot().getServerHostname(), channel.getName()), value);
	}
	
	public void remove(SettingsContext context) {
		context.remove(key);
	}
	
	public void remove() {
		remove(new SettingsContext(settings, prefix, null, null));
	}
	
	public void remove(String server) {
		remove(new SettingsContext(settings, prefix, server, null));
	}
	
	public void remove(Channel channel) {
		remove(new SettingsContext(settings, prefix, channel.getBot().getServerHostname(), channel.getName()));
	}
}