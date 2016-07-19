package io.shockah.skylark.botcontrol;

import io.shockah.skylark.Bot;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;

public class ReloadModulesCommand extends NamedCommand<Void, String> {
	private final BotControlPlugin plugin;
	
	public ReloadModulesCommand(BotControlPlugin plugin) {
		super("reload");
		this.plugin = plugin;
	}

	@Override
	public Void parseInput(GenericUserMessageEvent e, String input) {
		return null;
	}

	@Override
	public CommandResult<String> call(CommandCall call, Void input) {
		if (call.outputMedium == null)
			call.outputMedium = CommandCall.Medium.Notice;
		if (!plugin.permissionsPlugin.permissionGranted(call.event.getUser(), plugin, names[0]))
			return CommandResult.error("Permission required.");
		
		call.event.<Bot>getBot().manager.serverManager.app.pluginManager.reload();
		if (call.outputMedium == null)
			call.outputMedium = CommandCall.Medium.Notice;
		return CommandResult.of("Done.");
	}
}