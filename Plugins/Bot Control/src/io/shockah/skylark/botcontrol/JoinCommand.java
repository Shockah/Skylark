package io.shockah.skylark.botcontrol;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.base.Joiner;
import io.shockah.skylark.Bot;
import io.shockah.skylark.BotManager;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;

public class JoinCommand extends NamedCommand<List<String>, Map<String, Bot>> {
	private final BotControlPlugin plugin;
	
	public JoinCommand(BotControlPlugin plugin) {
		super("join");
		this.plugin = plugin;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> convertToInput(GenericUserMessageEvent e, Object input) throws CommandParseException {
		if (input instanceof List<?>)
			return (List<String>)input;
		return super.convertToInput(e, input);
	}

	@Override
	public List<String> parseInput(GenericUserMessageEvent e, String input) {
		return Arrays.asList(input.trim().split("\\s"));
	}

	@Override
	public CommandResult<Map<String, Bot>> call(CommandCall call, List<String> input) {
		if (call.outputMedium == null)
			call.outputMedium = CommandCall.Medium.Notice;
		if (!plugin.permissionsPlugin.permissionGranted(call.event.getUser(), plugin, names[0]))
			return CommandResult.error("Permission required.");
		
		Map<String, Bot> result = new HashMap<>();
		BotManager manager = call.event.<Bot>getBot().manager;
		for (String channelName : input) {
			Bot bot = manager.joinChannel(channelName);
			result.put(channelName, bot);
		}
		
		String ircOutput = String.format("Joined channels: %s", Joiner.on(", ").join(result.keySet()));
		return CommandResult.of(result, ircOutput);
	}
}