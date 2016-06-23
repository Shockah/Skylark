package io.shockah.skylark.factoids;

import io.shockah.skylark.commands.CommandProvider;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;

public class FactoidCommandProvider extends CommandProvider {
	@Override
	public NamedCommand<?, ?> provide(GenericUserMessageEvent e, String commandName) {
		return null;
	}
}