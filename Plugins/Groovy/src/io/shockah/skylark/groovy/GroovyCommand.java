package io.shockah.skylark.groovy;

import io.shockah.json.JSONObject;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;

public class GroovyCommand extends NamedCommand<String, Object> {
	protected final GroovyPlugin plugin;
	
	public GroovyCommand(GroovyPlugin plugin) {
		super("groovy", "gr");
		this.plugin = plugin;
	}

	@Override
	public String parseInput(GenericUserMessageEvent e, String input) throws CommandParseException {
		return input;
	}

	@Override
	public CommandResult<Object> call(CommandCall call, String input) {
		return CommandResult.of(plugin.getShell(JSONObject.of(
			"call", call,
			"user", call.event.getUser(),
			"channel", call.event.getChannel()
		)).evaluate(input));
	}
}