package skylark.settings;

import java.util.HashMap;
import java.util.Map;
import skylark.PluginInfo;

public class SettingsContext {
	public static final String
		GLOBAL = "global",
		SERVER = "server",
		CHANNEL = "channel";
	
	protected final Settings settings;
	protected final String prefix;
	protected final String server;
	protected final String channel;
	
	protected SettingsContext(Settings settings, String prefix, String server, String channel) {
		this.settings = settings;
		this.prefix = prefix;
		this.server = server == null ? null : server.toLowerCase();
		this.channel = channel == null ? null : channel.toLowerCase();
	}
	
	protected SettingsContext(Settings settings, PluginInfo pinfo, String server, String channel) {
		this(settings, pinfo.packageName(), server, channel);
	}
	
	protected SettingsContext(Settings settings, skylark.Plugin plugin, String server, String channel) {
		this(settings, plugin.pinfo, server, channel);
	}
	
	protected SettingsContext(Settings settings, String server, String channel) {
		this(settings, (String)null, server, channel);
	}
	
	public String getContextIdentifier() {
		if (server != null) {
			if (channel != null)
				return CHANNEL;
			return SERVER;
		}
		return GLOBAL;
	}
	
	protected String prefixedKey(String key) {
		if (key.indexOf('|') != -1)
			return key;
		if (prefix == null)
			return key;
		return String.format("%s|%s", prefix, key);
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
		String pkey = prefixedKey(key);
		if (server != null) {
			if (channel != null) {
				Settings.ChannelContext channelContext = new Settings.ChannelContext(server, channel);
				if (settings.perChannel.containsKey(channelContext) && settings.perChannel.get(channelContext).containsKey(pkey))
					return true;
			}
			if (settings.perServer.containsKey(server) && settings.perServer.get(server).containsKey(pkey))
				return true;
		}
		if (settings.global.containsKey(pkey))
			return true;
		return false;
	}
	
	public boolean containsKeyInContext(String key) {
		String pkey = prefixedKey(key);
		if (server != null) {
			if (channel != null) {
				Settings.ChannelContext channelContext = new Settings.ChannelContext(server, channel);
				return settings.perChannel.containsKey(channelContext) && settings.perChannel.get(channelContext).containsKey(pkey);
			}
			return settings.perServer.containsKey(server) && settings.perServer.get(server).containsKey(pkey);
		}
		return settings.global.containsKey(pkey);
	}
	
	public Object get(String key) {
		String pkey = prefixedKey(key);
		if (server != null) {
			if (channel != null) {
				Settings.ChannelContext channelContext = new Settings.ChannelContext(server, channel);
				if (settings.perChannel.containsKey(channelContext)) {
					Map<String, Object> perChannel = settings.perChannel.get(channelContext);
					if (perChannel.containsKey(pkey))
						return perChannel.get(pkey);
				}
			}
			if (settings.perServer.containsKey(server)) {
				Map<String, Object> perServer = settings.perServer.get(server);
				if (perServer.containsKey(pkey))
					return perServer.get(pkey);
			}
		}
		if (settings.global.containsKey(pkey))
			return settings.global.get(pkey);
		return null;
	}
	
	public Object getInContext(String key) {
		String pkey = prefixedKey(key);
		if (server != null) {
			if (channel != null) {
				Settings.ChannelContext channelContext = new Settings.ChannelContext(server, channel);
				if (settings.perChannel.containsKey(channelContext)) {
					Map<String, Object> perChannel = settings.perChannel.get(channelContext);
					return perChannel.containsKey(pkey) ? perChannel.get(pkey) : null;
				}
			}
			if (settings.perServer.containsKey(server)) {
				Map<String, Object> perServer = settings.perServer.get(server);
				return perServer.containsKey(pkey) ? perServer.get(pkey) : null;
			}
		}
		return settings.global.containsKey(pkey) ? settings.global.get(pkey) : null;
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
	
	public long getLong(String key) {
		if (containsKey(key))
			return (long)get(key);
		throw new IllegalArgumentException(String.format("No setting with key '%s' found.", key));
	}
	
	public long getLongInContext(String key) {
		if (containsKeyInContext(key))
			return (long)getInContext(key);
		throw new IllegalArgumentException(String.format("No setting with key '%s' found.", key));
	}
	
	public long getLong(String key, long def) {
		return (long)get(key, def);
	}
	
	public long getLongInContext(String key, long def) {
		return (long)getInContext(key, def);
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
		String pkey = prefixedKey(key);
		if (server != null) {
			if (channel != null) {
				Settings.ChannelContext channelContext = new Settings.ChannelContext(server, channel);
				if (!settings.perChannel.containsKey(channelContext))
					settings.perChannel.put(channelContext, new HashMap<String, Object>());
				settings.perChannel.get(channelContext).put(pkey, obj);
				settings.modified(this, pkey);
				return;
			}
			if (!settings.perServer.containsKey(server))
				settings.perServer.put(server, new HashMap<String, Object>());
			settings.perServer.get(server).put(pkey, obj);
			settings.modified(this, pkey);
			return;
		}
		settings.global.put(pkey, obj);
		settings.modified(this, pkey);
	}
	
	public void putDefault(String key, Object obj) {
		if (!containsKeyInContext(key))
			put(key, obj);
	}
	
	public void remove(String key) {
		String pkey = prefixedKey(key);
		if (server != null) {
			if (channel != null) {
				Settings.ChannelContext channelContext = new Settings.ChannelContext(server, channel);
				if (settings.perChannel.containsKey(channelContext)) {
					Map<String, Object> perChannel = settings.perChannel.get(channelContext);
					if (perChannel.containsKey(pkey)) {
						perChannel.remove(pkey);
						settings.removed(this, pkey);
					}
				}
				return;
			}
			if (settings.perServer.containsKey(server)) {
				Map<String, Object> perServer = settings.perServer.get(server);
				if (perServer.containsKey(pkey)) {
					perServer.remove(pkey);
					settings.removed(this, pkey);
				}
			}
			return;
		}
		if (settings.global.containsKey(pkey)) {
			settings.global.remove(pkey);
			settings.removed(this, pkey);
		}
	}
}