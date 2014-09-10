package surlannounce;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Pair;
import pl.shockah.func.Func;
import pl.shockah.json.JSONObject;
import shocky3.JSONUtil;
import shocky3.PluginInfo;
import shocky3.TimeDuration;
import shocky3.pircbotx.Bot;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Plugin extends shocky3.ListenerPlugin {
	public static final long THROTTLE = 1000 * 60;
	public static final Pattern
		REGEX_NORMALIZE = Pattern.compile("^(https?\\://)(?:www\\.)?(.*)$");
	
	protected JSONObject j = null;
	public final DefaultURLAnnouncer announcer;
	protected List<URLAnnouncer> announcers = new LinkedList<>();
	public Map<String, List<Sender>> lastLinked = Collections.synchronizedMap(new HashMap<String, List<Sender>>());
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
		announcer = new DefaultURLAnnouncer(this);
	}
	
	public void add(URLAnnouncer... urlas) {
		for (URLAnnouncer urla : urlas) {
			if (!announcers.contains(urla)) {
				announcers.add(urla);
			}
		}
	}
	public void remove(URLAnnouncer... urlas) {
		for (URLAnnouncer urla : urlas) {
			announcers.remove(urla);
		}
	}
	
	protected void onLoad() {
		botApp.settings.add(this, "characters", ".");
		announcers.clear();
		
		add(announcer);
		
		DBCollection dbc = botApp.collection(this);
		for (DBObject dbo : JSONUtil.all(dbc.find())) {
			JSONObject j = JSONUtil.fromDBObject(dbo);
			String url = j.getString("url");
			if (!lastLinked.containsKey(url)) {
				lastLinked.put(url, Collections.synchronizedList(new ArrayList<Sender>()));
			}
			lastLinked.get(url).add(new Sender(
				botApp.serverManager.byServerName(j.getString("server")),
				j.getString("channel"),
				j.getString("nick"),
				new Date(j.getInt("timestamp") * 1000l)
			));
		}
	}
	
	protected void onUnload() {
		announcers.clear();
	}
	
	protected void onMessage(MessageEvent<Bot> e) {
		String msg = e.getMessage();
		if (msg.matches(".*(?:https?\\://).*") || msg.matches(".*www\\..*")) {
			String[] spl = msg.split("\\s");
			
			for (String s : spl) {
				try {
					new URL(s);
					String s2 = normalizeURL(s);
					
					String sLastLinked = null;
					if (!lastLinked.containsKey(s2)) {
						lastLinked.put(s2, Collections.synchronizedList(new ArrayList<Sender>()));
					}
					List<Sender> senders = lastLinked.get(s2);
					Sender newSender = new Sender(
						botApp.serverManager.byBot(e),
						e.getChannel().getName(),
						e.getUser().getNick(),
						new Date()
					);
					int index = senders.indexOf(newSender);
					DBCollection dbc = botApp.collection(this);
					if (index == -1) {
						senders.add(newSender);
						dbc.insert(JSONUtil.toDBObject(JSONObject.make(
							"url", s2,
							"server", newSender.manager.name,
							"channel", newSender.channel,
							"nick", newSender.nick,
							"timestamp", (int)(newSender.date.getTime() / 1000l)
						)));
					} else {
						Sender sender = senders.get(index);
						dbc.update(JSONUtil.toDBObject(JSONObject.make(
							"url", s2,
							"server", sender.manager.name,
							"channel", sender.channel
						)), JSONUtil.toDBObject(JSONObject.make("$set", JSONObject.make(
							"nick", newSender.nick,
							"timestamp", (int)(newSender.date.getTime() / 1000l)
						))));
						sLastLinked = String.format("Last linked by %s, %s ago.", sender.nick, TimeDuration.format(sender.date));
						boolean drop = false;
						drop = newSender.date.getTime() - sender.date.getTime() < THROTTLE;
						sender.nick = newSender.nick;
						sender.date = newSender.date;
						if (drop) continue;
					}
					
					String announce = getAnnouncement(e, s2);
					if (announce != null) {
						e.respond(announce);
					}
					if (sLastLinked != null) {
						e.respond(sLastLinked);
					}
				} catch (Exception ex) {}
			}
		}
	}
	
	public String normalizeURL(String url) {
		if (!url.contains("://")) {
			url = "http://" + url;
		}
		
		Matcher m = REGEX_NORMALIZE.matcher(url);
		if (m.find()) {
			url = m.group(1) + m.group(2);
		}
		
		return url;
	}
	
	public String getAnnouncement(MessageEvent<Bot> e, String url) {
		List<Pair<Func<String>, URLAnnouncer.EPriority>> list = new LinkedList<>();
		for (URLAnnouncer urla : announcers) {
			urla.provide(list, botApp, e, url);
		}
		
		if (!list.isEmpty()) {
			Collections.sort(list, new Comparator<Pair<Func<String>, URLAnnouncer.EPriority>>(){
				public int compare(Pair<Func<String>, URLAnnouncer.EPriority> p1, Pair<Func<String>, URLAnnouncer.EPriority> p2) {
					return Integer.compare(p2.get2().value, p1.get2().value);
				}
			});
			
			for (Pair<Func<String>, URLAnnouncer.EPriority> p : list) {
				String announce = p.get1().f();
				if (announce != null) {
					return announce;
				}
			}
		}
		return null;
	}
}