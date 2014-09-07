package syoutube;

import org.pircbotx.PircBotX;
import scommands.Command;
import shocky3.Shocky;
import shocky3.pircbotx.NullableChannelUserEvent;

public class CmdYoutube extends Command {
	protected final Plugin pluginYoutube;
	
	public CmdYoutube(Plugin plugin) {
		super(plugin, "youtube", "yt");
		pluginYoutube = plugin;
	}
	
	public void call(Shocky botApp, NullableChannelUserEvent<PircBotX> e, String trigger, String args) {
		VideoInfo info = pluginYoutube.getVideoSearch(args);
		if (info == null) {
			e.respond("No videos found.");
		} else {
			e.respond(String.format("[%s]", info.format()));
		}
	}
}