package io.shockah.skylark.youtube;

import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;

public class YouTubeCommand extends NamedCommand<String, Video> {
	public final YouTubePlugin plugin;
	
	public YouTubeCommand(YouTubePlugin plugin) {
		super("youtube", "yt");
		this.plugin = plugin;
	}
	
	@Override
	public String parseInput(GenericUserMessageEvent e, String input) throws CommandParseException {
		return input;
	}

	@Override
	public CommandResult<Video> call(CommandCall call, String input) {
		Video video = plugin.search(input);
		return CommandResult.of(video, video.format(true));
	}
}