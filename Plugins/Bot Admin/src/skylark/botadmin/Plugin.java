package skylark.botadmin;

import java.util.ArrayList;
import java.util.List;
import pl.shockah.json.JSONObject;
import skylark.BotManager;
import skylark.PluginInfo;
import skylark.commands.Command;
import skylark.commands.CommandOutput;
import skylark.pircbotx.Bot;

public class Plugin extends skylark.ListenerPlugin {
	@Dependency
	protected static skylark.commands.Plugin commandsPlugin;
	
	protected Command
		joinCommand,
		partCommand;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		commandsPlugin.register(
			joinCommand = new Command.Delegate(this, "join", pinfo.packageName() + ".join", commandsPlugin.getSplitCommandInputParser(), (e, json) -> {
				JSONObject j = new JSONObject();
				JSONObject jChannels = j.putNewObject("channels");
				
				List<String> joined = new ArrayList<>();
				BotManager manager = e.<Bot>getBot().manager;
				for (String channelName : json.asList().ofStrings()) {
					Bot bot = manager.joinChannel(channelName);
					if (bot != null) {
						jChannels.put(channelName, bot.getNick());
						joined.add(channelName);
					}
				}
				
				return new CommandOutput(j, "Joined channels: " + String.join(", ", joined));
			}),
			partCommand = new Command.Delegate(this, "part", pinfo.packageName() + ".part", commandsPlugin.getSplitCommandInputParser(), (e, json) -> {
				JSONObject j = new JSONObject();
				JSONObject jChannels = j.putNewObject("channels");
				
				List<String> parted = new ArrayList<>();
				BotManager manager = e.<Bot>getBot().manager;
				for (String channelName : json.asList().ofStrings()) {
					Bot bot = manager.partChannel(channelName);
					if (bot != null) {
						jChannels.put(channelName, bot.getNick());
						parted.add(channelName);
					}
				}
				
				return new CommandOutput(j, "Parted channels: " + String.join(", ", parted));
			})
		);
	}
	
	protected void onUnload() {
		commandsPlugin.unregister(
			joinCommand,
			partCommand
		);
	}
}