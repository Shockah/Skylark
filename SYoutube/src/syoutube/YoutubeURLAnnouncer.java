package syoutube;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Box;
import pl.shockah.Pair;
import pl.shockah.func.Func;
import shocky3.Shocky;
import shocky3.pircbotx.Bot;
import surlannounce.URLAnnouncer;

public class YoutubeURLAnnouncer extends URLAnnouncer {
	public static final Pattern
		REGEX_URL1 = Pattern.compile("https?\\://youtube\\.com/watch.*[\\?&]v=([^\\?&]+).*"),
		REGEX_URL2 = Pattern.compile("https?\\://youtu\\.be/(.*)");
	
	protected final Plugin pluginYoutube;
	
	public YoutubeURLAnnouncer(Plugin plugin) {
		super(plugin);
		pluginYoutube = plugin;
	}

	public void provide(List<Pair<Func<String>, EPriority>> candidates, Shocky botApp, MessageEvent<Bot> e, final String url) {
		final Box<Boolean> includeShortUrl = new Box<>(false);
		final String vid = videoIDFromURL(url, includeShortUrl);
		if (vid != null) {
			candidates.add(new Pair<Func<String>, EPriority>(new Func<String>(){
				public String f() {
					return String.format("[%s]", pluginYoutube.getVideoInfo(vid).format(includeShortUrl.value));
				}
			}, EPriority.Medium));
		}
	}
	
	public String videoIDFromURL(String url) {
		return videoIDFromURL(url, null);
	}
	public String videoIDFromURL(String url, Box<Boolean> includeShortUrl) {
		Matcher m;
		if (includeShortUrl != null) {
			includeShortUrl.value = false;
		}
		
		m = REGEX_URL1.matcher(url);
		if (m.find()) {
			includeShortUrl.value = true;
			return m.group(1);
		}
		
		m = REGEX_URL2.matcher(url);
		if (m.find()) {
			return m.group(1);
		}
		
		return null;
	}
}