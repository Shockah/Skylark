package syoutube;

import scommands.CommandStack;
import scommands.TextCommand;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdYoutube extends TextCommand {
	protected final Plugin pluginYoutube;
	
	public CmdYoutube(Plugin plugin) {
		super(plugin, "youtube", "yt");
		pluginYoutube = plugin;
	}
	
	public String call(GenericUserMessageEvent e, String input, CommandStack stack) {
		VideoInfo info = pluginYoutube.getVideoSearch(input);
		if (info == null)
			return "No videos found.";
		else
			return String.format("[%s]", info.format());
	}
}