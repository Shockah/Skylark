package surlannounce;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Pair;
import pl.shockah.func.Func;
import pl.shockah.json.JSONObject;
import shocky3.PluginInfo;

public class Plugin extends shocky3.ListenerPlugin {
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
	
	protected void onMessage(MessageEvent<PircBotX> e) {
		String msg = e.getMessage();
		if (msg.matches(".*(?:https?\\://).*") || msg.matches(".*www\\..*")) {
			String[] spl = msg.split("\\s");
			
			for (String s : spl) {
				try {
					new URL(s);
					String announce = getAnnouncement(e, s);
					if (announce != null) {
						e.respond(announce);
					}
				} catch (Exception ex) {}
			}
		}
	}
	
	public String getAnnouncement(MessageEvent<PircBotX> e, String url) {
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
			
			return list.get(0).get1().f();
		}
		return null;
	}
}