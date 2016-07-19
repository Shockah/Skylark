package io.shockah.skylark.botcontrol;

import io.shockah.skylark.Bot;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;

public class ReloadModulesCommand extends NamedCommand<Void, String> {
	public ReloadModulesCommand() {
		super("reload");
	}

	@Override
	public Void parseInput(GenericUserMessageEvent e, String input) {
		return null;
	}

	@Override
	public CommandResult<String> call(CommandCall call, Void input) {
		//if (!plugin.permissionGranted(call.event.getUser(), "reload"))
		//	return CommandResult.error("Permission required.");
		
		call.event.<Bot>getBot().manager.serverManager.app.pluginManager.reload();
		if (call.outputMedium == null)
			call.outputMedium = CommandCall.Medium.Notice;
		return CommandResult.of("Done.");
	}
}