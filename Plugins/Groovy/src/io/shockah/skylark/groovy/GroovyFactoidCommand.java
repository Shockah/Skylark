package io.shockah.skylark.groovy;

import java.util.LinkedHashMap;
import java.util.Map;
import groovy.lang.Tuple;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.factoids.AbstractFactoidCommand;
import io.shockah.skylark.factoids.db.Factoid;

public class GroovyFactoidCommand<T, R> extends AbstractFactoidCommand<T, R> {
	public final GroovyPlugin plugin;
	
	public GroovyFactoidCommand(GroovyPlugin plugin, Factoid factoid) {
		super(factoid);
		this.plugin = plugin;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T convertToInput(GenericUserMessageEvent e, Object input) throws CommandParseException {
		return (T)input;
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
			Object result = plugin.getShell(variables, new UserGroovySandboxImpl(), call.event).evaluate(factoid.raw);
			
			if (result instanceof CommandResult<?>)
				return (CommandResult<R>)result;
			
			if (result instanceof Tuple) {
				Tuple tuple = (Tuple)result;
				if (tuple.size() == 2)
					return (CommandResult<R>)CommandResult.of(tuple.get(0), tuple.get(1).toString());
			}
			
			return (CommandResult<R>)CommandResult.of(result);
		} catch (Exception e) {
			return CommandResult.error(e.getMessage());
		}
	}
}