package ssteam;

import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import shocky3.PluginInfo;
import com.github.kevinsawicki.http.HttpRequest;

public class Plugin extends shocky3.Plugin {
	@Dependency protected static surlannounce.Plugin pluginURLAnnounce;
	
	public SteamURLAnnouncer announcer;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		pluginURLAnnounce.add(
			announcer = new SteamURLAnnouncer(this)
		);
	}
	
	public AppInfo getAppInfo(int id) {
		try {
			HttpRequest req = HttpRequest.get(String.format("http://store.steampowered.com/api/appdetails"), true,
				"appids", id);
			if (req.ok()) {
				JSONObject j = new JSONParser().parseObject(req.body());
				
				JSONObject jApp = j.getObject("" + id);
				if (!jApp.contains("success") || !jApp.getBoolean("success")) {
					return null;
				}
				JSONObject jData = jApp.getObject("data");
				return getAppInfo(jData);
			}
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	
	protected AppInfo getAppInfo(JSONObject j) {
		int id = 0;
		try {
			if (j.contains("steam_appid")) {
				Object o = j.get("steam_appid");
				if (o instanceof String) {
					id = Integer.parseInt((String)o);
				} else if (o instanceof Number) {
					id = ((Number)o).intValue();
				}
				if (id != 0) {
					AppInfo info = new AppInfo(id);
					info.name = j.getString("name");
					info.developers = j.getList("developers").ofStrings();
					if (j.contains("price_overview")) {
						JSONObject jPrice = j.getObject("price_overview");
						info.priceType = jPrice.getString("currency");
						info.priceBase = jPrice.getInt("initial");
						info.price = jPrice.getInt("final", info.priceBase);
						if (info.price != info.priceBase) {
							info.discount = jPrice.getDouble("discount_percent") * .01d;
						}
					}
					if (j.contains("platforms")) {
						JSONObject jPlatforms = j.getObject("platforms");
						info.forWindows = jPlatforms.getBoolean("windows", false);
						info.forLinux = jPlatforms.getBoolean("linux", false);
						info.forOSX = jPlatforms.getBoolean("mac", false);
					}
					if (j.contains("metacritic")) {
						JSONObject jMetacritic = j.getObject("metacritic");
						info.metascore = jMetacritic.getInt("score", 0);
					}
					return info;
				}
			}
		} catch (Exception e) {}
		return null;
	}
}