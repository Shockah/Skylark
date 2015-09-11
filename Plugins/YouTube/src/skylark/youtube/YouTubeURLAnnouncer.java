package skylark.youtube;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import skylark.urlannouncer.URLAnnouncer;

public class YouTubeURLAnnouncer extends URLAnnouncer {
	protected final Plugin plugin;
	
	public YouTubeURLAnnouncer(Plugin plugin) {
		super(plugin, PRIORITY_HIGH);
		this.plugin = plugin;
	}
	
	public boolean matches(String url) {
		return Plugin.FULL_URL_PATTERN.matcher(url).find() || Plugin.SHORT_URL_PATTERN.matcher(url).find();
	}
	
	public String text(String url) {
		Pattern[] patterns = new Pattern[] {
			Plugin.FULL_URL_PATTERN, Plugin.SHORT_URL_PATTERN
		};
		for (Pattern pattern : patterns) {
			Matcher m = pattern.matcher(url);
			if (m.find()) {
				String id = m.group(1);
				VideoInfo info = plugin.performGetRequest(id);
				if (id != null)
					return info.format(false);
			}
		}
		return null;
	}
}