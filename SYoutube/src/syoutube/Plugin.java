package syoutube;

import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import shocky3.PluginInfo;
import com.github.kevinsawicki.http.HttpRequest;

public class Plugin extends shocky3.Plugin {
	@Dependency protected static surlannounce.Plugin pluginURLAnnounce;
	@Dependency(internalName = "Shocky.Commands") protected static shocky3.Plugin pluginCmd;
	
	protected JSONObject j = null;
	public YoutubeURLAnnouncer announcer;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		pluginURLAnnounce.add(
			announcer = new YoutubeURLAnnouncer(this)
		);
	}
	
	protected void postLoad() {
		if (pluginCmd != null) {
			scommands.Plugin pluginCmd = (scommands.Plugin)Plugin.pluginCmd;
			pluginCmd.provider.add(
				new CmdYoutube(this)
			);
		}
	}
	
	public VideoInfo getVideoSearch(String query) {
		try {
			HttpRequest req = HttpRequest.get(String.format("http://gdata.youtube.com/feeds/api/videos"), true,
				"v", 2,
				"alt", "jsonc",
				"max-results", 1,
				"q", query);
			if (req.ok()) {
				JSONObject j = new JSONParser().parseObject(req.body());
				JSONObject jData = j.getObject("data");
				if (jData.getInt("totalItems") == 0) return null;
				return getVideoInfo(jData.getList("items").getObject(0));
			}
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	
	public VideoInfo getVideoInfo(String vid) {
		try {
			HttpRequest req = HttpRequest.get(String.format("http://gdata.youtube.com/feeds/api/videos/%s", vid), true,
				"v", 2,
				"alt", "jsonc");
			if (req.ok()) {
				JSONObject j = new JSONParser().parseObject(req.body());
				
				JSONObject jData = j.getObject("data");
				return getVideoInfo(jData);
			}
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	
	protected VideoInfo getVideoInfo(JSONObject j) {
		VideoInfo info = new VideoInfo(j.getString("id"));
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