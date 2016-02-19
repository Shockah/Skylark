package skylark.botadmin;

import java.util.ArrayList;
import java.util.List;
import me.shockah.skylark.event.GenericUserMessageEvent;
import org.pircbotx.Channel;
import pl.shockah.json.JSONObject;
import skylark.commands.CommandOutput;
import skylark.commands.CommandStack;
import skylark.commands.TypedArgCommand;
import skylark.old.BotManager;
import skylark.old.pircbotx.Bot;

public class PartCommand extends TypedArgCommand<String[]> {
	public static final String
		COMMAND_NAME = "part";
	
	public PartCommand(Plugin plugin) {
		super(plugin, COMMAND_NAME, String.format("%s.%s", plugin.pinfo.packageName(), COMMAND_NAME), Plugin.commandsPlugin.getSplitCommandInputParser());
	}
	
	protected String[] getArg(GenericUserMessageEvent e, JSONObject json) {
		String[] args = getSplitArgs(json);
		if (args.length == 0)
			args = new String[] { e.getChannel().getName() };
		return args;
	}
	
	protected boolean isAllowed(CommandStack stack, GenericUserMessageEvent e, String[] arg, List<String> messages) {
		boolean _super = super.isAllowed(stack, e, arg, messages);
		if (_super)
			return true;
		
		BotManager manager = e.<Bot>getBot().manager;
		for (String channelName : arg) {
			Bot bot = manager.botForChannel(channelName);
			if (bot != null) {
				Channel channel = bot.getUserChannelDao().getChannel(channelName);
				if (!channel.isOp(e.getUser())) {
					if (messages != null)
						messages.add(String.format("Not an op of channel '%s'.", channelName));
					return false;
				}
			}
		}
		return true;
	}
	
	protected CommandOutput onExecute(CommandStack stack, GenericUserMessageEvent e, String[] arg) {
		JSONObject j = new JSONObject();
		JSONObject jChannels = j.putNewObject("channels");
		
		List<String> parted = new ArrayList<>();
		BotManager manager = e.<Bot>getBot().manager;
		for (String channelName : arg) {
			Bot bot = manager.partChannel(channelName);
			if (bot != null) {
				jChannels.put(channelName, bot.getNick());
				parted.add(channelName);
			}
		}
		
		return new CommandOutput(j, "Parted channels: " + String.join(", ", parted));
	}
}