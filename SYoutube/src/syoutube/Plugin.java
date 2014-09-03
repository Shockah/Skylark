package syoutube;

import pl.shockah.json.JSONObject;
import shocky3.PluginInfo;
import surlannounce.URLAnnouncer;

public class Plugin extends shocky3.ListenerPlugin {
	@Dependency protected static surlannounce.Plugin pluginURLAnnounce;
	
	protected JSONObject j = null;
	public URLAnnouncer announcer;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		pluginURLAnnounce.add(
			announcer = new YoutubeURLAnnouncer(this)
		);
	}
}