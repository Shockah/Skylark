package io.shockah.skylark.botcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.pircbotx.Channel;
import com.google.common.base.Joiner;
import io.shockah.skylark.Bot;
import io.shockah.skylark.BotManager;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;

public class PartCommand extends NamedCommand<List<String>, List<String>> {
	private final BotControlPlugin plugin;
	
	public PartCommand(BotControlPlugin plugin) {
		super("part");
		this.plugin = plugin;
	}

	@Override
	public List<String> parseInput(GenericUserMessageEvent e, String input) {
		input = input.trim();
		
		if (input.isEmpty()) {
			if (e.getChannel() == null) {
				throw new IllegalArgumentException("You must provide a channel.");
			} else {
				input = e.getChannel().getName();
			}
		}
		
		return Arrays.asList(input.split("\\s"));
	}

	@Override
	public CommandResult<List<String>> call(CommandCall call, List<String> input) {
		if (call.outputMedium == null)
			call.outputMedium = CommandCall.Medium.Notice;
		if (!plugin.permissionsPlugin.permissionGranted(call.event.getUser(), plugin, names[0]))
			return CommandResult.error("Permission required.");
		
		List<String> result = new ArrayList<>();
		BotManager manager = call.event.<Bot>getBot().manager;
		for (String channelName : input) {
			Channel channel = manager.getChannel(channelName);
			if (channel != null) {
				result.add(channelName);
				channel.send().part();
			}
		}
		
		String ircOutput = String.format("Left channels: %s", Joiner.on(", ").join(result));
		return CommandResult.of(result, ircOutput);
	}
}