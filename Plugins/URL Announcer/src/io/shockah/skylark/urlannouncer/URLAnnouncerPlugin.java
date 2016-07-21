package io.shockah.skylark.urlannouncer;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;
import io.shockah.skylark.Bot;
import io.shockah.skylark.plugin.ListenerPlugin;
import io.shockah.skylark.plugin.PluginManager;
import io.shockah.skylark.urlannouncer.db.AnnouncedURL;
import io.shockah.skylark.util.ReadWriteList;
import io.shockah.skylark.util.TimeDuration;

public class URLAnnouncerPlugin extends ListenerPlugin {
	public static final long DEFAULT_THRESHOLD = 1000l * 60l;
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
		getConfig().putDefault("threshold", DEFAULT_THRESHOLD);
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
					
					String lastNick = null;
					Date lastDate = null;
					AnnouncedURL announced = getAnnouncedURL(e.getChannel(), normalized);
					if (announced == null) {
						manager.app.databaseManager.create(AnnouncedURL.class, obj -> {
							obj.server = e.getChannel().<Bot>getBot().manager.name;
							obj.channel = e.getChannel().getName();
							obj.url = normalized;
							obj.nick = e.getUser().getNick();
						});
					} else {
						Date now = new Date();
						lastNick = announced.nick;
						lastDate = announced.date;
						boolean skipAnnounce = lastDate.getTime() + getConfig().getLong("threshold") > now.getTime();
						
						announced.update(obj -> {
							obj.date = now;
							obj.counter++;
							obj.nick = e.getUser().getNick();
						});
						
						if (skipAnnounce)
							continue;
					}
					
					String title = getTitleForURL(normalized);
					if (title != null) {
						e.getChannel().send().message(String.format("[%s]", title));
						if (lastDate != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
							e.getChannel().send().message(String.format(
								"Last linked by %s at %s UTC (%s ago); %d times total.",
								lastNick, sdf.format(lastDate), TimeDuration.format(lastDate), announced.counter
							));
						}
					}
				} catch (Exception ex) {
				}
			}
		}
	}
	
	protected String getTitleForURL(String url) {
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
	
	public AnnouncedURL getAnnouncedURL(Channel channel, String url) {
		return manager.app.databaseManager.queryFirst(AnnouncedURL.class, (builder, where) -> {
			where.equals(AnnouncedURL.SERVER_COLUMN, channel.<Bot>getBot().manager.name);
			where.equals(AnnouncedURL.CHANNEL_COLUMN, channel.getName());
			where.equals(AnnouncedURL.URL_COLUMN, url);
		});
	}
}