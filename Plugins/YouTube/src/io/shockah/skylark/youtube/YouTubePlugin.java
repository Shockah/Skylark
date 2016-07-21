package io.shockah.skylark.youtube;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Joiner;
import io.shockah.json.JSONList;
import io.shockah.json.JSONObject;
import io.shockah.json.JSONParser;
import io.shockah.skylark.UnexpectedException;
import io.shockah.skylark.commands.CommandsPlugin;
import io.shockah.skylark.plugin.Plugin;
import io.shockah.skylark.plugin.PluginManager;
import io.shockah.skylark.urlannouncer.URLAnnouncerPlugin;
import io.shockah.skylark.util.TimeDuration;

public class YouTubePlugin extends Plugin {
	public static final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";
	public static final String VIDEOS_URL = "https://www.googleapis.com/youtube/v3/videos";
	
	@Dependency("io.shockah.skylark.commands")
	protected Plugin commandsOptionalPlugin;
	
	@Dependency("io.shockah.skylark.urlannouncer")
	protected Plugin urlAnnouncerOptionalPlugin;
	
	private Object command;
	private Object urlAnnouncer;
	
	public YouTubePlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onAllPluginsLoaded() {
		if (commandsOptionalPlugin != null) {
			CommandsPlugin commandsPlugin = (CommandsPlugin)commandsOptionalPlugin;
			YouTubeCommand command = new YouTubeCommand(this);
			commandsPlugin.addNamedCommand(command);
			this.command = command;
		}
		
		if (urlAnnouncerOptionalPlugin != null) {
			URLAnnouncerPlugin urlAnnouncerPlugin = (URLAnnouncerPlugin)urlAnnouncerOptionalPlugin;
			YouTubeURLAnnouncer urlAnnouncer = new YouTubeURLAnnouncer(this);
			urlAnnouncerPlugin.addAnnouncer(urlAnnouncer);
			this.urlAnnouncer = urlAnnouncer;
		}
	}
	
	@Override
	protected void onUnload() {
		if (commandsOptionalPlugin != null) {
			CommandsPlugin commandsPlugin = (CommandsPlugin)commandsOptionalPlugin;
			commandsPlugin.removeNamedCommand((YouTubeCommand)command);
		}
		
		if (urlAnnouncerOptionalPlugin != null) {
			URLAnnouncerPlugin urlAnnouncerPlugin = (URLAnnouncerPlugin)urlAnnouncerOptionalPlugin;
			urlAnnouncerPlugin.removeAnnouncer((YouTubeURLAnnouncer)urlAnnouncer);
		}
	}
	
	private int parseResponseDuration(String duration) {
		if (!duration.startsWith("PT"))
			throw new IllegalArgumentException();
		duration = duration.substring(2);
		return TimeDuration.parseSeconds(duration);
	}
	
	public Video getVideo(String id) {
		return getVideos(id)[0];
	}
	
	public Video[] getVideos(String... ids) {
		try {
			JSONObject j = new JSONParser().parseObject(HttpRequest.get(VIDEOS_URL, true,
				"id", Joiner.on(",").join(ids),
				"part", "snippet,contentDetails,statistics",
				"key", getConfig().getString("apiKey")
			).body());
			
			JSONList<JSONObject> jItems = j.getList("items").ofObjects();
			Video[] results = new Video[jItems.size()];
			for (int i = 0; i < jItems.size(); i++) {
				JSONObject jItem = jItems.get(i);
				Video video = new Video();
				
				video.id = jItem.getString("id");
				
				JSONObject jSnippet = jItem.getObject("snippet");
				video.title = jSnippet.getString("title");
				video.channelTitle = jSnippet.getString("channelTitle");
				
				JSONObject jContentDetails = jItem.getObject("contentDetails");
				video.durationInSeconds = parseResponseDuration(jContentDetails.getString("duration"));
				
				JSONObject jStatistics = jItem.getObject("statistics");
				video.views = Long.parseLong(jStatistics.getString("viewCount"));
				video.likes = Integer.parseInt(jStatistics.getString("likeCount"));
				video.dislikes = Integer.parseInt(jStatistics.getString("dislikeCount"));
				
				results[i] = video;
			}
			
			return results;
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}
	
	public Video search(String query) {
		return search(query, 1)[0];
	}
	
	public Video[] search(String query, int maxResults) {
		try {
			JSONObject j = new JSONParser().parseObject(HttpRequest.get(SEARCH_URL, true,
				"type", "video",
				"part", "id",
				"q", query,
				"maxResults", maxResults,
				"key", getConfig().getString("apiKey")
			).body());
			
			JSONList<JSONObject> jItems = j.getList("items").ofObjects();
			String[] videoIds = new String[jItems.size()];
			for (int i = 0; i < videoIds.length; i++)
				videoIds[i] = jItems.get(i).getObject("id").getString("videoId");
			
			return getVideos(videoIds);
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}
}