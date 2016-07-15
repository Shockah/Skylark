package io.shockah.skylark.factoids;

import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandProvider;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.factoids.db.Factoid;

public class FactoidCommandProvider extends CommandProvider {
	protected final FactoidsPlugin plugin;
	
	public FactoidCommandProvider(FactoidsPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public NamedCommand<?, ?> provide(GenericUserMessageEvent e, String commandName) {
		Factoid factoid = plugin.findActiveFactoid(e, commandName);
		if (factoid == null)
			return null;
		
		FactoidType type = plugin.getType(factoid.type);
		if (type == null) {
			return new NamedCommand<Void, Void>(commandName) {
				@Override
				public Void parseInput(GenericUserMessageEvent e, String input) throws CommandParseException {
					return null;
				}

				@Override
				public CommandResult<Void> call(CommandCall call, Void input) {
					return CommandResult.error(String.format("Unknown factoid type %s.", factoid.type));
				}
			};
		}
		
		return type.createCommand(factoid);
	}
}