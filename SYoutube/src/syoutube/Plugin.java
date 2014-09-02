package syoutube;

import pl.shockah.json.JSONObject;
import shocky3.PluginInfo;
import surlannounce.URLAnnouncer;

public class Plugin extends shocky3.ListenerPlugin {
	protected JSONObject j = null;
	public URLAnnouncer announcer;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		surlannounce.Plugin pluginURLAnnounce = botApp.pluginManager.byInternalName("Shocky.SURLAnnounce");
		pluginURLAnnounce.add(
			announcer = new YoutubeURLAnnouncer(this)
		);
	}
}