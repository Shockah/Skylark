package syoutube;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Pair;
import pl.shockah.func.Func;
import shocky3.Plugin;
import shocky3.Shocky;
import surlannounce.URLAnnouncer;

public class YoutubeURLAnnouncer extends URLAnnouncer {
	public static final Pattern
		REGEX_URL1 = Pattern.compile("https?\\://youtube\\.com/.*[\\?&]v=([^\\?&]+).*"),
		REGEX_URL2 = Pattern.compile("https?\\://youtu\\.be/(.*)");
	
	public YoutubeURLAnnouncer(Plugin plugin) {
		super(plugin);
	}

	public void provide(List<Pair<Func<String>, EPriority>> candidates, Shocky botApp, MessageEvent<PircBotX> e, final String url) {
		final String vid = videoIDFromURL(url);
		if (vid != null) {
			candidates.add(new Pair<Func<String>, EPriority>(new Func<String>(){
				public String f() {
					return String.format("[%s]", getVideoInfo(vid));
				}
			}, EPriority.Medium));
		}
	}
	
	public String videoIDFromURL(String url) {
		Matcher m;
		
		m = REGEX_URL1.matcher(url);
		if (m.find()) {
			return m.group(1);
		}
		
		m = REGEX_URL2.matcher(url);
		if (m.find()) {
			return m.group(1);
		}
		
		return null;
	}
	
	public String getVideoInfo(String vid) {
		return null;
	}
}