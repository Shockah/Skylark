package skylark.youtube;

import me.shockah.skylark.event.GenericUserMessageEvent;
import pl.shockah.json.JSONObject;
import skylark.commands.CommandOutput;
import skylark.commands.CommandStack;
import skylark.commands.TypedArgCommand;

public class YouTubeCommand extends TypedArgCommand<String> {
	public static final String
		COMMAND_NAME = "youtube";
	
	protected final Plugin plugin;
	
	public YouTubeCommand(Plugin plugin) {
		super(plugin, COMMAND_NAME, String.format("%s.%s", plugin.pinfo.packageName(), COMMAND_NAME),
			((skylark.commands.Plugin)Plugin.commandsPlugin).getSimpleCommandInputParser()
		);
		this.plugin = plugin;
	}

	protected String getArg(GenericUserMessageEvent e, JSONObject json) {
		return getSimpleArg(json);
	}
	
	protected CommandOutput onExecute(CommandStack stack, GenericUserMessageEvent e, String arg) {
		VideoInfo info = plugin.performSearchRequest(arg);
		return new CommandOutput(info.json, info.format(true));
	}
}