package io.shockah.skylark.groovy;

import java.util.LinkedHashMap;
import java.util.Map;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.factoids.db.Factoid;

public class GroovyFactoidCommand<T, R> extends NamedCommand<T, R> {
	public final GroovyPlugin plugin;
	public final Factoid factoid;
	
	public GroovyFactoidCommand(GroovyPlugin plugin, Factoid factoid) {
		super(factoid.name);
		this.plugin = plugin;
		this.factoid = factoid;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T parseInput(GenericUserMessageEvent e, String input) throws CommandParseException {
		return (T)input;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<R> call(CommandCall call, T input) {
		try {
			Map<String, Object> variables = new LinkedHashMap<>();
			variables.put("call", call);
			variables.put("user", call.event.getUser());
			variables.put("channel", call.event.getChannel());
			variables.put("input", input);
			return (CommandResult<R>)CommandResult.of(plugin.getShell(variables).evaluate(factoid.raw));
		} catch (Exception e) {
			return CommandResult.error(e.getMessage());
		}
	}
}