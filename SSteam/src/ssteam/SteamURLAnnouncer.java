package ssteam;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Pair;
import pl.shockah.func.Func;
import shocky3.Shocky;
import shocky3.pircbotx.Bot;
import surlannounce.URLAnnouncer;

public class SteamURLAnnouncer extends URLAnnouncer {
	public static final Pattern
		REGEX_URL = Pattern.compile("https?\\://store\\.steampowered\\.com/app/([0-9]+).*");
	
	protected final Plugin pluginSteam;
	
	public SteamURLAnnouncer(Plugin plugin) {
		super(plugin);
		pluginSteam = plugin;
	}

	public void provide(List<Pair<Func<String>, EPriority>> candidates, Shocky botApp, final MessageEvent<Bot> e, final String url) {
		final int app = appIDFromURL(url);
		if (app != 0) {
			candidates.add(new Pair<Func<String>, EPriority>(new Func<String>(){
				public String f() {
					boolean checkmarks = false;
					if (e.getChannel().getMode().contains("c")) checkmarks = true;
					return String.format("[%s]", pluginSteam.getAppInfo(app).format(checkmarks));
				}
			}, EPriority.Medium));
		}
	}
	
	public int appIDFromURL(String url) {
		Matcher m = REGEX_URL.matcher(url);
		if (m.find()) {
			try {
				return Integer.parseInt(m.group(1));
			} catch (Exception e) {}
		}
		return 0;
	}
}