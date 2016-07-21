package io.shockah.skylark.urlannouncer;

import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.hooks.events.MessageEvent;
import io.shockah.skylark.plugin.ListenerPlugin;
import io.shockah.skylark.plugin.PluginManager;
import io.shockah.skylark.util.ReadWriteList;

public class URLAnnouncerPlugin extends ListenerPlugin {
	public static final Pattern NORMALIZE_PATTERN = Pattern.compile("^(https?\\://)(?:www\\.)?(.*)$");
	
	protected ReadWriteList<URLAnnouncer> announcers = new ReadWriteList<>(new ArrayList<>());
	
	public URLAnnouncerPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	public void addAnnouncer(URLAnnouncer announcer) {
		announcers.add(0, announcer);
	}
	
	public void removeAnnouncer(URLAnnouncer announcer) {
		announcers.remove(announcer);
	}
	
	@Override
	protected void onLoad() {
		announcers.add(new DefaultURLAnnouncer());
	}
	
	@Override
	protected void onMessage(MessageEvent e) {
		String msg = e.getMessage();
		if (msg.matches(".*(?:https?\\://).*") || msg.matches(".*www\\..*")) {
			String[] spl = msg.split("\\s");
			for (String s : spl) {
				try {
					new URL(s);
					String normalized = normalizeURL(s);
					String title = getTitleForUrl(normalized);
					
					if (title != null)
						e.getChannel().send().message(String.format("[%s]", title));
				} catch (Exception ex) {
				}
			}
		}
	}
	
	protected String getTitleForUrl(String url) {
		return announcers.firstResult(announcer -> announcer.getTitleForURL(url));
	}
	
	private String normalizeURL(String url) {
		if (!url.contains("://"))
			url = "http://" + url;
		
		Matcher m = NORMALIZE_PATTERN.matcher(url);
		if (m.find())
			url = m.group(1) + m.group(2);
		
		return url;
	}
}