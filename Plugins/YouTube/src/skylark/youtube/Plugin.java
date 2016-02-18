package skylark.youtube;

import java.util.regex.Pattern;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import skylark.old.PluginInfo;
import com.github.kevinsawicki.http.HttpRequest;

public class Plugin extends skylark.old.ListenerPlugin {
	public static final Pattern
		FULL_URL_PATTERN = Pattern.compile("https?\\://(?:www.)?youtube\\.com/watch.*[\\?&]v=([^\\?&#]+).*"),
		SHORT_URL_PATTERN = Pattern.compile("https?\\://youtu\\.be/(.*)");
	public static final String
		API_SEARCH_URL = "http://gdata.youtube.com/feeds/api/videos",
		API_GET_URL = "http://gdata.youtube.com/feeds/api/videos/";
	
	@Dependency(packageName = "Skylark.Commands")
	protected static skylark.old.Plugin commandsPlugin;
	
	@Dependency(packageName = "Skylark.URLAnnouncer")
	protected static skylark.old.Plugin urlAnnouncerPlugin;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void postLoad() {
		if (Plugin.commandsPlugin != null) {
			skylark.commands.Plugin commandsPlugin = (skylark.commands.Plugin)Plugin.commandsPlugin;
			commandsPlugin.register(
				new YouTubeCommand(this)
			);
		}
		if (Plugin.urlAnnouncerPlugin != null) {
			skylark.urlannouncer.Plugin urlAnnouncerPlugin = (skylark.urlannouncer.Plugin)Plugin.urlAnnouncerPlugin;
			urlAnnouncerPlugin.register(
				new YouTubeURLAnnouncer(this)
			);
		}
	}
	
	protected void onUnload() {
		if (Plugin.commandsPlugin != null) {
			skylark.commands.Plugin commandsPlugin = (skylark.commands.Plugin)Plugin.commandsPlugin;
			commandsPlugin.unregister(this);
		}
		if (Plugin.urlAnnouncerPlugin != null) {
			skylark.urlannouncer.Plugin urlAnnouncerPlugin = (skylark.urlannouncer.Plugin)Plugin.urlAnnouncerPlugin;
			urlAnnouncerPlugin.unregister(this);
		}
	}
	
	public VideoInfo performSearchRequest(String query) {
		try {
			HttpRequest req = HttpRequest.get(API_SEARCH_URL, true,
				"v", 2,
				"alt", "jsonc",
				"max-results", 1,
				"q", query);
			if (req.ok()) {
				JSONObject j = new JSONParser().parseObject(req.body());
				JSONObject jData = j.getObject("data");
				if (jData.getInt("totalItems") == 0)
					return null;
				return videoInfoFromResponseJSON(jData.getList("items").getObject(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public VideoInfo performGetRequest(String id) {
		try {
			HttpRequest req = HttpRequest.get(String.format("%s/%s", API_GET_URL, id), true,
				"v", 2,
				"alt", "jsonc");
			if (req.ok()) {
				JSONObject j = new JSONParser().parseObject(req.body());
				JSONObject jData = j.getObject("data");
				return videoInfoFromResponseJSON(jData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public VideoInfo videoInfoFromResponseJSON(JSONObject j) {
		VideoInfo info = new VideoInfo(j.getString("id"), j);
		info.uploader = j.getString("uploader");
		info.title = j.getString("title");
		info.description = j.getString("description", null);
		info.duration = j.getInt("duration");
		if (j.contains("likeCount") || j.contains("ratingCount")) {
			int likeCount = j.contains("likeCount") ? Integer.parseInt(j.getString("likeCount")) : 0;
			int ratingCount = j.contains("ratingCount") ? j.getInt("ratingCount") : 0;
			info.voteUp = likeCount;
			info.voteDown = ratingCount - info.voteUp;
		}
		info.views = j.getInt("viewCount", 0);
		return info;
	}
}