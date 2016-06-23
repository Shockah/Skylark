package io.shockah.skylark.botcontrol;

import io.shockah.skylark.Bot;
import io.shockah.skylark.BotManager;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandValue;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.pircbotx.Channel;
import com.google.common.base.Joiner;

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
	public CommandValue<List<String>> call(CommandCall call, List<String> input) {
		if (!plugin.permissionGranted(call.event.getUser(), "part"))
			return CommandValue.error("Permission required.");
		
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
		return new CommandValue.Simple<List<String>>(result, ircOutput);
	}
}