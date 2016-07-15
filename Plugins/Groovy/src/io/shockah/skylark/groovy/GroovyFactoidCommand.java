package io.shockah.skylark.groovy;

import io.shockah.json.JSONObject;
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
		return (CommandResult<R>)CommandResult.of(plugin.getShell(JSONObject.of(
			"call", call,
			"user", call.event.getUser(),
			"channel", call.event.getChannel(),
			"input", input
		)).evaluate(factoid.raw));
	}
}