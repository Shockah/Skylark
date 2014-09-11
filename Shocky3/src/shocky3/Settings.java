package shocky3;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.pircbotx.Channel;
import pl.shockah.json.JSONObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Settings {
	public final Shocky botApp;
	protected Map<String, Setting<?>> settings = Collections.synchronizedMap(new HashMap<String, Setting<?>>());
	
	public Settings(Shocky botApp) {
		this.botApp = botApp;
	}
	
	public void read() {
		DBCollection dbc = botApp.collection("settings");
		for (DBObject dbo : JSONUtil.all(dbc.find())) {
			JSONObject j = JSONUtil.fromDBObject(dbo);
			
			Setting<Object> s = new Setting<>(j.get("defaultValue"));
			if (j.contains("custom")) {
				JSONObject jCustom = j.getObject("custom");
				for (String key : jCustom.keys()) {
					s.custom.put(key, jCustom.get(key));
				}
			}
			settings.put(j.getString("name"), s);
		}
	}
	public void write() {
		List<JSONObject> list = new LinkedList<>();
		for (Map.Entry<String, Setting<?>> entry : settings.entrySet()) {
			JSONObject j = new JSONObject();
			j.put("name", entry.getKey());
			j.put("defaultValue", entry.getValue().defaultValue);
			if (!entry.getValue().custom.isEmpty()) {
				JSONObject j2 = new JSONObject();
				for (Map.Entry<String, ?> entry2 : entry.getValue().custom.entrySet()) {
					j2.put(entry2.getKey(), entry2.getValue());
				}
				j.put("custom", j2);
			}
			list.add(j);
		}
		
		DBCollection dbc = botApp.collection("settings");
		for (JSONObject j : list) {
			dbc.update(JSONUtil.toDBObject(JSONObject.make(
				"name", j.get("name")
			)), JSONUtil.toDBObject(j), true, false);
		}
	}
	
	public <T> void add(Plugin plugin, String setting, T defaultValue) {
		setting = String.format("%s|%s", plugin.pinfo.internalName(), setting);
		synchronized (settings) {
			if (settings.containsKey(setting)) return;
			settings.put(setting, new Setting<T>(defaultValue, plugin));
		}
	}
	public <T> void add(String setting, T defaultValue) {
		synchronized (settings) {
			if (settings.containsKey(setting)) return;
			settings.put(setting, new Setting<T>(defaultValue));
		}
	}
	
	public String getStringForChannel(Channel channel, Plugin plugin, String setting) {
		return getStringForChannel(channel, String.format("%s|%s", plugin.pinfo.internalName(), setting));
	}
	public String getStringForChannel(Channel channel, String setting) {
		return getStringForChannel(channel.getBot().getConfiguration().getServerHostname(), channel.getName(), setting);
	}
	public String getStringForChannel(String server, String channel, String setting) {
		return (String)getForChannel(server, channel, setting);
	}
	
	public Object getForChannel(Channel channel, Plugin plugin, String setting) {
		return getForChannel(channel, String.format("%s|%s", plugin.pinfo.internalName(), setting));
	}
	public Object getForChannel(Channel channel, String setting) {
		return getForChannel(channel.getBot().getConfiguration().getServerHostname(), channel.getName(), setting);
	}
	public Object getForChannel(String server, String channel, String setting) {
		synchronized (settings) {
			Setting<?> s = settings.get(setting);
			String key = String.format("%s|%s", server, channel);
			if (s.custom.containsKey(key)) return s.custom.get(key);
			return s.defaultValue;
		}
	}
	
	private class Setting<T> {
		public List<Plugin> plugins = Collections.synchronizedList(new LinkedList<Plugin>());
		public T defaultValue;
		public Map<String, T> custom = Collections.synchronizedMap(new HashMap<String, T>());
		
		public Setting(T defaultValue, Plugin... plugins) {
			this.defaultValue = defaultValue;
			for (Plugin plugin : plugins) this.plugins.add(plugin);
		}
	}
}