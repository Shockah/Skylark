package skylark.settings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.pircbotx.Channel;
import pl.shockah.json.JSONObject;
import skylark.PluginInfo;
import skylark.util.JSON;

public class Settings {
	protected static JSONObject buildQueryJSON(SettingsContext context, String key) {
		JSONObject query = JSONObject.make(
			"context", context.getContextIdentifier(),
			"key", key
		);
		if (context.server != null) {
			query.put("server", context.server);
			if (context.channel != null)
				query.put("channel", context.channel);
		}
		return query;
	}
	
	public final Plugin plugin;
	
	protected final Map<String, Object>
		global = Collections.synchronizedMap(new HashMap<String, Object>());
	protected final Map<ChannelContext, Map<String, Object>>
		perChannel = Collections.synchronizedMap(new HashMap<ChannelContext, Map<String, Object>>());
	protected final Map<String, Map<String, Object>>
		perServer = Collections.synchronizedMap(new HashMap<String, Map<String, Object>>());
	
	public Settings(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public SettingsContext getContext() {
		return new SettingsContext(this, null, null);
	}
	
	public SettingsContext getContext(String server) {
		return new SettingsContext(this, server, null);
	}
	
	public SettingsContext getContext(String server, String channel) {
		return new SettingsContext(this, server, channel);
	}
	
	public SettingsContext getContext(Channel channel) {
		return new SettingsContext(this, channel.getBot().getServerHostname(), channel.getName());
	}
	
	public SettingsContext getContext(PluginInfo pinfo) {
		return new SettingsContext(this, pinfo, null, null);
	}
	
	public SettingsContext getContext(PluginInfo pinfo, String server) {
		return new SettingsContext(this, pinfo, server, null);
	}
	
	public SettingsContext getContext(PluginInfo pinfo, String server, String channel) {
		return new SettingsContext(this, pinfo, server, channel);
	}
	
	public SettingsContext getContext(PluginInfo pinfo, Channel channel) {
		return new SettingsContext(this, pinfo, channel.getBot().getServerHostname(), channel.getName());
	}
	
	public SettingsContext getContext(skylark.Plugin plugin) {
		return new SettingsContext(this, plugin, null, null);
	}
	
	public SettingsContext getContext(skylark.Plugin plugin, String server) {
		return new SettingsContext(this, plugin, server, null);
	}
	
	public SettingsContext getContext(skylark.Plugin plugin, String server, String channel) {
		return new SettingsContext(this, plugin, server, channel);
	}
	
	public SettingsContext getContext(skylark.Plugin plugin, Channel channel) {
		return new SettingsContext(this, plugin, channel.getBot().getServerHostname(), channel.getName());
	}
	
	public <T> Setting<T> getSetting(String key) {
		return new Setting<T>(this, (String)null, key);
	}
	
	public <T> Setting<T> getSetting(PluginInfo pinfo, String key) {
		return new Setting<T>(this, pinfo, key);
	}
	
	public <T> Setting<T> getSetting(skylark.Plugin plugin, String key) {
		return new Setting<T>(this, plugin, key);
	}
	
	public void read() {
		JSON.forEachJSONObject(plugin.botApp.collection("settings").find(), j -> {
			SettingsContext context = null;
			switch (j.getString("context", "global")) {
				case "global":
					context = getContext();
					break;
				case "server":
					context = getContext(j.getString("server"));
					break;
				case "channel":
					context = getContext(j.getString("server"), j.getString("channel"));
					break;
			}
			if (context != null)
				context.put(j.getString("key"), j.get("value"));
		});
	}
	
	protected void modified(SettingsContext context, String key) {
		JSONObject query = buildQueryJSON(context, key);
		JSONObject j = query.copy();
		Object val = context.getInContext(key);
		j.put("value", val);
		
		plugin.botApp.collection("settings").update(JSON.toDBObject(query), JSON.toDBObject(j), true, false);
	}
	
	protected void removed(SettingsContext context, String key) {
		JSONObject query = buildQueryJSON(context, key);
		plugin.botApp.collection("settings").remove(JSON.toDBObject(query));
	}
	
	protected static final class ChannelContext {
		public final String server;
		public final String channel;
		
		public ChannelContext(String server, String channel) {
			this.server = server;
			this.channel = channel;
		}
		
		public boolean equals(Object obj) {
			if (!(obj instanceof ChannelContext))
				return false;
			ChannelContext o = (ChannelContext)obj;
			return server.equals(o.server) && channel.equals(o.channel);
		}
	}
}