package syoutube;

import scommands.Command;
import scommands.CommandResult;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdYoutube extends Command {
	protected final Plugin pluginYoutube;
	
	public CmdYoutube(Plugin plugin) {
		super(plugin, "youtube", "yt");
		pluginYoutube = plugin;
	}
	
	public void call(GenericUserMessageEvent e, String trigger, String args, CommandResult result) {
		VideoInfo info = pluginYoutube.getVideoSearch(args);
		if (info == null) {
			result.add("No videos found.");
		} else {
			result.add(String.format("[%s]", info.format()));
		}
	}
}