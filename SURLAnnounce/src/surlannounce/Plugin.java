package surlannounce;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Pair;
import pl.shockah.func.Func;
import pl.shockah.json.JSONObject;
import shocky3.Bot;
import shocky3.PluginInfo;

public class Plugin extends shocky3.ListenerPlugin {
	public static final Pattern
		REGEX_NORMALIZE = Pattern.compile("^(https?\\://)(?:www\\.)?(.*)$");
	
	protected JSONObject j = null;
	public final DefaultURLAnnouncer announcer;
	protected List<URLAnnouncer> announcers = new LinkedList<>();
	
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
					String announce = getAnnouncement(e, s2);
					if (announce != null) {
						e.respond(announce);
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