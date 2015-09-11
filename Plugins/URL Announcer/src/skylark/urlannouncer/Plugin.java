package skylark.urlannouncer;

import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.json.JSONObject;
import skylark.BotManager;
import skylark.PluginInfo;
import skylark.pircbotx.Bot;
import skylark.settings.Setting;
import skylark.util.JSON;
import skylark.util.Synced;
import skylark.util.TimeDuration;
import com.mongodb.DBCollection;

public class Plugin extends skylark.ListenerPlugin {
	public static final Pattern
		HTTP_URL_PATTERN = Pattern.compile(".*(?:https?\\://).*"),
		WWW_URL_PATTERN = Pattern.compile(".*www\\..*"),
		URL_NORMALIZER_PATTERN = Pattern.compile("^(https?\\://)(?:www\\.)?(.*)$");
	
	public static final String
		THROTTLE_TIME_KEY = "ThrottleTime";
	
	public static final long
		DEFAULT_THROTTLE_TIME = 1000l * 60l;
	
	@Dependency
	protected static skylark.settings.Plugin settingsPlugin;
	
	protected final List<URLAnnouncer> announcers = Synced.list();
	protected final Map<EntryKey, Map<String, Entry>> lastLinked = Synced.map();
	
	protected Setting<Long> throttleTimeSetting;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		throttleTimeSetting = settingsPlugin.<Long>getSetting(this, THROTTLE_TIME_KEY);
		throttleTimeSetting.putDefault(DEFAULT_THROTTLE_TIME);
		
		synchronized (lastLinked) {
			register(
				new DefaultURLAnnouncer(this)
			);
			
			JSON.forEachJSONObject(botApp.collection(this).find(), j -> {
				EntryKey entryKey = new EntryKey(botApp.serverManager.byServerName(j.getString("server")), j.getString("channel"));
				if (!lastLinked.containsKey(entryKey))
					lastLinked.put(entryKey, Synced.map());
				Map<String, Entry> lastLinked2 = lastLinked.get(entryKey);
				Entry entry = new Entry(j.getString("nick"), new Date(j.getLong("timestamp")), j.getInt("counter"));
				lastLinked2.put(j.getString("url"), entry);
			});
		}
	}
	
	protected void onUnload() {
		announcers.clear();
		lastLinked.clear();
	}
	
	public void register(URLAnnouncer announcer) {
		synchronized (announcers) {
			announcers.add(announcer);
			Collections.sort(announcers, (i1, i2) -> i1.priority == i2.priority ? 0 : (i1.priority > i2.priority ? -1 : 1));
		}
	}
	
	public void register(URLAnnouncer... announcers) {
		for (URLAnnouncer announcer : announcers)
			register(announcer);
	}
	
	public void unregister(URLAnnouncer announcer) {
		announcers.remove(announcer);
	}
	
	public void unregister(URLAnnouncer... announcers) {
		for (URLAnnouncer announcer : announcers)
			unregister(announcer);
	}
	
	public void unregister(skylark.Plugin plugin) {
		Synced.iterate(announcers, (announcer, ith) -> {
			if (announcer.plugin == plugin)
				ith.remove();
		});
	}
	
	protected void onMessage(MessageEvent e) {
		String msg = e.getMessage();
		long throttleTime = throttleTimeSetting.get(e.getChannel());
		
		if (HTTP_URL_PATTERN.matcher(msg).find() || WWW_URL_PATTERN.matcher(msg).find()) {
			String[] spl = msg.split("\\s");
			for (String s : spl)
				try {
					new URL(s);
					String url = normalizeURL(s);
					EntryKey entryKey = new EntryKey(e.<Bot>getBot().manager, e.getChannel().getName());
					
					String sLastLinked = null;
					synchronized (lastLinked) {
						if (!lastLinked.containsKey(entryKey))
							lastLinked.put(entryKey, Synced.map());
						Map<String, Entry> lastLinked2 = lastLinked.get(entryKey);
						synchronized (lastLinked2) {
							Entry entry = lastLinked2.get(url);
							Entry entry2 = new Entry(e.getUser().getNick());
							DBCollection dbc = botApp.collection(this);
							if (entry == null) {
								dbc.insert(JSON.toDBObject(JSONObject.make(
									"server", entryKey.manager.name,
									"channel", entryKey.channel,
									"url", url,
									"counter", 0,
									"nick", entry2.nick,
									"timestamp", entry2.date.getTime()
								)));
							} else {
								dbc.update(JSON.toDBObject(JSONObject.make(
									"server", entryKey.manager.name,
									"channel", entryKey.channel,
									"url", url
								)), JSON.toDBObject(JSONObject.make("$set", JSONObject.make(
									"counter", 
									"nick", entry2.nick,
									"timestamp", entry2.date.getTime()
								))));
								sLastLinked = String.format("Last linked by %s, %s ago; %d times total.", entry.nick, TimeDuration.format(entry.date), entry2.counter);
							}
							lastLinked2.put(url, entry2);
							
							if (entry != null && entry2.date.getTime() - entry.date.getTime() < throttleTime)
								continue;
							
							String announcement = getAnnouncement(url);
							if (announcement != null)
								e.respond(announcement);
							if (sLastLinked != null)
								e.respond(sLastLinked);
						}
					}
				} catch (Exception ex) { }
		}
	}
	
	public String normalizeURL(String url) {
		if (!url.contains("://"))
			url = "http://" + url;
		Matcher m = URL_NORMALIZER_PATTERN.matcher(url);
		if (m.find())
			url = m.group(1) + m.group(2);
		return url;
	}
	
	public String getAnnouncement(String url) {
		synchronized (announcers) {
			for (URLAnnouncer announcer : announcers)
				if (announcer.matches(url)) {
					String text = announcer.text(url);
					if (text != null)
						return text;
				}
		}
		return null;
	}
	
	public static final class EntryKey {
		public final BotManager manager;
		public final String channel;
		
		public EntryKey(BotManager manager, String channel) {
			this.manager = manager;
			this.channel = channel;
		}
		
		public boolean equals(Object other) {
			if (!(other instanceof EntryKey))
				return false;
			EntryKey o = (EntryKey)other;
			return o.manager == manager && o.channel.equals(channel);
		}
	}
	
	public static final class Entry {
		public final String nick;
		public final Date date;
		public final int counter;
		
		public Entry(String nick) {
			this(nick, new Date(), 0);
		}
		
		public Entry(String nick, Date date) {
			this(nick, date, 0);
		}
		
		public Entry(String nick, Date date, int counter) {
			this.nick = nick;
			this.date = date;
			this.counter = counter;
		}
	}
}