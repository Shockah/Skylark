package io.shockah.skylark.commands;

import java.util.HashMap;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.util.ReadWriteMap;

public class DefaultCommandProvider extends CommandProvider {
	private final ReadWriteMap<String, NamedCommand<?, ?>> commands = new ReadWriteMap<>(new HashMap<>());
	
	@Override
	public NamedCommand<?, ?> provide(GenericUserMessageEvent e, String commandName) {
		return commands.get(commandName);
	}
	
	public void addNamedCommand(NamedCommand<?, ?> command) {
		commands.writeOperation(commands -> {
			for (String name : command.names) {
				commands.put(name, command);
			}
		});
	}
	
	public void removeNamedCommand(NamedCommand<?, ?> command) {
		commands.writeOperation(commands -> {
			for (String name : command.names) {
				commands.remove(name);
			}
		});
	}
}