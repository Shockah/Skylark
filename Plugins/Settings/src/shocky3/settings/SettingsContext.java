package shocky3.settings;

import java.util.HashMap;
import java.util.Map;

public class SettingsContext {
	public static final String
		GLOBAL = "global",
		SERVER = "server",
		CHANNEL = "channel";
	
	protected final Settings settings;
	protected final String server;
	protected final String channel;
	
	protected SettingsContext(Settings settings, String server, String channel) {
		this.settings = settings;
		this.server = server == null ? null : server.toLowerCase();
		this.channel = channel == null ? null : channel.toLowerCase();
	}
	
	public String getContextIdentifier() {
		if (server != null) {
			if (channel != null)
				return CHANNEL;
			return SERVER;
		}
		return GLOBAL;
	}
	
	public SettingsContext getParentContext() {
		if (server != null) {
			if (channel != null)
				return new SettingsContext(settings, server, null);
			return new SettingsContext(settings, null, null);
		}
		return null;
	}
	
	public SettingsContext getGlobalContext() {
		if (server == null)
			return this;
		return new SettingsContext(settings, null, null);
	}
	
	public SettingsContext getServerContext() {
		if (server != null) {
			if (channel == null)
				return this;
			return new SettingsContext(settings, server, null);
		}
		throw new IllegalStateException("No channel to get the settings context for.");
	}
	
	public boolean containsKey(String key) {
		if (server != null) {
			if (channel != null) {
				Settings.ChannelContext channelContext = new Settings.ChannelContext(server, channel);
				if (settings.perChannel.containsKey(channelContext) && settings.perChannel.get(channelContext).containsKey(key))
					return true;
			}
			if (settings.perServer.containsKey(server) && settings.perServer.get(server).containsKey(key))
				return true;
		}
		if (settings.global.containsKey(key))
			return true;
		return false;
	}
	
	public boolean containsKeyInContext(String key) {
		if (server != null) {
			if (channel != null) {
				Settings.ChannelContext channelContext = new Settings.ChannelContext(server, channel);
				return settings.perChannel.containsKey(channelContext) && settings.perChannel.get(channelContext).containsKey(key);
			}
			return settings.perServer.containsKey(server) && settings.perServer.get(server).containsKey(key);
		}
		return settings.global.containsKey(key);
	}
	
	public Object get(String key) {
		if (server != null) {
			if (channel != null) {
				Settings.ChannelContext channelContext = new Settings.ChannelContext(server, channel);
				if (settings.perChannel.containsKey(channelContext)) {
					Map<String, Object> perChannel = settings.perChannel.get(channelContext);
					if (perChannel.containsKey(key))
						return perChannel.get(key);
				}
			}
			if (settings.perServer.containsKey(server)) {
				Map<String, Object> perServer = settings.perServer.get(server);
				if (perServer.containsKey(key))
					return perServer.get(key);
			}
		}
		if (settings.global.containsKey(key))
			return settings.global.get(key);
		return null;
	}
	
	public Object getInContext(String key) {
		if (server != null) {
			if (channel != null) {
				Settings.ChannelContext channelContext = new Settings.ChannelContext(server, channel);
				if (settings.perChannel.containsKey(channelContext)) {
					Map<String, Object> perChannel = settings.perChannel.get(channelContext);
					return perChannel.containsKey(key) ? perChannel.get(key) : null;
				}
			}
			if (settings.perServer.containsKey(server)) {
				Map<String, Object> perServer = settings.perServer.get(server);
				return perServer.containsKey(key) ? perServer.get(key) : null;
			}
		}
		return settings.global.containsKey(key) ? settings.global.get(key) : null;
	}
	
	public Object get(String key, Object def) {
		if (containsKey(key))
			return get(key);
		return def;
	}
	
	public Object getInContext(String key, Object def) {
		if (containsKeyInContext(key))
			return getInContext(key);
		return def;
	}
	
	public int getInt(String key) {
		if (containsKey(key))
			return (int)get(key);
		throw new IllegalArgumentException(String.format("No setting with key '%s' found.", key));
	}
	
	public int getIntInContext(String key) {
		if (containsKeyInContext(key))
			return (int)getInContext(key);
		throw new IllegalArgumentException(String.format("No setting with key '%s' found.", key));
	}
	
	public int getInt(String key, int def) {
		return (int)get(key, def);
	}
	
	public int getIntInContext(String key, int def) {
		return (int)getInContext(key, def);
	}
	
	public double getDouble(String key) {
		if (containsKey(key))
			return (double)get(key);
		throw new IllegalArgumentException(String.format("No setting with key '%s' found.", key));
	}
	
	public double getDoubleInContext(String key) {
		if (containsKeyInContext(key))
			return (double)getInContext(key);
		throw new IllegalArgumentException(String.format("No setting with key '%s' found.", key));
	}
	
	public double getDouble(String key, double def) {
		return (double)get(key, def);
	}
	
	public double getDoubleInContext(String key, double def) {
		return (double)getInContext(key, def);
	}
	
	public String getString(String key) {
		if (containsKey(key))
			return (String)get(key);
		throw new IllegalArgumentException(String.format("No setting with key '%s' found.", key));
	}
	
	public String getStringInContext(String key) {
		if (containsKeyInContext(key))
			return (String)getInContext(key);
		throw new IllegalArgumentException(String.format("No setting with key '%s' found.", key));
	}
	
	public String getString(String key, String def) {
		return (String)get(key, def);
	}
	
	public String getStringInContext(String key, String def) {
		return (String)getInContext(key, def);
	}
	
	public void put(String key, Object obj) {
		if (server != null) {
			if (channel != null) {
				Settings.ChannelContext channelContext = new Settings.ChannelContext(server, channel);
				if (!settings.perChannel.containsKey(channelContext))
					settings.perChannel.put(channelContext, new HashMap<String, Object>());
				settings.perChannel.get(channelContext).put(key, obj);
				return;
			}
			if (!settings.perServer.containsKey(server))
				settings.perServer.put(server, new HashMap<String, Object>());
			settings.perServer.get(server).put(key, obj);
			return;
		}
		settings.global.put(key, obj);
	}
	
	public void putDefault(String key, Object obj) {
		if (!containsKeyInContext(key))
			put(key, obj);
	}
}