package skylark.botadmin;

import java.util.ArrayList;
import java.util.List;
import pl.shockah.json.JSONObject;
import skylark.commands.CommandOutput;
import skylark.commands.CommandStack;
import skylark.commands.TypedArgCommand;
import skylark.old.BotManager;
import skylark.old.pircbotx.Bot;
import skylark.old.pircbotx.event.GenericUserMessageEvent;

public class JoinCommand extends TypedArgCommand<String[]> {
	public static final String
		COMMAND_NAME = "join";
	
	public JoinCommand(Plugin plugin) {
		super(plugin, COMMAND_NAME, String.format("%s.%s", plugin.pinfo.packageName(), COMMAND_NAME), Plugin.commandsPlugin.getSplitCommandInputParser());
	}

	protected String[] getArg(GenericUserMessageEvent e, JSONObject json) {
		return getSplitArgs(json);
	}
	
	protected CommandOutput onExecute(CommandStack stack, GenericUserMessageEvent e, String[] arg) {
		JSONObject j = new JSONObject();
		JSONObject jChannels = j.putNewObject("channels");
		
		List<String> joined = new ArrayList<>();
		BotManager manager = e.<Bot>getBot().manager;
		for (String channelName : arg) {
			Bot bot = manager.joinChannel(channelName);
			if (bot != null) {
				jChannels.put(channelName, bot.getNick());
				joined.add(channelName);
			}
		}
		
		return new CommandOutput(j, "Joined channels: " + String.join(", ", joined));
	}
}