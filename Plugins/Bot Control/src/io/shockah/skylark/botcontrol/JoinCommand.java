package io.shockah.skylark.botcontrol;

import io.shockah.skylark.Bot;
import io.shockah.skylark.BotManager;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandValue;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.base.Joiner;

public class JoinCommand extends NamedCommand<List<String>, Map<String, Bot>> {
	private final BotControlPlugin plugin;
	
	public JoinCommand(BotControlPlugin plugin) {
		super("join");
		this.plugin = plugin;
	}

	@Override
	public List<String> prepareInput(GenericUserMessageEvent e, String input) {
		return Arrays.asList(input.split("\\s"));
	}

	@Override
	public CommandValue<Map<String, Bot>> call(CommandCall call, List<String> input) {
		try {
			if (!plugin.permissionsPlugin.permissionGranted(call.event.getUser(), plugin.info.packageName() + ".join"))
				return CommandValue.error("Permission required.");
		} catch (SQLException e) {
			e.printStackTrace();
			return CommandValue.error(e.getMessage());
			
		}
		
		Map<String, Bot> result = new HashMap<>();
		BotManager manager = call.event.<Bot>getBot().manager;
		for (String channelName : input) {
			Bot bot = manager.joinChannel(channelName);
			result.put(channelName, bot);
		}
		
		String ircOutput = String.format("Joined channels: %s", Joiner.on(", ").join(result.keySet()));
		return new CommandValue.Simple<Map<String,Bot>>(result, ircOutput);
	}
}