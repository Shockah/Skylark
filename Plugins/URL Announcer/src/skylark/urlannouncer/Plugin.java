package skylark.urlannouncer;

import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.json.JSONObject;
import skylark.old.PluginInfo;
import skylark.old.pircbotx.Bot;
import skylark.old.util.JSON;
import skylark.old.util.Synced;
import skylark.old.util.TimeDuration;
import skylark.settings.Setting;
import com.mongodb.DBCollection;

public class Plugin extends skylark.old.ListenerPlugin {
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
	
	protected Setting<Long> throttleTimeSetting;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		throttleTimeSetting = settingsPlugin.<Long>getSetting(this, THROTTLE_TIME_KEY);
		throttleTimeSetting.putDefault(DEFAULT_THROTTLE_TIME);
		
		register(
			new DefaultURLAnnouncer(this)
		);
	}
	
	protected void onUnload() {
		announcers.clear();
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
	
	public void unregister(skylark.old.Plugin plugin) {
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
					DBCollection dbc = collection();
					
					JSONObject query = JSONObject.make(
						"server", e.<Bot>getBot().manager.name,
						"channel", e.getChannel().getName(),
						"url", url
					);
					
					JSONObject jEntry = JSON.fromDBObject(dbc.findOne(JSON.toDBObject(query)));
					
					String sLastLinked = null;
					if (jEntry == null) {
						JSONObject doc = query.copy();
						doc.put("counter", 1);
						doc.put("nick", e.getUser().getNick());
						doc.put("timestamp", new Date().getTime());
						dbc.insert(JSON.toDBObject(doc));
					} else {
						JSONObject doc = query.copy();
						doc.put("counter", jEntry.getInt("counter") + 1);
						doc.put("nick", e.getUser().getNick());
						doc.put("timestamp", new Date().getTime());
						dbc.update(JSON.toDBObject(query), JSON.toDBObject(doc));
						
						sLastLinked = String.format(
							"Last linked by %s, %s ago; %d times total.",
							jEntry.getString("nick"),
							TimeDuration.format(new Date(jEntry.getLong("timestamp"))),
							doc.getInt("counter")
						);
					}
					
					if (jEntry != null && new Date().getTime() - jEntry.getLong("timestamp") < throttleTime)
						continue;
					
					String announcement = getAnnouncement(url);
					if (announcement != null)
						e.respond(announcement);
					if (sLastLinked != null)
						e.respond(sLastLinked);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
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