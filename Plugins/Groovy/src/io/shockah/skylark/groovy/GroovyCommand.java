package io.shockah.skylark.groovy;

import java.util.LinkedHashMap;
import java.util.Map;
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
		try {
			Map<String, Object> variables = new LinkedHashMap<>();
			variables.put("call", call);
			variables.put("user", call.event.getUser());
			variables.put("channel", call.event.getChannel());
			return CommandResult.of(plugin.getShell(variables, new UserGroovySandbox(), call.event).evaluate(input));
		} catch (Exception e) {
			return CommandResult.error(e.getMessage());
		}
	}
}