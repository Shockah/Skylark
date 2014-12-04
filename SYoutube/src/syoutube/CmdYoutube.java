package syoutube;

import scommands.Command;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdYoutube extends Command {
	protected final Plugin pluginYoutube;
	
	public CmdYoutube(Plugin plugin) {
		super(plugin, "youtube", "yt");
		pluginYoutube = plugin;
	}
	
	public String call(GenericUserMessageEvent e, String trigger, String args, boolean chain) {
		VideoInfo info = pluginYoutube.getVideoSearch(args);
		if (info == null) {
			if (!chain) e.respond("No videos found.");
			return "No videos found.";
		} else {
			String _s = String.format("[%s]", info.format());
			if (!chain) e.respond(_s);
			return _s;
		}
	}
}