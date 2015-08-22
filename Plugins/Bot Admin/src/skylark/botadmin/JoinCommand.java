package skylark.botadmin;

import java.util.ArrayList;
import java.util.List;
import pl.shockah.json.JSONObject;
import skylark.BotManager;
import skylark.commands.Command;
import skylark.commands.CommandOutput;
import skylark.commands.CommandStack;
import skylark.pircbotx.Bot;
import skylark.pircbotx.event.GenericUserMessageEvent;

public class JoinCommand extends Command {
	public static final String
		COMMAND_NAME = "join";
	
	public JoinCommand(Plugin plugin) {
		super(plugin, COMMAND_NAME, String.format("%s.%s", plugin.pinfo.packageName(), COMMAND_NAME), Plugin.commandsPlugin.getSplitCommandInputParser());
	}
	
	protected CommandOutput onExecute(CommandStack stack, GenericUserMessageEvent e, JSONObject json) {
		JSONObject j = new JSONObject();
		JSONObject jChannels = j.putNewObject("channels");
		
		List<String> joined = new ArrayList<>();
		BotManager manager = e.<Bot>getBot().manager;
		for (String channelName : getSplitArgs(json)) {
			Bot bot = manager.joinChannel(channelName);
			if (bot != null) {
				jChannels.put(channelName, bot.getNick());
				joined.add(channelName);
			}
		}
		
		return new CommandOutput(j, "Joined channels: " + String.join(", ", joined));
	}
}