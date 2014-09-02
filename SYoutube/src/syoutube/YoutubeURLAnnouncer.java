package syoutube;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Pair;
import pl.shockah.func.Func;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import shocky3.Plugin;
import shocky3.Shocky;
import shocky3.TimeDuration;
import surlannounce.URLAnnouncer;
import com.github.kevinsawicki.http.HttpRequest;

public class YoutubeURLAnnouncer extends URLAnnouncer {
	public static final Pattern
		REGEX_URL1 = Pattern.compile("https?\\://youtube\\.com/watch.*[\\?&]v=([^\\?&]+).*"),
		REGEX_URL2 = Pattern.compile("https?\\://youtu\\.be/(.*)");
	
	public YoutubeURLAnnouncer(Plugin plugin) {
		super(plugin);
	}

	public void provide(List<Pair<Func<String>, EPriority>> candidates, Shocky botApp, MessageEvent<PircBotX> e, final String url) {
		final String vid = videoIDFromURL(url);
		if (vid != null) {
			candidates.add(new Pair<Func<String>, EPriority>(new Func<String>(){
				public String f() {
					return String.format("[%s]", getVideoInfo(vid).format());
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
	
	public VideoInfo getVideoInfo(String vid) {
		VideoInfo info = new VideoInfo(vid);
		
		try {
			HttpRequest req = HttpRequest.get(String.format("http://gdata.youtube.com/feeds/api/videos/%s", vid), true,
				"v", 2,
				"alt", "jsonc");
			if (req.ok()) {
				JSONObject j = new JSONParser().parseObject(req.body());
				
				JSONObject jData = j.getObject("data");
				if (jData.contains("uploader")) info.uploader = jData.getString("uploader");
				if (jData.contains("title")) info.title = jData.getString("title");
				if (jData.contains("description")) info.description = jData.getString("description");
				if (jData.contains("duration")) info.duration = jData.getInt("duration");
				if (jData.contains("likeCount") || jData.contains("ratingCount")) {
					int likeCount = jData.contains("likeCount") ? Integer.parseInt(jData.getString("likeCount")) : 0;
					int ratingCount = jData.contains("ratingCount") ? jData.getInt("ratingCount") : 0;
					info.voteUp = likeCount;
					info.voteDown = ratingCount - info.voteUp;
				}
				if (jData.contains("viewCount") || jData.contains("ratingCount")) info.views = jData.getInt("viewCount");
			}
		} catch (Exception e) {e.printStackTrace();}
		
		return info;
	}
	
	public static final class VideoInfo {
		public final String id;
		public String uploader, title, description;
		public int duration, voteUp, voteDown, views;
		
		public VideoInfo(String id) {
			this.id = id;
		}
		
		public String format() {
			return format(true);
		}
		public String format(boolean includeShortUrl) {
			DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
			symbols.setGroupingSeparator(',');
			DecimalFormat formatter = new DecimalFormat("###,###", symbols);
			StringBuilder sb = new StringBuilder();
			
			sb.append(String.format(" | &b%s&r added by &b%s&r", title, uploader));
			if (duration != 0) {
				sb.append(String.format(" | %s long", TimeDuration.formatSeconds(duration)));
			}
			if (views != 0) {
				sb.append(String.format(" | %s view%s", formatter.format(views), views == 1 ? "" : "s"));
			}
			if (voteUp + voteDown != 0) {
				sb.append(String.format(" | +%s / -%s", formatter.format(voteUp), formatter.format(voteDown)));
			}
			
			if (includeShortUrl) {
				sb.append(String.format(" | http://youtu.be/%s", id));
			}
			
			String ret = sb.toString().substring(3);
			ret = ret.replace("&b", Colors.BOLD);
			ret = ret.replace("&r", Colors.NORMAL);
			return ret;
		}
	}
}