package io.shockah.skylark.youtube;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.shockah.skylark.urlannouncer.URLAnnouncer;
import io.shockah.skylark.util.Box;

public class YouTubeURLAnnouncer extends URLAnnouncer {
	public static final Pattern URL1_PATTERN = Pattern.compile("https?\\://youtube\\.com/watch.*[\\?&]v=([^\\?&#]+).*");
	public static final Pattern URL2_PATTERN = Pattern.compile("https?\\://youtu\\.be/(.*)");
	
	public final YouTubePlugin plugin;
	
	public YouTubeURLAnnouncer(YouTubePlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public String getTitleForURL(String url) {
		Box<Boolean> box = new Box<>();
		String videoId = videoIDFromURL(url, box);
		if (videoId == null)
			return null;
		return plugin.getVideo(videoId).format(box.value);
	}
	
	public String videoIDFromURL(String url) {
		return videoIDFromURL(url, null);
	}
	
	public String videoIDFromURL(String url, Box<Boolean> includeShortUrl) {
		Matcher m;
		if (includeShortUrl != null)
			includeShortUrl.value = false;
		
		m = URL1_PATTERN.matcher(url);
		if (m.find()) {
			includeShortUrl.value = true;
			return m.group(1);
		}
		
		m = URL2_PATTERN.matcher(url);
		if (m.find())
			return m.group(1);
		
		return null;
	}
}